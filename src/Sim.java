import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

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
	public static double g = 1;
	public static Color bgColor = Color.white;
	public static Color bgColorI = Color.black;
	public static double totalElapsedTime = 0.0;
	private Simstate simstate;
	private boolean paused = true;
	private double stepsToDo = 0.0;
	private double speed = 1.0;
	private double stepSize = 0.01;
	private double stepsPerTick = (speed/stepSize)/tps;
	private double scale = 10;
	private boolean realTime = false;
	private double targetTime = 0.0;
	private int completion = 0;
	private double totalTime = 0;
	private double timerTime = 0;
	private int totalSteps = 0;
	private ArrayList<Integer> queue = new ArrayList<Integer>();
	private ArrayList<Integer> origionalQueue = new ArrayList<Integer>();
	private ArrayList<Double> timings = new ArrayList<Double>();
	private int testIterations = 1;
	private int currentIterations = 0;
	
	public Sim(boolean d) {
		debug = d;
		init();
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
	    frame.setVisible(realTime);
		simstate = new Simstate();
	}
	
	public void start(){
	    running = true;
	    thread = new Thread(this);
	    thread.start();
	    simstate = new Simstate();
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
	        if(realTime){
	        	if(totalElapsedTime < targetTime || targetTime == 0.0){
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
	        	}else{
	        		if(!paused){
			    		System.out.println("finished");
			    		pause();
			    		System.out.println("paused");
		    			simstate.printEnergyError();
		    			System.out.println();
		    			System.out.println();
		    		}
	        	}
		    }else{
		    	if(!paused){
		    		if(totalElapsedTime < targetTime || targetTime == 0.0){
		    			tick();
		    		}else{
		    			currentIterations++;
		    			if(origionalQueue.size() > 0)System.out.println("finished iteration " + currentIterations);
			    		else System.out.println("finished");
		    			if(!paused)pause();
		    			simstate.printEnergyError();
		    			System.out.println("simulated " + totalSteps + " steps in "+ (totalTime/1000) + "s");
		    			timings.add(totalTime/1000);
		    			System.out.println();
		    			System.out.println();
		    			if(queue.size() > 0){
		    				partialReset();
		    				if(currentIterations >= testIterations){
		    					currentIterations = 0;
		    					queue.remove(0);
		    				}
		    				if(queue.size() > 0){
		    					randomSpawn(queue.get(0));
		    					pause();
		    				}else{
		    					System.out.println();
		    					System.out.println();
		    					System.out.println();
		    					System.out.println("finished testing simulation time");
		    				}
		    			}
		    		}
		    	}
		    }
	    }
	}
	
	public void tick() {
		if(realTime){
			stepsToDo += stepsPerTick;
			while(stepsToDo >= 1){
				step();
				stepsToDo--;
			}
		}else{
			step();
		}
	}
	
	public void step(){
		totalElapsedTime += stepSize;
		if(targetTime != 0.0){
			if((int)(100*(totalElapsedTime/targetTime)) != completion){
				completion = (int)(100*(totalElapsedTime/targetTime));
				System.out.println(completion + "%");
			}
		}
		totalSteps++;
		startTimer();
		simstate.tick(stepSize);
		stopTimer();
	}
	
	public void render() {
		// checking dimensions
		dimensions = new Dimension(frame.getWidth(), frame.getHeight());
		// getting buffer strategy
		BufferStrategy strategy = this.getBufferStrategy(); 		
	    if(strategy == null){
	      createBufferStrategy(3);
	      return;
	    }	    
	    Graphics g = strategy.getDrawGraphics(); // getting the graphics object
	    Graphics2D g2d = (Graphics2D) g.create();
	    // drawcalls	
	    // background
	    g2d.setColor(bgColor);
	    g2d.fillRect(0,0,dimensions.width,dimensions.height); // filling background white
	    // rendering simstate
	    simstate.render(g2d, dimensions.width/2, dimensions.height/2, dimensions.width/scale);
	    // rendering debug fps/tps
	    g2d.setColor(bgColorI);
	    g2d.drawString("Tps: " + ticksPerSecond + " Fps: " + framesPerSecond, 20, 20);
	    if(paused)g2d.drawString("Paused", 20, 60);
	    
	    // disposing of the graphics object and sending image to video card   
	    g2d.dispose();
	    strategy.show();
	}
	
	public boolean pause(){
		paused = !paused;
		return paused;
	}
	public boolean getPaused(){
		return paused;
	}
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p, Color c){
		simstate.addPlanet(x, y, vX, vY, m, p, c);
	}
	public String[] getPlanetsAsText(){
		return simstate.getPlanetsAsText();
	}
	
	public void reset(){
		System.out.println("sim reset");
		if(!paused)paused = true;
		simstate = new Simstate();
		g = 1;
		stepsToDo = 0.0;
		speed = 1.0;
		stepSize = 0.01;
		calculateTimings();
		scale = 10;
		totalElapsedTime = 0.0;
		targetTime = 0.0;
		totalSteps = 0;
		queue = new ArrayList<Integer>();
		origionalQueue = new ArrayList<Integer>();
		timings = new ArrayList<Double>();
		currentIterations = 0;
		resetData();
		resetTimer();
		render();
	}
	
	public void partialReset(){
		System.out.println("sim partial reset");
		if(!paused)paused = true;
		int algorithm = simstate.getAlgorithm();
		simstate = new Simstate();
		simstate.setAlgorithm(algorithm);
		stepsToDo = 0.0;
		totalElapsedTime = 0.0;
		totalSteps = 0;
		resetData();
		resetTimer();
		render();
	}
	
	public boolean changeSpeed(String[] args){
		try{
			speed = Double.parseDouble(args[0]);
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			return false;
		}
		calculateTimings();
		System.out.println("speed: " + speed);
		return true;
	}
	
	public boolean changeStepSize(String[] args){
		try{
			stepSize = Double.parseDouble(args[0]);
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			return false;
		}
		calculateTimings();
		System.out.println("stepsize: " + stepSize);
		return true;
	}
	
	public void calculateTimings(){
		stepsToDo = 0;
		double stepsPerSecond = speed/stepSize;		
		stepsPerTick = stepsPerSecond/tps;

	}
	
	public void setAlgorithm(int a){
		simstate.setAlgorithm(a);
	}
	
	public void setG(double g){
		Sim.g = g;
		System.out.println("g set to: " + g);
	}
	
	public void setScale(double s){
		scale = s;
		System.out.println("scale set to: " + scale);
		render();
	}
	
	public void setRealTime(boolean rt){
		if(rt == realTime){
			System.out.println("mode already set");
		}else{
			realTime = rt;
			if(realTime){
				setVisible(true);
				render();
			}else{
				setVisible(false);
			}
		}
	}
	
	public void setTargetTime(double t){
		targetTime = t;
		System.out.println("target time: " + targetTime);
	}
	
	public void setCollectData(boolean c){
		simstate.setCollectData(c);
	}
	
	public void resetData(){
		simstate.resetData();
	}
	
	public void saveData(String fileName){
		simstate.saveData(fileName);
	}
	
	public void setBGColor(Color c){
		bgColor = c;
		int r = 255-bgColor.getRed();
		int g = 255-bgColor.getGreen();
		int b = 255-bgColor.getBlue();
		// bgColorI = new Color(r,g,b); perfect invert but looks ugly
		// choosing white or black depending on average saturation
		// >/< 1/2 the color scale (0.5*255)
		int gr = (r+g+b)/3;
		if (gr < 128) bgColorI = Color.black;
		else bgColorI = Color.white;
		render();
	}
	
	public void setHistoryLength(double l){
		simstate.setHistoryLength(l);
	}
	
	public void setHistoryDetail(int d){
		simstate.setHistoryDetail(d);
	}
	
	public void setDataDetail(int d){
		simstate.setDataDetail(d);
	}
	
	public void setHistoryGradient(boolean g){
		simstate.setHistoryGradient(g);
	}
	
	public void startTimer(){
		timerTime = System.currentTimeMillis();
	}
	
	public void stopTimer(){
		totalTime += System.currentTimeMillis() - timerTime;
	}
	
	public void resetTimer(){
		timerTime = 0;
		totalTime = 0;
	}
	
	public void randomSpawn(int n){
		for(int i = 0; i < n; i++){
			double x = (Math.random()*100.0) - 50.0;
			double y = (Math.random()*100.0) - 50.0;
			double vX = (Math.random()) - 0.5;
			double vY = Math.sqrt(1-(vX*vX));
			if(Math.random()>0.5) vY *= -1;
			double m = 1.0;
			double p = 10.0;
			Color c = Color.black;
			simstate.addPlanet(x, y, vX, vY, m, p, c);
		}
		System.out.println("spawned " + n + " planets");
	}
	
	public void testSimTime(int[] t){
		testIterations = t[0];
		if (testIterations < 1){
			testIterations = 1;
			System.out.println("number of iterations set to 1");
		}else{
			System.out.println("number of iterations set to " + testIterations);
		}
		for(int i = 1; i < t.length; i++){
			queue.add(t[i]);
			origionalQueue.add(t[i]);
		}
		randomSpawn(queue.get(0));
		currentIterations = 0;
	}
	
	public void saveTimings(String fileName){
		if(timings.size() > 0){
			String[] s = new String[origionalQueue.size()];
			int completion = 0;
			for(int i = 0; i < origionalQueue.size(); i++){
				double average = 0;
				for(int j = i * testIterations; 
						j < i * testIterations + testIterations; j++){
					average += timings.get(j);
				}
				average /= (double)testIterations;
				s[i] = origionalQueue.get(i) + " " + average;
				int newCompletion = 
					(int)(100.0*((double)i/(double)origionalQueue.size()));
				if(newCompletion != completion){
					completion = newCompletion;
					System.out.println(completion + "%");
				}
			}
			System.out.println("finished collecting data");
			FileWriter.writeFile(fileName, s);
		}else{
			System.out.println("no data to save");
		}
	}
}
