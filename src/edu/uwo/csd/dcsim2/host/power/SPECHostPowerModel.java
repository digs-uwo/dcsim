package edu.uwo.csd.dcsim2.host.power;

import edu.uwo.csd.dcsim2.host.Host;

public class SPECHostPowerModel implements HostPowerModel {

	private double powerLevels[];
	
	public SPECHostPowerModel(
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
	public double getPowerConsumption(Host host) {
		
		double cpuUtilization = host.getCpuManager().getCpuUtilization();
		
		//calculate power level above and below current utilization. Calculate both independently to handle special cases (i.e. 100%)
		int lower = (int)Math.floor(cpuUtilization / 10);
		int upper = (int)Math.ceil(cpuUtilization / 10); 
		
		return powerLevels[lower] + ((powerLevels[upper] - powerLevels[lower]) * ((cpuUtilization % 10) / 10));
	}

}
