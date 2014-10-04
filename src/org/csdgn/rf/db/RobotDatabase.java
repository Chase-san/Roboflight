/**
 * Copyright (c) 2013 Robert Maupin
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
package org.csdgn.rf.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import roboflight.Robot;

/**
 * Manages the robots and their locations.
 * @author Robert Maupin
 */
public class RobotDatabase {
	private static ArrayList<File> getClassList(final File directory) {
		final ArrayList<File> list = new ArrayList<File>();
		if(!directory.exists()) return list;
		for(final File file : directory.listFiles()) {
			if(file.isDirectory()) {
				list.addAll(getClassList(file));
				continue;
			}
			if(file.getName().endsWith(".class")) list.add(file);
		}
		return list;
	}
	
	private final ArrayList<ClassInfo> robots = new ArrayList<ClassInfo>();
	private final ArrayList<String> directories = new ArrayList<String>();

	public Robot createRobotInstance(final ClassInfo info) throws IOException, ReflectiveOperationException {
		//I think it goes something like this
		final URLClassLoader loader = new URLClassLoader(
				new URL[] { info.parent.toURI().toURL() },
				ClassLoader.getSystemClassLoader());
		
		//For when we go to implement the output change
		
//		Class<?> system = loader.loadClass("java.lang.System");
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PrintStream out = new PrintStream(baos, true, "UTF-8");
//		system.getMethod("setOut", PrintStream.class).invoke(null, out);
//		system.getMethod("setErr", PrintStream.class).invoke(null, out);
		
		Class<?> robot = loader.loadClass(info.toString());
		
		loader.close();
		
		return (Robot)robot.newInstance();
	}
	
	public void addDirectory(final String dir) {
		directories.add(dir);
	}
	
	public List<ClassInfo> getRobotList() {
		return robots;
	}

	public void rebuildDatabase() {
		robots.clear();
		final ArrayList<ClassInfo> possibleRobots = new ArrayList<ClassInfo>();
		final ArrayList<ClassInfo> other = new ArrayList<ClassInfo>();
		for(final String directoryName : directories) {
			final File dir = new File(directoryName);
			if(!dir.isDirectory()) continue;
			// build class list
			for(final File f : getClassList(dir)) {
				ClassInfo info = null;
				try {
					info = ClassInfo.getClassInfo(new BufferedInputStream(new FileInputStream(f)));
					info.parent = dir;
				} catch(final IOException e) {
					e.printStackTrace();
				}
				if(info == null) continue;
				boolean rootClass = false;
				if("roboflight/BasicRobot".equals(info.superName)) {
					rootClass = true;
					possibleRobots.add(info);
				} else for(final String str : info.interfaceNames)
					if("roboflight/Robot".equals(str)) {
						rootClass = true;
						possibleRobots.add(info);
						break;
					}
				
				if(!rootClass) other.add(info);
			}
			if(other.size() > 0) {
				int size = -1;
				while(size != possibleRobots.size()) {
					size = possibleRobots.size();
					final Iterator<ClassInfo> it = other.iterator();
					while(it.hasNext()) {
						final ClassInfo info = it.next();
						final String s = info.superName;
						if(s != null) // check possible robots for this class
							for(final ClassInfo root : possibleRobots) {
								if(root.isFinal) continue;
								if(root.thisName.equals(s)) {
									possibleRobots.add(info);
									it.remove();
									break;
								}
							}
					}
				}
			}
			// add all non-abstract classes
			for(final ClassInfo info : possibleRobots) {
				if(!info.isAbstract && !info.isInterface && info.isPublic) {
					//TODO get all required classes for each of these robots
					//not sure if I need those yet, but I will find out soon enough
					robots.add(info);
				}
			}
		}
	}
}
