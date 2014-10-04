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
package org.csdgn.rf.gui;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * A class for handling the 3D Camera.
 * 
 * @author Robert Maupin
 * 
 */
public class Camera extends Vector3f {
	private static final long serialVersionUID = -8842056265721188012L;

	public static final double TILT_LIMIT = Math.PI / 2.0 - Math.PI / 1024;

	public static final float MIN_DISTANCE = 1.5f;
	public static final float MAX_DISTANCE = 2.5f;

	private static final Quaternion invert(final Quaternion q) {
		final float norm = 1f / (q.w * q.w + q.x * q.x + q.y * q.y + q.z * q.z);
		return new Quaternion(q.x * -norm, q.y * -norm, q.z * -norm, q.w * norm);
	}

	/**
	 * Verical Tilt
	 */
	public float angleY = 0;

	/**
	 * Horizontal Tilt
	 */
	public float angleXZ = (float) (-Math.PI / 8);

	/**
	 * Vertical Momentum
	 */
	public float momentumY = 0;

	/**
	 * Horizontal Momentum
	 */
	public float momentumXZ = 0;

	/**
	 * Distance from center
	 */
	public float distance = 2;

	/**
	 * Spacial camera rotation
	 */
	private final Quaternion rotation = new Quaternion(0, 0, 0, 1);

	/**
	 * Updates the camera's position according to the current angles. Be sure to
	 * set the position to center facing position first.
	 */
	public void updateCamera() {
		/*
		 * Hah, this almost makes it look like I know what the hell I am doing
		 * doesn't it. The scary part is that I came up with it myself. -- Chase
		 */
		angleY += momentumY *= 0.9;
		angleXZ += momentumXZ *= 0.9;

		// limit the vertical tilt
		angleY = (float) Math.min(TILT_LIMIT, Math.max(-TILT_LIMIT, angleY));

		distance = Math.max(MIN_DISTANCE, Math.min(distance, MAX_DISTANCE));

		// Calculate the rotation
		final Quaternion xz = new Quaternion(0f, (float) Math.sin(angleXZ * 0.5), 0f, (float) Math.cos(angleXZ * 0.5));
		final Quaternion y = new Quaternion(0f, 0f, (float) Math.sin(angleY * 0.5), (float) Math.cos(angleY * 0.5));

		Quaternion.mul(xz, y, rotation);

		// Now for the magical bit. This is how you 'project' in 3D.
		final Quaternion rot = new Quaternion(rotation);
		Quaternion.mul(rot, new Quaternion(1, 0, 0, 0), rot);
		Quaternion.mul(rot, invert(rotation), rot);
		final Vector3f trans = new Vector3f(rot.x, rot.y, rot.z);
		trans.scale(distance);
		translate(trans.x, trans.y, trans.z);
		// see wasn't that easy? </sarcasm>
	}
}
