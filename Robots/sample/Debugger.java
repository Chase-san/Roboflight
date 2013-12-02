package sample;

import roboflight.BasicRobot;
import roboflight.Missile;
import roboflight.util.Vector;

public class Debugger extends BasicRobot {
	@Override
	public void onBattleStarted() {
		//thrust towards center for 4 turns
		setThrust(getPosition().normalize().scale(-4));
		
		Missile m = setFireMissile(new Vector(1,0,0));
		if(m != null)
			m.setThrust(m.getVelocity().scale(-1));
	}
}
