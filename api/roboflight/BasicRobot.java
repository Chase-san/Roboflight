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
package roboflight;

import roboflight.events.BattleStartedEvent;
import roboflight.events.BulletHitEvent;
import roboflight.events.HitByBulletEvent;
import roboflight.events.MissileUpdateEvent;
import roboflight.events.RobotDeathEvent;
import roboflight.events.RobotUpdateEvent;
import roboflight.events.TurnEndedEvent;
import roboflight.events.TurnStartedEvent;
import roboflight.util.Vector;

/**
 * This is a simple extendable class that implements the Robot and RobotPeer.
 * 
 * @author Robert Maupin
 * 
 */
public class BasicRobot implements Robot, RobotPeer {
	private RobotPeer peer;

	@Override
	public double getBulletHeat() {
		return peer.getBulletHeat();
	}

	@Override
	public double getEnergy() {
		return peer.getEnergy();
	}

	@Override
	public String getName() {
		return peer.getName();
	}

	@Override
	public Vector getPosition() {
		return peer.getPosition();
	}

	@Override
	public long getTime() {
		return peer.getTime();
	}

	@Override
	public Vector getVelocity() {
		return peer.getVelocity();
	}

	@Override
	public void onBattleStarted(BattleStartedEvent e) {
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
	}

	@Override
	public void onMissileUpdate(MissileUpdateEvent e) {

	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
	}

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
	}

	@Override
	public void onTurnEnded(TurnEndedEvent e) {
	}

	@Override
	public void onTurnStarted(TurnStartedEvent e) {
	}

	@Override
	public Bullet setFireBullet(Vector target) {
		return peer.setFireBullet(target);
	}

	@Override
	public Missile setFireMissile(Vector target) {
		return peer.setFireMissile(target);
	}

	@Override
	public final void setRobotPeer(RobotPeer peer) {
		this.peer = peer;
	}

	@Override
	public void setThrust(Vector thrust) {
		peer.setThrust(thrust);
	}
}
