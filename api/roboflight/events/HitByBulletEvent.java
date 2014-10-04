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
package roboflight.events;

import roboflight.Bullet;

/**
 * This interface defines an event that is generated when a bullet from another
 * robot hits your robot.
 * 
 * @author Robert Maupin
 */
public interface HitByBulletEvent extends Event {
	/**
	 * This method returns the bullet that hit the robot.
	 * 
	 * @return The Bullet from that hit the robot.
	 */
	public Bullet getBullet();

	/**
	 * This method returns the name of the robot that fired the bullet.
	 * 
	 * @return the name of the robot that fired the bullet.
	 */
	public String getName();
}
