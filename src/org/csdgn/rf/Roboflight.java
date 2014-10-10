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
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.csdgn.rf.gui.MainWindow;
import org.csdgn.utils.Files;

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
	
	private static final String NATIVE_DIR = "libs/native";
	private static final String MAVEN_BASE = "http://mirrors.ibiblio.org/maven2/org/lwjgl/lwjgl/";
	private static final String MAVEN_METADATA = MAVEN_BASE+"lwjgl/maven-metadata.xml";
	private static final String MAVEN_LWJGL = MAVEN_BASE+"lwjgl/{VERSION}/lwjgl-{VERSION}.jar";
	private static final String MAVEN_UTIL = MAVEN_BASE+"lwjgl_util/{VERSION}/lwjgl_util-{VERSION}.jar";
	private static final String MAVEN_NATIVE_WIN = MAVEN_BASE+"lwjgl-platform/{VERSION}/lwjgl-platform-{VERSION}-natives-windows.jar";
	private static final String MAVEN_NATIVE_NUX = MAVEN_BASE+"lwjgl-platform/{VERSION}/lwjgl-platform-{VERSION}-natives-linux.jar";
	private static final String MAVEN_NATIVE_OSX = MAVEN_BASE+"lwjgl-platform/{VERSION}/lwjgl-platform-{VERSION}-natives-osx.jar";
	
	private static void downloadLWJGL() throws IOException {
		String metadata = new String(Files.getAndClose(new URL(MAVEN_METADATA).openStream()),StandardCharsets.UTF_8);
		String version = metadata.substring(metadata.indexOf("<latest>")+8,metadata.indexOf("</latest>"));
		
		File file = new File("libs/lwjgl.jar");
		if(!file.exists()) {
			byte[] data = Files.getAndClose(new URL(MAVEN_LWJGL.replace("{VERSION}", version)).openStream());
			Files.setFileContents(file, data);
		}
		file = new File("libs/lwjgl_util.jar");
		if(!file.exists()) {
			byte[] data = Files.getAndClose(new URL(MAVEN_UTIL.replace("{VERSION}", version)).openStream());
			Files.setFileContents(file, data);
		}
		
		file = new File(NATIVE_DIR);

		if(!file.exists()) {
			URL url = null;
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
				url = new URL(MAVEN_NATIVE_OSX.replace("{VERSION}", version));
			} else if(os.indexOf("win") >= 0) {
				url = new URL(MAVEN_NATIVE_WIN.replace("{VERSION}", version));
			} else if(os.indexOf("nux") >= 0) {
				url = new URL(MAVEN_NATIVE_NUX.replace("{VERSION}", version));
			}
			if(url != null) {
				file.mkdir();
				File jar = new File(file,"native.jar");
				byte[] data = Files.getAndClose(url.openStream());
				Files.setFileContents(jar, data);
				
				/* now extract the natives */
				ZipFile zip = new ZipFile(jar);
				
				Enumeration<? extends ZipEntry> en = zip.entries();
				while(en.hasMoreElements()) {
					ZipEntry e = en.nextElement();
					String name = e.getName();
					if(name.contains("lwjgl")) {
						data = Files.getAndClose(zip.getInputStream(e));
						Files.setFileContents(new File(file, name.substring(name.lastIndexOf('/')+1)), data);
					}
				}
				
				zip.close();
				
				jar.delete();
			}
		}
	}

	private static void loadLibraries() throws Exception {
		/* load all software libraries */
		int lwjgl = 0;
		for(File file : new File("libs").listFiles()) {
			String name = file.getName();
			if(!name.endsWith(".jar")) {
				continue;
			}
			if(name.contains("lwjgl")) {
				++lwjgl;
			}
			addSoftwareLibrary(file);
		}
		
		if(lwjgl != 2 || !new File(NATIVE_DIR).exists()) {
			int opt = JOptionPane.showConfirmDialog(null, "The LWJGL Library was incomplete or not found,\n" + ARTIFACT_NAME +
					" requires this library to display the game.\n"
					+ "Do you want to automatically download it?", "Library Not Found", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE);
			if(opt == JOptionPane.YES_OPTION) {
				downloadLWJGL();
				loadLibraries();
			} else {
				System.exit(0);
			}
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
			/* TODO add NOGUI command line switch and stuff */
			System.setProperty("org.lwjgl.librarypath", new File(NATIVE_DIR).getAbsolutePath());
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
