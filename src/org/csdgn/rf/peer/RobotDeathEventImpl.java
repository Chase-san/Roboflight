package org.csdgn.rf.peer;

import roboflight.events.RobotDeathEvent;

public class RobotDeathEventImpl implements RobotDeathEvent {
	private final String name;
	public RobotDeathEventImpl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
