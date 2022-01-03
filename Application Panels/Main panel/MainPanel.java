package com.ziadsaoud;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.fazecast.jSerialComm.SerialPort;

public class MainPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static SerialPort sp;
	protected String name;
	private ImageIcon error_icon;
	private ImageIcon about;
	protected JButton info;
	public MainPanel() {
		about = new ImageIcon(getClass().getResource("info.png"));
		info=new JButton();
		info.setIcon(about);
		info.setBounds(700, 0, 40, 40);
		add(info);
	}
	public void setSerialPort(SerialPort Sport) {
		sp=Sport;
	}
	public SerialPort getSerialPort() {
		return sp;
	}
	public void setBaudRate(int b) {
		error_icon = new ImageIcon(getClass().getResource("error.png"));
		if(sp!=null) {
		sp.setBaudRate(b);
		}else {
			JOptionPane.showMessageDialog(null, "Specify com port please","Error", JOptionPane.WARNING_MESSAGE,error_icon);
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setInfoLocation(int x,int y) {
		info.setBounds(x, y, 40, 40);
	}
	
}
