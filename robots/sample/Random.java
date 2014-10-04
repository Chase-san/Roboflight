package sample;

import roboflight.BasicRobot;
import roboflight.events.RobotUpdateEvent;
import roboflight.events.TurnEndedEvent;
import roboflight.util.Vector;

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
