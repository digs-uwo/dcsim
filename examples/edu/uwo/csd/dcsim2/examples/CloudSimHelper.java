package edu.uwo.csd.dcsim2.examples;

import java.io.File;
import java.util.ArrayList;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.vm.*;

public class CloudSimHelper {

	public static VMDescription createVMDesc(DataCentreSimulation simulation, int coreCapacity) {
		
		//create workload (random)
		Workload workload = new RandomWorkload(simulation, coreCapacity, 300000);
		simulation.addWorkload(workload);
		
		//create single tier (web tier)
		WebServerTier webServerTier = new WebServerTier(128, 1024, 1, 0, 0); //128MB RAM, 1GB Storage, 1 cpu per request, 0 bw per request, 0 cpu overhead
		webServerTier.setWorkTarget(workload);
		
		//set the tier as the target for the external workload
		workload.setWorkTarget(webServerTier);
		
		//build VMDescription
		int cores = 1; //requires 1 core
		int memory = 128;
		int bandwidth = 0; //16MB = 16384KB
		long storage = 1024; //1GB
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, webServerTier);

		return vmDescription;
	}
	
	public static ArrayList<VMAllocationRequest> createPlanetLabVMs(DataCentreSimulation simulation, String tracePath, int nVms) {
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		
		File inputFolder = new File(tracePath);
		File[] files = inputFolder.listFiles();
		
		if (nVms > files.length)
			throw new RuntimeException("Could not create " + nVms + " PlanetLab workloads, only " + files.length + " exist in folder '" + tracePath + "'");
		
		int vmSize[] = {1000, 2000, 2500, 3250};
		
		for (int i = 0; i < nVms; ++i) {
			
			int vmType = i / (int) Math.ceil((double) nVms / 4);
			
			Workload workload = new PlanetLabTraceWorkload(simulation, files[i].getAbsolutePath(), vmSize[vmType], 0);
			simulation.addWorkload(workload);
			
			//create single tier (web tier)
			WebServerTier webServerTier = new WebServerTier(1024, 1024, 1, 0, 0); //1GB RAM, 1GB Storage, 1 cpu per request, 0 bw per request, 0 cpu overhead
			webServerTier.setWorkTarget(workload);
			
			//set the tier as the target for the external workload
			workload.setWorkTarget(webServerTier);
			
			//build VMDescription
			int cores = 1; //requires 1 core
			int memory = 1024;
			int bandwidth = 102400; //100Mb/s
			long storage = 1024; //1GB
			VMDescription vmDescription = new VMDescription(cores, vmSize[vmType], memory, bandwidth, storage, webServerTier);
			
			vmList.add(new VMAllocationRequest(vmDescription));
		}
		
		return vmList;
	}
	
	public static VMDescription createTraceVMDesc(DataCentreSimulation simulation, String fileName, int coreCapacity, long offset) {
		
		//create workload (external)
		Workload workload = new TraceWorkload(simulation, fileName, coreCapacity, offset); //scale of 2700 + 300 overhead = 1 core on ProLiantDL380G5QuadCoreHost
		simulation.addWorkload(workload);
		
		//create single tier (web tier)
		WebServerTier webServerTier = new WebServerTier(128, 1024, 1, 0, 0); //256MB RAM, 0MG Storage, 1 cpu per request, 1 bw per request, 300 cpu overhead
		webServerTier.setWorkTarget(workload);
		
		//set the tier as the target for the external workload
		workload.setWorkTarget(webServerTier);
		
		//build VMDescription
		int cores = 1; //requires 1 core
		int memory = 1024;
		int bandwidth = 0; //16MB = 16384KB
		long storage = 1024; //1GB
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, webServerTier);

		return vmDescription;
	}
	
}
