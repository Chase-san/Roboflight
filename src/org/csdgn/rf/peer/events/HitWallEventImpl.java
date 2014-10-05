package org.csdgn.rf.peer.events;

import roboflight.events.HitWallEvent;
import roboflight.util.Vector;

public class HitWallEventImpl extends EventImpl implements HitWallEvent {

	private final Vector hitPos;
	
	public HitWallEventImpl(long time, Vector hitPos) {
		super(time);
		this.hitPos = hitPos;
	}

	@Override
	public Vector getWallHitPosition() {
		return hitPos.clone();
	}
}
