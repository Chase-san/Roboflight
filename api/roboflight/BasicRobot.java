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

import java.awt.Color;

import roboflight.events.BattleStartedEvent;
import roboflight.events.BulletHitEvent;
import roboflight.events.BulletMissEvent;
import roboflight.events.HitByBulletEvent;
import roboflight.events.HitByMissileEvent;
import roboflight.events.HitWallEvent;
import roboflight.events.MissileHitEvent;
import roboflight.events.MissileMissEvent;
import roboflight.events.MissileUpdateEvent;
import roboflight.events.RobotDeathEvent;
import roboflight.events.RobotUpdateEvent;
import roboflight.events.TurnEndedEvent;
import roboflight.events.TurnStartedEvent;
import roboflight.util.Vector;

/**
 * This is a simple extendible class that implements the Robot and RobotPeer.
 * 
 * @author Robert Maupin
 * 
 */
public class BasicRobot implements Robot, RobotPeer {
	private RobotPeer peer;

	@Override
	public int getBulletDelay() {
		return peer.getBulletDelay();
	}

	@Override
	public double getEnergy() {
		return peer.getEnergy();
	}

	@Override
	public int getMissileDelay() {
		return peer.getMissileDelay();
	}

	@Override
	public String getName() {
		return peer.getName();
	}

	@Override
	public int getOthers() {
		return peer.getOthers();
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
	public void onBulletMiss(BulletMissEvent e) {
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
	}

	@Override
	public void onHitByMissile(HitByMissileEvent e) {
	}

	@Override
	public void onHitWall(HitWallEvent e) {
	}

	@Override
	public void onMissileHit(MissileHitEvent e) {
	}

	@Override
	public void onMissileMiss(MissileMissEvent e) {
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
	public void setColor(Color color) {
		peer.setColor(color);
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
