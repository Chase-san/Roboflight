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
package org.csdgn.rf.peer.events;

import roboflight.events.RobotUpdateEvent;
import roboflight.util.Vector;

public class RobotUpdateEventImpl extends EventImpl implements RobotUpdateEvent {
	private final String name;
	private final Vector velocity;
	private final Vector position;
	private final double energy;

	public RobotUpdateEventImpl(long time, final String name, final Vector velocity, final Vector position,
			final double energy) {
		super(time);
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
