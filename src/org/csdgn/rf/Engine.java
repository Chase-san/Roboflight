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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.csdgn.plugin.ClassInfo;

import roboflight.Robot;

/**
 * This class is responsible for running and managing battles.
 * 
 * @author Robert Maupin
 * 
 */
public class Engine {

	private final Executor battleExecutor;
	private BattleRunner current;
	private RobotDatabase database;

	public Engine() {
		// test directory
		database = new RobotDatabase();

		database.build();

		battleExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = Executors.defaultThreadFactory().newThread(r);
				thread.setName("BattleThread");
				return thread;
			}
		});
	}

	public boolean isBattleRunning() {
		return current != null && !current.isPaused();
	}
	
	public BattleRunner getCurrentBattle() {
		return current;
	}

	public RobotDatabase getDatabase() {
		return database;
	}

	public void startBattle(ClassInfo[] classes) {
		Robot[] robots = new Robot[classes.length];

		// load robots
		for(int i = 0; i < classes.length; ++i) {
			try {
				robots[i] = database.createRobotInstance(classes[i]);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		startBattle(robots);
	}

	public void startBattle(Robot[] robots) {
		stopCurrentBattle();
		current = BattleRunner.create(robots);
		battleExecutor.execute(current);
	}

	public void stopCurrentBattle() {
		if(current != null) {
			// shut it down to stop it quickly
			current.stop();
			current.setFPS(BattleRunner.START_FPS);

			// unpause it (or it will never stop)
			if(current.isPaused()) {
				current.setPaused(false);
			}

			current = null;
		}
	}

}
