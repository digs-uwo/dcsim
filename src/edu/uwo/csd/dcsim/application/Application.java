package edu.uwo.csd.dcsim.application;

import java.util.*;

import edu.uwo.csd.dcsim.application.sla.ServiceLevelAgreement;
import edu.uwo.csd.dcsim.common.HashCodeUtil;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.ShutdownVmEvent;
import edu.uwo.csd.dcsim.vm.*;

/**
 * Represents an Application running in the DataCentre, consisting of an incoming Workload source
 * and a set of application tiers. Each tier completes work and passes it on to the next tier, until
 * the final tier returns it to the Workload. A tier consists of one or more running Applications.
 * 
 * @author Michael Tighe
 *
 */
public abstract class Application {

	int id;
	protected Simulation simulation;
	ServiceLevelAgreement sla = null;
	private ArrayList<ApplicationListener> applicationListeners = new ArrayList<ApplicationListener>();
	private final int hashCode;
	
	public Application(Simulation simulation) {
		this.simulation = simulation;
		simulation.addApplication(this);
		this.id = simulation.nextId(Application.class.toString());
		
		//init hashCode
		hashCode = generateHashCode();
	}
	
	public abstract void initializeScheduling();
	public abstract boolean updateDemand();
	public abstract void postScheduling();
	public abstract void advanceSimulation();
	
	/**
	 * Generate a set of VMAllocationRequests for the initial set of VMs run the Application
	 * 
	 * @return
	 */
	public ArrayList<VmAllocationRequest> createInitialVmRequests() {
		ArrayList<VmAllocationRequest> vmList = new ArrayList<VmAllocationRequest>();
		
		//create a VMAllocationRequest for the minimum number of instances in each task
		for (Task task : getTasks()) {
			vmList.addAll(task.createInitialVmRequests());
		}
		return vmList;
	}

	public boolean canShutdown() {
		
		//verify that none of the VMs in the service are currently migrating
		for (Task task : getTasks()) {
			for (TaskInstance instance : task.getInstances()) {
				if (instance.getVM().isMigrating() || instance.getVM().isPendingMigration()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void shutdownApplication(AutonomicManager target, Simulation simulation) {
		
		for (Task task : getTasks()) {
			ArrayList<TaskInstance> instances = task.getInstances();
			for (TaskInstance instance : instances) {
				Vm vm = instance.getVM();
				VmAllocation vmAlloc = vm.getVMAllocation();
				Host host = vmAlloc.getHost();
			
				if (vm.isMigrating() || instance.getVM().isPendingMigration())
					throw new RuntimeException("Tried to shutdown migrating VM #" + vm.getId() + ". Operation not allowed in simulation.");
				
				simulation.sendEvent(new ShutdownVmEvent(target, host.getId(), vm.getId()));				
			}
		}
		
		simulation.removeApplication(this);
		
		for (ApplicationListener listener : applicationListeners) {
			listener.onShutdownApplication(this);
		}
	}
	
	public abstract int getTotalCpuDemand();
	public abstract int getTotalCpuScheduled();
	
	/**
	 * Get the tasks that this Application consists of 
	 * @return
	 */
	public abstract ArrayList<Task> getTasks();

	public ArrayList<ApplicationListener> getApplicationListeners() {
		return applicationListeners;
	}
	
	public void addApplicationListener(ApplicationListener listener) {
		applicationListeners.add(listener);
	}
	
	public void removeApplicationListener(ApplicationListener listener) {
		applicationListeners.remove(listener);
	}
	
	public Simulation getSimulation() {
		return simulation;
	}

	public int getId() {
		return id;
	}
	
	public ServiceLevelAgreement getSla() {
		return sla;
	}
	
	public void setSla(ServiceLevelAgreement sla) {
		this.sla = sla;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	private int generateHashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getId());
		result = HashCodeUtil.hash(result, simulation.getSimulationTime());
		return result;
	}
	
	public int getMaxSize() {
		int size = 0;
		for (Task task : getTasks()) {
			size += task.maxInstances;
		}
		return size;
	}
	
	public int getSize() {
		int size = 0;
		for (Task task :getTasks()) {
			size += task.getInstances().size();
		}
		return size;
	}
	
}
