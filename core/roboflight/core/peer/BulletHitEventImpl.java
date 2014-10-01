package roboflight.core.peer;

import roboflight.Bullet;
import roboflight.events.BulletHitEvent;

public class BulletHitEventImpl implements BulletHitEvent {
	private final String name;
	private final Bullet bullet;
	
	public BulletHitEventImpl(Bullet bullet, String name) {
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
