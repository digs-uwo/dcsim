package edu.uwo.csd.dcsim2.application;

public class RandomWorkload extends Workload {

	long lastUpdate = 0;
	
	public RandomWorkload(int mean, int stepSize, int changeInterval) {
		super();		
	}
	
	@Override
	protected double retrievePendingWork() {
		
		//TODO 
		generateRandom(1);
		
		return 0;
	}

	private int generateRandom(int mean) {			
		double L = Math.exp(-mean);
		double p = 1.0;
		int k = 0;
		
		do {
			k++;
			p *= Math.random();
		} while (p > L);
		
		return k - 1;
	}

	@Override
	protected long updateWorkLevel() {
		// TODO Auto-generated method stub
		return 0;
	}
		
	
}
