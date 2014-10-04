package sample;

import roboflight.BasicRobot;
import roboflight.Missile;
import roboflight.events.RobotUpdateEvent;
import roboflight.util.Vector;

public class Launcher extends BasicRobot {
	Vector thrust = new Vector();
	Missile missile;
	String target;
	long lastTime = 0;
	boolean fired = false;
	
	public void onTurnStarted() {
		if(missile != null && !missile.isActive())
			fired = false;
	}
	
	public void onRobotUpdate(RobotUpdateEvent e) {
		Vector pos = getPosition();
		setFireBullet(e.getPosition().sub(pos));
		
		Vector p = getPosition();
		
		if(!fired) {
			missile = setFireMissile(e.getPosition().sub(p));
			if(missile != null) {
				target = e.getName();
				fired = true;
			}
		}
		
		if(fired && e.getName().equals(target)) {
			missile.setThrust(e.getPosition().sub(missile.getPosition()));
		}
		
		//thrust towards the center of the field.
		if(p.length() > 800) {
			lastTime = getTime();
		}
		
		//thurst in this direction for 20 turns
		if(getTime() - lastTime < 20) {
			thrust = p.normalize().scale(-1);
		}
		
		setThrust(thrust);
	}
}
