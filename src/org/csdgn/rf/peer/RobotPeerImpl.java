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

import java.awt.Color;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;

import org.csdgn.rf.CoreUtils;

import roboflight.Bullet;
import roboflight.Missile;
import roboflight.Robot;
import roboflight.RobotPeer;
import roboflight.events.*;
import roboflight.util.Rules;
import roboflight.util.Vector;

/**
 * Implementation of the RobotPeer for internal use.
 * 
 * @author Robert Maupin
 * 
 */
public class RobotPeerImpl implements RobotPeer, Runnable {
	private final Vector thrust = new Vector();
	private final Vector velocity = new Vector();
	private final Vector position = new Vector();
	private final Vector lastThrust = new Vector();
	private final ArrayDeque<Event> eventQueue = new ArrayDeque<Event>();
	private final Robot robot;
	private boolean isPeerAndOutSet = false;
	private CharArrayWriter out = new CharArrayWriter();
	private String name;
	private float[] color = new float[] { 1, 1, 1 };
	private int bulletDelay = Rules.BULLET_START_DELAY;
	private int missileDelay = Rules.MISSILE_START_DELAY;
	private BulletImpl bullet;
	private MissileImpl missile;
	private boolean fireBullet = false;
	private boolean fireMissile = false;
	private boolean alive = true;
	private boolean enabled = true;
	private boolean hitWall = false;
	private double energy = Rules.ROBOT_START_ENERGY;
	private int others = 0;
	private long time = 0;

	public RobotPeerImpl(Robot robot) {
		this.robot = robot;
	}

	public void addEvent(Event e) {
		eventQueue.add(e);
	}

	public void clearQueue() {
		eventQueue.clear();
	}

	public boolean didHitWall() {
		return hitWall;
	}

	public void disable() {
		enabled = false;
		energy = 0;
		thrust.set(0, 0, 0);
	}

	@Override
	public int getBulletDelay() {
		return bulletDelay;
	}

	public BulletImpl getBulletFired() {
		if(!fireBullet) {
			return null;
		}
		fireBullet = false;
		energy -= Rules.BULLET_COST;
		bulletDelay = Rules.BULLET_DELAY;
		return bullet;
	}

	public float[] getColor() {
		return color;
	}

	@Override
	public final double getEnergy() {
		return energy;
	}

	public Vector getLastThrust() {
		return lastThrust.clone();
	}

	@Override
	public int getMissileDelay() {
		return missileDelay;
	}

