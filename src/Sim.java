import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;


public class Sim extends Canvas implements Runnable{
	
	public JFrame frame;
	private Dimension dimensions = new Dimension(1200, 900);
	private String name = "2D-N-Body-Simulation";
	private double tps = 60.0;
	private double fps = 60.0;
	private boolean debug = false;
	private boolean running = false;
	private Thread thread;
	private int ticksPerSecond = 0;
	private int framesPerSecond = 0;
	
	public final static double G = 6.67408*Math.pow(10, -11);
	public static double g = G;
	private Simstate simstate;
	private boolean paused = true;
	private double ticksToDo = 0.0;
	private double stepSize = 1.0;
	private double stepsPerTick = 1.0;
	private double scale = 1;
	
	public Sim(boolean d) {
		debug = d;
		init();
		simstate = new Simstate();
	}
	
	public void init(){
	    //creating the window
		setPreferredSize(dimensions);
	    setMinimumSize(dimensions);
	    setMaximumSize(dimensions);
	    setSize(dimensions);
	    frame = new JFrame(name);
	    frame.setResizable(true);
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
		final double timeT = 1000000000 / tps;
		final double timeF = 1000000000 / fps;
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
	        	if(!paused || frame.isFocused()) render();
	            frames++;
	            deltaF--;
	        }
	        if (System.currentTimeMillis() - timer > 1000) {
	            if (debug) {
	                System.out.println("tps: " + ticks + " fps: " + frames);
	            }
	            ticksPerSecond = ticks;
	            framesPerSecond = frames;
	            ticks = 0;
	            frames = 0;
	            timer += 1000;
	        }
	    }
	}
	
	private void tick() {
		ticksToDo += stepsPerTick;
		while(ticksToDo >= 1){
			simstate.tick(stepSize);
			ticksToDo--;
		}
	}
	
	private void render() {
		//checking dimensions
		dimensions = new Dimension(frame.getWidth(), frame.getHeight());
		//getting buffer strategy
		BufferStrategy strategy = this.getBufferStrategy(); 		
	    if(strategy == null){
	      createBufferStrategy(3);
	      return;
	    }	    
	    Graphics g = strategy.getDrawGraphics(); //getting the graphics object
	    
	    //drawcalls	
	    //background
	    g.setColor(Color.white);
	    g.fillRect(0,0,dimensions.width,dimensions.height); //filling background white
	    //rendering simstate
	    simstate.render(g, dimensions.width/2, dimensions.height/2, dimensions.width/scale);
	    //rendering debug fps/tps
    	g.setColor(Color.black);
    	g.drawString("Tps: " + ticksPerSecond + " Fps: " + framesPerSecond, 20, 20);
	    
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
		render();
		System.out.println("reset");
	}
	
	public boolean changeSpeed(String[] args){
		double speed;
		double stepsPerSecond;
		try{
			speed = Double.parseDouble(args[0]);
			stepsPerSecond = Double.parseDouble(args[1]);
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			return false;
		}
		ticksToDo = 0;
		stepSize = speed/stepsPerSecond;
		stepsPerTick = stepsPerSecond/tps;
		
		System.out.println("stepsize: " + stepSize);
		System.out.println("steps per tick: " + stepsPerTick);
		
		return true;
	}
	
	public void setAlgorithm(int a){
		simstate.setAlgorithm(a);
	}
	
	public void setG(double g){
		this.g = g;
		System.out.println("g set to: " + g);
	}
	
	public void setScale(double s){
		scale = s;
		System.out.println("scale set to: " + scale);
		render();
	}
}
