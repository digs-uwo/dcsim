package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.vm.*;

public class VMRelocationPolicyST03 extends VMRelocationPolicy {

	public VMRelocationPolicyST03(DataCentre dc, long interval) {
		super(dc, interval);
	}

	@Override
	public void execute() {
		ArrayList<Host> hostList = dc.getHosts();
		
	}

}
