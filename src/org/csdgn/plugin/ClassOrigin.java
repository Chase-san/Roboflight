package org.csdgn.plugin;

import java.io.File;
import java.util.HashMap;

public class ClassOrigin {
	public final boolean inJar;
	public final File file;
	public final String path;
	public final HashMap<String,ClassInfo> database;
	
	public ClassOrigin(HashMap<String,ClassInfo> database, File file) {
		this.database = database;
		this.inJar = false;
		this.file = file;
		this.path = null;
	}
	
	public ClassOrigin(HashMap<String,ClassInfo> database, File file, String path) {
		this.database = database;
		this.inJar = true;
		this.file = file;
		this.path = path;
	}
}
