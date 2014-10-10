/**
 * Copyright (c) 2014 Robert Maupin
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
import java.util.HashMap;

/**
 * A class to list the origin of a given class.
 * 
 * @author Robert Maupin
 * 
 */
public class ClassOrigin {
	public final boolean inJar;
	public final File file;
	public final String path;
	public final HashMap<String, ClassInfo> database; /* TODO make access to this immutable somehow */

	public ClassOrigin(HashMap<String, ClassInfo> database, File file) {
		this.database = database;
		inJar = false;
		this.file = file;
		path = null;
	}

	public ClassOrigin(HashMap<String, ClassInfo> database, File file, String path) {
		this.database = database;
		inJar = true;
		this.file = file;
		this.path = path;
	}
}
