/**
 * Copyright (c) 2013-2014 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.rf.peer;

import org.csdgn.rf.CoreUtils;

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
	public boolean isActive() {
		return active;
	}

	public boolean isArmed() {
		return armed;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	@Override
	public void setThrust(final Vector thrust) {
		if(CoreUtils.isBadVector(thrust)) {
			return;
		}
		this.thrust.set(thrust);
	}

	public void update() {
		if(!active) {
			return;
		}
		
		Vector thrust = this.thrust.clone();

		// normalize thrust if we need to
		if(thrust.lengthSq() > Rules.MISSILE_MAX_THRUST * Rules.MISSILE_MAX_THRUST) {
			thrust.normalize().scale(Rules.MISSILE_MAX_THRUST);
		}

		// update velocity
		velocity.add(thrust);

		// normalize velocity if we need to
		if(velocity.lengthSq() > Rules.MISSILE_MAX_VELOCITY * Rules.MISSILE_MAX_VELOCITY) {
			velocity.normalize().scale(Rules.MISSILE_MAX_VELOCITY);
		}

		// update position
		position.add(velocity);

		// deplete thrust
		this.thrust.sub(thrust);

		if(!Rules.isInBattlefield(position)) {
			active = false;
		}
	}

}
