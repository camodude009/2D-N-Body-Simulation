import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;


public class Sim extends Canvas implements Runnable{
	
	public JFrame frame;
	private final static Dimension DIMENSIONS = new Dimension(1200, 900);
	private final String NAME = "2D-N-Body-Simulation";
	private boolean debug = false;
	private boolean running = false;
	private Thread thread;
	private int fps = 0, tps = 0;
	
	private Simstate simstate;
	private boolean paused = true;
	public final static double G = 6.67408*Math.pow(10, -11);
	
	
	public Sim(boolean d) {
		debug = d;
		init();
		
		simstate = new Simstate();
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
		long lastTime = System.nanoTime();
	    double nsPerTick = 1000000000.0 / 60.0;
	    
	    int ticks = 0;
	    int frames = 0;
	    
	    long lastTick = System.currentTimeMillis();
	    double unprocessed = 0;
	    
	    while (running) {
		    long now = System.nanoTime();
		    unprocessed += (now - lastTime) / nsPerTick;
		    lastTime = now;
		    boolean shouldRender = true;
		    //update queue
		    while (unprocessed >= 1) {
			    ticks++;
			    if(!paused) tick();
			    unprocessed --;
			    shouldRender = true;
		    }
		    //break between update & render
		    try {
		    	Thread.sleep(1);
		    } catch (InterruptedException e) {
		    	e.printStackTrace();
		    }
		    //render
		    if (shouldRender) {
		    	frames++;
		    	if(!paused) render();
		    }
		    //fps timer
		    if (System.currentTimeMillis() - lastTick >= 1000) {
			    if(debug) System.out.println(ticks + " ticks, " + frames + " frames");
			    fps = frames;
			    tps = ticks;
			    frames = 0;
			    ticks = 0;
			    lastTick += 1000;
		    }
	    }
	}
	
	private void tick() {
		simstate.tick();
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
	    simstate.render(g);
	    if(debug){
	    	g.setColor(Color.black);
	    	g.drawString("Tps: " + tps + " Fps: " + fps, 20, 20);
	    }
	    
	    //disposing of the graphics object and sending image to video card   
	    g.dispose();
	    strategy.show();
	}
	
	public boolean pause(){
		paused = !paused;
		return paused;
	}
	public boolean getPaused(){
		return paused;
	}
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p){
		simstate.addPlanet(x, y, vX, vY, m, p);
		render();
	}
	public String[] getPlanetsAsText(){
		return simstate.getPlanetsAsText();
	}
	
	public void reset(){
		simstate = new Simstate();
	}
	
}
