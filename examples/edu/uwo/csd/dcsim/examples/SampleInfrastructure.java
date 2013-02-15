package edu.uwo.csd.dcsim.examples;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.DefaultResourceSchedulerFactory;

/**
 * This class creates a sample virtualized data centre environment.
 * 
 * @author Gaston Keller
 *
 */
public class SampleInfrastructure {

	public static final int N_CLUSTERS = 5;
	public static final int N_RACKS = 10;
	public static final int N_HOSTS = 40;
//	public static final int N_VMS = 600; // 20000
	
	/**
	 * Creates a data centre, organized in Clusters, which contain a collection
	 * of Racks, which in turn contain a collection of Hosts.
	 */
	public static DataCentre createDataCentre(Simulation simulation) {
		// Define Switch types.
		SwitchFactory switch10g48p = new SwitchFactory(10000000, 48, 100);
		SwitchFactory switch40g24p = new SwitchFactory(40000000, 24, 100);
		
		// Define Host types.
		Host.Builder proLiantDL380G5QuadCore = HostModels.ProLiantDL380G5QuadCore(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		Host.Builder proLiantDL160G5E5420 = HostModels.ProLiantDL160G5E5420(simulation).privCpu(500).privBandwidth(131072)
				.resourceManagerFactory(new DefaultResourceManagerFactory())
				.resourceSchedulerFactory(new DefaultResourceSchedulerFactory());
		
		// Define Rack types.
		Rack.Builder seriesA = new Rack.Builder(simulation).nSlots(40).nHosts(N_HOSTS)
				.hostBuilder(proLiantDL380G5QuadCore)
				.switchFactory(switch10g48p);
		
		Rack.Builder seriesB = new Rack.Builder(simulation).nSlots(40).nHosts(N_HOSTS)
				.hostBuilder(proLiantDL160G5E5420)
				.switchFactory(switch10g48p);
		
		// Define Cluster types.
		Cluster.Builder series09 = new Cluster.Builder(simulation).nRacks(N_RACKS).nSwitches(1)
				.rackBuilder(seriesA)
				.switchFactory(switch40g24p);
		
		Cluster.Builder series11 = new Cluster.Builder(simulation).nRacks(N_RACKS).nSwitches(1)
				.rackBuilder(seriesB)
				.switchFactory(switch40g24p);
		
		// Create data centre.
		DataCentre dc = new DataCentre(simulation, switch40g24p);
		
		// Create clusters in data centre.
		for (int i = 0; i < N_CLUSTERS; i++) {
			if (i % 2 == 0)
				dc.addCluster(series09.build());
			else
				dc.addCluster(series11.build());
		}
		
		return dc;
	}
	
	public static void main(String[] args) {
		
		DataCentre dc = createDataCentre(new Simulation("sampleInfrastructure"));
		
		System.out.println("Data centre created.");
		
		System.out.println("DATA CENTRE: " + dc.toString());
		
		for (Cluster c : dc.getClusters()) {
			System.out.println("CLUSTER: " + c.getId());
			
			for (Rack r : c.getRacks()) {
				System.out.println("RACK: " + r.getId());
				
				for (Host h : r.getHosts()) {
					System.out.println("HOST: " + h.getId());
					
				}
			}
		}

	}

}
