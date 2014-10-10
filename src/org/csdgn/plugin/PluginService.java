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
	private ArrayList<HashMap<String, ClassInfo>> looseDBs;
	private ArrayList<HashMap<String, ClassInfo>> jarDBs;
	private ArrayList<ClassInfo> list;

	public PluginService() {
		interfaces = new ArrayList<String>();
		classes = new ArrayList<String>();
		exploreDirs = new ArrayList<File>();
		list = new ArrayList<ClassInfo>();
		looseDBs = new ArrayList<HashMap<String, ClassInfo>>();
		jarDBs = new ArrayList<HashMap<String, ClassInfo>>();
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
			HashMap<String, ClassInfo> db = new HashMap<String, ClassInfo>();
			explore(file, explored, db, true, true, true, true);
			looseDBs.add(db);
		}
		for(int i = 0; i < looseDBs.size(); ++i) {
			buildFromDatabase(looseDBs.get(i));
		}
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

	private void explore(File base, HashSet<File> explored, HashMap<String, ClassInfo> loosedb, boolean subdir,
			boolean isRoot, boolean getClass, boolean getJar) throws IOException {
		if(explored.contains(base)) {
			return;
		}
		if(base.isDirectory()) {
			explored.add(base);
			if(subdir || isRoot) {
				for(File file : base.listFiles()) {
					explore(file, explored, loosedb, subdir, false, getClass, getJar);
				}
			}
		} else if(base.isFile()) {
			String name = base.getName();
			if(getClass && name.endsWith(CLASS_EXT)) {
				FileInputStream fis = new FileInputStream(base);
				ClassInfo info = new ClassInfo(fis);
				fis.close();
				info.setOrigin(new ClassOrigin(loosedb, base.getAbsoluteFile()));
				loosedb.put(info.thisName, info);
			} else if(getJar && name.endsWith(JAR_EXT)) {
				exploreJar(base);
			}
		}
	}

	private void exploreJar(File jar) throws IOException {
		HashMap<String, ClassInfo> db = new HashMap<String, ClassInfo>();
		ZipFile zip = new ZipFile(jar, ZipFile.OPEN_READ);
		Enumeration<? extends ZipEntry> en = zip.entries();
		while(en.hasMoreElements()) {
			ZipEntry e = en.nextElement();
			if(e.getName().endsWith(CLASS_EXT)) {
				InputStream in = zip.getInputStream(e);
				ClassInfo info = new ClassInfo(in);
				info.setOrigin(new ClassOrigin(db, jar.getAbsoluteFile(), e.getName()));
				db.put(info.thisName, info);
				in.close();
			}
		}
		zip.close();

		jarDBs.add(db);
	}

	private static void depends(ClassInfo info, HashMap<String, ClassInfo> db, ArrayList<ClassInfo> list) {
		ClassInfo nfo = db.get(info.superName);
		if(nfo != null) {
			depends(nfo, db, list);
			list.add(nfo);
		}
		for(String inter : info.getInterfaceNames()) {
			nfo = db.get(inter);
			if(nfo != null) {
				depends(nfo, db, list);
				list.add(nfo);
			}
		}
		for(String ref : info.getClassReferenceNames()) {
			nfo = db.get(ref);
			if(nfo != null) {
				depends(nfo, db, list);
				list.add(nfo);
			}
		}
	}

	public static ArrayList<ClassInfo> getDependancies(ClassInfo info) {
		ArrayList<ClassInfo> list = new ArrayList<ClassInfo>();
		depends(info, info.getOrigin().database, list);
		return list;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void reset() {
		classes.clear();
		interfaces.clear();
		looseDBs.clear();
		jarDBs.clear();
		list.clear();
	}
}
