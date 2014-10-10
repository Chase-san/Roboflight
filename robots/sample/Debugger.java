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

import java.util.ArrayList;

import roboflight.BasicRobot;
import roboflight.events.*;

public class Debugger extends BasicRobot {
	
	@Override
	public void onBattleStarted(BattleStartedEvent e) {
		System.out.println("Debug: Battle Started!");
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		System.out.printf("Debug: Fired bullet hit %s at %s.\n",e.getName(),e.getBullet().getPosition());
	}

	@Override
	public void onBulletMiss(BulletMissEvent e) {
		System.out.printf("Debug: Fired bullet missed at %s.\n",e.getBullet().getPosition());
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		System.out.printf("Debug: Hit by %s's bullet at %s.\n", e.getName(), e.getBullet().getPosition());
	}

	@Override
	public void onHitByMissile(HitByMissileEvent e) {
		System.out.printf("Debug: Hit by %s's missile at %s.\n",e.getName(),e.getMissile().getPosition());
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		System.out.printf("Debug: Hit wall at %s.\n",e.getPosition());
	}

	@Override
	public void onMissileHit(MissileHitEvent e) {
		System.out.printf("Debug: Missile hit %s at %s.\n",e.getName(),e.getMissile().getPosition());
	}

	@Override
	public void onMissileMiss(MissileMissEvent e) {
		System.out.printf("Debug: Missile at %s missed.\n",e.getMissile().getPosition());
	}

	@Override
	public void onMissileUpdate(MissileUpdateEvent e) {
		System.out.printf("Debug: Missile of %s at %s %s.\n",e.getName(),e.getPosition(),e.getVelocity());
	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		System.out.printf("Debug: %s Died.\n",e.getName());
	}

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
		System.out.printf("Debug: Detected %s (%.1f) at %s %s.\n", e.getName(), e.getEnergy(), e.getPosition(), e.getVelocity());
	}

	@Override
	public void onTurnEnded(TurnEndedEvent e) {
		System.out.printf("Debug: Turn %d Ended!\n",e.getTime());
	}

	@Override
	public void onTurnStarted(TurnStartedEvent e) {
		System.out.printf("Debug: Turn %d Started!\n",e.getTime());
	}
}
