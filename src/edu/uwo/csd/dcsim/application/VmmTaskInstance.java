package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.common.HashCodeUtil;

/**
 * @author Michael Tighe
 *
 */
public class VmmTaskInstance extends TaskInstance {

	private VmmTask task;
	private final int hashCode;
	
	public VmmTaskInstance(VmmTask task) {
		this.task = task;
		
		//init hashCode
		hashCode = generateHashCode();
	}

	@Override
	public void postScheduling() {
	
	}

	@Override
	public Task getTask() {
		return task;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	private int generateHashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, task.getId());
		result = HashCodeUtil.hash(result, task.getInstances().size());
		return result;
	}

}
