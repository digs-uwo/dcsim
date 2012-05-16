package edu.uwo.csd.dcsim.host.power;

import edu.uwo.csd.dcsim.host.Host;

/**
 * LinearHostPowerModel models Host power consumption as a linear function starting from
 * a fixed consumption at idle CPU and scaling linearly with CPU utilization to a maximum
 * power consumption level.
 * 
 * @author Michael Tighe
 *
 */
public class LinearHostPowerModel implements HostPowerModel {

	private final double idlePower;
	private final double maxPower;
	
	public LinearHostPowerModel(double idlePower, double maxPower) {
		this.idlePower = idlePower;
		this.maxPower = maxPower;
	}
	
	@Override
	public double getPowerConsumption(Host.HostState state, double cpu) {
		if (state == Host.HostState.ON)
			return getPowerConsumption(cpu);
		else
			return 0;
	}

	@Override
	public double getPowerConsumption(double cpu) {
		return idlePower + ((maxPower - idlePower) * cpu);
	}
	
}
