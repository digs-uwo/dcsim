package edu.uwo.csd.dcsim.examples;

import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.InteractiveApplicationTier;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.examples.management.RelocationPolicy;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.power.LinearHostPowerModel;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.management.policies.HostMonitoringPolicy;
import edu.uwo.csd.dcsim.management.policies.HostOperationsPolicy;
import edu.uwo.csd.dcsim.management.policies.HostStatusPolicy;
import edu.uwo.csd.dcsim.management.policies.DefaultVmPlacementPolicy;
import edu.uwo.csd.dcsim.vm.*;

/**
 * A basic example of how to setup and run a simulation without using some of the helper functions. This should
 * give a better understanding of the underlying structure of the simulation.
 * 
 * @author Michael Tighe
 *
 */
public class SimpleExampleDetailed extends SimulationTask {

	private static Logger logger = Logger.getLogger(SimpleExampleDetailed.class);
	
	public static void main(String args[]) {
		
		//MUST initialize logging when starting simulations
		Simulation.initializeLogging();
		
		//create an instance of our task, with the name "simple", to run for 86400000ms (1 day)
		SimulationTask task = new SimpleExampleDetailed("simple", 86400000);
		
		//run the simulation
		task.run();
		
		//get the results of the simulation
		Collection<Metric> metrics = task.getResults();
		
		//output metric values
		for (Metric metric : metrics) {
			logger.info(metric.getName() + "=" + metric.toString()); //metric.getValue() returns the raw value, while toString() provides formatting
		}
		
		//write the metric values to a trace file
		SimulationTraceWriter traceWriter = new SimulationTraceWriter(task);
		traceWriter.writeTrace();
		
	}
	
	public SimpleExampleDetailed(String name, long duration) {
		super(name, duration);
	}

	@Override
	public void setup(Simulation simulation) {
		
		/*
		 * Here we set up the simulation, but we do not run it.
		 */
		
		/*
		 * Create a new DataCenter object
		 */
		DataCentre dc = new DataCentre(simulation);
		
		//Add the DataCentre to the simulation
		simulation.addDatacentre(dc);
		
		/*
		 * Create and configure an AutonomicManager to manage the datacentre. This manager just needs to handle
		 * VM placement. To do this, it needs the HostPoolManager capability to keep track of hosts and host
		 * status updates (required by the placement policy), as well as a HostStatusPolicy to receive and process 
		 * host status updates. Finally, it requires the actual VmPlacementPolicy to handle placement.
		 */
		
		//Create the HostPoolManager capability separately, as we need to reference it later to add hosts
		HostPoolManager hostPool = new HostPoolManager();
		
		//Create a new AutonomicManager with this capability
		AutonomicManager dcAM = new AutonomicManager(simulation, hostPool);
		
		//Install the HostStatusPolicy and VmPlacementPolicy
		dcAM.installPolicy(new HostStatusPolicy(5));
		dcAM.installPolicy(new DefaultVmPlacementPolicy(0.5, 0.9, 0.85)); //TODO replace with a more basic placement policy
		
		/*
		 * Create a Host to add to the DataCentre. For this example, we will create a single Host using the Host.Builder class.
		 * 
		 * We add the default manager and scheduler, which oversubscribes CPU and schedules it fairly to all running VMs.
		 */
		
		Host host = new Host.Builder(simulation).nCpu(2).nCores(4).coreCapacity(2500).memory(16384).bandwidth(1310720).storage(36864)
				.privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory())
				.powerModel(new LinearHostPowerModel(150, 300))
				.build();
		
		//Add the Host to the DataCentre
		dc.addHost(host);
		
		//Create an AutonomicManager for the Host, with the HostManager capability (provides access to the host being managed)
		AutonomicManager hostAM = new AutonomicManager(simulation, new HostManager(host));
		
		//Install a HostMonitoringPolicy, which sends status updates to the datacentre manager, set to execute every 5 minutes
		hostAM.installPolicy(new HostMonitoringPolicy(dcAM), SimTime.minutes(5), 0);
		
		//Install a HostOperationsPolicy, which handles basic host operations
		hostAM.installPolicy(new HostOperationsPolicy());
		
		//Optionally, we can "install" the manager into the Host. This ensures that the manager does not run when the host is
		//not 'ON', and triggers hooks in the manager and policies on power on and off.
		host.installAutonomicManager(hostAM);
		
		//Add the Host to the DataCentre
		dc.addHost(host);
		
