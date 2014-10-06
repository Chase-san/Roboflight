package roboflight.util;

public class Utils {
	public static final double lineSegmentPointDist(Vector line0, Vector line1, Vector point) {
		return Math.sqrt(lineSegmentPointDistSq(line0, line1, point));
	}
	
	public static final double lineSegmentPointDistSq(Vector line0, Vector line1, Vector point) {
		double lineLenSq = line1.distanceSq(line0);
		double dot = (line0.x-point.x)*(line1.x-line0.x)
				+(line0.y-point.y)*(line1.y-line0.y)
				+(line0.z-point.z)*(line1.z-line0.z);
		return (line0.distanceSq(point)*lineLenSq-dot)/lineLenSq;
	}
	
	public static final Vector nearestPointOnLineSegment(Vector line0, Vector line1, Vector point) {
		double lenSq = line0.distanceSq(line1);
		if(lenSq == 0.0) {
			return line0.clone();
		}
		
		Vector pv = point.clone().sub(line0);
		Vector wv = line1.clone().sub(line0);
		
		double t = pv.dot(wv) / lenSq;
		if(t <= 0) {
			return line0.clone();
		} else if(t >= 1) {
			return line1.clone();
		}
		
		return pv.set(line0).add(wv.scale(t));
	}

	private Utils() {
	}
}
