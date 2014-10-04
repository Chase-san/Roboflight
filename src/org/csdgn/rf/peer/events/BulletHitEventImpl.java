package org.csdgn.rf.peer.events;

import roboflight.Bullet;
import roboflight.events.BulletHitEvent;

public class BulletHitEventImpl extends EventImpl implements BulletHitEvent {
	private final String name;
	private final Bullet bullet;

	public BulletHitEventImpl(long time, Bullet bullet, String name) {
		super(time);
		this.name = name;
		this.bullet = bullet;
	}

	@Override
	public Bullet getBullet() {
		return bullet;
	}

	@Override
	public String getName() {
		return name;
	}
}
