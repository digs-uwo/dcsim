package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.InteractiveApplication;
import edu.uwo.csd.dcsim.application.sla.InteractiveServiceLevelAgreement;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.examples.management.ConsolidationPolicy;
import edu.uwo.csd.dcsim.examples.management.RelocationPolicy;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.HostModels;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.host.resourcemanager.DefaultResourceManagerFactory;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.capabilities.*;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.management.policies.*;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

public class ApplicationExample extends SimulationTask {

	private static Logger logger = Logger.getLogger(ApplicationExample.class);
	
	public static void main(String args[]) {
	
		Simulation.initializeLogging();
		
		SimulationTask task = new ApplicationExample("AppExample", SimTime.days(10));
		
		task.run();
		
		task.getMetrics().printDefault(logger);
		
	}
	
	public ApplicationExample(String name, long duration) {
		super(name, duration);
		this.setMetricRecordStart(SimTime.minutes(10));
		this.setRandomSeed(6662890007189740306l);
	}

	@Override
	public void setup(Simulation simulation) {
		
		DataCentre dc = new DataCentre(simulation);
		
		//Add the DataCentre to the simulation
		simulation.addDatacentre(dc);
		
		//Create the HostPoolManager capability separately, as we need to reference it later to add hosts
		HostPoolManager hostPool = new HostPoolManager();
		
		//Create a new AutonomicManager with this capability
		AutonomicManager dcAM = new AutonomicManager(simulation, hostPool);
		
		//Install the HostStatusPolicy and VmPlacementPolicy
		dcAM.installPolicy(new HostStatusPolicy(5));
		dcAM.installPolicy(new DefaultVmPlacementPolicy());
		
		//Create hosts
		
		Host.Builder hostBuilder = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		//Instantiate the Hosts
		ArrayList<Host> hosts = new ArrayList<Host>();
		for (int i = 0; i < 20; ++i) {
			Host host = hostBuilder.build();
			
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
			
			hosts.add(host);
		}
		
		//Create applications
		ArrayList<VmAllocationRequest> vmRequests = new ArrayList<VmAllocationRequest>();
		
		for (int i = 0; i < 80; ++i) {
//			StaticWorkload workload = new StaticWorkload(simulation);
			TraceWorkload workload = new TraceWorkload(simulation, "traces/clarknet", (int)(simulation.getRandom().nextDouble() * 200000000));
			InteractiveApplication.Builder appBuilder = new InteractiveApplication.Builder(simulation).workload(workload).thinkTime(4)
					.task(1, 1, new Resources(2500,1,1,1), 0.005, 1)
					.task(2, 2, new Resources(2500,1,1,1), 0.02, 1)
					.task(1, 1, new Resources(2500,1,1,1), 0.01, 1);
			
			InteractiveApplication app = appBuilder.build();
//			workload.setScaleFactor(app.calculateMaxWorkloadResponseTimeLimit(1)); //scale to 1s response time
			workload.setScaleFactor(app.calculateMaxWorkloadUtilizationLimit(0.98f)); //scale to 98% utilization. Scaling to 100% will result in an explosion in work level
			
			InteractiveServiceLevelAgreement sla = new InteractiveServiceLevelAgreement(app).responseTime(1, 1); //sla limit at 1s response time
			app.setSla(sla);
			
			//place applications
			vmRequests.addAll(app.createInitialVmRequests());

		}
		
		VmPlacementEvent vmPlacementEvent = new VmPlacementEvent(dcAM, vmRequests);
		simulation.sendEvent(vmPlacementEvent, 0);
		
		dcAM.installPolicy(new RelocationPolicy(0.5, 0.9, 0.85), SimTime.hours(1), SimTime.hours(1) + 1);
		dcAM.installPolicy(new ConsolidationPolicy(0.5, 0.9, 0.85), SimTime.hours(2), SimTime.hours(2) + 2);
	}
	
}

