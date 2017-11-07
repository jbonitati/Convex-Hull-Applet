import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main extends JApplet implements ActionListener, MouseListener, ThreadCompleteListener{
	/**
	 * Convex Hull demonstration applet by Joey Bonitati
	 */
	private static final long serialVersionUID = 1L;
	private static final long STEP = 1000000, SLOW = 1500, FAST = 200, REAL = 10, REALEST = 0;
	JButton clear, add, start, stepbutton, pause, play, stop;
	JTextField addx;
	JLabel xlabel, instruction;
	JPanel top, direct;
	JMenuBar menubar;
	JMenu algorithmMenu, speedMenu, controlMenu;
	ButtonGroup algorithmGroup, speedGroup;
	JRadioButtonMenuItem step, slow, fast, real, realest, 
		naive, jarvis, graham, quickhull, incremental, divandconq, kirkpatrick, chan;
	//JSlider speedslider;
	JMenuItem close;
	public static PointDisplay display;
	public static ArrayList<Point> points;
	public static ArrayList<Line> lines;
	public static NotifyingThread athread; //Thread for running algorithms
	private static String infoText = "";
	public static long steptime = Main.FAST;
	public static int stepcount = 0;
	
	public void init(){
		points = new ArrayList<Point>();
		lines = new ArrayList<Line>();
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.setSize(1200, 1200);
		
		top = new JPanel();
		c.add(top, BorderLayout.NORTH);
		top.setEnabled(true);
		top.setBackground(Color.white);
		/*
		speedslider = new JSlider(0, 10000 - (int)REAL, 10000 - (int)FAST);
		speedslider.addChangeListener(this);
		top.add(speedslider);
		*/
		
		clear = new JButton("Clear points");
		top.add(clear);
		clear.addActionListener(this);
		
		xlabel = new JLabel("x = ");
		top.add(xlabel);
		
		addx = new JTextField("10", 10);
		addx.setHorizontalAlignment(JTextField.CENTER);
		top.add(addx);
		
		add = new JButton("Add x points");
		add.addActionListener(this);
		top.add(add);
		
		start = new JButton("Start");
		start.addActionListener(this);
		top.add(start);
		
		direct = new JPanel();
		c.add(direct, BorderLayout.SOUTH);
		direct.setEnabled(false);
		direct.setBackground(Color.gray);
		
		stepbutton = new JButton("Step");
		stepbutton.addActionListener(this);
		direct.add(stepbutton);
		
		pause = new JButton("Pause");
		pause.addActionListener(this);
		direct.add(pause);
		
		play = new JButton("Play");
		play.addActionListener(this);
		direct.add(play);
		
		stop = new JButton("Stop");
		stop.addActionListener(this);
		direct.add(stop);
		
		display = new PointDisplay();
		c.add(display , BorderLayout.CENTER);
		
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		algorithmMenu = new JMenu("Algorithm");
		algorithmMenu.setMnemonic('a');
		menubar.add(algorithmMenu);
		
		speedMenu = new JMenu("Speed");
		speedMenu.setMnemonic('s');
		menubar.add(speedMenu);
		
		speedGroup = new ButtonGroup();
		
		step = new JRadioButtonMenuItem("Step by Step", false);
		step.addActionListener(this);
		speedMenu.add(step);
		speedGroup.add(step);
		
		slow = new JRadioButtonMenuItem("Slow", false);
		slow.addActionListener(this);
		speedMenu.add(slow);
		speedGroup.add(slow);
		
		fast = new JRadioButtonMenuItem("Fast", true);
		fast.addActionListener(this);
		speedMenu.add(fast);
		speedGroup.add(fast);
		
		real = new JRadioButtonMenuItem("Very Fast");
		real.addActionListener(this);
		speedMenu.add(real);
		speedGroup.add(real);
		
		realest = new JRadioButtonMenuItem("Real-Time");
		realest.addActionListener(this);
		speedMenu.add(realest);
		speedGroup.add(realest);
		
		algorithmGroup = new ButtonGroup();
		
		naive = new JRadioButtonMenuItem("Naive", true);
		algorithmMenu.add(naive);
		algorithmGroup.add(naive);
		
		jarvis = new JRadioButtonMenuItem("Gift Wrapping/Jarvis March");
		algorithmMenu.add(jarvis);
		algorithmGroup.add(jarvis);
		
		graham = new JRadioButtonMenuItem("Graham scan");
		algorithmMenu.add(graham);
		algorithmGroup.add(graham);
		
		quickhull = new JRadioButtonMenuItem("Quickhull");
		algorithmMenu.add(quickhull);
		algorithmGroup.add(quickhull);
		
		incremental = new JRadioButtonMenuItem("Incremental");
		algorithmMenu.add(incremental);
		algorithmGroup.add(incremental);
		
		divandconq = new JRadioButtonMenuItem("Divide and Conquer");
		divandconq.setVisible(false);
		algorithmMenu.add(divandconq);
		algorithmGroup.add(divandconq);
		
		kirkpatrick = new JRadioButtonMenuItem("Kirkpatrick-Seidel");
		kirkpatrick.setVisible(true);
		algorithmMenu.add(kirkpatrick);
		algorithmGroup.add(kirkpatrick);
		
		chan = new JRadioButtonMenuItem("Chan's Algorithm");
		chan.setVisible(false);
		algorithmMenu.add(chan);
		algorithmGroup.add(chan);
		
		controlMenu = new JMenu("Options");
		controlMenu.setMnemonic('o');
		controlMenu.setVisible(false);
		menubar.add(controlMenu);
		
		display.addMouseListener(this);
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e){
		Object s = e.getSource();
		if(s == clear){
			if(top.isEnabled()){
				//delete all points and lines
				points.clear();
				lines.clear();
				Main.setInfoText("");
				repaint();
			}
		}else if(s == add){
			if(top.isEnabled()){
				String text = addx.getText();
				int x = Integer.parseInt(text);
				for(int i = 0; i < x; i++){
					points.add(new Point(Math.random()*.9 + 0.05, Math.random()*.9 + 0.05));
				}
				repaint();
			}
		}else if(s == start){
			if(top.isEnabled()){
				top.setEnabled(false);
				direct.setEnabled(true);
				top.setBackground(Color.gray);
				direct.setBackground(Color.white);
				if(naive.isSelected()){
					athread = new Algorithm.Naive();
				}
				else if(jarvis.isSelected()){
					athread = new Algorithm.Jarvis();
				}
				else if(graham.isSelected()){
					athread = new Algorithm.Graham();
				}
				else if(quickhull.isSelected()){
					athread = new Algorithm.Quick();
				}
				else if(incremental.isSelected()){
					athread = new Algorithm.Incremental();
				}else if(divandconq.isSelected()){
					athread = new Algorithm.DivideandConquer();
				}else if(kirkpatrick.isSelected()){
					athread = new Algorithm.Ultimate();
				}
				athread.addListener(this);
				Algorithm.start();		
			}
		}else if(s == stepbutton){
			if(direct.isEnabled() && athread.isAlive()) athread.interrupt();
		}
		else if(s == play){
			if(direct.isEnabled() && athread.isAlive()) {
				athread.interrupt();
				if(step.isSelected()) steptime = Main.STEP;
				if(slow.isSelected()) steptime = Main.SLOW;
				if(fast.isSelected()) steptime = Main.FAST;
				if(real.isSelected()) steptime = Main.REAL;
				if(realest.isSelected()) steptime = Main.REALEST;
			}
		}else if(s == pause){
			if(direct.isEnabled() && athread.isAlive()){
				steptime = Main.STEP;
			}
		}else if(s == stop){
			if(direct.isEnabled()){
				direct.setEnabled(false);
				top.setEnabled(true);
				direct.setBackground(Color.gray);
				top.setBackground(Color.white);
			}
			if(athread.isAlive()){
				athread.stop();
				athread.removeListener(this);
			}
		}else if(s == step){
			steptime = Main.STEP;
		}else if(s == slow){
			steptime = Main.SLOW;
		}else if(s == fast){
			steptime = Main.FAST;
		}else if(s == real){
			steptime = Main.REAL;
		}else if(s == realest){
			steptime = Main.REALEST;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(top.isEnabled()){
			points.add(new Point((double)e.getX() / display.getWidth(), (double)e.getY() / display.getHeight()));
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void threadCompleted(Thread t) {
		athread.removeListener(this);
		direct.setEnabled(false);
		top.setEnabled(true);
		direct.setBackground(Color.gray);
		top.setBackground(Color.white);
	}
	
	public static void setInfoText(String s){
		infoText = s;
	}
	
	public static String getInfoText(){
		return infoText;
	}
}