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
import roboflight.Missile;
import roboflight.events.*;
import roboflight.util.Vector;

/**
 * Launcher is a robot that launches a missile and flies it towards the enemy.
 * 
 * @author Robert Maupin
 * 
 */
public class Launcher extends BasicRobot {
	Vector thrust = new Vector();
	Missile missile;
	String target;

	@Override
	public void onBattleStarted(BattleStartedEvent e) {
		/* fly towards center */
		thrust.set(getPosition().scale(-1));
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		/* reverse when we hit the wall */
		thrust.scale(-1);
	}

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		if(missile == null) {
			missile = setFireMissile(e.getPosition().sub(getPosition()));
			target = e.getName();
		} else if(e.getName().equals(target)) {
			missile.setThrust(e.getPosition().sub(missile.getPosition()));
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent e) {
		/* apply thrust */
		setThrust(thrust);
	}

	@Override
	public void onTurnStarted(TurnStartedEvent e) {
		if(missile != null && !missile.isActive()) {
			missile = null;
		}
	}
}
