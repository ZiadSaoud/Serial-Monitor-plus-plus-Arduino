package com.ziadsaoud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Led_ON_OFF extends MainPanel {
	private static final long serialVersionUID = 1L;
	private JButton Led_on;
	private JButton Led_off;
	private JButton connect;
	private ImageIcon image;
	private ImageIcon error_icon;
	private ImageIcon on;
	private ImageIcon off;
	private ImageIcon usb;
	private ImageIcon logo;
	//fix the prints to JOptionPanes
	public Led_ON_OFF() {
		setPreferredSize(new Dimension(800,800));
		setLayout(null);
		logo = new ImageIcon(getClass().getResource("logo.png"));
		info.setBounds(725, 100, 40, 40);
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				JOptionPane.showMessageDialog(null,"When the user presses the \"LED ON\" button the arduino will receive the following string \"LedOn\"\nthat the user can read and turn on the Led on the Arduino.\nSame goes for the \"LED OFF\" button the received string is \"LedOff\" ","About this example",JOptionPane.INFORMATION_MESSAGE,logo);
			}
		});
			
		Font font = new Font("TimesNewRoman",Font.CENTER_BASELINE,35);
		Led_on = new JButton("LED ON");
		Led_on.setBounds(420, 500, 300, 100);
		Led_on.setFont(font);
		Led_off = new JButton("LED OFF");
		Led_off.setBounds(80,500, 300, 100);
		Led_off.setFont(font);
		Led_on.setEnabled(false);
		Led_off.setEnabled(false);
		connect = new JButton("Connect");
		connect.setBounds(350, 400, 100, 50);
		image = new ImageIcon(getClass().getResource("circuit.jpg"));
		error_icon = new ImageIcon(getClass().getResource("error.png")); 
		on=new ImageIcon(getClass().getResource("on.png"));
		off=new ImageIcon(getClass().getResource("off.png"));
		usb = new ImageIcon(getClass().getResource("usb.png"));
		name="LED Example";
		Led_on.setIcon(on);
		Led_off.setIcon(off);
		Led_off.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String off= "LedOff";
					if(sp.isOpen()) {
						sp.writeBytes(off.getBytes(), off.length());
					}else {
						//System.out.println("Connect the arduino to your pc and try connecting again");
						JOptionPane.showMessageDialog(null, "Connect the Arduino to your PC and please try connecting again", "Error", JOptionPane.WARNING_MESSAGE,usb);
					}
			}
			
		});
		
		Led_on.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String on = "LedOn";
					if(sp.isOpen()) {
					sp.writeBytes(on.getBytes(), on.length());
					}else {
						//System.out.println("Connect the arduino to your pc and try connecting again");
						JOptionPane.showMessageDialog(null, "Connect the Arduino to your PC and please try connecting again", "Error", JOptionPane.WARNING_MESSAGE,usb);
					}
			}
			
		});
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sp==null) {
					JOptionPane.showMessageDialog(null, "Specify com port please", "Error", JOptionPane.WARNING_MESSAGE,error_icon);
				}else {
					if(!sp.openPort()) {
						//System.out.println(" Connect the Arduino to your PC and please try connecting again ");
						JOptionPane.showMessageDialog(null, "Connect the Arduino to your PC and please try connecting again", "Error", JOptionPane.WARNING_MESSAGE,usb);
					}else {
						Led_on.setEnabled(true);
						Led_off.setEnabled(true);
					}
				}
				
			}
			
		});
		
		repaint();
		setBackground(Color.gray);
		add(Led_on);
		add(Led_off);
		add(connect);
		
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		image.paintIcon(this, g, 200, 50);
	}	
}
