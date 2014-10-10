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

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import javax.swing.UIManager;

import org.csdgn.rf.gui.MainWindow;

/**
 * The Main Roboflight class.
 * 
 * @author Robert Maupin
 * 
 */
public class Roboflight {
	public static final String ARTIFACT_NAME = "Roboflight";
	public static final String ARTIFACT_VERSION = "MS1";
	public static final String ARTIFACT_TITLE = Roboflight.ARTIFACT_NAME + " " + Roboflight.ARTIFACT_VERSION;

	private static void addSoftwareLibrary(File file) throws Exception {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file.toURI().toURL() });
	}

	public static boolean isDevelopmentEnvironment() {
		if(System.getenv("eclipse") == null) {
			return false;
		}
		return true;
	}

	private static void loadLibraries() throws Exception {
		/* load all software libraries */
		for(File file : new File("libs").listFiles()) {
			if(!file.getName().endsWith("jar")) {
				continue;
			}
			addSoftwareLibrary(file);
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			Locale.setDefault(Locale.US);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) { /* Failure is somewhat unimportant. */
		}

		if(!isDevelopmentEnvironment()) {
			// We only need to manually load the libraries if we are not
			// in development, as the development system takes care of that.
			loadLibraries();
		}

		final Engine engine = new Engine();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow window = new MainWindow();
				window.setEngine(engine);
				window.setVisible(true);
				window.start();
			}
		});

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
