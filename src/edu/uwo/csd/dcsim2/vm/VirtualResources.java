package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

public class VirtualResources {
	
	private ArrayList<Integer> cores = new ArrayList<Integer>();
	private int memory = 0;
	private int bandwidth = 0;
	private long storage = 0;
	
	public VirtualResources add(VirtualResources v2) {
		
		VirtualResources sum = new VirtualResources();
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		ArrayList<Integer> cores = new ArrayList<Integer>();
		int remainingCpu = 0;
		if (this.getCores().size() > v2.getCores().size()) {
			//total cpu to be dispersed evenly to all cores
			for (int core : v2.getCores()) {
				remainingCpu += core;
			}
		} else {
			for (int i = 0; i < this.getCores().size(); ++i) {
				sum.getCores().add(this.getCores().get(i) + v2.getCores().get(i));
			}
			if (this.getCores().size() < v2.getCores().size()) {
				//total remaining cpu to be dispersed evenly to all cores
				for (int i = this.getCores().size(); i < v2.getCores().size(); ++i) {
					remainingCpu += v2.getCores().get(i);
				}
			}
		}
		
		//add remaining cpu to all cores evenly
		if (remainingCpu > 0) {
			int remainder = remainingCpu % cores.size();
			for (int i = 0; i < cores.size(); ++i) {
				if (i == 0) {
					int amount = cores.get(i) + (int)Math.floor(remainingCpu / cores.size());
					
					//evenly distribute remainder from division among cores
					if (remainder > 0) {
						++amount;
						--remainder;
					}
						
					sum.getCores().add(amount);
				} else {
					sum.getCores().add(cores.get(i) + (int)Math.floor(remainingCpu / cores.size()));
				}
			}
		} else {
			for (int core : cores) {
				sum.getCores().add(core);
			}
		}
		
		
		sum.setBandwidth(this.getBandwidth() + v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 */
		sum.setMemory(Math.max(this.getMemory(), v2.getMemory()));
		sum.setStorage(Math.max(this.getStorage(), v2.getStorage()));
		
		return sum;
	}
	
	public VirtualResources subtract(VirtualResources v2) {
			
		VirtualResources difference = new VirtualResources();
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		
		ArrayList<Integer> cores = new ArrayList<Integer>();
		int remainingCpu = 0;
		if (this.getCores().size() > v2.getCores().size()) {
			//total cpu to be subtracted evenly from all cores
			for (int core : v2.getCores()) {
				remainingCpu += core;
			}
		} else {
			for (int i = 0; i < this.getCores().size(); ++i) {
				difference.getCores().add(this.getCores().get(i) - v2.getCores().get(i));
			}
			if (this.getCores().size() < v2.getCores().size()) {
				//total remaining cpu to be subtracted evenly from all cores
				for (int i = this.getCores().size(); i < v2.getCores().size(); ++i) {
					remainingCpu += v2.getCores().get(i);
				}
			}
		}
		
		//subtract remaining cpu from all cores evenly
		if (remainingCpu > 0) {
			int remainder = remainingCpu % cores.size();
			for (int i = 0; i < cores.size(); ++i) {
				if (i == 0) {
					int amount = cores.get(i) - (int)Math.floor(remainingCpu / cores.size());
					
					//evenly distribute remainder from division among cores
					if (remainder > 0) {
						--amount;
						--remainder;
					}
						
					difference.getCores().add(amount);
				} else {
					difference.getCores().add(cores.get(i) - (int)Math.floor(remainingCpu / cores.size()));
				}
			}
		} else {
			for (int core : cores) {
				difference.getCores().add(core);
			}
		}

		difference.setBandwidth(this.getBandwidth() - v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 * TODO is this correct? Can this even be defined?
		 */
		difference.setMemory(Math.max(this.getMemory(), v2.getMemory()));
		difference.setStorage(Math.max(this.getStorage(), v2.getStorage()));
		
		return difference;
	}
	
	public VirtualResources() {
		
	}
	
	public VirtualResources(int nCores) {
		for (int i = 0; i < nCores; ++i) {
			cores.add(0);
		}
	}
	
	public ArrayList<Integer> getCores() {
		return cores;
	}
	
	public void setCores(ArrayList<Integer> cores) {
		if (cores.size() != this.cores.size()) {
			//TODO throw runtime exception
		}
		this.cores = cores;
	}
	
	public int getTotalCpu() {
		int cpu = 0;
		for (int core : cores) {
			cpu += core;
		}
		return cpu;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public void setStorage(long storage) {
		this.storage = storage;
	}
	
}
