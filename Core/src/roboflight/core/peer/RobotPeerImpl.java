/**
 * Copyright (c) 2013 Robert Maupin
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
package roboflight.core.peer;

import roboflight.Bullet;
import roboflight.Missile;
import roboflight.Robot;
import roboflight.RobotPeer;
import roboflight.util.Rules;
import roboflight.util.Vector;

/**
 * Implementation of the RobotPeer for internal use.
 * 
 * @author Robert Maupin
 * 
 */
public class RobotPeerImpl implements RobotPeer {
	private final Vector thrust = new Vector();
	private final Vector velocity = new Vector();
	private final Vector position = new Vector();
	private final Vector lastThrust = new Vector();
	private Robot robot;
	private String name;
	private double bulletHeat = Rules.BULLET_START_HEAT;
	private BulletImpl bullet;
	private MissileImpl missile;
	private boolean fireBullet = false;
	private boolean fireMissile = false;
	private boolean alive = true;
	private boolean enabled = true;
	private double energy = Rules.ROBOT_START_ENERGY;
	private long time = 0;

	public void disable() {
		enabled = false;
		energy = 0;
		thrust.set(0, 0, 0);
	}

	public BulletImpl getBulletFired() {
		if(!fireBullet) return null;
		fireBullet = false;
		energy -= Rules.BULLET_COST;
		bulletHeat += Rules.BULLET_HEAT;
		return bullet;
	}

	@Override
	public double getBulletHeat() {
		return bulletHeat;
	}

	@Override
	public final double getEnergy() {
		return energy;
	}

	public Vector getLastThrust() {
		return lastThrust.clone();
	}

	public MissileImpl getMissileFired() {
		if(!fireMissile) return null;
		energy -= Rules.MISSILE_COST;
		fireMissile = false;
		return missile;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final Vector getPosition() {
		return position.clone();
	}

	public Vector getPositionVector() {
		return position;
	}

	public Robot getRobot() {
		return robot;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnergy(final double energy) {
		this.energy = energy;
		if(this.energy < 0)
			this.energy = 0;
	}

	@Override
	public final Bullet setFireBullet(final Vector target) {
		if(!enabled || bulletHeat > 0 || energy < Rules.BULLET_COST
				|| target.lengthSq() == 0) return null;
		fireBullet = true;
		bullet = new BulletImpl(this);
		bullet.getPositionVector().set(position);
		bullet.getVelocityVector().set(target).normalize().scale(Rules.BULLET_VELOCITY);
		return bullet;
	}

	@Override
	public final Missile setFireMissile(final Vector target) {
		if(!enabled || energy < Rules.MISSILE_COST || target.lengthSq() == 0) return null;
		fireMissile = true;
		
		missile = new MissileImpl(this);
		missile.getPositionVector().set(position);
		missile.getVelocityVector().set(target).normalize().scale(Rules.MISSILE_FIRE_VELOCITY);
		
		return missile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRobot(final Robot robot) {
		this.robot = robot;
	}

	@Override
	public final void setThrust(final Vector thrust) {
		this.thrust.set(thrust);
	}

	public void setTime(final long time) {
		this.time = time;
	}
	
	public void kill() {
		alive = false;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void update() {
		Vector thrust = this.thrust.clone();
		if(enabled) {
			if(thrust.lengthSq() > 0)
				System.out.print("A " + this.thrust);
			
			// normalize thrust if we need to
			if(thrust.lengthSq() > Rules.ROBOT_MAX_THRUST * Rules.ROBOT_MAX_THRUST)
				thrust.normalize().scale(Rules.ROBOT_MAX_THRUST);
			// update velocity
			velocity.add(thrust);
			// deplete thrust
			this.thrust.sub(thrust);
			
			if(thrust.lengthSq() > 0)
				System.out.println(" B "+this.thrust);
		}
		// normalize velocity if we need to
		if(velocity.lengthSq() > Rules.ROBOT_MAX_VELOCITY * Rules.ROBOT_MAX_VELOCITY)
			velocity.normalize().scale(Rules.ROBOT_MAX_VELOCITY);
		if(!Rules.isRobotInBattlefield(position.clone().add(velocity)))
			velocity.set(0, 0, 0);
		// update position
		position.add(velocity);
		lastThrust.set(thrust);
		// cool down our gun (bullet heat)
		bulletHeat -= Rules.BULLET_COOLING_RATE;
		if(bulletHeat < 0) bulletHeat = 0;
	}
}
