package edu.uwo.csd.dcsim2.examples.svm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DCSimUpdateController;
import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.application.Service;
import edu.uwo.csd.dcsim2.application.SingleTierWebService;
import edu.uwo.csd.dcsim2.application.WebServerTier;
import edu.uwo.csd.dcsim2.application.workload.PlanetLabTraceWorkload;
import edu.uwo.csd.dcsim2.application.workload.TraceWorkload;
import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

/**
 * A helper class containing common methods for use in experiments designed for the DCSim paper submitted to SVM 
 * @author Michael Tighe
 *
 */
public class SVMHelper {

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
	
	private static Logger logger = Logger.getLogger(SVMHelper.class);
	
	public static DataCentre createDataCentre() {
		//create datacentre
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(); //new VMPlacementPolicyFixedCount(7);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		Simulation.getInstance().setSimulationUpdateController(new DCSimUpdateController(dc));
		
		dc.addHosts(createHosts());
		
		return dc;
	}

	private static ArrayList<Host> createHosts() {
		ArrayList<Host> hosts = new ArrayList<Host>();
		
		for (int i = 0; i < N_HOSTS; ++i) {
			Host host;
			CpuManager cpuManager = new StaticOversubscribingCpuManager(500);
			MemoryManager memoryManager = new StaticMemoryManager();
			BandwidthManager bandwidthManager = new StaticBandwidthManager(131072); //assumes separate 1GB link for migration
			StorageManager storageManager = new StaticStorageManager();
			CpuScheduler cpuScheduler = new FairShareCpuScheduler();
			
			if (i % 2 == 1) {
				host = new ProLiantDL360G5E5450Host(cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);
			} else {
				host = new ProLiantDL160G5E5420Host(cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);
			}
			
			hosts.add(host);
		}
		
		return hosts;
	}
	
	public static ArrayList<VMAllocationRequest> createVmList(boolean allocAvg) {
		
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>(N_VMS);
		
		for (int i = 0; i < N_VMS; ++i) {
			String trace = TRACES[i % N_TRACES];
			long offset = (int)(Utility.getRandom().nextDouble() * OFFSET_MAX[i % N_TRACES]);
			
			int size = VM_SIZES[i % N_VM_SIZES];
			int cores = VM_CORES[i % N_VM_SIZES];
			int memory = VM_RAM[i % N_VM_SIZES];
			
			Service service = createService(trace, offset, size, cores, memory);
			
			//vmList.addAll(service.createInitialVmRequests());
			
			VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(service.getServiceTiers().get(0).getVMDescription());
			
			if (allocAvg)
				vmAllocationRequest.setCpu((int)Math.round(TRACE_AVG[i % N_TRACES] * (size - CPU_OVERHEAD) + CPU_OVERHEAD));
			
			vmList.add(vmAllocationRequest);
		}
		
		Collections.shuffle(vmList, Utility.getRandom());
		
		return vmList;
	}
	

	private static Service createService(String fileName, long offset, int coreCapacity, int cores, int memory) {
		
		//create workload (external)
		Workload workload = new TraceWorkload(fileName, (coreCapacity * cores) - CPU_OVERHEAD, offset); //scale to n replicas
		
		int bandwidth = 12800; //100 Mb/s
		long storage = 1024; //1GB
		
		SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, CPU_OVERHEAD);

		return webService;

	}
	
	public static void placeVms(ArrayList<VMAllocationRequest> vmList, DataCentre dc) {
		
		if (!dc.getVMPlacementPolicy().submitVMs(vmList))
			throw new RuntimeException("Could not place all VMs");
		
		//turn off hosts with no VMs
		for (Host host : dc.getHosts()) {
			if (host.getVMAllocations().size() == 0) {
				host.setState(Host.HostState.OFF);
			}
		}
	}
	
	public static void runSimulation(long duration, long recordStart) {
		
		
		//run the simulation
		Simulation.getInstance().run(duration, recordStart);
		
		
	}
	
	
	public static ArrayList<VMAllocationRequest> createPlanetLabVmList(String tracePath) {
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>(N_VMS);
		
		File inputFolder = new File(tracePath);
		File[] files = inputFolder.listFiles();
		
		if (N_VMS > files.length)
			throw new RuntimeException("Could not create " + N_VMS + " PlanetLab workloads, only " + files.length + " exist in folder '" + tracePath + "'");
		
		for (int i = 0; i < N_VMS; ++i) {
			
			int vmType = i % 3;
			
			Workload workload = new PlanetLabTraceWorkload(files[i].getAbsolutePath(), VM_SIZES[vmType], 0);
			
			//build VMDescription
			int cores = 1; //requires 1 core
			int memory = 1024;
			int bandwidth = 16384; //102400; //100Mb/s
			long storage = 1024; //1GB
			
			//create single tier (web tier)
			WebServerTier webServerTier = new WebServerTier(memory, storage, 1, 0, 0); //1GB RAM, 1GB Storage, 1 cpu per request, 0 bw per request, 0 cpu overhead
			webServerTier.setWorkTarget(workload);
			
			//set the tier as the target for the external workload
			workload.setWorkTarget(webServerTier);
			
			VMDescription vmDescription = new VMDescription(cores, VM_SIZES[vmType], memory, bandwidth, storage, webServerTier);
			
			vmList.add(new VMAllocationRequest(vmDescription));
		}
		
		
		return vmList;
	}
	
	
}
