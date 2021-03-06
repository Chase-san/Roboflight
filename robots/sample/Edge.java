/**
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package sample;

import roboflight.BasicRobot;
import roboflight.events.RobotUpdateEvent;
import roboflight.events.TurnEndedEvent;
import roboflight.util.Vector;

/**
 * This robot drives along the edge of the field around the Y axis and fires
 * inward towards enemy robots.
 * 
 * @author Robert Maupin [Chase]
 * 
 */
public class Edge extends BasicRobot {
	Vector thrust = new Vector(1, 0, 0);

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		// Fire at the enemy
		// This is the roboflight version of head on targeting
		setFireBullet(e.getPosition().sub(getPosition()));
	}

	@Override
	public void onTurnEnded(TurnEndedEvent e) {
		Vector p = getPosition();
		setThrust(thrust);

		// If the distance from the center is 900 from the center we need to
		// turn to avoid running into the edge
		if(p.length() > 800) {
			// Basically we need a perpendicular tangent space vector, this
			// sounds complicated but is actually rather simple

			// We need an outward vector from the origin to our position, which
			// we can get by normalizing our position
			p.normalize();

			// Now we need a sample vector that is perpendicular to our last
			// vector, in this case, up is simple to do
			Vector up = new Vector(0, 1, 0);

			// now we just take the cross product which produces the last vector
			// that we didn't already have, and use that as our thrust.
			thrust = Vector.cross(p, up);

			// However we have a problem, when we run into the walls we stop, so
			// we have to point inward a bit.

			// So lets just add the inverse of the outward vector (the one that
			// was our position) to our thrust, that will make us thrust more
			// inward.

			// So invert the outward vector
			p.scale(-1);

			// But if we scale down the now inward (towards origin) vector, we
			// can make a smoother turning curve, so lets do that.
			p.scale(0.25);

			// You could replace the last two with p.scale(-0.25);

			// Now we add it to the thrust
			thrust.add(p);

			// This step isn't required as the game normalizes the thrust if it
			// is over MAX_ROBOT_THRUST. However lets do it for completeness.
			thrust.normalize();

			// Apply the thrust, this will override the previous thrust that was
			// set above.
			setThrust(thrust);
		}
	}
}
