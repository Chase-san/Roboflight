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
 * Random is a robot that flies around randomly while shooting bullets at enemies.
 * @author Robert Maupin
 */
public class Random extends BasicRobot {

	Vector thrust = new Vector(0, 0, 0);

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		// fire directly at the enemy!
		setFireBullet(e.getPosition().sub(getPosition()));
	}

	@Override
	public void onTurnEnded(TurnEndedEvent e) {
		// increase the previous thrust
		// this is makes it so we are more likely to move then randomly
		// thrust in all directions and not move at all
		thrust.scale(2);
		// give it some random thrust
		thrust.add(new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1));
		// add this to keep it away from the edges
		thrust.add(getPosition().normalize().scale(-0.2));
		thrust.normalize();

		setThrust(thrust);
	}

}
