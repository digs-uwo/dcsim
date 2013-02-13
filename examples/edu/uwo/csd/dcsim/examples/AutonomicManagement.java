package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.Service;
import edu.uwo.csd.dcsim.application.Services;
import edu.uwo.csd.dcsim.application.workload.TraceWorkload;
import edu.uwo.csd.dcsim.application.workload.Workload;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.examples.managers.*;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.DefaultResourceManagerFactory;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

public class AutonomicManagement extends SimulationTask {

	private static Logger logger = Logger.getLogger(AutonomicManagement.class);
	
	private static final int N_HOSTS = 2;
	private static final int N_VMS = 10;
	
	public static void main(String args[]) {
		//MUST initialize logging when starting simulations
		Simulation.initializeLogging();
		
		SimulationTask task = new AutonomicManagement("autonomic_management");
		
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
		super(name, SimTime.days(1));
		this.setRandomSeed(randomSeed);
		this.setMetricRecordStart(0);
	}
	
	public AutonomicManagement(String name) {
		super(name, SimTime.days(1));
	}

	@Override
	public void setup(Simulation simulation) {

		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(simulation);
		
		DataCentre dc = new DataCentre(simulation, vmPlacementPolicy);
		simulation.addDatacentre(dc);
		
		DataCentreAutonomicManager dcAM = new DataCentreAutonomicManager();
		dcAM.installPolicy(new HostStatePolicy());
		dcAM.installPolicy(new RelocationPolicy());
		dcAM.installPolicy(new ConsolidationPolicy());
		
		RelocateEvent relocateEvent = new RelocateEvent(simulation, dcAM, SimTime.hours(2));
		relocateEvent.start(1);
		ConsolidateEvent consolidateEvent = new ConsolidateEvent(simulation, dcAM, SimTime.hours(4));
		consolidateEvent.start(2);
		
		//create hosts
		Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		//Instantiate Hosts and add them to the datacentre
		Host host;
		for (int i = 0; i < N_HOSTS; ++i) {
			host = proLiantDL160G5E5420.build();
			
			HostAutonomicManager hostAM = new HostAutonomicManager(host, dcAM);
			hostAM.installPolicy(new HostMonitoringPolicy());
			HostMonitorEvent event = new HostMonitorEvent(simulation, hostAM, SimTime.hours(1));
			event.start();
			
			dc.addHost(host);
		}
		
		//Instantiate VMs and submit them to the datacentre
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		for (int i = 0; i < N_VMS; ++i) {
			//create a new workload for this VM
			Workload workload = new TraceWorkload(simulation, "traces/clarknet", 2200, 0);
			simulation.addWorkload(workload);
			
			//create the service this VM will be a part of
			Service service = Services.singleTierInteractiveService(workload, 1, 2500, 1024, 12800, 1024, 1, 300, 1, Integer.MAX_VALUE);
			
			//create a new VMAllocationRequest using the VMDescription from the service, add it to the vm list
			vmList.add(new VMAllocationRequest(service.getServiceTiers().get(0).getVMDescription()));
		}
		
		//submit the VMs to the datacentre for placement
		vmPlacementPolicy.submitVMs(vmList);
		
		
		
		
	}

}
