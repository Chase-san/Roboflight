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

import roboflight.util.Vector;

/**
 * The peer used to interact with the game engine.
 * 
 * @author Robert Maupin
 * 
 */
public interface RobotPeer {
	/**
	 * Returns the current bullet heat.
	 */
	public double getBulletHeat();

	/**
	 * Gets your robots current energy.
	 * 
	 * @return Your robots energy.
	 */
	public double getEnergy();

	/**
	 * Returns the name of your robot.
	 * 
	 * @return your robot's name.
	 */
	public String getName();

	/**
	 * Gets your robots current position in space.
	 * 
	 * @return A vector with your robots position.
	 */
	public Vector getPosition();

	/**
	 * Gets the current game time.
	 * 
	 * @return the current game time.
	 */
	public long getTime();

	/**
	 * Gets your robots current velocity through space.
	 * 
	 * @return A vector with your robots velocity.
	 */
	public Vector getVelocity();

	/**
	 * Sets a bullet to fire.
	 * 
	 * @param target
	 *            Vector to fire the bullet at.
	 */
	public Bullet setFireBullet(Vector target);

	/**
	 * Sets a missile to fire.
	 * 
	 * @param target
	 *            Vector to fire the missile at.
	 */
	public Missile setFireMissile(Vector target);

	/**
	 * Sets the thrust to use for the next update. This resets to 0 at the start
	 * of every turn.
	 * 
	 * @param thrust
	 *            Vector to apply to the robots movement.
	 */
	public void setThrust(Vector thrust);
}
