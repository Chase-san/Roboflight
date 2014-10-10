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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.csdgn.plugin.ClassInfo;
import org.csdgn.plugin.ClassOrigin;
import org.csdgn.plugin.PluginClassLoader;
import org.csdgn.plugin.PluginService;

import roboflight.BasicRobot;
import roboflight.Robot;

/**
 * Manages the robots and their locations.
 * 
 * @author Robert Maupin
 */
public class RobotDatabase {

	private PluginService service;
	private ArrayList<ClassInfo> robots;

	public RobotDatabase() {
		service = new PluginService();
		robots = new ArrayList<ClassInfo>();

		service.addDirectory(new File("robots"));
		service.addClass(Robot.class);
		service.addClass(BasicRobot.class);
	}

	public void build() {
		try {
			service.build(true, true);
		} catch(IOException e) {
			e.printStackTrace();
		}

		for(ClassInfo info : service.getList()) {
			if(info.isAbstract || !info.isPublic) {
				continue;
			}
			robots.add(info);
		}
	}

	public Robot createRobotInstance(final ClassInfo info) throws IOException, ReflectiveOperationException {
		final PluginClassLoader loader = new PluginClassLoader(ClassLoader.getSystemClassLoader());
		final ClassOrigin origin = info.getOrigin();

		if(origin.inJar) {
			loader.defineJar(info);
		} else {
			Set<ClassInfo> depends = PluginService.getDependancies(info);
			for(ClassInfo dep : depends) {
				loader.defineClass(dep);
			}
		}
		
		loader.whitelist("roboflight.**");
		loader.whitelist("java.awt.Color");
		loader.whitelist("java.awt.Point");
		loader.whitelist("java.awt.Rectangle");
		loader.whitelist("java.awt.geom.**");
		loader.whitelist("java.lang.**");
		loader.blacklist("java.lang.SecurityManager");
		loader.blacklist("java.lang.Thread");
		loader.blacklist("java.lang.ThreadGroup");
		loader.blacklist("java.lang.ThreadLocal");
		loader.blacklist("java.lang.Runtime");
		loader.blacklist("java.lang.RuntimePermission");
		loader.blacklist("java.lang.ProcessBuilder");
		loader.blacklist("java.lang.Process");
		/* until I can find a neater way to handle it... */
		loader.blacklist("java.lang.System");
		loader.blacklist("java.lang.InheritableThreadLocal");
		loader.blacklist("java.lang.instrument.**");
		loader.blacklist("java.lang.invoke.**");
		loader.blacklist("java.lang.management.**");
		loader.blacklist("java.lang.ref.**");
		/* reflection is just a boatload of trouble */
		loader.blacklist("java.lang.reflect.**");
		loader.whitelist("java.math.**");
		loader.whitelist("java.util.**");
		loader.whitelist("java.util.concurrent.**");
		loader.blacklist("java.util.jar.**");
		loader.blacklist("java.util.logging.**");
		loader.blacklist("java.util.prefs.**");
		loader.blacklist("java.util.spi.**");
		loader.blacklist("java.util.zip.**");

		// For when we go to implement the output change (not sure if this will
		// work though)

		// Class<?> system = loader.loadClass("java.lang.System");
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// PrintStream out = new PrintStream(baos, true, "UTF-8");
		// system.getMethod("setOut", PrintStream.class).invoke(null, out);
		// system.getMethod("setErr", PrintStream.class).invoke(null, out);

		Class<?> robot = loader.loadClass(info.toString());

		return (Robot) robot.newInstance();
	}

	public ArrayList<ClassInfo> getRobotList() {
		return robots;
	}

}
