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
package roboflight.events;

import roboflight.Missile;

/**
 * This interface defines an event that is generated when a missile from another
 * robot hits your robot.
 * 
 * @author Robert Maupin
 */
public interface HitByMissileEvent extends Event {
	/**
	 * This method returns the missile that hit the robot.
	 * 
	 * @return The missile from that hit the robot.
	 */
	public Missile getMissile();

	/**
	 * This method returns the name of the robot that fired the missile.
	 * 
	 * @return the name of the robot that fired the missile.
	 */
	public String getName();
}
