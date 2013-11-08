package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.ManagementMetrics.MigrationType;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.MigrationEvent;

public class MigrationAction extends ManagementAction {

	private AutonomicManager sourceHostManager;
	private Host source;
	private Host target;
	private int vmId;
	private boolean verifyVm = false; //when true, the action will first verify that the migrating VM hasn't left the DC prior to the action executing
	
	public MigrationAction(AutonomicManager sourceHostManager, Host source, Host target, int vmId) {
		this.sourceHostManager = sourceHostManager;
		this.source = source;
		this.target = target;
		this.vmId = vmId;
	}
	
	public MigrationAction(AutonomicManager sourceHostManager, Host source, Host target, int vmId, boolean verifyVm) {
		this.sourceHostManager = sourceHostManager;
		this.source = source;
		this.target = target;
		this.vmId = vmId;
		this.verifyVm = verifyVm;
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
		
		//verify that the VM is still on the host (if it shut down after this action was created, it may not be)
		if (verifyVm) {
			if (source.getVMAllocation(vmId) == null) {
				simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vmId + " from Host #" + source.getId() + " is not longer present on the source host");
				completeAction();
				return;
			}
		}
		
		if (target.getState() != Host.HostState.ON && target.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(target, PowerState.POWER_ON));
		}
		
		//send migration event to source
		MigrationEvent migEvent = new MigrationEvent(sourceHostManager, target, vmId);
		
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
			//simulation.getSimulationMetrics().getManagementMetrics().addMigration(triggeringEntity.getClass());
			
			if (source.getRack() == target.getRack())
				simulation.getSimulationMetrics().getManagementMetrics().addMigration(triggeringEntity.getClass(), MigrationType.INTRARACK);
			else if (source.getRack().getCluster() == target.getRack().getCluster())
				simulation.getSimulationMetrics().getManagementMetrics().addMigration(triggeringEntity.getClass(), MigrationType.INTRACLUSTER);
			else
				simulation.getSimulationMetrics().getManagementMetrics().addMigration(triggeringEntity.getClass(), MigrationType.INTERCLUSTER);
		}

		//TODO improve logging output
		simulation.getLogger().debug(triggeringEntity.getClass().getName() + " Migrating VM #" + vmId + " from Host #" + source.getId() + " to #" + target.getId());
	}
	
}
