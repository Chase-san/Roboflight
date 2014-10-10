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

import java.awt.Color;

import roboflight.BasicRobot;
import roboflight.events.*;
import roboflight.util.Rules;
import roboflight.util.Vector;

public class Alpha extends BasicRobot {
	Vector thrust = new Vector();
	double nearest = Double.POSITIVE_INFINITY;

	public void onBattleStarted(BattleStartedEvent e) {
		setColor(Color.CYAN);
	}
	
	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		Vector ePosition = e.getPosition();
		Vector position = getPosition();
		
		/* targeting */
		double distSq = ePosition.distanceSq(position);
		if(distSq < nearest) {
			nearest = distSq;
			
			Vector target = calculateLinearTarget(position, Rules.BULLET_VELOCITY, ePosition, e.getVelocity());
	
			setFireBullet(target.sub(position));
		}
		
		/* movement */
		avoid(ePosition);
	}

	public void onTurnStarted(TurnStartedEvent e) {
		nearest = Double.POSITIVE_INFINITY;
		thrust.set(0,0,0);
	}
	
	public void onRobotDeath(RobotDeathEvent e) {
		if(getOthers() == 0) {
			setThrust(getVelocity().scale(-1));
		}
	}

	public void onTurnEnded(TurnEndedEvent e) {
		if(getOthers() == 0) {
			return;
		}
		
		/* movement */
		/* wall avoidance */
		avoid(getPosition().normalize().scale(Rules.BATTLEFIELD_RADIUS));
		
		/* avoid origin */
		avoid(new Vector());
		
		setThrust(thrust.normalize());
	}
	
	void avoid(Vector target) {
		target = target.clone();
		/* determine relative position */
		target.sub(getPosition());
		double dist = target.lengthSq();
		/* inverse this to get the direction to travel */
		target.scale(-1);
		/* nearer are more dangerous */
		target.scale(1.0/dist);
		
		thrust.add(target);
	}

	Vector calculateLinearTarget(Vector origin, double speed, Vector target, Vector targetVel) {
		Vector rel = target.clone().sub(origin);
		double a = speed * speed - targetVel.lengthSq();
		double b = rel.dot(targetVel);
		double c = Math.sqrt(b * b + a * rel.lengthSq());
		return targetVel.clone().scale((b + c) / a).add(target);
	}

}
