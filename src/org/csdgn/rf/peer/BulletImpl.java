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
		if(hasNextPosition) {
			return nextPosition;
		}
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
		if(!Rules.isInBattlefield(getNextPosition())) {
			active = false;
		}
	}
}
