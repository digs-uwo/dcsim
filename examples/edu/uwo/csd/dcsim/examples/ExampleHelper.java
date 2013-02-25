package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.*;
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
 * A helper class containing common methods for use in experiments designed for the DCSim paper submitted to SVM 
 * @author Michael Tighe
 *
 */
public class ExampleHelper {

	public static final int N_HOSTS = 200; //200
	public static final int N_VMS = 400; //400
	
	public static final int CPU_OVERHEAD = 200;
	public static final int[] VM_SIZES = {1500, 2500, 3000, 3000};
	public static final int[] VM_CORES = {1, 1, 1, 2};
	public static final int[] VM_RAM = {512, 1024, 1024, 1024};
	public static final int N_VM_SIZES = 4;
	
	public static final int N_TRACES = 5; 
	public static final String[] TRACES = {"traces/clarknet", 
		"traces/epa",
		"traces/sdsc",
		"traces/google_cores_job_type_0", 
		"traces/google_cores_job_type_1",
		"traces/google_cores_job_type_2",
		"traces/google_cores_job_type_3"};	
	public static final long[] OFFSET_MAX = {200000000, 40000000, 40000000, 15000000, 15000000, 15000000, 15000000};
	public static final double[] TRACE_AVG = {0.32, 0.25, 0.32, 0.72, 0.74, 0.77, 0.83};
	
	private static Logger logger = Logger.getLogger(ExampleHelper.class);
	
	public static AutonomicManager createDataCentre(Simulation simulation) {
		//create datacentre
		DataCentre dc = new DataCentre(simulation);
		simulation.addDatacentre(dc);
		
		HostPoolManager hostPool = new HostPoolManager();
		AutonomicManager dcAM = new AutonomicManager(simulation, hostPool);
		dcAM.installPolicy(new HostStatusPolicy(5));
		dcAM.installPolicy(new DefaultVmPlacementPolicy(0.5, 0.9, 0.85));
		
		dc.addHosts(createHosts(simulation, dcAM, hostPool));
		
		return dcAM;
	}

	private static ArrayList<Host> createHosts(Simulation simulation, AutonomicManager dcAM, HostPoolManager hostPool) {
		ArrayList<Host> hosts = new ArrayList<Host>();
		
		for (int i = 0; i < N_HOSTS; ++i) {
			Host host;
			
			Host.Builder proLiantDL360G5E5450 = HostModels.ProLiantDL360G5E5450(simulation).privCpu(500).privBandwidth(131072)
					.resourceManagerFactory(new DefaultResourceManagerFactory())
					.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
			
			Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
					.resourceManagerFactory(new DefaultResourceManagerFactory())
					.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
			
			if (i % 2 == 1) {
				host = proLiantDL360G5E5450.build();
			} else {
				host = proLiantDL160G5E5420.build();
			}
			
			host.setState(Host.HostState.OFF); //turn hosts off by default
			
			AutonomicManager hostAM = new AutonomicManager(simulation, new HostManager(host));
			hostAM.installPolicy(new HostMonitoringPolicy(dcAM), SimTime.minutes(5), SimTime.minutes(simulation.getRandom().nextInt(4)) );
			hostAM.installPolicy(new HostOperationsPolicy());
			
			host.installAutonomicManager(hostAM);
			
			hostPool.addHost(host, hostAM);
			hosts.add(host);
		}
		
		return hosts;
	}
	
	public static ArrayList<VMAllocationRequest> createVmList(Simulation simulation, boolean allocAvg) {
		
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>(N_VMS);
		
		for (int i = 0; i < N_VMS; ++i) {
			String trace = TRACES[i % N_TRACES];
			long offset = (int)(simulation.getRandom().nextDouble() * OFFSET_MAX[i % N_TRACES]);
			
			int size = VM_SIZES[i % N_VM_SIZES];
			int cores = VM_CORES[i % N_VM_SIZES];
			int memory = VM_RAM[i % N_VM_SIZES];
			
			Service service = createService(simulation, trace, offset, size, cores, memory);
			
			//vmList.addAll(service.createInitialVmRequests());
			
			VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(service.getServiceTiers().get(0).getVMDescription());
			
			if (allocAvg)
				vmAllocationRequest.setCpu((int)Math.round(TRACE_AVG[i % N_TRACES] * (size - CPU_OVERHEAD) + CPU_OVERHEAD));
			
			vmList.add(vmAllocationRequest);
		}
		
		Collections.shuffle(vmList, simulation.getRandom());
		
		return vmList;
	}
	

	private static Service createService(Simulation simulation, String fileName, long offset, int coreCapacity, int cores, int memory) {
		
		//create workload (external)
		Workload workload = new TraceWorkload(simulation, fileName, (coreCapacity * cores) - CPU_OVERHEAD, offset); //scale to n replicas
		simulation.addWorkload(workload);
		
		
		int bandwidth = 12800; //100 Mb/s
		long storage = 1024; //1GB

		Service service = Services.singleTierInteractiveService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, CPU_OVERHEAD, 1, Integer.MAX_VALUE); 
		
		return service;

	}
	
	public static void placeVms(ArrayList<VMAllocationRequest> vmList, AutonomicManager dcAM, Simulation simulation) {
		
		VmPlacementEvent vmPlacementEvent = new VmPlacementEvent(dcAM, vmList);
		
		//ensure that all VMs are placed, or kill the simulation
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
	}
	
	public static void printMetrics(Collection<Metric> metrics) {
		for (Metric metric : metrics) {
			logger.info(metric.getName() +
					" = " +
					metric.toString());
		}
	}
	
}
