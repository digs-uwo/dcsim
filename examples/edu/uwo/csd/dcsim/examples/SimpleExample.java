package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.Applications;
import edu.uwo.csd.dcsim.application.InteractiveApplication;
import edu.uwo.csd.dcsim.application.workload.TraceWorkload;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.examples.management.RelocationPolicy;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.HostModels;
import edu.uwo.csd.dcsim.host.resourcemanager.DefaultResourceManagerFactory;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.management.policies.HostMonitoringPolicy;
import edu.uwo.csd.dcsim.management.policies.HostOperationsPolicy;
import edu.uwo.csd.dcsim.management.policies.HostStatusPolicy;
import edu.uwo.csd.dcsim.management.policies.DefaultVmPlacementPolicy;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

/**
 * A basic example of how to setup and run a simulation.
 * 
 * @author Michael Tighe
 *
 */
public class SimpleExample extends SimulationTask {

	private static Logger logger = Logger.getLogger(SimpleExample.class);
	
	public static void main(String args[]) {
		
		//MUST initialize logging when starting simulations
		Simulation.initializeLogging();
		
		//create an instance of our task, with the name "simple", to run for 10 minutes
		SimulationTask task = new SimpleExample("simple", SimTime.minutes(10));
		
		//run the simulation
		task.run();

		//output metric values
		task.getMetrics().printDefault(logger);
		
	}
	
	public SimpleExample(String name, long duration) {
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
		dcAM.installPolicy(new DefaultVmPlacementPolicy());
		
		/*
		 * Create a Host to add to the DataCentre. For this example, we will create a single Host, using
		 * a factory method in the HostModels class to create a prebuilt host model. This returns
		 * a Host.Builder object, which has been partially initialized with the properties of the Host. We
		 * still must add the resource manager and resource scheduler. This is done by adding factories for the
		 * resource manager and scheduler. We add the default manager and scheduler, which oversubscribes CPU
		 * and distributes it fairly between all VMs.
		 * 
		 * Then we must add an AutonomicManager to manage the Host. The main tasks of this manager are to send
		 * periodic status updates to the datacentre manager, and to handle events for basic host operations such
		 * as VM instantiation and migration.
		 */
		
		Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		//Instantiate the Host
		Host host = proLiantDL160G5E5420.build();
		
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
		 * which bases the workload on a trace file. We will use the "clarknet" trace. The final value
		 * is the time offset at which to start the trace, which we will leave at 0. 
		 */
		TraceWorkload workload = new TraceWorkload(simulation, "traces/clarknet", 0);
		
		/*
		 * We use the Applications helper class to build an Interactive Application with a single Task. The Task will have only one Task Instance, and
		 * therefore only one VM will be required to run this Application.
		 * 
		 * We specify that VM in the Task should use 1 core of 2500 capacity, 1024MB of RAM, 12800KB of Bandwidth (100Mb/s) (NOTE: bandwidth is in KB),
		 * 1024MB of storage. The Task requires 0.01 to service a single request.
		 */
		InteractiveApplication application = Applications.singleTaskInteractiveApplication(simulation, workload, 1, 2500, 1024, 12800, 1024, 0.01);
		
		/*
		 * Now we must scale the workload to match our Application. Trace workloads have values in the range [0, 1], which must be scale appropriately
		 * to provide the required number of clients for the Interactive Application. We set the scale equal to the calculated maximum number of clients
		 * required to push the Application utilization to 98%. We choose 98% instead of 100% because it requires a significantly larger number of clients
		 * to push the Application completely to 100% utilization.
		 */
		workload.setScaleFactor(application.calculateMaxWorkloadUtilizationLimit(0.98));
		
		/*
		 * Now we create the VMAllocationRequest(s) to submit to the DataCentre. A VMAllocationRequest represents a request for a Host to allocate
		 * resources for the instantiation of a VM. We can get the set of requests for initial deployment from the Application. In this case, there
		 * will be only one.
		 */
		ArrayList<VmAllocationRequest> vmAllocationRequests = application.createInitialVmRequests();
		
		/*
		 * Next we submit the VMAllocationRequest list to the DataCentre, which will place it on a Host. Since we only have one Host, that is 
		 * where it will be placed. This is done by sending a VmPlacmentEvent to the datacentre AutonomicManager.
		 */
		VmPlacementEvent vmPlacementEvent = new VmPlacementEvent(dcAM, vmAllocationRequests); 

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
