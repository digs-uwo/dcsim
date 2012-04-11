package edu.uwo.csd.dcsim2.host.power;

import edu.uwo.csd.dcsim2.host.*;

public interface HostPowerModel {

	/**
	 * Calculates the current power consumption of a Host, in Watts. The entire Host object is supplied, rather than
	 * simply CPU utilization, so that future work may consider other aspects beyond simply CPU Utilization.
	 * @param host The host to caculate the power consumption of
	 * @return The current power consumption of the Host, in Watts.
	 */
	public double getPowerConsumption(Host host);
	
	public double getPowerConsumption(double cpu);
	
}
