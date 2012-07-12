package edu.uwo.csd.dcsim.extras.experiments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.DataCentreSimulation;
import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.FairShareCpuSchedulerFactory;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

/**
 * This class serves to create a common virtualized data centre environment in 
 * which to run our experiments.
 * 
 * @author Gaston Keller
 *
 */
public class DataCentreTestEnvironment {

	// The data centre is intended to be organized in the future in 5 clusters 
	// of 10 racks each, with each rack containing 40 servers, for a total 
	// count of 2000 servers.
	public static final int N_HOSTS = 200; // 2000
	public static final int N_VMS = 2000; // 20000
	
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
	
	private static Logger logger = Logger.getLogger(DataCentreTestEnvironment.class);
	
	/**
	 * Creates a DataCentre object, which contains a collection of hosts, and 
	 * uses the VMPlacementPolicyFFD for the initial placement of VMs in the 
	 * data centre.
	 * 
	 * @param simulation
	 * @return		the built data centre
	 */
	public static DataCentre createDataCentre(Simulation simulation) {
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(simulation);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		dc.addHosts(createHosts(simulation));
		return dc;
	}
	
	/**
	 * Creates the collection of hosts for the data centre. The hosts are 
	 * equally divided between the two available models: proLiantDL360G5E5450 
	 * and proLiantDL160G5E5420.
	 * 
	 * @param simulation
	 * @return		the collection of hosts
	 */
	private static ArrayList<Host> createHosts(Simulation simulation) {
		ArrayList<Host> hosts = new ArrayList<Host>();
		
		for (int i = 0; i < N_HOSTS; ++i) {
			Host host;
			
			Host.Builder proLiantDL360G5E5450 = HostModels.ProLiantDL360G5E5450(simulation).privCpu(500).privBandwidth(131072)
					.cpuManagerFactory(new OversubscribingCpuManagerFactory())
					.memoryManagerFactory(new SimpleMemoryManagerFactory())
					.bandwidthManagerFactory(new SimpleBandwidthManagerFactory())
					.storageManagerFactory(new SimpleStorageManagerFactory())
					.cpuSchedulerFactory(new FairShareCpuSchedulerFactory(simulation));
			
			Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
					.cpuManagerFactory(new OversubscribingCpuManagerFactory())
					.memoryManagerFactory(new SimpleMemoryManagerFactory())
					.bandwidthManagerFactory(new SimpleBandwidthManagerFactory())
					.storageManagerFactory(new SimpleStorageManagerFactory())
					.cpuSchedulerFactory(new FairShareCpuSchedulerFactory(simulation));
			
			if (i % 2 == 1) {
				host = proLiantDL360G5E5450.build();
			} else {
				host = proLiantDL160G5E5420.build();
			}
			
			hosts.add(host);
		}
		
		return hosts;
	}
	
	/**
	 * Creates a collection of VM allocation requests with which to populate 
	 * a data centre. Each VM is mapped to a service -- which will provide 
	 * the workload for the VM -- and is assigned a set of resource needs.
	 * 
	 * @param simulation
	 * @param allocAvg
	 * @return
	 */
	public static ArrayList<VMAllocationRequest> createVmList(DataCentreSimulation simulation, boolean allocAvg) {
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>(N_VMS);
		
		for (int i = 0; i < N_VMS; ++i) {
			String trace = TRACES[i % N_TRACES];
			long offset = (int)(simulation.getRandom().nextDouble() * OFFSET_MAX[i % N_TRACES]);
			
			int size = VM_SIZES[i % N_VM_SIZES];
			int cores = VM_CORES[i % N_VM_SIZES];
			int memory = VM_RAM[i % N_VM_SIZES];
			
			Service service = createService(simulation, trace, offset, size, cores, memory);
			VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(service.getServiceTiers().get(0).getVMDescription());
			
			if (allocAvg)
				vmAllocationRequest.setCpu((int)Math.round(TRACE_AVG[i % N_TRACES] * (size - CPU_OVERHEAD) + CPU_OVERHEAD));
			vmList.add(vmAllocationRequest);
		}
		
		Collections.shuffle(vmList, simulation.getRandom());
		return vmList;
	}
	
	/**
	 * Creates a single-tier interactive service to provide workload to a VM.
	 * 
	 * @return a single-tier interactive service
	 */
	private static Service createService(DataCentreSimulation simulation, String fileName, long offset, int coreCapacity, int cores, int memory) {
		// Create workload (external) for the service.
		Workload workload = new TraceWorkload(simulation, fileName, (coreCapacity * cores) - CPU_OVERHEAD, offset); //scale to n replicas
		simulation.addWorkload(workload);
		
		int bandwidth = 12800; //100 Mb/s
		long storage = 1024; //1GB
		Service service = Services.singleTierInteractiveService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, CPU_OVERHEAD, 1, Integer.MAX_VALUE); 
		
		return service;
	}
	
	/**
	 * Places the list of VMs in the given data centre, using the VM Placement 
	 * policy of the latter, and turns off any host that had no VMs assigned 
	 * to it in the initial placement.
	 * 
	 * @param vmList	list of VMs to place in the data centre
	 * @param dc		the data centre
	 */
	public static void placeVms(ArrayList<VMAllocationRequest> vmList, DataCentre dc) {
		if (!dc.getVMPlacementPolicy().submitVMs(vmList))
			throw new RuntimeException("Could not place all VMs");
		
		// Turn off hosts with no VMs assigned.
		for (Host host : dc.getHosts()) {
			if (host.getVMAllocations().size() == 0) {
				host.setState(Host.HostState.OFF);
			}
		}
	}
	
	/**
	 * Formats a simulation run results for output.
	 * 
	 * @param metrics	simulation run results
	 */
	public static void printMetrics(Collection<Metric> metrics) {
		for (Metric metric : metrics) {
			logger.info(metric.getName() +
					" = " +
					metric.toString());
		}
	}
	
}
