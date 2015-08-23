import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;


public class Game extends Canvas implements Runnable{
	
	public final static Dimension DIMENSIONS = new Dimension(900, 900);
	public JFrame frame;
	public final String NAME = "2D-N-Body-Simulation";
	private boolean debug = false;
	private boolean running = false;
	private Thread thread;
	private Gamestate gamestate;
	private int fps = 0, tps = 0;
	
	
	public Game(String[] args) {
		//initiate
		for(String s: args){
			switch (s){
				case "-d":
					debug = true;
					break;
			}
		}
		gamestate = new Gamestate();
	}
	
	public void start(){	    
	    running = true;
	    thread = new Thread(this);
	    thread.start();
	}
	
	public synchronized void stop() {
		running = false;
	    thread.stop();
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
			    tick();
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
		    	render();
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
		gamestate.tick();
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
	    gamestate.render(g);
	    if(debug){
	    	g.setColor(Color.black);
	    	g.drawString("Fps: " + fps + " Tps: " + tps, 20, 20);
	    }
	    
	    //disposing of the graphics object and sending image to video card   
	    g.dispose();
	    strategy.show();
	}
	
}
