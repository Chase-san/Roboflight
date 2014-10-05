package sample;

import roboflight.BasicRobot;
import roboflight.events.*;
import roboflight.util.Vector;

public class Debugger extends BasicRobot {
	@Override
	public void onMissileUpdate(MissileUpdateEvent e) {
		Vector velocity = e.getVelocity();
		System.out.println(e.getPosition() + " " + velocity + "(" + velocity.length() + ")");
	}
}
