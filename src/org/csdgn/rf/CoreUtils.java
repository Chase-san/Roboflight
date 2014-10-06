package org.csdgn.rf;

import roboflight.util.Vector;

public class CoreUtils {
	public static boolean isBadVector(Vector vec) {
		return vec == null || vec.isNaN() || vec.isInfinite() || vec.lengthSq() == 0;
	}
	private CoreUtils() {}
}
