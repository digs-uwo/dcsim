package edu.uwo.csd.dcsim2.management.action;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.Event;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.SimulationEntity;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.management.stub.HostStub;
import edu.uwo.csd.dcsim2.management.stub.VmStub;
import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;

public class MigrationAction implements ManagementAction {
	
	private static Logger logger = Logger.getLogger(MigrationAction.class);
	
	private static Map<SimulationEntity, Integer> migrationCount = new HashMap<SimulationEntity, Integer>();

	private HostStub source;
	private HostStub target;
	private VmStub vm;
	
	private static void incrementMigrationCount(SimulationEntity triggeringEntity) {
		int count = 0;
		if (migrationCount.containsKey(triggeringEntity)) {
			count = migrationCount.get(triggeringEntity);
		}
		migrationCount.put(triggeringEntity, count + 1);
	}
	
	public static Map<SimulationEntity, Integer> getMigrationCount() {
		return migrationCount;
	}
	
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
	public void execute(SimulationEntity triggeringEntity) {
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vm.getVM().getVMAllocation()); //create allocation request based on current allocation
		
		if (target.getHost().getState() != Host.HostState.ON && target.getHost().getState() != Host.HostState.POWERING_ON) {
			Simulation.getInstance().sendEvent(
					new Event(Host.HOST_POWER_ON_EVENT,
							Simulation.getInstance().getSimulationTime(),
							triggeringEntity,
							target.getHost())
					);
		}
		
		target.getHost().sendMigrationEvent(vmAllocationRequest, vm.getVM(), source.getHost());
		
		incrementMigrationCount(triggeringEntity);
		
		//if the source host will no longer contain any VMs, instruct it to shut down
		if (source.getVms().size() == 0) {
			Simulation.getInstance().sendEvent(
					new Event(Host.HOST_POWER_OFF_EVENT,
							Simulation.getInstance().getSimulationTime(),
							triggeringEntity,
							source.getHost())
					);
		}
		
		logger.debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vm.getVM().getId() + " from Host #" + source.getHost().getId() + " to #" + target.getHost().getId());
	}
	
}
