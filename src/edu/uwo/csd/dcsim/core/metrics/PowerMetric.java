package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class PowerMetric extends Metric {

	private double powerConsumed = 0;			//the total power consumed during the simulation, in watt-seconds
	private double currentPowerConsumption = 0;	//the current rate of power consumption, in watt-seconds
	
	private PowerMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	/**
	 * Add the power consumption rate of a host
	 * @param hostPowerConsumption The hosts rate of power consumption (NOT the power consumed over the last time interval)
	 */
	public void addHostPowerConsumption(double hostPowerConsumption) {
		powerConsumed += hostPowerConsumption * simulation.getElapsedSeconds(); //calculate in watt-seconds
		currentPowerConsumption += hostPowerConsumption;
	}
	
	@Override
	public double getValue() {
		return powerConsumed;
	}

	@Override
	public double getCurrentValue() {
		return currentPowerConsumption;
	}

	@Override
	public void onStartTimeInterval() {
		currentPowerConsumption = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}

	public static PowerMetric getMetric(Simulation simulation, String name) {
		PowerMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (PowerMetric)simulation.getMetric(name);
		}
		else {
			metric = new PowerMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(powerConsumed / 3600000)) + "kWh"; //output power consumed as kWh
	}

}
