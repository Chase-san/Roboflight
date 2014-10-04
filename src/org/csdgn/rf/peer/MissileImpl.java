package org.csdgn.rf.peer;

import roboflight.Missile;
import roboflight.util.Rules;
import roboflight.util.Vector;

public class MissileImpl implements Missile {
	private final RobotPeerImpl owner;
	private final Vector velocity = new Vector();
	private final Vector position = new Vector();
	private final Vector thrust = new Vector();
	private boolean active = false;
	private boolean armed = false;

	public MissileImpl(final RobotPeerImpl owner) {
		this.owner = owner;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	public RobotPeerImpl getOwner() {
		return owner;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public boolean isArmed() {
		return armed;
	}

	@Override
	public final Vector getPosition() {
		return position.clone();
	}

	public Vector getPositionVector() {
		return position;
	}

	@Override
	public final Vector getVelocity() {
		return velocity.clone();
	}

	public Vector getVelocityVector() {
		return velocity;
	}

	@Override
	public void setThrust(final Vector thrust) {
		this.thrust.set(thrust);
	}

	public void update() {
		Vector thrust = this.thrust.clone();

		// normalize thrust if we need to
		if (thrust.lengthSq() > Rules.MISSILE_MAX_THRUST
				* Rules.MISSILE_MAX_THRUST)
			thrust.normalize().scale(Rules.MISSILE_MAX_THRUST);

		// update velocity
		velocity.add(thrust);

		// deplete thrust
		this.thrust.sub(thrust);

		// normalize velocity if we need to
		if (velocity.lengthSq() > Rules.MISSILE_MAX_VELOCITY
				* Rules.MISSILE_MAX_VELOCITY)
			velocity.normalize().scale(Rules.MISSILE_MAX_VELOCITY);

		// update position
		position.add(velocity);

		// zero out thrust
		thrust.set(0, 0, 0);

		if (!Rules.isInBattlefield(position))
			active = false;
	}

}
