package edu.uwo.csd.dcsim.core.metrics;

public class Counter {

	private int val = 0;
	
	public int getValue() {
		return val;
	}
	
	public int getValueAndReset() {
		int value = getValue();
		reset();
		return value;
	}
	
	public void increment() {
		++val;
	}
	
	public void add(int val){
		this.val += val;
	}
	
	public void reset() {
		val = 0;
	}
	
}
