package org.csdgn.rf.peer.events;

import org.csdgn.rf.peer.RobotPeerImpl;

import roboflight.Bullet;
import roboflight.util.Rules;
import roboflight.util.Vector;

public class BulletImpl implements Bullet {
	private final RobotPeerImpl owner;
	private final Vector velocity = new Vector();
	private final Vector position = new Vector();
	private boolean active = false;
	private boolean hasNextPosition = false;
	private final Vector nextPosition = new Vector();

	public BulletImpl(final RobotPeerImpl owner) {
		this.owner = owner;
	}

	public Vector getNextPosition() {
		if (hasNextPosition)
			return nextPosition;
		hasNextPosition = true;
		// we use line sphere intersection for collision
		return nextPosition.set(position).add(velocity);
	}

	public RobotPeerImpl getOwner() {
		return owner;
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
	public final boolean isActive() {
		return active;
	}

	public boolean isOwner(final RobotPeerImpl rp) {
		return rp == owner;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void update() {
		// update position
		position.add(velocity);
		hasNextPosition = false;
		if (!Rules.isInBattlefield(getNextPosition()))
			active = false;
	}
}
