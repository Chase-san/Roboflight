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
package org.csdgn.rf.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.WindowConstants;

import roboflight.util.Rules;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.csdgn.rf.BattleRunner;
import org.csdgn.rf.Engine;
import org.csdgn.rf.Roboflight;
import org.csdgn.rf.db.ClassInfo;
import org.csdgn.rf.peer.RobotPeerImpl;

/**
 * Main display window for the game. By the way, I hate GUIs.
 * 
 * @author Robert Maupin
 */
public class MainWindow extends JFrame {
	private class MenuController implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String cmd = e.getActionCommand();
			if("exit".equals(cmd)) {
				dispose();
			} else if("new".equals(cmd)) {
				dialog.setVisible(true);
			} else if("about".equals(cmd)) {
			}
		}
	}

	private BattleDialog dialog;
	private static final long serialVersionUID = 1510059463358895508L;
	private final RenderDisplay display;
	private ArrayList<JLabel> stats = new ArrayList<JLabel>();
	private Engine engine;
	private Timer timer;
	private JPanel sidePanel;
	private JSlider fpsSlider;
	private JButton btnPause;
	private JButton btnStop;

	public MainWindow() {
		super(Roboflight.ARTIFACT_TITLE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
		setResizable(false);
		display = new RenderDisplay();
		getContentPane().add(display, BorderLayout.CENTER);
		sidePanel = new JPanel();
		sidePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		getContentPane().add(sidePanel, BorderLayout.EAST);
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

		dialog = new BattleDialog(this);

		{
			final JCheckBox chckbxRobotLocators = new JCheckBox("Robot Locators");
			chckbxRobotLocators.setSelected(RenderDisplay.DRAW_ROBOT_LOCATORS);
			chckbxRobotLocators.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					RenderDisplay.DRAW_ROBOT_LOCATORS = chckbxRobotLocators.isSelected();
				}
			});
			sidePanel.add(chckbxRobotLocators);
			final JCheckBox chckbxAxis = new JCheckBox("Axes");
			chckbxAxis.setSelected(RenderDisplay.DRAW_AXIS);
			chckbxAxis.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					RenderDisplay.DRAW_AXIS = chckbxAxis.isSelected();
				}
			});
			sidePanel.add(chckbxAxis);
			final JCheckBox chckbxGrid = new JCheckBox("Grid");
			chckbxGrid.setSelected(RenderDisplay.DRAW_GRID);
			chckbxGrid.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					RenderDisplay.DRAW_GRID = chckbxGrid.isSelected();
				}
			});
			sidePanel.add(chckbxGrid);
		}

		getContentPane().add(createControlPanel(), BorderLayout.SOUTH);

		setJMenuBar(createMenu());
		pack();
	}

	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();

		controlPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BattleRunner runner = engine.getCurrentBattle();
				if(runner != null) {
					if(runner.isPaused()) {
						runner.setPaused(false);
						btnPause.setText("Pause");
					} else {
						runner.setPaused(true);
						btnPause.setText("Resume");
					}
				}
			}
		});
		btnPause.setEnabled(false);
		controlPanel.add(btnPause);
		controlPanel.add(Box.createHorizontalStrut(4));

		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPause.setEnabled(false);
				btnStop.setEnabled(false);
				fpsSlider.setEnabled(false);

				resetSidebarStats();

				engine.stopCurrentBattle();
			}
		});
		btnStop.setEnabled(false);
		controlPanel.add(btnStop);
		controlPanel.add(Box.createHorizontalStrut(4));

		JLabel lblFps = new JLabel("FPS");
		lblFps.setEnabled(false);
		controlPanel.add(lblFps);

		fpsSlider = new JSlider();
		fpsSlider.setMaximum(100);
		fpsSlider.setEnabled(false);
		fpsSlider.setPaintLabels(true);
		fpsSlider.setSnapToTicks(true);
		fpsSlider.setPaintTicks(true);
		fpsSlider.setMajorTickSpacing(10);
		fpsSlider.setValue(20);
		fpsSlider.setMinorTickSpacing(2);
		fpsSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!fpsSlider.getValueIsAdjusting()) {
					BattleRunner battle = engine.getCurrentBattle();
					if(battle != null) {
						battle.setFPS(fpsSlider.getValue());
					}
				}
			}
		});
		controlPanel.add(fpsSlider);

		controlPanel.add(Box.createHorizontalGlue());

		return controlPanel;
	}

	private JMenuBar createMenu() {
		final MenuController ac = new MenuController();
		final JMenuBar menuBar = new JMenuBar();
		final JMenu mnbattle = new JMenu("Battle");
		mnbattle.setMnemonic('B');
		menuBar.add(mnbattle);
		final JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setMnemonic('N');
		mntmNew.setActionCommand("new");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmNew.addActionListener(ac);
		mnbattle.add(mntmNew);
		mnbattle.addSeparator();
		final JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic('x');
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mntmExit.setActionCommand("exit");
		mntmExit.addActionListener(ac);
		mnbattle.add(mntmExit);
		final JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		final JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setMnemonic('A');
		mntmAbout.setActionCommand("about");
		mntmAbout.addActionListener(ac);
		mnHelp.add(mntmAbout);
		return menuBar;
	}

	@Override
	public void dispose() {
		display.dispose();
		while(display.isCreated()) {
			try {
				Thread.sleep(100);
			} catch(final InterruptedException e) {
			}
		}
		stop();
		super.dispose();
		System.exit(0);
	}

	public void resetSidebarStats() {
		for(JLabel label : stats) {
			sidePanel.remove(label);
		}
		stats.clear();

		RepaintManager.currentManager(sidePanel).markCompletelyDirty(sidePanel);
	}

	public void setEngine(final Engine engine) {
		// now pass it right on along to the display
		this.engine = engine;
		display.setEngine(engine);
		dialog.initialize(engine);
	}

	public void start() {
		stop();
		timer = new Timer("GLUpdateThread", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				BattleRunner battle = engine.getCurrentBattle();
				if(battle != null) {
					List<RobotPeerImpl> list = battle.getRobotPeers();
					for(int i = 0; i < stats.size(); ++i) {
						RobotPeerImpl robot = list.get(i);
						stats.get(i).setText(
								String.format("<html>%c: %s<br>Energy: %.1f</html>", 0x41 + i, robot.getName(),
										robot.getEnergy()));
					}
				}
				display.update();
			}
		}, 100, 50);
	}

	public void startBattle() {
		resetSidebarStats();

		ClassInfo[] robots = dialog.getSelectedRobots();

		for(int i = 0; i < robots.length; ++i) {
			JLabel label = new JLabel("TEST");

			label.setBorder(BorderFactory.createEtchedBorder());
			label.setText(String.format("<html>%c: %s<br>Energy: %.1f</html>", 0x41 + i, robots[i].toString(),
					Rules.ROBOT_START_ENERGY));

			stats.add(label);
			sidePanel.add(label);
		}

		RepaintManager.currentManager(sidePanel).markCompletelyDirty(sidePanel);

		// fpsSlider
		// btnPause
		// btnStop
		fpsSlider.setEnabled(true);
		fpsSlider.setValue(BattleRunner.START_FPS);
		btnPause.setEnabled(true);
		btnStop.setEnabled(true);

		engine.startBattle(robots);
		// stats
	}

	public void stop() {
		if(timer != null) {
			timer.purge();
			timer.cancel();
			timer = null;
		}
	}
}
