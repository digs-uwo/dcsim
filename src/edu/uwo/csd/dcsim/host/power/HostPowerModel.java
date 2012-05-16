package edu.uwo.csd.dcsim.host.power;

import edu.uwo.csd.dcsim.host.*;

/**
 * HostPowerModel determines the current power consumption level of a Host. All HostPowerModel implementations should be immutable.
 * 
 * @author Michael Tighe
 *
 */
public interface HostPowerModel {

	/**
	 * Calculates the current power consumption of a Host, in Watts.
	 * @param host The host to caculate the power consumption of
	 * @return The current power consumption of the Host, in Watts.
	 */
	public double getPowerConsumption(Host.HostState state, double cpu);
	
	/**
	 * Calculates the power consumption of a Host, in Watts, given that the host is in the ON state and has the
	 * specified CPU utilization
	 * @param cpu
	 * @return
	 */
	public double getPowerConsumption(double cpu);
	
}
