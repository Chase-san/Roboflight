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
package roboflight.core;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.UIManager;
import roboflight.core.gui.MainWindow;

/**
 * The Main Roboflight class.
 * @author Robert Maupin
 *
 */
public class Roboflight {
	public static final String ARTIFACT_NAME = "Roboflight";
	public static final String ARTIFACT_VERSION = "0.4 MS1";
	public static final String ARTIFACT_TITLE = Roboflight.ARTIFACT_NAME + " " + Roboflight.ARTIFACT_VERSION;
	
	public static void main(String[] args) {
		try {
			Locale.setDefault(Locale.US);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) { /* Failure is unimportant. */ }
		
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
