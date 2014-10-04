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
package org.csdgn.rf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.csdgn.rf.peer.MissileImpl;
import org.csdgn.rf.peer.RobotPeerImpl;
import org.csdgn.rf.peer.events.BattleStartedEventImpl;
import org.csdgn.rf.peer.events.BulletHitEventImpl;
import org.csdgn.rf.peer.events.BulletImpl;
import org.csdgn.rf.peer.events.HitByBulletEventImpl;
import org.csdgn.rf.peer.events.MissileUpdateEventImpl;
import org.csdgn.rf.peer.events.RobotDeathEventImpl;
import org.csdgn.rf.peer.events.RobotUpdateEventImpl;
import org.csdgn.rf.peer.events.TurnEndedEventImpl;
import org.csdgn.rf.peer.events.TurnStartedEventImpl;

import roboflight.Robot;
import roboflight.util.Rules;
import roboflight.util.Utils;
import roboflight.util.Vector;

/**
 * This class manages and actually executes the battle between robots.
 * 
 * @author Robert Maupin
 */
public class BattleRunner implements Runnable {
	public static final int START_FPS = 20;

	public static BattleRunner create(Robot[] robot) {
		BattleRunner runner = new BattleRunner();

		for(Robot r : robot) {
			if(r == null) {
				continue;
			}
			RobotPeerImpl peer = new RobotPeerImpl();
			peer.setRobot(r);
			peer.setName(r.getClass().getName());
			peer.setTime(-1);

			// set a random starting position
			// TODO make sure the robots do not overlap on startup
			Vector p = peer.getPositionVector();
			do {
				p.set((Math.random() * 2 - 1) * Rules.BATTLEFIELD_RADIUS, (Math.random() * 2 - 1)
						* Rules.BATTLEFIELD_RADIUS, (Math.random() * 2 - 1) * Rules.BATTLEFIELD_RADIUS);
			} while(!Rules.isRobotInBattlefield(p));

			runner.robots.add(peer);
			r.setRobotPeer(peer);
		}

		return runner;
	}

	private double fps = START_FPS;
	private long tick = 0;
	private List<RobotPeerImpl> robots = new ArrayList<RobotPeerImpl>();
	private List<BulletImpl> bullets = new ArrayList<BulletImpl>();
	private List<MissileImpl> missiles = new ArrayList<MissileImpl>();

	private ExecutorService executors = Executors.newCachedThreadPool();

	private boolean running = false;
	private boolean paused = false;

	public List<BulletImpl> getBullets() {
		return bullets;
	}

	public List<MissileImpl> getMissiles() {
		return missiles;
	}

	public List<RobotPeerImpl> getRobotPeers() {
		return robots;
	}

