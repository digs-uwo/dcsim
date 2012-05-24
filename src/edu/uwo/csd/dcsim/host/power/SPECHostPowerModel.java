package edu.uwo.csd.dcsim.host.power;

import edu.uwo.csd.dcsim.host.Host;

/**
 * SPECHostPowerModel is a HostPowerModel that uses power utilization data in the form provided by the SPECpower benchmark
 * to determine host power consumption.
 * 
 * @author Michael Tighe
 *
 */
public class SPECHostPowerModel implements HostPowerModel {

	private final double suspended;
	private final double powerLevels[];
	
	public SPECHostPowerModel(
			double suspended,
			double util0, 
			double util10, 
			double util20,
			double util30,
			double util40,
			double util50,
			double util60,
			double util70,
			double util80,
			double util90,
			double util100) {
		
		this.suspended = suspended;
		powerLevels = new double[11];
		powerLevels[0] = util0;
		powerLevels[1] = util10;
		powerLevels[2] = util20;
		powerLevels[3] = util30;
		powerLevels[4] = util40;
		powerLevels[5] = util50;
		powerLevels[6] = util60;
		powerLevels[7] = util70;
		powerLevels[8] = util80;
		powerLevels[9] = util90;
		powerLevels[10] = util100;
	}
	
	@Override
	public double getPowerConsumption(Host.HostState state, double cpu) {
		
		if (state == Host.HostState.ON || 
				state == Host.HostState.POWERING_OFF || 
				state == Host.HostState.POWERING_ON ||
				state == Host.HostState.SUSPENDING) {
			double cpuUtilization = cpu;

			return getPowerConsumption(cpuUtilization);
		} else if (state == Host.HostState.SUSPENDED) {
			return suspended;
		} else {
			return 0;
		}
	}

	@Override
	public double getPowerConsumption(double cpu) {
		//calculate power level above and below current utilization. Calculate both independently to handle special cases (i.e. 100%)
		int lower = (int)Math.floor((cpu * 100) / 10);
		int upper = (int)Math.ceil((cpu * 100) / 10); 
		
		return powerLevels[lower] + ((powerLevels[upper] - powerLevels[lower]) * ((cpu * 10) % 1));
	}

}
