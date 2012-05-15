package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.common.ObjectBuilder;
import edu.uwo.csd.dcsim2.vm.VMDescription;

/**
 * ServiceBuilder provides a simpler interface for building a multi-tiered Service. It automatically handles linking the workload and tiers
 * together in the order in which they are added. While this is a more convenient method of building a Service, it is not mandatory; Service
 * can still be instantiated directly.
 * 
 * @author Michael Tighe
 *
 */
public class ServiceBuilder implements ObjectBuilder<Service> {

	private Workload workload;
	private ArrayList<Service.ServiceTier> tiers =  new ArrayList<Service.ServiceTier>();
	
	public ServiceBuilder workload(Workload value) {
		this.workload = value;
		return this;
	}
	
	public ServiceBuilder tier(ApplicationTier applicationTier, VMDescription vmDescription, int minSize, int maxSize) {
		Service.ServiceTier tier = new Service.ServiceTier(applicationTier, vmDescription, minSize, maxSize);
		tiers.add(tier);
		return this;
	}
	
	@Override
	public Service build() {
		
		if (workload == null)
			throw new IllegalStateException("Cannot build a Service with no Workload");
		if (tiers.size() == 0)
			throw new IllegalStateException("Cannot build a Service with no tiers");
		
		Service service = new Service();
		service.setWorkload(workload);
		
		workload.setWorkTarget(tiers.get(0).getApplicationTier());
		for (int i = 1; i < tiers.size(); ++i) {
			tiers.get(i - 1).getApplicationTier().setWorkTarget(tiers.get(i).getApplicationTier());
		}
		tiers.get(tiers.size() - 1).getApplicationTier().setWorkTarget(workload);
		
		for (Service.ServiceTier tier : tiers)
			service.addServiceTier(tier);
		
		
		return service;
	}

}
