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
package roboflight.util;

/**
 * The basic rules class that defines things about the battle and the field.
 * 
 * @author Robert Maupin
 */
public final class Rules {
	public static final double BATTLEFIELD_RADIUS = 1000;
	public static final double ROBOT_MAX_THRUST = 1;
	public static final double ROBOT_MAX_VELOCITY = 10;
	public static final double ROBOT_RADIUS = 30;
	public static final double ROBOT_START_ENERGY = 100;
	public static final double BULLET_DAMAGE = 2;
	public static final double BULLET_COST = 0.5;
	public static final int BULLET_DELAY = 10;
	public static final int BULLET_START_DELAY = 50;
	public static final double BULLET_ONHIT_GAIN = 2;
	public static final double BULLET_VELOCITY = 20;
	public static final int MISSILE_DELAY = 20;
	public static final int MISSILE_START_DELAY = 80;
	public static final double MISSILE_COST = 8;
	public static final double MISSILE_DAMAGE = 16;
	public static final double MISSILE_ONHIT_GAIN = 16;
	public static final double MISSILE_RADIUS = 10;
	public static final double MISSILE_MAX_THRUST = 0.5;
	public static final double MISSILE_MAX_VELOCITY = 15;
	/** Under this speed missiles are undetectable. */
	public static final double MISSILE_HIDDEN_VELOCITY_THRESHOLD = 0.25;

	public static final boolean isInBattlefield(final Vector position) {
		return position.lengthSq() < Rules.BATTLEFIELD_RADIUS * Rules.BATTLEFIELD_RADIUS;
	}

	public static final boolean isRobotInBattlefield(final Vector position) {
		double radius = Rules.BATTLEFIELD_RADIUS - Rules.ROBOT_RADIUS;
		return position.lengthSq() < radius*radius;
	}

	private Rules() {
	}
}
