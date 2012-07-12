package edu.uwo.csd.dcsim.examples;

import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.InteractiveApplicationTier;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.power.LinearHostPowerModel;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.FairShareCpuSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

/**
 * A basic example of how to setup and run a simulation without using some of the helper functions. This should
 * give a better understanding of the underlying structure of the simulation.
 * 
 * @author Michael Tighe
 *
 */
public class SimpleExampleDetailed extends DCSimulationTask {

	private static Logger logger = Logger.getLogger(SimpleExampleDetailed.class);
	
	public static void main(String args[]) {
		
		//MUST initialize logging when starting simulations
		Simulation.initializeLogging();
		
		//create an instance of our task, with the name "simple", to run for 86400000ms (1 day)
		DCSimulationTask task = new SimpleExampleDetailed("simple", 86400000);
		
		//run the simulation
		task.run();
		
		//get the results of the simulation
		Collection<Metric> metrics = task.getResults();
		
		//output metric values
		for (Metric metric : metrics) {
			logger.info(metric.getName() + "=" + metric.toString()); //metric.getValue() returns the raw value, while toString() provides formatting
		}
		
	}
	
	public SimpleExampleDetailed(String name, long duration) {
		super(name, duration);
	}

	@Override
	public void setup(DataCentreSimulation simulation) {
		
		/*
		 * Here we set up the simulation, but we do not run it.
		 */
		
		/* 
		 * Create a VMPlacementPolicy for the DataCentre. The VMPlacementPolicy handles placing 
		 * VMs submitted to the DataCentre on Hosts. We create a VMPlacementPolicyFFD, which implements
		 * a First Fit Decreasing algorithm for placing VMs. This algorithm attempts to pack VMs on a small
		 * number of Hosts. 
		 */
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(simulation);
		
		/*
		 * Create a new DataCenter object
		 */
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		//Add the DataCentre to the simulation
		simulation.addDatacentre(dc);
		
		/*
		 * Create a Host to add to the DataCentre. For this example, we will create a single Host using the Host.Builder class.
		 * 
		 * We add simple managers for all resources, except for CPU, for which we 
		 * add the Oversubscribing manager. Simple managers allocate a fixed amount of resources up to
		 * the maximum resource available. The Oversubscribing manager allocates any amount of CPU resources,
		 * even if the allocated amount exceeds the actual Host capacity.
		 * 
		 * We add the FairShareCpuScheduler to the host, which gives each VM a chance to use an equal amount of CPU on the Host. This
		 * does not mean that each VM will use an equal amount, as it may not require it. In this case, another VM may use the leftover
		 * CPU.
		 */
		
		Host host = new Host.Builder(simulation).nCpu(2).nCores(4).coreCapacity(2500).memory(16384).bandwidth(1310720).storage(36864)
				.privCpu(500).privBandwidth(131072)
				.cpuManagerFactory(new OversubscribingCpuManagerFactory())
				.memoryManagerFactory(new SimpleMemoryManagerFactory())
				.bandwidthManagerFactory(new SimpleBandwidthManagerFactory())
				.storageManagerFactory(new SimpleStorageManagerFactory())
				.cpuSchedulerFactory(new FairShareCpuSchedulerFactory(simulation))
				.powerModel(new LinearHostPowerModel(150, 300))
				.build();
		
		//Add the Host to the DataCentre
		dc.addHost(host);
		

		
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
		 * We need to configure the workload to send work to our application tier, and for the tier to send completed work
		 * back to the workload.
		 */
		workload.setWorkTarget(appTier);
		appTier.setWorkTarget(workload);
		
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
		 * where it will be placed. Note that we can do this before the simulation is run, so that we have an initial placement at the
		 * beginning of the simulation. Once our host accepts and creates a VMAllocation for the VM, it will instantiate the VM using
		 * the VMDescription.
		 */
		dc.getVMPlacementPolicy().submitVM(vmAllocationRequest);
		
		/*
		 * At this point we can add Management Policies to the simulation. Since we only have one Host and one VM running, a Management Policy
		 * won't have anything to do, but we will add one anyways to show how it is done.
		 * 
		 * First we need to create the policy. We specify the datacentre which it will manage and the lower, upper, and target
		 * CPU utilization thresholds. Note that we can create any number of ManagementPolicies that we want.
		 * 
		 * Policies run in the simulation as a 'Daemon', which is simply a piece of code that is executed on a fixed interval. The policy
		 * implements the Daemon interface. We create a DaemonScheduler to run the daemon on a fixed interval. Finally, we start the daemon
		 * at the specified simulation time. 
		 */
		VMAllocationPolicyGreedy vmAllocationPolicyGreedy = new VMAllocationPolicyGreedy(dc, 0.5, 0.85, 0.85);
		DaemonScheduler daemon = new FixedIntervalDaemonScheduler(simulation, 600000, vmAllocationPolicyGreedy);
		daemon.start(600000);
		
		/*
		 * The simulation is now ready. It will be executed when the run() method is called externally.
		 */
		
	}

}
