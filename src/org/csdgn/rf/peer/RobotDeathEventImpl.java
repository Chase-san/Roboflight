package org.csdgn.rf.peer;

import roboflight.events.RobotDeathEvent;

public class RobotDeathEventImpl extends EventImpl implements RobotDeathEvent {
	private final String name;

	public RobotDeathEventImpl(long time, String name) {
		super(time);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