	public boolean isPaused() {
		return paused;
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			while(paused) {
				try {
					Thread.sleep(250);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			long time = System.currentTimeMillis();

			if(tick == 0) {
				System.out.printf("BSE @ %d\n", tick);
				BattleStartedEventImpl bse = new BattleStartedEventImpl(tick);

				for(RobotPeerImpl rp : robots) {
					rp.addEvent(bse);
				}
			}

			TurnStartedEventImpl tse = new TurnStartedEventImpl(tick);
			for(RobotPeerImpl rp : robots) {
				rp.setTime(tick);

				// run onTurnStart
				if(rp.isEnabled() && rp.isAlive()) {
					rp.addEvent(tse);
				}
			}

			synchronized(this) {
				updateBullets();
				updateMissiles();
			}

			// create a randomized list for the updates, makes things a
			// little more fair
			List<RobotPeerImpl> robots2 = new ArrayList<RobotPeerImpl>(robots);
			Collections.shuffle(robots2);

			// We don't shuffle it for every separate robot, that would be
			// annoying.

			// run update
			for(RobotPeerImpl rp : robots) {
				if(!rp.isEnabled() || !rp.isAlive()) {
					continue;
				}

				for(RobotPeerImpl rp2 : robots2) {
					if(rp == rp2 || !rp2.isAlive()) {
						continue;
					}
					RobotUpdateEventImpl rue = new RobotUpdateEventImpl(tick, rp2.getName(), rp2.getVelocityVector(),
							rp2.getPositionVector(), rp2.getEnergy());
					rp.addEvent(rue);
				}
			}

			/* run on turn ended */
			TurnEndedEventImpl tee = new TurnEndedEventImpl(tick);
			for(RobotPeerImpl rp : robots) {
				if(!rp.isAlive()) {
					continue;
				}

				if(rp.isEnabled()) {
					rp.addEvent(tee);
				}
			}

			runRobots();

			// executors.execute();

			/* Update Robots */
			for(RobotPeerImpl rp : robots) {
				if(!rp.isAlive()) {
					continue;
				}

				// update robots
				rp.update();

				if(!rp.isEnabled()) {
					continue;
				}

				synchronized(this) {
					// try and fire all bullets
					BulletImpl b = rp.getBulletFired();
					if(b != null) {
						b.setActive(true);
						bullets.add(b);
					}

					// try and fire all missiles
					MissileImpl m = rp.getMissileFired();
					if(m != null) {
						m.setActive(true);
						missiles.add(m);
					}
				}

				if(rp.getEnergy() == 0) {
					rp.disable();
				}
			}

			// randomize robot list?

			++tick;

			time = (long) ((1000.0 / fps) - (System.currentTimeMillis() - time));
			try {
				Thread.sleep(time > 1 ? time : 1);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void runRobots() {
		/* run all robots events */
		ArrayList<Future<?>> future = new ArrayList<Future<?>>();
		for(RobotPeerImpl rp : robots) {
			future.add(executors.submit(rp));
		}
		/* 5 seconds till kill */
		long runtime = System.currentTimeMillis();
		while(System.currentTimeMillis() - runtime < 5000) {
			boolean wait = false;
			for(Future<?> f : future) {
				/* check if done */
				if(!f.isDone()) {
					wait = true;
				}
			}
			if(!wait) {
				break;
			}
		}
		for(Future<?> f : future) {
			if(!f.isDone()) {
				f.cancel(true);
			}
		}
		for(RobotPeerImpl rp : robots) {
			rp.clearQueue();
		}
	}

	public void setFPS(double fps) {
		this.fps = fps;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void stop() {
		running = false;
	}

	private void updateBullets() {
		// update all bullets
		Iterator<BulletImpl> bit = bullets.iterator();
		while(bit.hasNext()) {
			BulletImpl b = bit.next();

			if(!b.isActive()) {
				// remove the bullet
				bit.remove();
				// TODO inform robot the bullet has gone out of bounds
				continue;
			}

			Vector p = b.getPositionVector();

			RobotPeerImpl hit = null;

			// nearest point on line segment to sphere
			for(RobotPeerImpl rp : robots) {
				if(b.isOwner(rp) || !rp.isAlive()) {
					continue;
				}
				if(rp.getPositionVector().distanceSq(p) < Rules.ROBOT_RADIUS * Rules.ROBOT_RADIUS) {
					hit = rp;
					break;
				}
			}

			if(hit != null) {
				b.setActive(false);
				bit.remove();

				// generate hitbybullet and bullethit events

				b.getOwner().addEvent(new BulletHitEventImpl(tick, b, hit.getName()));

				hit.addEvent(new HitByBulletEventImpl(tick, b, b.getOwner().getName()));

				hit.setEnergy(hit.getEnergy() - Rules.BULLET_DAMAGE);
				if(hit.getEnergy() <= 0) {
					// generate death event
					RobotDeathEventImpl e = new RobotDeathEventImpl(tick, hit.getName());

					// inform all robots about the robot death
					for(RobotPeerImpl rp : robots) {
						if(rp.isAlive()) {
							rp.addEvent(e);
						}
					}

					hit.kill();
				}
				RobotPeerImpl owner = b.getOwner();
				owner.setEnergy(owner.getEnergy() + Rules.BULLET_ONHIT_GAIN);

				continue;
			}

			b.update();
		}
	}

	private void updateMissiles() {
		// missiles unlike robots die on the edge of the field
		Iterator<MissileImpl> mit = missiles.iterator();
		while(mit.hasNext()) {
			MissileImpl m = mit.next();

			if(!m.isActive()) {
				// remove the missile
				mit.remove();
				// TODO inform robot the missile has gone out of bounds
				continue;
			}

			Vector p = m.getPositionVector();

			RobotPeerImpl hit = null;

			// attempt to arm it
			if(!m.isArmed()) {
				if(m.getOwner().getPositionVector().distanceSq(p) > Rules.ROBOT_RADIUS * Rules.ROBOT_RADIUS
						- Rules.MISSILE_RADIUS * Rules.MISSILE_RADIUS) {
					m.setArmed(true);
				}
			}

			// check collision
			if(m.isArmed()) {
				for(RobotPeerImpl rp : robots) {
					if(!rp.isAlive()) {
						continue;
					}
					if(rp.getPositionVector().distanceSq(p) < Rules.ROBOT_RADIUS * Rules.ROBOT_RADIUS
							- Rules.MISSILE_RADIUS * Rules.MISSILE_RADIUS) {
						hit = rp;
						break;
					}
				}
			}

			if(hit != null) {
				m.setActive(false);
				mit.remove();

				hit.setEnergy(hit.getEnergy() - Rules.MISSILE_DAMAGE);
				if(hit.getEnergy() <= 0) {
					// generate death event
					RobotDeathEventImpl e = new RobotDeathEventImpl(tick, hit.getName());

					// inform all robots about the robot death
					for(RobotPeerImpl rp : robots) {
						if(rp.isAlive()) {
							rp.addEvent(e);
						}
					}

					hit.kill();
				}
				RobotPeerImpl owner = m.getOwner();
				owner.setEnergy(owner.getEnergy() + Rules.MISSILE_ONHIT_GAIN);

				continue;
			} else {
				// update robots with the missiles information
				// only if a line between them is unbroken (by other robots)
				if(m.getVelocityVector().lengthSq() > 0) {
					for(RobotPeerImpl rp : robots) {

						boolean blocked = false;
						for(RobotPeerImpl test : robots) {
							if(test == rp) {
								continue;
							}
							if(Utils.intersectLineSphere(rp.getPositionVector(), m.getPositionVector(),
									test.getPositionVector(), Rules.ROBOT_RADIUS)) {
								blocked = true;
								break;
							}
						}

						if(!blocked) {
							// send update
							MissileUpdateEventImpl mue = new MissileUpdateEventImpl(tick, m.getOwner().getName(),
									m.getVelocity(), m.getPosition(), m.getOwner() == rp);

							rp.addEvent(mue);

						}
					}
				}
			}

			m.update();
		}
	}
}
