package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class ResourceManager {

	private Host host;
	
	public Host getHost() {
		return host;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public abstract boolean isCapable(VMDescription vmDescription);
	public abstract boolean hasCapacity(VMAllocation vmAllocate);
	public abstract boolean allocateResource(VMAllocation vmAllocation);
	public abstract boolean deallocateResource(VMAllocation vmAllocation);
	
}
