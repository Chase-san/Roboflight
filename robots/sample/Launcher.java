/**
 * Copyright (c) 2013-2014 Robert Maupin
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package sample;

import roboflight.BasicRobot;
import roboflight.Missile;
import roboflight.events.*;
import roboflight.util.Vector;

/**
 * Launcher is a robot that launches a missile and flies it towards the enemy.
 * @author Robert Maupin
 *
 */
public class Launcher extends BasicRobot {
	Vector thrust = new Vector();
	Missile missile;
	String target;

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		if(missile == null) {
			missile = setFireMissile(e.getPosition().sub(getPosition()));
			target = e.getName();
		} else if(e.getName().equals(target)) {
			missile.setThrust(e.getPosition().sub(missile.getPosition()));
		}
	}
	
	@Override
	public void onBattleStarted(BattleStartedEvent e) {
		/* fly towards center */
		thrust.set(getPosition().scale(-1));
	}
	
	@Override
	public void onHitWall(HitWallEvent e) {
		/* reverse when we hit the wall */
		thrust.scale(-1);
	}
	
	@Override
	public void onTurnStarted(TurnStartedEvent e) {
		if(missile != null && !missile.isActive()) {
			missile = null;
		}
	}
	
	@Override
	public void onTurnEnded(TurnEndedEvent e) {
		/* apply thrust */
		setThrust(thrust);
	}
}
