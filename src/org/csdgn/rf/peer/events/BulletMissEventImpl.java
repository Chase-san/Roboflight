package org.csdgn.rf.peer.events;

import roboflight.Bullet;
import roboflight.events.BulletMissEvent;

public class BulletMissEventImpl extends EventImpl implements BulletMissEvent {
	private final Bullet bullet;

	public BulletMissEventImpl(long time, Bullet bullet) {
		super(time);
		this.bullet = bullet;
	}

	@Override
	public Bullet getBullet() {
		return bullet;
	}

}
