/**
 * Copyright (c) 2013-2014 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package roboflight;

import java.io.PrintWriter;

import roboflight.events.BattleStartedEvent;
import roboflight.events.BulletHitEvent;
import roboflight.events.BulletMissEvent;
import roboflight.events.HitByBulletEvent;
import roboflight.events.HitByMissileEvent;
import roboflight.events.HitWallEvent;
import roboflight.events.MissileHitEvent;
import roboflight.events.MissileMissEvent;
import roboflight.events.MissileUpdateEvent;
import roboflight.events.RobotDeathEvent;
import roboflight.events.RobotUpdateEvent;
import roboflight.events.TurnEndedEvent;
import roboflight.events.TurnStartedEvent;

/**
 * The main interface you need to extend to create a robot.
 * 
 * @author Robert Maupin
 */
public interface Robot {
	/**
	 * This method is called at the start of a battle
	 */
	public void onBattleStarted(BattleStartedEvent e);

	/**
	 * This method is called when the robot has hit another robot with a bullet.
	 */
	public void onBulletHit(BulletHitEvent e);

	/**
	 * This method is called when one of the robot's bullets has gone out of
	 * bounds by hitting the wall.
	 */
	public void onBulletMiss(BulletMissEvent e);

	/**
	 * This method is called when the robot has been hit by a bullet.
	 */
	public void onHitByBullet(HitByBulletEvent e);

	/**
	 * This method is called when the robot has been hit by a missile.
	 */
	public void onHitByMissile(HitByMissileEvent e);

	/**
	 * This method is called when the robot hits the outer wall.
	 */
	public void onHitWall(HitWallEvent e);

	/**
	 * This method is called when the robot has hit another robot with a
	 * missile.
	 */
	public void onMissileHit(MissileHitEvent e);

	/**
	 * This method is called when one of the robot's missiles has gone out of
	 * bounds by hitting the wall.
	 */
	public void onMissileMiss(MissileMissEvent e);

	/**
	 * This method is called to supply information about a missile.
	 */
	public void onMissileUpdate(MissileUpdateEvent e);

	/**
	 * This method is called when the robot dies. May include this robot as
	 * well.
	 */
	public void onRobotDeath(RobotDeathEvent e);

	/**
	 * This method is called to supply information about another opponent. It is
	 * called multiple times by the engine to inform you about multiple
	 * opponents.
	 */
	public void onRobotUpdate(RobotUpdateEvent e);

	/**
	 * This method is called at the end of a turn.
	 */
	public void onTurnEnded(TurnEndedEvent e);

	/**
	 * This method is called at the start of a turn.
	 */
	public void onTurnStarted(TurnStartedEvent e);

	/**
	 * Used to set the RobotPeer for this robot. There shouldn't be a need for a
	 * robot to call this method itself.
	 * 
	 * @param peer
	 *            The RobotPeer set by the game
	 */
	public void setRobotPeer(RobotPeer peer);

	/**
	 * Used to set the PrintWriter output for this robot. There shouldn't be a
	 * need for a robot to call this method itself.
	 * 
	 * @param out
	 *            The PrintWriter set by the game.
	 */
	public void setOut(PrintWriter out);
}
