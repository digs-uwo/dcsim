package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.ActionCountMetric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.MigrationEvent;

public class MigrationAction implements ManagementAction {
	
	private static final String MIGRATION_COUNT_METRIC = "migrationCount";

	private AutonomicManager sourceHostManager;
	private Host source;
	private Host target;
	private int vmId;
	
	public MigrationAction(AutonomicManager sourceHostManager, Host source, Host target, int vmId) {
		this.sourceHostManager = sourceHostManager;
		this.source = source;
		this.target = target;
		this.vmId = vmId;
	}
	
	public AutonomicManager getSourceHostManager() {
		return sourceHostManager;
	}
	
	public Host getSource() {
		return source;
	}
	
	public Host getTarget() {
		return target;
	}
	
	public int getVmId() {
		return vmId;
	}
	
	/**
	 * Perform this VM migration
	 * @param triggeringEntity The SimulationEntity (VMRelocationPolicy, VMConsolidiationPolicy, etc.) that is triggering this migration
	 */
	public void execute(Simulation simulation, Object triggeringEntity) {
		
		if (target.getState() != Host.HostState.ON && target.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(target, PowerState.POWER_ON));
		}
		
		//send migration event to source
		simulation.sendEvent(new MigrationEvent(sourceHostManager, target, vmId, true));
		
		if (simulation.isRecordingMetrics()) {
			ActionCountMetric.getMetric(simulation, MIGRATION_COUNT_METRIC + "-" + triggeringEntity.getClass().getSimpleName()).incrementCount();
		}

		//TODO improve logging output
		simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vmId + " from Host #" + source.getId() + " to #" + target.getId());
	}
	
}
