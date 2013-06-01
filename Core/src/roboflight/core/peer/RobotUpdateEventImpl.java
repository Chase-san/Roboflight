package roboflight.core.peer;

import roboflight.events.RobotUpdateEvent;
import roboflight.util.Vector;

public class RobotUpdateEventImpl implements RobotUpdateEvent {
	private final String name;
	private final Vector velocity;
	private final Vector position;
	private final double energy;

	public RobotUpdateEventImpl(final String name, final Vector velocity, final Vector position, final double energy) {
		this.name = name;
		this.velocity = velocity;
		this.position = position;
		this.energy = energy;
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Vector getPosition() {
		return position.clone();
	}

	@Override
	public Vector getVelocity() {
		return velocity.clone();
	}
}
