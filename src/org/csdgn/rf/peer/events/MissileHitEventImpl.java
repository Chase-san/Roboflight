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

import roboflight.Missile;
import roboflight.events.MissileHitEvent;

public class MissileHitEventImpl extends EventImpl implements MissileHitEvent {
	private final String name;
	private final Missile missile;

	public MissileHitEventImpl(long time, Missile missile, String name) {
		super(time);
		this.name = name;
		this.missile = missile;
	}

	@Override
	public Missile getMissile() {
		return missile;
	}

	@Override
	public String getName() {
		return name;
	}
}