	public MissileImpl getMissileFired() {
		if(!fireMissile) {
			return null;
		}
		fireMissile = false;
		energy -= Rules.MISSILE_COST;
		missileDelay = Rules.MISSILE_DELAY;
		return missile;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public int getOthers() {
		return others;
	}

	@Override
	public final Vector getPosition() {
		return position.clone();
	}

	public Vector getPositionVector() {
		return position;
	}

	@Override
	public final long getTime() {
		return time;
	}

	@Override
	public final Vector getVelocity() {
		return velocity.clone();
	}

	public Vector getVelocityVector() {
		return velocity;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFiringBullet() {
		return fireBullet;
	}

	public boolean isFiringMissile() {
		return fireMissile;
	}

	public void kill() {
		alive = false;
	}

	@Override
	public void run() {
		/* run all events here! */
		if(!enabled) {
			return;
		}
		try {
			if(!isPeerAndOutSet) {
				isPeerAndOutSet = true;
				robot.setRobotPeer(this);
				robot.setOut(new PrintWriter(out));
			}

			while(!eventQueue.isEmpty()) {
				Event e = eventQueue.pop();

				/*
				 * - IMPORTANT - Keep these in alphabetical order. That way it
				 * is easy to check if any are missing.
				 */
				if(e instanceof BattleStartedEvent) {
					robot.onBattleStarted((BattleStartedEvent) e);
				} else if(e instanceof BulletHitEvent) {
					robot.onBulletHit((BulletHitEvent) e);
				} else if(e instanceof BulletMissEvent) {
					robot.onBulletMiss((BulletMissEvent) e);
				} else if(e instanceof HitByBulletEvent) {
					robot.onHitByBullet((HitByBulletEvent) e);
				} else if(e instanceof HitByMissileEvent) {
					robot.onHitByMissile((HitByMissileEvent) e);
				} else if(e instanceof HitWallEvent) {
					robot.onHitWall((HitWallEvent) e);
				} else if(e instanceof MissileHitEvent) {
					robot.onMissileHit((MissileHitEvent) e);
				} else if(e instanceof MissileMissEvent) {
					robot.onMissileMiss((MissileMissEvent) e);
				} else if(e instanceof MissileUpdateEvent) {
					robot.onMissileUpdate((MissileUpdateEvent) e);
				} else if(e instanceof RobotDeathEvent) {
					robot.onRobotDeath((RobotDeathEvent) e);
				} else if(e instanceof RobotUpdateEvent) {
					robot.onRobotUpdate((RobotUpdateEvent) e);
				} else if(e instanceof TurnEndedEvent) {
					robot.onTurnEnded((TurnEndedEvent) e);
				} else if(e instanceof TurnStartedEvent) {
					robot.onTurnStarted((TurnStartedEvent) e);
				}
			}
		} catch(SecurityException e) {
			enabled = false;
			System.out.println(name + " has thrown a security exception during operation and has been disabled.");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void setColor(Color color) {
		if(color == null) {
			return;
		}
		this.color = color.getRGBColorComponents(null);
		/* limit color to acceptable range, min value for each is 0.2f */
		this.color[0] = this.color[0] * 0.8f + 0.2f;
		this.color[1] = this.color[1] * 0.8f + 0.2f;
		this.color[2] = this.color[2] * 0.8f + 0.2f;
	}

	public void setEnergy(final double energy) {
		this.energy = energy;
		if(this.energy <= 0) {
			this.energy = 0;
		}
	}

	@Override
	public final Bullet setFireBullet(final Vector target) {
		if(!enabled || bulletDelay > 0 || energy < Rules.BULLET_COST || CoreUtils.isBadVector(target)) {
			return null;
		}
		fireBullet = true;
		bullet = new BulletImpl(this);
		bullet.getPositionVector().set(position);
		bullet.getVelocityVector().set(target).normalize().scale(Rules.BULLET_VELOCITY);
		return bullet;
	}

	@Override
	public final Missile setFireMissile(final Vector target) {
		if(!enabled || missileDelay > 0 || energy < Rules.MISSILE_COST || CoreUtils.isBadVector(target)) {
			return null;
		}
		fireMissile = true;
		missile = new MissileImpl(this);
		missile.getPositionVector().set(position);
		Vector mVel = missile.getVelocityVector();
		mVel.set(target);
		if(mVel.lengthSq() > Rules.MISSILE_MAX_VELOCITY * Rules.MISSILE_MAX_VELOCITY) {
			mVel.normalize().scale(Rules.MISSILE_MAX_VELOCITY);
		}

		return missile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOthersCount(int count) {
		others = count;
	}

	@Override
	public final void setThrust(final Vector thrust) {
		if(CoreUtils.isBadVector(thrust)) {
			return;
		}
		this.thrust.set(thrust);
	}

	public void setTime(final long time) {
		this.time = time;
	}

	public void update() {
		Vector thrust = this.thrust.clone();
		if(enabled) {
			// normalize thrust if we need to
			if(thrust.lengthSq() > Rules.ROBOT_MAX_THRUST * Rules.ROBOT_MAX_THRUST) {
				thrust.normalize().scale(Rules.ROBOT_MAX_THRUST);
			}

			// update velocity
			velocity.add(thrust);

			// deplete thrust
			this.thrust.sub(thrust);
		}
		// normalize velocity if we need to
		if(velocity.lengthSq() > Rules.ROBOT_MAX_VELOCITY * Rules.ROBOT_MAX_VELOCITY) {
			velocity.normalize().scale(Rules.ROBOT_MAX_VELOCITY);
		}
		hitWall = false;
		if(!Rules.isRobotInBattlefield(position.clone().add(velocity))) {
			velocity.set(0, 0, 0);
			hitWall = true;
			/*
			 * Place robot in the nearest position that is within the
			 * battlefield. This is simpler then calculating the precise
			 * velocity needed to get to the wall, but the result is the same.
			 */
			position.normalize();
			position.scale(Rules.BATTLEFIELD_RADIUS - Rules.ROBOT_RADIUS - Double.MIN_VALUE);
		}

		// update position
		position.add(velocity);
		lastThrust.set(thrust);
		// reduce the firing delay
		if(bulletDelay > 0) {
			--bulletDelay;
		}
		if(missileDelay > 0) {
			--missileDelay;
		}
	}

	public String getOutput() {
		out.flush();
		String output = out.toString();
		out.reset();
		return output;
	}
}
