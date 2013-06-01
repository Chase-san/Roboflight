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

import roboflight.util.Vector;

/**
 * This interface is used to supply information about a Missile.
 * @author Robert Maupin
 */
public interface MissileUpdateEvent {
	/**
	 * This method returns the owner of the missile.
	 * @return The missiles owner.
	 */
	public String getName();
	
	/**
	 * This method returns true if the missile belongs to this robot.
	 * @return true, if the missile belongs to this robot.
	 */
	public boolean isOwned();
	
	/**
	 * This method returns the position of the missile.
	 * @return The missile's position.
	 */
	public Vector getPosition();

	/**
	 * This method returns the velocity of the missile.
	 * @return The missile's velocity.
	 */
	public Vector getVelocity();
	
}
