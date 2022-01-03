package com.ziadsaoud;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.fazecast.jSerialComm.SerialPort;

public class App_frame implements WindowListener {

	private  SerialPort[] SPs;
	private  JPanel mainPanel;
	private  ArrayList<JPanel> panels;
	private  MainPanel panel;
	private CardLayout layout;
	private  int[] bd= {9600,38400,57600,115200};
	private  ImageIcon aboutIcon;
	private  static JFrame frame;
	private  ImageIcon logo;
	public App_frame() {
		logo = new ImageIcon(getClass().getResource("logoo.png"));
		frame = new JFrame("Serial Monitor++");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setIconImage(logo.getImage());
		mainPanel = new JPanel();
		panel = new MainPanel();
		layout = new CardLayout();
		mainPanel.setLayout(layout);
		panels = new ArrayList<JPanel>();
		panels.add(new Led_ON_OFF());//add new examples here.
		panels.add(new Potentiometer());
		panels.add(new Serial_Monitor());
		for(JPanel p: panels) {
			mainPanel.add(p.getName(),p);
		}
		aboutIcon = new ImageIcon(getClass().getResource("logo.png"));
		SPs = SerialPort.getCommPorts();
		JMenuBar jb = new JMenuBar();
		JMenu settings = new JMenu("Settings");
		JMenu SM = new JMenu("Serial Monitor++");
		JMenu file = new JMenu("File");
		JMenu examples = new JMenu("Examples");
		JMenu Baud = new JMenu("BaudRate: 9600 (default)");
		JMenu Port = new JMenu("Port");
		JMenuItem about = new JMenuItem("About");
		JMenuItem exit = new JMenuItem("Quit Serial Monitor++");
		JMenuItem led_example = new JMenuItem("LED Example");
		JMenuItem pot_example = new JMenuItem("Potentiometer Example");//add new menu item here
		JMenuItem serial_monitor = new JMenuItem("Serial Monitor");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.getSerialPort()!=null) {
					if(panel.getSerialPort().isOpen()) {
						panel.getSerialPort().closePort();
				}
			}
				System.exit(0);
			}
			
		});
		jb.add(SM);
		jb.add(file);
		jb.add(settings);
		file.add(examples);
		examples.add(led_example);
		examples.addSeparator();
		examples.add(pot_example);
		examples.addSeparator();
		examples.add(serial_monitor);//add new menu item to the global menu
		settings.add(Baud);
		settings.addSeparator();
		settings.add(Port);
		SM.add(about);
		SM.addSeparator();
		SM.add(exit);
		for(int i=0;i<SPs.length;i++) {//select communication port
			JMenuItem item=new JMenuItem(SPs[i].getSystemPortName());
			Port.add(item);
			item.addActionListener(addPortListener(Port));
		}
		settings.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					Port.removeAll();
					SPs = SerialPort.getCommPorts();
					for(int i=0;i<SPs.length;i++) {//select communication port
						JMenuItem item=new JMenuItem(SPs[i].getSystemPortName());
						Port.add(item);
						item.addActionListener(addPortListener(Port));
					}
					
				}
			}
			
		});
		for(int j=0;j<bd.length;j++) {//select BaudRate.
			if(j==0) {
				JMenuItem br = new JMenuItem(bd[j]+" (default)");
				Baud.add(br);
				br.addActionListener(addBaudListener(Baud));
				continue;
			}
			JMenuItem br_arr = new JMenuItem(bd[j]+"");
			Baud.add(br_arr);
			br_arr.addActionListener(addBaudListener(Baud));
		}
		led_example.addActionListener(addExampleListener());
		pot_example.addActionListener(addExampleListener());
		serial_monitor.addActionListener(addExampleListener());//add new action listener for the new menu.
		about.addActionListener(new ActionListener() {//about

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Serial Monitor ++ is developed for Arduino beginners and developers\nso that they can interact with the arduino board from their PC\nin a clean and elegant way!\nDeveloper Instagram: @electronics_hobbyistss","About",JOptionPane.INFORMATION_MESSAGE, aboutIcon);
				
			}

		});
		layout.show(mainPanel, "Serial Monitor");
		frame.getContentPane().add(mainPanel);
		frame.setJMenuBar(jb);
		frame.setResizable(false);
		frame.setPreferredSize(new Dimension(800,800));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	
	private  ActionListener addPortListener(JMenu port) {
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String portName="";
				JMenuItem p = (JMenuItem) e.getSource();
				portName = p.getText();
				for(int i=0;i<SPs.length;i++) {
					if(SPs[i].getSystemPortName().equals(portName)) {
						panel.setSerialPort(SPs[i]);
						panel.setBaudRate(9600);
						port.setText("Port: "+portName);
						break;
					}
				}
			}
			
		};
		return al;
	}

	private  ActionListener addBaudListener(JMenu baud) {
		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int b=9600;
				String s=((JMenuItem) e.getSource()).getText();
				if(!s.equals("9600 (default)")) {
					b=Integer.parseInt(s);
					panel.setBaudRate(b);
					baud.setText("BaudRate: "+s);
				}else {
					b=9600;
					panel.setBaudRate(b);
					baud.setText("BaudRate: "+s);
				}
				
			}
			
		};
		return al;
	}
	
	private ActionListener addExampleListener() {
		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				layout.show(mainPanel, item.getText());
			}
			
		};
		return al;
	}
	public static void setLocation(int x,int y) {
		frame.setLocation(x, y);
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(panel.getSerialPort()!=null) {
			if(panel.getSerialPort().isOpen()) {
			panel.getSerialPort().closePort();
		}
	}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
