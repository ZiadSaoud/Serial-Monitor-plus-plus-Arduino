package com.ziadsaoud;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.fazecast.jSerialComm.SerialPort;

public class Serial_Monitor extends MainPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StringBuilder sb;
	private volatile boolean kill=true;
	private ImageIcon error_icon;
	private ImageIcon usb;
	private JCheckBox auto;
	private mouse m;
	private JTextArea t;
	public Serial_Monitor() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss");
		setLayout(null);
		setInfoLocation(745, 5);
		name="Serial Monitor";
	    t = new JTextArea();
		t.setEditable(false);
		JTextField field = new JTextField();
		field.setBounds(5, 10, 665, 30);
		JButton send = new JButton("Send");
		send.setBounds(670, 10, 75, 30);
		send.setEnabled(false);
		JButton save = new JButton("Save to text file");
		save.setBounds(310, 690, 150, 35);
		JButton clear = new JButton("Clear output");
		clear.setBounds(460, 690, 130, 35);
		JCheckBox time = new JCheckBox("Show timestamp",false);
		time.setBounds(10, 690, 139, 30);
		JCheckBox print = new JCheckBox("Print sent data",true);
		print.setBounds(10, 670, 150, 30);
	    auto = new JCheckBox("Autoscroll",true);
		auto.setBounds(10, 710, 100, 30);
		JButton start = new JButton("Start");
		start.setBounds(590, 690, 100, 35);
		JButton stop = new JButton("Stop");
		stop.setBounds(690, 690, 100, 35);
		stop.setEnabled(false);
		error_icon = new ImageIcon(getClass().getResource("error.png"));
		usb = new ImageIcon(getClass().getResource("usb.png"));
		sb = new StringBuilder();
		m = new mouse();
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = field.getText();
				if(print.isSelected()) {
					if(time.isSelected()) {
						t.append(dtf.format(LocalTime.now())+" -> "+"Sent: "+s+"\n");
						sb.append(dtf.format(LocalTime.now())+" -> "+"Sent: "+s+"\n");
					}else {
						t.append("Sent: "+s+"\n");
						sb.append("Sent: "+s+"\n");
					}
					sp.writeBytes(s.getBytes(), s.getBytes().length);
				}else {
					sp.writeBytes(s.getBytes(), s.getBytes().length);
				}
				field.setText("");
			}
			
		});
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File out = new File("output");//add file chooser.
				try{
					char[] out_char = new char[sb.length()];
					FileWriter fw = new FileWriter(out);
					sb.getChars(0, sb.length(), out_char, 0);
					fw.write(out_char);
					fw.close();
				}catch(IOException e1) {
					
				}
			}
			
		});
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				t.setText("");
				sb.delete(0, sb.length());
			}
			
		});
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sp==null) {
					JOptionPane.showMessageDialog(null, "Specify com port please", "Error", JOptionPane.WARNING_MESSAGE,error_icon);
				}else {
					if(!sp.openPort()) {
						JOptionPane.showMessageDialog(null, "Connect the Arduino to your PC please", "Error", JOptionPane.WARNING_MESSAGE,usb);
					}else {
						send.setEnabled(true);
						stop.setEnabled(true);
						sp.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
						Scanner scan = new Scanner(sp.getInputStream());
						kill=false;
						
						Thread th = new Thread() {

							@Override
							public void run() {
								while(scan.hasNextLine() && !kill) {
									String s = scan.nextLine();
									if(time.isSelected()) {
										s = dtf.format(LocalTime.now())+" -> "+s;
									}
									t.append(s+"\n");
									if(auto.isSelected())
										t.setCaretPosition(t.getDocument().getLength());
									sb.append(s+"\n");
								}
							scan.close();	
							}
							
						};
						th.start();
					}
				}
				
			}
			
		}); 
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				kill=true;
				send.setEnabled(false);
				stop.setEnabled(false);
				sp.closePort();
			}
			
		});
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			//about
				
			}
			
		});
		t.addMouseWheelListener(m);
		auto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				t.addMouseWheelListener(m);
			}
			
		});
		JScrollPane scroll = new JScrollPane(t,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(0, 50, 800, 600);
		add(field);
		add(auto);
		add(send);
		add(clear);
		add(save);
		add(time);
		add(print);
		add(start);
		add(stop);
		add(scroll);	
	}
	
	private class mouse implements MouseWheelListener{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			auto.setSelected(false);
			t.removeMouseWheelListener(this);	
		}
	}
	
	
}
