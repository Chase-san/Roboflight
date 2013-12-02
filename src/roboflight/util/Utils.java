package roboflight.util;

public class Utils {
	private Utils() { }
	public static final double linePointDistanceSq(Vector p, Vector v0, Vector v1) {
		Vector vx = v1.clone().sub(v0);
		return vx.clone().cross(v0.clone().sub(p)).lengthSq()/vx.lengthSq();
	}
	public static final double linePointDistance(Vector p, Vector v0, Vector v1) {
		return Math.sqrt(linePointDistanceSq(p,v0,v1));
	}
	public static final boolean intersectLineSphere(Vector v0, Vector v1, Vector p, double len) {
		if(linePointDistanceSq(v0,v1,p) < len*len)
			return true;
		return false;
	}
}
