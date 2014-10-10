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

import java.awt.peer.RobotPeer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.csdgn.plugin.ClassInfo;
import org.csdgn.plugin.ClassOrigin;
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
		service.addClass(RobotPeer.class);
		service.addClass(BasicRobot.class);
	}

	public ArrayList<ClassInfo> getRobotList() {
		return robots;
	}

	public void build() {
		try {
			service.build();
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

	private URL[] getClassRequirements(ClassInfo info) throws IOException {
		/*
		 * later on, using this, we can determine if the robot includes any
		 * classes we don't want them using which is better then a security
		 * manager in many respects.. white list anything in the jar, a few JRE
		 * stuff, and the roboflight api stuff
		 * 
		 * But for now...
		 */

		ArrayList<URL> urls = new ArrayList<URL>();
		ArrayList<ClassInfo> depends = PluginService.getDependancies(info);

		for(ClassInfo depinfo : depends) {
			urls.add(depinfo.getOrigin().file.toURI().toURL());
		}

		return urls.toArray(new URL[urls.size()]);
	}

	public Robot createRobotInstance(final ClassInfo info) throws IOException, ReflectiveOperationException {
		final URLClassLoader loader;
		final ClassOrigin origin = info.getOrigin();
		if(origin.inJar) {
			loader = new URLClassLoader(new URL[] { origin.file.toURI().toURL() }, ClassLoader.getSystemClassLoader());
		} else {
			loader = new URLClassLoader(getClassRequirements(info), ClassLoader.getSystemClassLoader());
		}

		// For when we go to implement the output change

		// Class<?> system = loader.loadClass("java.lang.System");
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// PrintStream out = new PrintStream(baos, true, "UTF-8");
		// system.getMethod("setOut", PrintStream.class).invoke(null, out);
		// system.getMethod("setErr", PrintStream.class).invoke(null, out);

		Class<?> robot = loader.loadClass(info.toString());

		loader.close();

		return (Robot) robot.newInstance();
	}

}
