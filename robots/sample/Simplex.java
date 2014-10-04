package sample;

import roboflight.BasicRobot;
import roboflight.events.BattleStartedEvent;

public class Simplex extends BasicRobot {
	@Override
	public void onBattleStarted(BattleStartedEvent e) {
		System.out.printf("Started @ %d!\n", e.getTime());
		setThrust(getPosition().normalize());
	}
}
