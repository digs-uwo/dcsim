package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.examples.management.*;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.DefaultResourceManagerFactory;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.*;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.management.policies.HostMonitoringPolicy;
import edu.uwo.csd.dcsim.management.policies.HostOperationsPolicy;
import edu.uwo.csd.dcsim.management.policies.HostStatusPolicy;
import edu.uwo.csd.dcsim.vm.*;

public class AutonomicManagement extends SimulationTask {

	private static Logger logger = Logger.getLogger(AutonomicManagement.class);
	
	private static final int N_HOSTS = 10;
	private static final int N_VMS = 50;
	
	public static void main(String args[]) {
		//MUST initialize logging when starting simulations
		Simulation.initializeLogging();
		
		SimulationTask task = new AutonomicManagement("autonomic_management", 1088501048448116498l);
		
		task.run();
		
		Collection<Metric> metrics = task.getResults();
		
		for (Metric metric : metrics) {
			logger.info(metric.getName() + "=" + metric.toString()); //metric.getValue() returns the raw value, while toString() provides formatting
		}
		
		//write the metric values to a trace file
		SimulationTraceWriter traceWriter = new SimulationTraceWriter(task);
		traceWriter.writeTrace();
	}
	
	public AutonomicManagement(String name, long randomSeed) {
		super(name, SimTime.days(15));
		this.setRandomSeed(randomSeed);
		this.setMetricRecordStart(0);
	}
	
	public AutonomicManagement(String name) {
		super(name, SimTime.days(5));
	}

	@Override
	public void setup(Simulation simulation) {
		
		DataCentre dc = new DataCentre(simulation);
		simulation.addDatacentre(dc);
		
		HostPoolManager hostPool = new HostPoolManager();
		AutonomicManager dcAM = new AutonomicManager(simulation, hostPool);
		dcAM.installPolicy(new HostStatusPolicy(5));
		dcAM.installPolicy(new VmPlacementPolicy(0.5, 0.9, 0.85));
		dcAM.installPolicy(new RelocationPolicy(0.5, 0.9, 0.85), SimTime.hours(1), SimTime.hours(1) + 1);
		dcAM.installPolicy(new ConsolidationPolicy(0.5, 0.9, 0.85), SimTime.hours(2), SimTime.hours(2) + 2);
		
		//create hosts
		Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		//Instantiate Hosts and add them to the datacentre
		Host host;
		for (int i = 0; i < N_HOSTS; ++i) {
			host = proLiantDL160G5E5420.build();
			
			AutonomicManager hostAM = new AutonomicManager(simulation, new HostManager(host));
			hostAM.installPolicy(new HostMonitoringPolicy(dcAM), SimTime.minutes(5), SimTime.minutes(simulation.getRandom().nextInt(5)));
			hostAM.installPolicy(new HostOperationsPolicy());

			host.installAutonomicManager(hostAM);
			
			dc.addHost(host);
			hostPool.addHost(host, hostAM); //this is the host pool used by the data centre manager
		}
		
		//Instantiate VMs and submit them to the datacentre
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		for (int i = 0; i < N_VMS; ++i) {
			//create a new workload for this VM
			Workload workload = new TraceWorkload(simulation, "traces/clarknet", 2200, (int)(simulation.getRandom().nextDouble() * 200000000));
			simulation.addWorkload(workload);
			
			//create the service this VM will be a part of
			Service service = Services.singleTierInteractiveService(workload, 1, 2500, 512, 12800, 1024, 1, 300, 1, Integer.MAX_VALUE);
			
			//create a new VMAllocationRequest using the VMDescription from the service, add it to the vm list
			vmList.add(new VMAllocationRequest(service.getServiceTiers().get(0).getVMDescription()));
		}
		
		//submit the VMs to the datacentre for placement
		VmPlacementEvent vmPlacementEvent = new VmPlacementEvent(dcAM, vmList);
		
		vmPlacementEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				VmPlacementEvent pe = (VmPlacementEvent)e;
				if (!pe.getFailedRequests().isEmpty()) {
					throw new RuntimeException("Could not place all VMs " + pe.getFailedRequests().size());
				}
			}
			
		});
		
		simulation.sendEvent(vmPlacementEvent, SimTime.minutes(0));
	}

}

