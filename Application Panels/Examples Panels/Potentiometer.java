package com.ziadsaoud;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

public class Potentiometer extends MainPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton start;
	private JButton pause;
	private JButton reset;
	private JButton editPlot;
	private JButton save;
	private double x = 0;
	private Thread thread;
	private Double Inc = 0.1;
	private volatile boolean stop = false;
	private ImageIcon error_icon;
	private ImageIcon usb;
	private Color gc;
	private JColorChooser color;
	private JCheckBox legend;
	private JTextField legend_text;
	private JCheckBox xAxis;
	private JTextField xAxis_text;
	private JCheckBox yAxis;
	private JTextField yAxis_text;
	private JCheckBox title;
	private JTextArea title_text;
	private SpinnerModel model;
	private int minx = 0;
	private int maxx = 50;
	private int miny = 0;
	private int maxy = 1050;
	private JFileChooser fileChooser;
	private ImageIcon logo;
	//update plot parameters.
	//select serial port.//done
	//info button.
	//reset tip.
	public Potentiometer() {
		setLayout(null);
		logo = new ImageIcon(getClass().getResource("logo.png"));
		info.setBounds(725, 100, 40, 40);
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"This example features a graph that allows the user to plot the data sent from the Arduino board to the PC using \"Serial.Println()\" function.\n"
						+ "You can edit the graph parameters such as: Title, Legend, Line color, Axis Range...\n"
						+ "You can save the data and the graph to Excel or save an image of the graph directly as png (Right click on the graph area)."
						+ "\nNote: It is recommended to unplug and reconnect the Arduino to the PC when resetting the graph\n"
						+ "in order to prevent weired values from appearing on the graph "
						+ "(we are working to fix this problem in future realese).","About this example",JOptionPane.INFORMATION_MESSAGE,logo);
			}
			
		});
		name = "Potentiometer Example";
		XYSeries series = new XYSeries("Legend");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Title", "X-axis", "Y-axis", dataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel plot = new ChartPanel(chart);
		plot.setPreferredSize(new Dimension(700,500));;
		plot.setBounds(50, 50, 650, 500);
		NumberAxis range_y = (NumberAxis) chart.getXYPlot().getRangeAxis();
		range_y.setRange(miny, maxy);
		NumberAxis domain_x = (NumberAxis) chart.getXYPlot().getDomainAxis();
		domain_x.setRange(minx,maxx);
		XYLineAndShapeRenderer render = new XYLineAndShapeRenderer();
		gc = new Color(255, 0, 0);
		render.setSeriesPaint(0, gc);
		render.setSeriesShapesVisible(0, false);
		chart.getXYPlot().setRenderer(render);
	    color = new JColorChooser();
		AbstractColorChooserPanel[] panels = color.getChooserPanels();
		color.setChooserPanels(new AbstractColorChooserPanel[] {panels[3]});
		color.setBounds(10, 120, 700, 350);
		color.setColor(255, 0, 0);
		legend = new JCheckBox("Legend",true);
		legend_text=new JTextField("Legend");
		xAxis = new JCheckBox("X-axis Label",true);
		xAxis_text = new JTextField("X-axis"); 
		yAxis = new JCheckBox("Y-axis Label",true);
		yAxis_text = new JTextField("Y-axis"); 
		title=new JCheckBox("Title",true);
		title_text = new JTextArea("Title");
		model = new SpinnerNumberModel(0.1,0,Double.MAX_VALUE,0.1);
		JSpinner spp = new JSpinner(model);
		save = new JButton("Save to Excel");
		save.setBounds(635, 600, 130, 50);
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Save Excel File");
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("MS Office Documents", "xlsx"));
					if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION && series.getItemCount()>0) {
						File f = fileChooser.getSelectedFile();//file output location fixxxx
						if(!f.getAbsolutePath().endsWith(".xlsx"))
						{
							f = new File(((String) f.getAbsolutePath()) + ".xlsx");
						}
						
					FileOutputStream fos = new FileOutputStream(f);
					XSSFWorkbook b = new XSSFWorkbook();
					XSSFSheet s = b.createSheet("Sheet 1");
					@SuppressWarnings("unchecked")
					List<XYDataItem> items =  series.getItems();
					int index=0;
					for(XYDataItem it :items) {
					s.createRow((short)index);
					s.getRow(index).createCell((short)0).setCellValue((Double) it.getX());
					s.getRow(index).createCell((short)1).setCellValue((Double) it.getY());
					index++;
					}
					XSSFDrawing drawing = s.createDrawingPatriarch();
					XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 5, 20, 25);
					XSSFChart chart = drawing.createChart(anchor);
					XDDFValueAxis bottomaxis = chart.createValueAxis(AxisPosition.BOTTOM);
					bottomaxis.setTitle(xAxis_text.getText());
					XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
		            leftAxis.setTitle(yAxis_text.getText());
					XDDFNumericalDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(s, new CellRangeAddress(0, series.getItemCount()-1, 0, 0));
					XDDFNumericalDataSource<Double> ys = XDDFDataSourcesFactory.fromNumericCellRange(s, new CellRangeAddress(0, series.getItemCount()-1, 1, 1));
					XDDFScatterChartData data = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER, bottomaxis, leftAxis);
		            XDDFScatterChartData.Series series1 = (XDDFScatterChartData.Series) data.addSeries(xs, ys);
		            String col = getColor(gc);
		            lineSeriesColor(series1, XDDFColor.from(hex2Rgb(col)));
		            series1.setTitle(legend_text.getText(), null);
		            series1.setSmooth(true);
		            series1.setMarkerStyle(MarkerStyle.NONE);
		            data.setVaryColors(false);
		            chart.setTitleText(title_text.getText());
		            chart.setTitleOverlay(false);
		            XDDFChartLegend legend = chart.getOrAddLegend();
		            legend.setPosition(LegendPosition.TOP_RIGHT);
		            chart.plot(data);
					b.write(fos);
					b.close();
					fos.close();
					}
				} catch (FileNotFoundException e1) {
					
				} catch (IOException e1) {
					
				}
			}
			
		});
		
		
		error_icon = new ImageIcon(getClass().getResource("error.png"));
		usb = new ImageIcon(getClass().getResource("usb.png"));
		start = new JButton("Start");
		start.setBounds(50, 600, 100, 50);
		pause = new JButton("Stop");
		pause.setBounds(200, 600, 100, 50);
		pause.setEnabled(false);
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sp==null) {
					//System.out.println("Enter serial port");//fix it.
					JOptionPane.showMessageDialog(null, "Specify com port please", "Error", JOptionPane.WARNING_MESSAGE,error_icon);
				}else {
					//System.out.println("entered");
					if(!sp.openPort()) {
						//System.out.println("Connect the arduino");//fix it.
						JOptionPane.showMessageDialog(null, "Connect the Arduino to your PC please", "Error", JOptionPane.WARNING_MESSAGE,usb);
					}else {
					sp.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0,0 );
					Scanner scan = new Scanner(sp.getInputStream());
					Inc = (Double) spp.getValue();
					x=minx;
					thread = new Thread() {
						@Override
						public void run() {
							stop=false;
							start.setText("Start");
							start.setEnabled(false);
							pause.setEnabled(true);
							double z;
							while(scan.hasNextLine() && !stop) {
								try {
									String s = scan.nextLine();
									z=Double.parseDouble(s);
									series.add(x, z);
									x=x+Inc;
								} catch (NumberFormatException e) {
								}
							}
							scan.close();
						}
						
					};
					thread.start();
				}
			}
		}
			
		});
		pause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sp.closePort();
				stop=true;
				pause.setEnabled(false);
				reset.setEnabled(true);
			}
			
			
		});
		reset = new JButton("Reset");
		reset.setBounds(350,600,100,50);
		reset.setEnabled(false);
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {//Recommended to disconnect Arduino and reconnect it.
				stop=true;
				series.clear();
				x=minx;
				pause.setEnabled(false);
				start.setEnabled(true);
				range_y.setRange(miny, maxy);
				domain_x.setRange(minx,maxx);
				reset.setEnabled(false);
			}
			
		});
		editPlot = new JButton("Edit Plot Parameters");
		editPlot.setBounds(470, 600, 150, 50);
		editPlot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Plot Parameters");
				frame.setPreferredSize(new Dimension(720,500));
				frame.setIconImage(logo.getImage());
				JPanel panel = new JPanel();
				panel.setLayout(null);
				JTextField min_x = new JTextField();
				min_x.setBounds(45, 40, 50, 25);
				min_x.setText(minx+"");
				JTextField max_x = new JTextField();
				max_x.setBounds(135, 40, 50, 25);
				max_x.setText(maxx+"");
				JLabel x_axis = new JLabel("X-axis Range:");
				x_axis.setBounds(10, 10, 100, 25);
				JLabel min = new JLabel("Min:");
				min.setBounds(10, 40, 30, 25);
				JLabel max = new JLabel("Max:");
				max.setBounds(100, 40, 30, 25);
				JLabel y_axis = new JLabel("Y-axis Range:");
				y_axis.setBounds(10, 65, 100, 25);
				JLabel min_2 = new JLabel("Min:");
				min_2.setBounds(10, 95, 30, 25);
				JLabel max_2 = new JLabel("Max:");
				max_2.setBounds(100, 95, 30, 25);
				JTextField min_y = new JTextField();
				min_y.setBounds(45, 95, 50, 25);
				min_y.setText(miny+"");
				JTextField max_y = new JTextField();
				max_y.setBounds(135, 95, 50, 25);
				max_y.setText(maxy+"");
				JLabel time = new JLabel("Time Interval:");
				time.setBounds(195, 3, 100, 25);
				spp.setBounds(300, 3, 60, 25);
				
				
				legend.setBounds(190, 20, 80, 40);
				legend_text.setBounds(300, 28, 100, 25);
				legend.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(legend.isSelected()) {
							render.setBaseSeriesVisibleInLegend(true);
						}else {
							render.setBaseSeriesVisibleInLegend(false);
						}
						
					}
					
				});
				xAxis.setBounds(190, 45, 113, 40);
				xAxis_text.setBounds(300, 53, 100, 25);
				xAxis_text.setToolTipText("To only remove the label please delete the text and click apply");
				xAxis.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(xAxis.isSelected()) {
							domain_x.setVisible(true);
						}else {
							domain_x.setVisible(false);
						}
						
					}
					
				});
				yAxis.setBounds(190, 70, 113, 40);
				yAxis_text.setBounds(300, 78, 100, 25);
				yAxis_text.setToolTipText("To only remove the label please delete the text and click apply");
				yAxis.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(yAxis.isSelected()) {
							range_y.setVisible(true);
						}else {
							range_y.setVisible(false);
						}
						
					}
				});
				title.setBounds(190, 95, 80, 40);
				title_text.setBounds(405, 10, 200, 120);
				title_text.setLineWrap(true);
				title.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(title.isSelected()) {
							title_text.setEditable(true);
							chart.setTitle(title_text.getText());
						}else {
							chart.setTitle("");
							title_text.setEditable(false);
						}
						
					}
					
				});
				
				JButton apply = new JButton("Apply");
				apply.setBounds(610,10,100,50);
				apply.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gc = color.getColor();
						render.setSeriesPaint(0, gc);
						series.setKey(legend_text.getText());
						domain_x.setLabel(xAxis_text.getText());
						range_y.setLabel(yAxis_text.getText());
						minx = Integer.parseInt(min_x.getText());
						maxx =  Integer.parseInt(max_x.getText());
						miny = Integer.parseInt(min_y.getText());
						maxy =  Integer.parseInt(max_y.getText());
						range_y.setRange(miny, maxy);
						domain_x.setRange(minx,maxx);
						if(title.isSelected()) {
							chart.setTitle(title_text.getText());
						}
					}
					
				});
				panel.add(xAxis);
				panel.add(xAxis_text);
				panel.add(yAxis);
				panel.add(yAxis_text);
				panel.add(legend);
				panel.add(legend_text);
				panel.add(title);
				panel.add(title_text);
				panel.add(apply);
				panel.add(color);
				panel.add(x_axis);
				panel.add(y_axis);
				panel.add(min);
				panel.add(min_2);
				panel.add(max);
				panel.add(max_2);
				panel.add(min_x);
				panel.add(max_x);
				panel.add(min_y);
				panel.add(max_y);
				panel.add(spp);
				panel.add(time);
				frame.getContentPane().add(panel);
				frame.pack();
				frame.setLocation(800,0);
				App_frame.setLocation(0, 0);
				frame.setResizable(false);
				frame.setVisible(true);
			}
			
		});
		add(plot);
		add(start);
		add(pause);
		add(reset);
		add(editPlot);
		add(save);
		}
	private  void lineSeriesColor(XDDFChartData.Series series, XDDFColor color) {
	    XDDFSolidFillProperties fill = new XDDFSolidFillProperties(color);
	    XDDFLineProperties line = new XDDFLineProperties();
	    line.setFillProperties(fill);
	    XDDFShapeProperties properties = series.getShapeProperties();
	    if (properties == null) {
	        properties = new XDDFShapeProperties();
	    }
	    properties.setLineProperties(line);
	    series.setShapeProperties(properties);
	}
	private  byte[] hex2Rgb(String colorStr) {
	    int r = Integer.valueOf(colorStr.substring(1, 3), 16);
	    int g = Integer.valueOf(colorStr.substring(3, 5), 16);
	    int b = Integer.valueOf(colorStr.substring(5, 7), 16);
	    return new byte[]{(byte) r, (byte) g, (byte) b};
	}
	private String getColor(Color c) {
		String r=Integer.toHexString(c.getRed());
		String g=Integer.toHexString(c.getGreen());
		String b=Integer.toHexString(c.getBlue());
		if(r.length()==1 ){
			r="0"+r;
		}
		if(g.length()==1 ){
			g="0"+g;
		}
		if(b.length()==1 ){
			b="0"+b;
		}
		return "#"+r+g+b;
	}
	
	}
