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
 * A 3 dimensional vector with double precision.
 * 
 * @author Robert Maupin
 */
public class Vector implements Cloneable {
	/**
	 * Creates a new vector that is the cross product of vector a and b.
	 * 
	 * <pre>
	 * x = a.y*b.z - a.z*b.y
	 * y = a.z*b.x - a.x*b.z
	 * z = a.x*b.y - a.y*b.x
	 * </pre>
	 * 
	 * @param a
	 *            The first vector
	 * @param b
	 *            The second vector
	 * @return The cross product of vector a and b.
	 */
	public static final Vector cross(final Vector a, final Vector b) {
		return new Vector(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x
				* b.y - a.y * b.x);
	}

	/**
	 * Calculates the Euler distance between the two vector.
	 * 
	 * @param p
	 *            The first vector.
	 * @param q
	 *            The second vector.
	 * @return the Euler distance
	 */
	public static final double distance(final Vector p, final Vector q) {
		return Math.sqrt((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y)
				+ (p.z - q.z) * (p.z - q.z));
	}

	/**
	 * Calculates the square of the Euler distance between the two vector.
	 * 
	 * @param p
	 *            The first vector.
	 * @param q
	 *            The second vector.
	 * @return the square of the Euler distance
	 */
	public static final double distanceSq(final Vector p, final Vector q) {
		return (p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y)
				+ (p.z - q.z) * (p.z - q.z);
	}

	/**
	 * Coordinate Value
	 */
	public double x, y, z;

	/**
	 * Creates a vector with x,y, and z initialized to 0.
	 */
	public Vector() {
		x = y = z = 0;
	}

	/**
	 * Creates a new vector with the x,y, and z components initialiazed to the
	 * given values.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * @param z
	 *            The Z coordinate.
	 */
	public Vector(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a copy of the given vector.
	 * 
	 * @param p
	 *            The vector to copy.
	 */
	public Vector(final Vector p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	/**
	 * Adds the given vector to this vector.
	 * 
	 * @param p
	 *            The vector to add.
	 * @return This vector.
	 */
	public Vector add(final Vector p) {
		x += p.x;
		y += p.y;
		z += p.z;
		return this;
	}

	/**
	 * Creates and returns a copy of this vector.
	 * 
	 * @return a copy of this vector
	 */
	@Override
	public Vector clone() {
		return new Vector(this);
	}

	/**
	 * Sets this vector to the cross/vector product of the given vector and this
	 * vector. This vector takes the role of the a vector in the cross product.
	 * 
	 * <pre>
	 * x = a.y*b.z - a.z*b.y
	 * y = a.z*b.x - a.x*b.z
	 * z = a.x*b.y - a.y*b.x
	 * </pre>
	 * 
	 * @param b
	 *            The vector to cross with this vector.
	 * @return This vector.
	 */
	public Vector cross(final Vector b) {
		final double x = y * b.z - z * b.y;
		final double y = z * b.x - this.x * b.z;
		z = this.x * b.y - this.y * b.x;
		this.y = y;
		this.x = x;
		return this;
	}

	/**
	 * Calculates the Euler distance from this vector to the given vector.
	 * 
	 * @param p
	 *            The target vector.
	 * @return The Euler distance to the given vector.
	 */
	public double distance(final Vector q) {
		return Math.sqrt((x - q.x) * (x - q.x) + (y - q.y) * (y - q.y)
				+ (z - q.z) * (z - q.z));
	}

	/**
	 * Calculates the square of the Euler distance from this vector to the given
	 * vector.
	 * 
	 * @param p
	 *            The target vector.
	 * @return The square of the Euler distance to the given vector.
	 */
	public double distanceSq(final Vector q) {
		return (x - q.x) * (x - q.x) + (y - q.y) * (y - q.y) + (z - q.z)
				* (z - q.z);
	}

	/**
	 * Returns the scalar product of this vector and the given vector.
	 * 
	 * @param p
	 *            The vector to dot with this vector.
	 * @return The scalar dot product of the two vector.
	 */
	public double dot(final Vector p) {
		return x * p.x + y * p.y + z * p.z;
	}

	/**
	 * Sets this vector to its unit vector.
	 * 
	 * @return This vector.
	 */
	public Vector normalize() {
		scale(1.0 / length());
		return this;
	}

	/**
	 * The length of this vector. This is identical to the vectors distance from
	 * the origin.
	 * 
	 * @return The length of this vector
	 */
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * The square of the length of this vector. This is identical to the square
	 * of the vectors distance from the origin.
	 * 
	 * @return The square of the length of this vector
	 */
	public double lengthSq() {
		return x * x + y * y + z * z;
	}

	/**
	 * Scales this vector by the given amount and returns itself. In laymen's
	 * terms this multiplies each component by the given scale.
	 * 
	 * @param scale
	 *            Amount to scale this vector by.
	 * @return This vector.
	 */
	public Vector scale(final double scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	/**
	 * Sets this vector to the given coordinates.
	 * 
	 * @param x
	 *            The X Coordinate.
	 * @param y
	 *            The Y Coordinate.
	 * @param z
	 *            The Z Coordinate.
	 * @return This vector.
	 */
	public Vector set(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Sets this vector to the given coordinates.
	 * 
	 * @param vec
	 *            The vector to get the coordinates from.
	 * @return This vector.
	 */
	public Vector set(final Vector vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	/**
	 * Sets this vectors X coordinate to the given value.
	 * 
	 * @param x
	 *            The X Coordinate.
	 * @return This vector.
	 */
	public Vector setX(final double x) {
		this.x = x;
		return this;
	}

	/**
	 * Sets this vectors Y coordinate to the given value.
	 * 
	 * @param y
	 *            The Y Coordinate.
	 * @return This vector.
	 */
	public Vector setY(final double y) {
		this.y = y;
		return this;
	}

	/**
	 * Sets this vectors Z coordinate to the given value.
	 * 
	 * @param z
	 *            The Z Coordinate.
	 * @return This vector.
	 */
	public Vector setZ(final double z) {
		this.z = z;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector.
	 * 
	 * @param b
	 *            The vector to subtract.
	 * @return This vector.
	 */
	public Vector sub(final Vector b) {
		x -= b.x;
		y -= b.y;
		z -= b.z;
		return this;
	}

	/**
	 * Returns a string representation of this vector.
	 */
	public String toString() {
		return String.format("<%f,%f,%f>", x, y, z);
	}
}
