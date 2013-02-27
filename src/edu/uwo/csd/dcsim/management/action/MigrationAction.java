package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.CountMetric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.MigrationEvent;

public class MigrationAction extends ManagementAction {
	
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
		MigrationEvent migEvent = new MigrationEvent(sourceHostManager, target, vmId, true);
		
		//add a callback listener to indicate this action is completed once the migration is finished
		migEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				//add 'migration complete' to trace
				completeAction();
			}
			
		});
		simulation.sendEvent(migEvent);
		
		if (simulation.isRecordingMetrics()) {
			CountMetric.getMetric(simulation, MIGRATION_COUNT_METRIC + "-" + triggeringEntity.getClass().getSimpleName()).incrementCount();
		}

		//TODO improve logging output
		simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vmId + " from Host #" + source.getId() + " to #" + target.getId());
	}
	
}
