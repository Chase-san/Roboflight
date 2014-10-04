package org.csdgn.rf.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;

import org.csdgn.rf.Engine;
import org.csdgn.rf.db.ClassInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5449215626352565697L;
	
	private final JPanel contentPanel = new JPanel();
	private DefaultListModel<ClassInfo> availableRobots;
	private DefaultListModel<ClassInfo> selectedRobots;
	private JList<ClassInfo> availableRobotList; 
	private JList<ClassInfo> selectedRobotList;
	private MainWindow owner;
	private JButton okButton;
	
	public BattleDialog(MainWindow owner) {
		super(owner,true);
		this.owner = owner;
		setTitle("New Battle");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		setPreferredSize(new Dimension(500,400));
		
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
		availableScroll.setPreferredSize(new Dimension(100,200));
		panel.add(availableScroll);
		
		availableRobots = new DefaultListModel<ClassInfo>();
		availableRobotList = new JList<ClassInfo>(availableRobots);
		availableRobotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableScroll.setViewportView(availableRobotList);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(20, 0, 20, 0));
		contentPanel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		btnAdd.setActionCommand("add");
		panel_1.add(btnAdd, BorderLayout.NORTH);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(this);
		btnRemove.setActionCommand("remove");
		panel_1.add(btnRemove, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Selected Robots", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane selectedScroll = new JScrollPane();
		selectedScroll.setPreferredSize(new Dimension(100,200));
		panel_2.add(selectedScroll);
		
		selectedRobots = new DefaultListModel<ClassInfo>();
		selectedRobotList = new JList<ClassInfo>(selectedRobots);
		selectedRobotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedScroll.setViewportView(selectedRobotList);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(0, 0, 8, 0));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.add(Box.createGlue());
			//OK Button
			okButton = new JButton("Start Battle");
			okButton.setActionCommand("start");
			okButton.addActionListener(this);
			buttonPane.add(okButton);
			okButton.setEnabled(false);
			getRootPane().setDefaultButton(okButton);
			
			buttonPane.add(Box.createHorizontalStrut(10));
			
			//Cancel Button
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("exit");
			cancelButton.addActionListener(this);
			buttonPane.add(cancelButton);
			buttonPane.add(Box.createGlue());
		}
		
		pack();
		setLocationRelativeTo(owner);
	}
	
	public void initialize(Engine engine) {
		for(ClassInfo info : engine.getDatabase().getRobotList()) {
			availableRobots.addElement(info);
		}
	}
	
	public ClassInfo[] getSelectedRobots() {
		int n = selectedRobots.size();
		ClassInfo[] robots = new ClassInfo[n];
		for(int i=0;i<n;++i) {
			robots[i] = selectedRobots.get(i);
		}
		return robots;
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
			int selected = availableRobotList.getSelectedIndex();
			if(selected == -1)
				return;
			selectedRobots.addElement(availableRobots.get(selected));
			okButton.setEnabled(true);
		} else if("remove".equals(cmd)) {
			int selected = selectedRobotList.getSelectedIndex();
			if(selected == -1)
				return;
			selectedRobots.remove(selected);
			int size = selectedRobots.size();
			if(selected >= size)
				selected = size - 1;
			selectedRobotList.setSelectedIndex(selected);
			if(size == 0)
				okButton.setEnabled(false);
		}
	}
}
