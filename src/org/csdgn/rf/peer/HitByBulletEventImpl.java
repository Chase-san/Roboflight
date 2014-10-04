package org.csdgn.rf.peer;

import roboflight.Bullet;
import roboflight.events.HitByBulletEvent;

public class HitByBulletEventImpl implements HitByBulletEvent {
	private final String name;
	private final Bullet bullet;
	
	public HitByBulletEventImpl(Bullet bullet, String name) {
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