		//Add the Host to the HostPoolManager capability of our datacentre AutonomicManager
		hostPool.addHost(host, hostAM);

		
		/*
		 * Next, we create a Service to run on in DataCentre.
		 * 
		 * First, we create a Workload to generate work for the Service. We create a TraceWorkload,
		 * which bases the workload on a trace file. We will use the "clarknet" trace. We set the scaleFactor
		 * to 2200, which effectively sets the range of the workload to [0, 2200]. We chose 2200 because a single
		 * core of our Host has 2500 CPU, and later we are going to create an InteractiveApplication that requires
		 * 1 CPU to complete 1 Work. In addition, we will set a 300 CPU fixed overhead, thus, 2500 - 300 = 2200 will
		 * give us an incoming workload that at peak value will completely saturate one core of our Host.
		 */
		Workload workload = new TraceWorkload(simulation, "traces/clarknet", 2200, 0);
		simulation.addWorkload(workload); //be sure to add the Workload to the simulation, or incoming workload will not be retrieved
		
		/*
		 * Next we must create an ApplicationFactory for the Application we want to run. We create an InteractiveApplicationTier,
		 * which both coordinates a tier of Applications and plays the role of ApplicationFactory.
		 * 
		 * We specify that each Application in the tier will require 1024MB of RAM and 1024MB of storage, statically. It will 
		 * require 1 CPU and 1 Bandwidth to complete 1 work unit, and there is a 300 CPU fixed overhead.
		 */
		InteractiveApplicationTier appTier = new InteractiveApplicationTier(1024, 1024, 1, 1, 300);
		
		/*
		 * We need to configure the application tier to get work from the workload, and for the workload to get completed work from the tier
		 * back to the workload.
		 */
		appTier.setWorkSource(workload);
		workload.setCompletedWorkSource(appTier);
		
		/*
		 * Now, we must create a VMDescription which describe the properties of the VM we want to create. We create a VM that 
		 * has 1 virtual core with 2500 capacity, 1024MB of RAM, 12800KB of bandwidth (100Mb/s), and 1024MB storage. We specify that
		 * it will run applications created by our application tier.
		 */
		VMDescription vmDescription = new VMDescription(1, 2500, 1024, 12800, 1024, appTier);
		
		/*
		 * Finally, we create a VMAllocationRequest to submit to the DataCentre. A VMAllocationRequest represents a request for a Host to allocate
		 * resources for the instantiation of a VM. We can create one based on a VMDescription, which we can grab from the single tier of our
		 * Service.
		 */
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vmDescription);
		
		/*
		 * Next we submit the VMAllocationRequest to the DataCentre, which will place it on a Host. Since we only have one Host, that is 
		 * where it will be placed. This is done by sending a VmPlacmentEvent to the datacentre AutonomicManager.
		 * Note that we have to delay the placement until time '1', so that the datacentre AutonomicManager has received at least one status
		 * message from the Hosts.
		 */
		VmPlacementEvent vmPlacementEvent = new VmPlacementEvent(dcAM, vmAllocationRequest); 

		//Ensure that all VMs are placed, or kill the simulation
		vmPlacementEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				VmPlacementEvent pe = (VmPlacementEvent)e;
				if (!pe.getFailedRequests().isEmpty()) {
					throw new RuntimeException("Could not place all VMs " + pe.getFailedRequests().size());
				}
			}
			
		});
		
		simulation.sendEvent(vmPlacementEvent, 0);
		
		/*
		 * At this point we can add Management Policies to AutonomicManagers to perform any other actions we desire. We will add a RelocationPolicy,
		 * which looks for stressed Hosts and migrations VMs to relieve the stress situation. Since we only have one Host and one VM running, the policy
		 * won't have anything to do, but we will add one anyways to show how it is done.
		 * 
		 * First we need to create the policy. We specify the lower, upper, and target CPU utilization thresholds. The RelocationPolicy must be installed
		 * in an AutonomicManager that has the HostPoolManager capability. Since the datacentre AutonomicManager has this capability, we can install the 
		 * policy into this AutonomicManager.
		 * 
		 * We specify that the policy should execute on a regular interval of 1 hour.
		 */
		
		dcAM.installPolicy(new RelocationPolicy(0.5, 0.9, 0.85), SimTime.hours(1), SimTime.hours(1) + 1);
		
		/*
		 * The simulation is now ready. It will be executed when the run() method is called externally.
		 */
		
	}

}
