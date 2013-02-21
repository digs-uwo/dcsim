package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.ActionCountMetric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.stub.HostStub;
import edu.uwo.csd.dcsim.management.stub.VmStub;
import edu.uwo.csd.dcsim.vm.VM;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class MigrationAction implements ManagementAction {
	
	private static final String MIGRATION_COUNT_METRIC = "migrationCount";

	private Host source;
	private Host target;
	private VM vm;
	
	//TODO remove, this is legacy code for old management algorithms
	public MigrationAction(HostStub source, HostStub target, VmStub vm) {
		this.source = source.getHost();
		this.target = target.getHost();
		this.vm = vm.getVM();
	}
	
	public MigrationAction(Host source, Host target, VM vm) {
		this.source = source;
		this.target = target;
		this.vm = vm;
	}
	
	public Host getSource() {
		return source;
	}
	
	public Host getTarget() {
		return target;
	}
	
	public VM getVm() {
		return vm;
	}
	
	/**
	 * Perform this VM migration
	 * @param triggeringEntity The SimulationEntity (VMRelocationPolicy, VMConsolidiationPolicy, etc.) that is triggering this migration
	 */
	public void execute(Simulation simulation, Object triggeringEntity) {
		
		//TODO this should be changed to a message to the hosts AM instructing it to perform a migration... sent to source or target?
		
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vm.getVMAllocation()); //create allocation request based on current allocation
		
		if (target.getState() != Host.HostState.ON && target.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(target, PowerState.POWER_ON));
		}
		
		target.sendMigrationEvent(vmAllocationRequest, vm, source);
		
		if (simulation.isRecordingMetrics()) {
			ActionCountMetric.getMetric(simulation, MIGRATION_COUNT_METRIC + "-" + triggeringEntity.getClass().getSimpleName()).incrementCount();
		}
		
		//if the source host will no longer contain any VMs, instruct it to shut down
		if (source.getVMAllocations().size() == 0) {
			simulation.sendEvent(new PowerStateEvent(source, PowerState.POWER_OFF));
		}
		
		//TODO improve logging output
		simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vm.getId() + " from Host #" + source.getId() + " to #" + target.getId());
	}
	
}
