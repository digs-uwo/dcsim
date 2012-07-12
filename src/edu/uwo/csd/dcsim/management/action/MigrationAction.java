package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.AggregateMetric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.stub.HostStub;
import edu.uwo.csd.dcsim.management.stub.VmStub;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class MigrationAction implements ManagementAction {
	
	private static final String MIGRATION_COUNT_METRIC = "migrationCount";

	private HostStub source;
	private HostStub target;
	private VmStub vm;
		
	public MigrationAction(HostStub source, HostStub target, VmStub vm) {
		this.source = source;
		this.target = target;
		this.vm = vm;
	}
	
	public HostStub getSource() {
		return source;
	}
	
	public HostStub getTarget() {
		return target;
	}
	
	public VmStub getVm() {
		return vm;
	}
	
	/**
	 * Perform this VM migration
	 * @param triggeringEntity The SimulationEntity (VMRelocationPolicy, VMConsolidiationPolicy, etc.) that is triggering this migration
	 */
	public void execute(Simulation simulation, Object triggeringEntity) {
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vm.getVM().getVMAllocation()); //create allocation request based on current allocation
		
		if (target.getHost().getState() != Host.HostState.ON && target.getHost().getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(
					new Event(Host.HOST_POWER_ON_EVENT,
							simulation.getSimulationTime(),
							triggeringEntity,
							target.getHost())
					);
		}
		
		target.getHost().sendMigrationEvent(vmAllocationRequest, vm.getVM(), source.getHost());
		
		if (simulation.isRecordingMetrics()) {
			AggregateMetric.getSimulationMetric(simulation, MIGRATION_COUNT_METRIC + "-" + triggeringEntity.getClass().getSimpleName()).addValue(1);
		}
		
		//if the source host will no longer contain any VMs, instruct it to shut down
		if (source.getVms().size() == 0) {
			simulation.sendEvent(
					new Event(Host.HOST_POWER_OFF_EVENT,
							simulation.getSimulationTime(),
							triggeringEntity,
							source.getHost())
					);
		}
		
		simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vm.getVM().getId() + " from Host #" + source.getHost().getId() + " to #" + target.getHost().getId());
	}
	
}
