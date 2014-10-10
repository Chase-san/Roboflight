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
package org.csdgn.rf.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;

import org.csdgn.plugin.ClassInfo;
import org.csdgn.rf.Engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattleDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5449215626352565697L;

	private final JPanel contentPanel = new JPanel();
	private DefaultListModel<ClassInfo> availableRobots;
	private DefaultListModel<ClassInfo> selectedRobots;
	private JList<ClassInfo> availableRobotList;
	private JList<ClassInfo> selectedRobotList;
	private MainWindow owner;
	private JButton okButton;
	
	private MouseAdapter listClickListener = new MouseAdapter() {
		@SuppressWarnings("unchecked")
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				JList<ClassInfo> list = (JList<ClassInfo>)e.getSource();
				int index = list.locationToIndex(e.getPoint());
				if(list == availableRobotList) {
					addRobot(index);
				} else {
					removeRobot(index);
				}
			}
			
		}
	};

	public BattleDialog(MainWindow owner) {
		super(owner, true);
		this.owner = owner;
		setTitle("New Battle");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setPreferredSize(new Dimension(500, 400));

		setLocationRelativeTo(owner);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Available Robots", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane availableScroll = new JScrollPane();
		availableScroll.setPreferredSize(new Dimension(100, 200));
		panel.add(availableScroll);

		availableRobots = new DefaultListModel<ClassInfo>();
		availableRobotList = new JList<ClassInfo>(availableRobots);
		availableRobotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableRobotList.addMouseListener(listClickListener);
		availableScroll.setViewportView(availableRobotList);

		JPanel center0 = new JPanel();
		center0.setBorder(new EmptyBorder(20, 0, 20, 0));
		contentPanel.add(center0);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		center0.setLayout(new BorderLayout(0, 0));
		btnAdd.setActionCommand("add");
		center0.add(btnAdd, BorderLayout.NORTH);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(this);
		btnRemove.setActionCommand("remove");
		center0.add(btnRemove, BorderLayout.SOUTH);
		
		JPanel center1 = new JPanel();
		center0.add(center1, BorderLayout.CENTER);
		center1.setLayout(new BorderLayout(0, 0));
		
		JButton btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(this);
		btnRemoveAll.setActionCommand("removeall");
		center1.add(btnRemoveAll, BorderLayout.SOUTH);
		
		JButton btnAddAll = new JButton("Add All");
		btnAddAll.addActionListener(this);
		btnAddAll.setActionCommand("addall");
		center1.add(btnAddAll, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Selected Robots", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane selectedScroll = new JScrollPane();
		selectedScroll.setPreferredSize(new Dimension(100, 200));
		panel_2.add(selectedScroll);

		selectedRobots = new DefaultListModel<ClassInfo>();
		selectedRobotList = new JList<ClassInfo>(selectedRobots);
		selectedRobotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedRobotList.addMouseListener(listClickListener);
		selectedScroll.setViewportView(selectedRobotList);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(0, 0, 8, 0));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.add(Box.createGlue());
			// OK Button
			okButton = new JButton("Start Battle");
			okButton.setActionCommand("start");
			okButton.addActionListener(this);
			buttonPane.add(okButton);
			okButton.setEnabled(false);
			getRootPane().setDefaultButton(okButton);

			buttonPane.add(Box.createHorizontalStrut(10));

			// Cancel Button
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("exit");
			cancelButton.addActionListener(this);
			buttonPane.add(cancelButton);
			buttonPane.add(Box.createGlue());
		}

		pack();
		setLocationRelativeTo(owner);
	}

	private void addRobot(int selected) {
		if(selected == -1 || selected >= availableRobots.size()) {
			return;
		}
		selectedRobots.addElement(availableRobots.get(selected));
		okButton.setEnabled(true);
	}
	
	private void removeRobot(int selected) {
		if(selected == -1 || selected >= selectedRobots.size()) {
			return;
		}
		selectedRobots.remove(selected);
		int size = selectedRobots.size();
		if(selected >= size) {
			selected = size - 1;
		}
		selectedRobotList.setSelectedIndex(selected);
		if(size == 0) {
			okButton.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if("exit".equals(cmd)) {
			setVisible(false);
		} else if("start".equals(cmd)) {
			setVisible(false);
			owner.startBattle();
		} else if("add".equals(cmd)) {
			addRobot(availableRobotList.getSelectedIndex());
		} else if("remove".equals(cmd)) {
			removeRobot(selectedRobotList.getSelectedIndex());
		} else if("addall".equals(cmd)) {
			int size = availableRobots.size();
			for(int i = 0; i < size; ++i) {
				selectedRobots.addElement(availableRobots.get(i));
			}
			okButton.setEnabled(true);
		} else if("removeall".equals(cmd)) {
			selectedRobots.removeAllElements();
			okButton.setEnabled(false);
		}
	}

	public ClassInfo[] getSelectedRobots() {
		int n = selectedRobots.size();
		ClassInfo[] robots = new ClassInfo[n];
		for(int i = 0; i < n; ++i) {
			robots[i] = selectedRobots.get(i);
		}
		return robots;
	}

	public void initialize(Engine engine) {
		for(ClassInfo info : engine.getDatabase().getRobotList()) {
			availableRobots.addElement(info);
		}
	}
}
