package org.csdgn.rf.peer.events;

import roboflight.Missile;
import roboflight.events.MissileMissEvent;

public class MissileMissEventImpl extends EventImpl implements MissileMissEvent {
	private final Missile missile;
	public MissileMissEventImpl(long time, Missile missile) {
		super(time);
		this.missile = missile;
	}

	@Override
	public Missile getMissile() {
		return missile;
	}

}
