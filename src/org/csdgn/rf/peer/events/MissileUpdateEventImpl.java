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

import roboflight.events.MissileUpdateEvent;
import roboflight.util.Vector;

public class MissileUpdateEventImpl extends EventImpl implements MissileUpdateEvent {
	private final String name;
	private final Vector velocity;
	private final Vector position;
	private final boolean owned;

	public MissileUpdateEventImpl(long time, final String name, final Vector velocity, final Vector position,
			final boolean owned) {
		super(time);
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
