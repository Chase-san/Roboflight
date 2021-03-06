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
 * This the missile control interface.
 * 
 * @author Robert Maupin
 */
public interface Missile {
	/**
	 * Gets the position of the missile.
	 * 
	 * @return the missiles position
	 */
	public Vector getPosition();

	/**
	 * Gets the velocity of the missile.
	 * 
	 * @return the missiles velocity
	 */
	public Vector getVelocity();

	/**
	 * Returns if this missile is currently active. Missiles will not become
	 * active until the turn after they are fired.
	 * 
	 * @return true, if the missile is active.
	 */
	public boolean isActive();

	/**
	 * Sets the thrust to use for the next update. This resets to 0 at the start
	 * of every turn.
	 * 
	 * @param thrust
	 *            Vector to apply to the missiles movement.
	 */
	public void setThrust(Vector thrust);
}
