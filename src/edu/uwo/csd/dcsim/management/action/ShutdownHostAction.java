package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;

public class ShutdownHostAction extends ManagementAction {

	private Host host;
	
	public ShutdownHostAction(Host host) {
		this.host = host;
	}
	
	@Override
	public void execute(Simulation simulation, Object triggeringEntity) {
		PowerStateEvent event = new PowerStateEvent(host, PowerState.POWER_OFF);

		event.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				completeAction();
			}
			
		});
		
		simulation.sendEvent(event);
	}

}
