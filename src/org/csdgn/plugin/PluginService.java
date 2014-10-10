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
package org.csdgn.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This gets all the robots and so forth. It is actually generic, so I can use
 * it anywhere
 * 
 * @author Robert Maupin
 */
public class PluginService {
	private static final String JAR_EXT = ".jar";
	private static final String CLASS_EXT = ".class";

	private ArrayList<String> interfaces;
	private ArrayList<String> classes;
	private ArrayList<File> exploreDirs;
	private HashMap<String, ClassInfo> looseDB;
	private ArrayList<HashMap<String, ClassInfo>> jarDBs;
	private ArrayList<ClassInfo> list;

	public PluginService() {
		interfaces = new ArrayList<String>();
		classes = new ArrayList<String>();
		looseDB = new HashMap<String, ClassInfo>();
		exploreDirs = new ArrayList<File>();
		list = new ArrayList<ClassInfo>();
	}

	public void addClass(Class<?> cls) {
		if(cls.isArray() || cls.isAnnotation() || cls.isPrimitive()) {
			return;
		}
		if(cls.isInterface()) {
			interfaces.add(cls.getName().replace('.', '/'));
		} else {
			classes.add(cls.getName().replace('.', '/'));
		}
	}

	public void addDirectory(File file) {
		if(file == null || !file.isDirectory()) {
			return;
		}
		exploreDirs.add(file);
	}

	public void build() throws IOException {
		HashSet<File> explored = new HashSet<File>();
		for(File file : exploreDirs) {
			explore(file, explored, true, true, true, true);
		}
		/*
		 * we can't quantify these... so we just throw them all into the same
		 * box
		 */
		buildFromDatabase(looseDB);
		for(int i = 0; i < jarDBs.size(); ++i) {
			buildFromDatabase(jarDBs.get(i));
		}
	}

	private void buildFromDatabase(HashMap<String, ClassInfo> db) {
		for(ClassInfo info : db.values()) {
			if(checkClass(db, info)) {
				list.add(info);
			}
		}
	}

	private boolean checkClass(HashMap<String, ClassInfo> db, ClassInfo info) {
		if(info == null) {
			return false;
		}

		/* check extends */
		for(String cls : classes) {
			if(cls.equals(info.superName)) {
				return true;
			}
		}

		/* check implements */
		String[] clsImpl = info.getInterfaceNames();
		for(String ref : interfaces) {
			for(String impl : clsImpl) {
				if(impl.equals(ref)) {
					return true;
				}
			}
		}

		/* check parent data */
		if(checkClass(db, db.get(info.superName))) {
			return true;
		}

		/* check interface data */
		for(String impl : clsImpl) {
			if(checkClass(db, db.get(impl))) {
				return true;
			}
		}

		return false;
	}

	private void explore(File base, HashSet<File> explored, boolean subdir, boolean isRoot, boolean getClass,
			boolean getJar) throws IOException {
		if(explored.contains(base)) {
			return;
		}
		if(base.isDirectory()) {
			explored.add(base);
			if(subdir || isRoot) {
				for(File file : base.listFiles()) {
					explore(file, explored, subdir, false, getClass, getJar);
				}
			}
		} else if(base.isFile()) {
			String name = base.getName();
			if(getClass && name.endsWith(CLASS_EXT)) {
				FileInputStream fis = new FileInputStream(base);
				ClassInfo info = new ClassInfo(fis);
				fis.close();
				info.setOrigin(new ClassOrigin(looseDB, base));
				looseDB.put(info.thisName, info);
			} else if(getJar && name.endsWith(JAR_EXT)) {
				exploreJar(base);
			}
		}
	}

	private void exploreJar(File jar) throws IOException {
		HashMap<String, ClassInfo> jardb = new HashMap<String, ClassInfo>();
		ZipFile zip = new ZipFile(jar, ZipFile.OPEN_READ);
		Enumeration<? extends ZipEntry> en = zip.entries();
		while(en.hasMoreElements()) {
			ZipEntry e = en.nextElement();
			if(e.getName().endsWith(CLASS_EXT)) {
				InputStream in = zip.getInputStream(e);
				ClassInfo info = new ClassInfo(in);
				info.setOrigin(new ClassOrigin(jardb, jar, e.getName()));
				jardb.put(info.thisName, info);
				in.close();
			}
		}
		zip.close();

		jarDBs.add(jardb);
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void reset() {
		classes.clear();
		interfaces.clear();
		looseDB.clear();
	}
}
