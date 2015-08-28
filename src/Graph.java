import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;


public class Graph extends Canvas implements Runnable{
	
	public JFrame frame;
	private final static Dimension DIMENSIONS = new Dimension(1200, 900);
	private final String NAME = "Graph";
	private final double TPS = 60.0;
	private final double FPS = 60.0;
	private boolean debug = false;
	private boolean running = false;
	private Thread thread;
	private int tps = 0;
	private int fps = 0;
	
	private ArrayList<Point> thePoints;
	private boolean paused = true;
	
	public Graph(boolean d) {
		debug = d;
		init();
		
		thePoints = new ArrayList<Point>();
	}
	
	public void init(){
	    //creating the window
		setPreferredSize(DIMENSIONS);
	    setMinimumSize(DIMENSIONS);
	    setMaximumSize(DIMENSIONS);
	    setSize(DIMENSIONS);
	    frame = new JFrame(NAME);
	    frame.setResizable(false);
	    frame.add(this); 
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	
	public void start(){	    
	    running = true;
	    thread = new Thread(this);
	    thread.start();
	}
	
	public void stop() {
		running = false;
	    thread.stop();
	}
	
	public void exit(){
		frame.setVisible(false);
	    frame.dispose();
	}
	
	public void setVisible(boolean v){
		frame.setVisible(v);
	}
	
	public void run() {
		long initialTime = System.nanoTime();
		final double timeT = 1000000000 / TPS;
		final double timeF = 1000000000 / FPS;
		double deltaT = 0;
		double deltaF = 0;
		int frames = 0;
		int ticks = 0;
		long timer = System.currentTimeMillis();
		
	    while (running) {
	        long currentTime = System.nanoTime();
	        deltaT += (currentTime - initialTime) / timeT;
	        deltaF += (currentTime - initialTime) / timeF;
	        initialTime = currentTime;
	        if (deltaT >= 1) {
	            if(!paused) tick();
	            ticks++;
	            deltaT--;
	        }
	        if (deltaF >= 1) {
	        	if(!paused) render();
	            frames++;
	            deltaF--;
	        }
	        if (System.currentTimeMillis() - timer > 1000) {
	            if (debug) {
	                System.out.println("TPS: " + ticks + " FPS: " + frames);
	            }
	            tps = ticks;
	            fps = frames;
	            ticks = 0;
	            frames = 0;
	            timer += 1000;
	        }
	    }
	}
	
	private void tick() {
		
	}
	
	private void render() {
		//getting buffer strategy
		BufferStrategy strategy = this.getBufferStrategy(); 		
	    if(strategy == null){
	      createBufferStrategy(3);
	      return;
	    }	    
	    Graphics g = strategy.getDrawGraphics(); //getting the graphics object
	    
	    //drawcalls	    
	    g.setColor(Color.white);
	    g.fillRect(0,0,DIMENSIONS.width,DIMENSIONS.height); //filling background white
	    plot(g);
    	g.setColor(Color.black);
    	g.drawString("Tps: " + tps + " Fps: " + fps, 20, 20);
	    
	    //disposing of the graphics object and sending image to video card   
	    g.dispose();
	    strategy.show();
	}
	
	public void plot(Graphics g){
		
	}
	
	public boolean pause(){
		paused = !paused;
		return paused;
	}
	public boolean getPaused(){
		return paused;
	}
	
	public void addPoint(Point p){
		thePoints.add(p);
	}
	
}
