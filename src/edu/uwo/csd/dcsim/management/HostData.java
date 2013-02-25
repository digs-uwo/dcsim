package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;

public class HostData {
	private HostDescription hostDescription;
	private AutonomicManager hostManager;
	private Host host;
	
	private HostStatus currentStatus = null;
	private HostStatus sandboxStatus = null; //this is a HostStatus variable that can be freely modified for use in policies
	private boolean statusValid = true;
	private long invalidationTime = -1;
	
	private ArrayList<HostStatus> history = new ArrayList<HostStatus>();
	
	public HostData(Host host, AutonomicManager hostManager) {
		this.host = host;
		this.hostManager = hostManager;
		
		hostDescription = new HostDescription(host);
		
		//initialize currentStatus in order to maintain a status of powered off Hosts
		currentStatus = new HostStatus(host, 0);
	}
	
	public void addHostStatus(HostStatus hostStatus, int historyWindowSize) {
		currentStatus = hostStatus;
		if (sandboxStatus == null) {
			resetSandboxStatusToCurrent();
		}
		//only return the status to 'valid' if we know it was sent at at time after it was invalidated
		//TODO this might cause problems if, instead of waiting for the next status, we request an immediate update
		//with the message arriving at the same sim time.
		if (hostStatus.getTimeStamp() > invalidationTime) {
			statusValid = true; //if status was invalidated, we now know it is correct
		}
		
		history.add(0, hostStatus);
		if (history.size() > historyWindowSize) {
			history.remove(history.size() - 1);
		}
	}

	public HostStatus getCurrentStatus() {
		//return a copy of the status to ensure that it is read-only
		return currentStatus.copy();
	}
	
	public void setSandboxStatus(HostStatus status) {
		sandboxStatus = status;
	}
	
	public HostStatus getSandboxStatus() {
		return sandboxStatus;
	}
	
	public void resetSandboxStatusToCurrent() {
		if (currentStatus != null) {
			this.sandboxStatus = currentStatus.copy();
		}
	}
	
	public ArrayList<HostStatus> getHistory() {
		//return a copy of the history to ensure that it is read-only
		ArrayList<HostStatus> historyCopy = new ArrayList<HostStatus>();
		for (HostStatus status : history) {
			historyCopy.add(status);
		}
		return historyCopy;
	}
	
	public int getId() {
		return host.getId();
	}
	
	public HostDescription getHostDescription() {
		return hostDescription;
	}
	
	public AutonomicManager getHostManager() {
		return hostManager;
	}
	
	public Host getHost() {
		return host;
	}
	
	public boolean isStatusValid() {
		return statusValid;
	}
	
	public void invalidateStatus(long time) {
		this.statusValid = false;
		invalidationTime = time;
	}
	
	public static boolean canHost(VmStatus vm, HostStatus currentStatus, HostDescription hostDescription) {
		return canHost(vm.getCores(), vm.getCoreCapacity(), vm.getResourcesInUse(), currentStatus, hostDescription);
	}
	
	public static boolean canHost(int reqCores, int reqCoreCapacity, Resources reqResources, HostStatus currentStatus, HostDescription hostDescription) {
		//verify that this host can host the given vm
		
		//check capabilities (e.g. core count, core capacity)
		if (hostDescription.getCpuCount() * hostDescription.getCoreCount() < reqCores)
			return false;
		if (hostDescription.getCoreCapacity() < reqCoreCapacity)
			return false;
		
		//check available resource
		Resources resourcesInUse = currentStatus.getResourcesInUse();
		if (hostDescription.getResourceCapacity().getCpu() - resourcesInUse.getCpu() < reqResources.getCpu())
			return false;
		if (hostDescription.getResourceCapacity().getMemory() - resourcesInUse.getMemory() < reqResources.getMemory())
			return false;
		if (hostDescription.getResourceCapacity().getBandwidth() - resourcesInUse.getBandwidth() < reqResources.getBandwidth())
			return false;
		if (hostDescription.getResourceCapacity().getStorage() - resourcesInUse.getStorage() < reqResources.getStorage())
			return false;
		
		return true;
	}
}
