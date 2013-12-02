package roboflight.core.peer;

import roboflight.events.MissileUpdateEvent;
import roboflight.util.Vector;

public class MissileUpdateEventImpl implements MissileUpdateEvent {
	private final String name;
	private final Vector velocity;
	private final Vector position;
	private final boolean owned;
	
	public MissileUpdateEventImpl(final String name, final Vector velocity, final Vector position,
			final boolean owned) {
		this.name = name;
		this.velocity = velocity;
		this.position = position;
		this.owned = owned;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Vector getPosition() {
		return position;
	}

	@Override
	public Vector getVelocity() {
		return velocity;
	}

	@Override
	public boolean isOwned() {
		return owned;
	}

}
