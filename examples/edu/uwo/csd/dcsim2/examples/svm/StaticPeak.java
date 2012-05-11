package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;
import java.util.Collection;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.core.metrics.Metric;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticPeak {

	public static void main(String args[]) {
		
		Simulation.initializeLogging();
		
		runSimulation("staticpeak-1", 1088501048448116498l);
//		runSimulation("staticpeak-2", 3081198553457496232l);
//		runSimulation("staticpeak-3", -2485691440833440205l);
//		runSimulation("staticpeak-4", 2074739686644571611l);
//		runSimulation("staticpeak-5", -1519296228623429147l);
		
	}
	
	public static void runSimulation(String name, long seed) {
		DataCentreSimulation simulation = new DataCentreSimulation(name, seed);

		DataCentre dc = SVMHelper.createDataCentre(simulation);
		simulation.addDatacentre(dc);
		
		ArrayList<VMAllocationRequest> vmList = SVMHelper.createVmList(simulation, false);
		
		SVMHelper.placeVms(vmList, dc);
				
		Collection<Metric> metrics = simulation.run(864000000, 86400000);
		
	}
	
}
