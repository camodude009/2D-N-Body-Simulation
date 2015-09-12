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
	public static double g = G;
	public static Color bgColor = Color.black;
	public static Color bgColorI = Color.white;
	private Simstate simstate;
	private boolean paused = true;
	private double stepsToDo = 0.0;
	private double speed = 60.0;
	private double stepSize = 1.0;
	private double stepsPerTick = 1.0;
	private double secondsPerTick = 1.0;
	private double scale = 1;
	private boolean realTime = true;
	private double totalElapsedTime = 0.0;
	private double targetTime = 0.0;
	private int completion = 0;
	private ArrayList<Double> momentum;
	private ArrayList<Double> eKin;
	private ArrayList<Double> ePot;
	private ArrayList<Double> energy;
	private ArrayList<Double> time;
	private boolean collectData = false;
	
	public Sim(boolean d, boolean rt) {
		debug = d;
		realTime = rt;
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
	    frame.setVisible(realTime);
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
			    		if(collectData){
		    				printEnergyError();
		    			}
		    		}
	        	}
		    }else{
		    	if(!paused){
		    		if(totalElapsedTime < targetTime || targetTime == 0.0){
		    			tick();
		    		}else{
			    		System.out.println("finished");
		    			pause();
		    			System.out.println("paused");
		    			if(collectData){
		    				printEnergyError();
		    			}
		    		}
		    	}
		    }
	    }
	}
	
	public void tick() {
		totalElapsedTime += secondsPerTick;
		if(targetTime != 0.0){
			if((int)(100*(totalElapsedTime/targetTime)) != completion){
				completion = (int)(100*(totalElapsedTime/targetTime));
				System.out.println(completion + "%");
			}
		}
		if(realTime){
			stepsToDo += stepsPerTick;
			while(stepsToDo >= 1){
				step();
				stepsToDo--;
			}
		}else{
			step();
		}
		if(collectData){
			collectData();
		}
	}
	
	public void step(){
		simstate.tick(stepSize);
	}
	
	public void render() {
		//checking dimensions
		dimensions = new Dimension(frame.getWidth(), frame.getHeight());
		//getting buffer strategy
		BufferStrategy strategy = this.getBufferStrategy(); 		
	    if(strategy == null){
	      createBufferStrategy(3);
	      return;
	    }	    
	    Graphics g = strategy.getDrawGraphics(); //getting the graphics object
	    Graphics2D g2d = (Graphics2D) g.create();
	    //drawcalls	
	    //background
	    g2d.setColor(bgColor);
	    g2d.fillRect(0,0,dimensions.width,dimensions.height); //filling background white
	    //rendering simstate
	    simstate.render(g2d, dimensions.width/2, dimensions.height/2, dimensions.width/scale);
	    //rendering debug fps/tps
	    g2d.setColor(bgColorI);
	    g2d.drawString("Tps: " + ticksPerSecond + " Fps: " + framesPerSecond, 20, 20);
	    
	    //disposing of the graphics object and sending image to video card   
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
		simstate = new Simstate();
		g = G;
		paused = true;
		stepsToDo = 0.0;
		speed = 60.0;
		stepSize = 1.0;
		stepsPerTick = 1.0;
		secondsPerTick = 1.0;
		scale = 1;
		totalElapsedTime = 0.0;
		targetTime = 0.0;
		resetData();
		render();
		System.out.println("sim reset");
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
		secondsPerTick = speed/tps;
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
	
	public void collectData(){
		double momentumX = 0;
		double momentumY = 0;
		double momentum = 0;
		double eKin = 0;
		double ePot = 0;
		double energy;
		for(Planet p: simstate.getPlanets()){
			momentumX += p.getMomentumX();
			momentumY += p.getMomentumY();
			eKin += p.getEKin();
		}
		for(int i = 0; i < simstate.getPlanets().size()-1; i++){
			Planet a = simstate.getPlanets().get(i);
			for (int j = i+1; j < simstate.getPlanets().size(); j++){
				Planet b = simstate.getPlanets().get(j);
				ePot += a.getEPot(b);
			}
		}
		energy = eKin + ePot;
		momentum = Math.sqrt(momentumX*momentumX+momentumY*momentumY);
		this.time.add(totalElapsedTime);
		this.momentum.add(momentum);
		this.eKin.add(eKin);
		this.ePot.add(ePot);
		this.energy.add(energy);
	}
	
	public void setCollectData(boolean c){
		if(!collectData && c){
			resetData();
			System.out.println("started collecting data");
		}else if(collectData && !c){
			System.out.println("stopped collecting data");
		}else if(collectData && c){
			System.out.println("already collecting data");
		}else{
			System.out.println("still not collecting data");
		}
		collectData = c;
	}
	
	public void resetData(){
		time = new ArrayList<Double>();
		momentum = new ArrayList<Double>();
		eKin = new ArrayList<Double>();
		ePot = new ArrayList<Double>();
		energy = new ArrayList<Double>();
		System.out.println("data reset");
	}
	
	public void saveData(String fileName){
		if(momentum.size() > 0){
			String[] s = new String[momentum.size()];
			for(int i = 0; i < momentum.size(); i++){
				s[i] = time.get(i) + " " + momentum.get(i) + " " + eKin.get(i) + " " + ePot.get(i) + " " + energy.get(i);
			}
			FileWriter.writeFile(fileName, s);
		}else{
			System.out.println("no data to save");
		}
	}
	
	public void setBGColor(Color c){
		bgColor = c;
		int r = 255-bgColor.getRed();
		int g = 255-bgColor.getGreen();
		int b = 255-bgColor.getBlue();
		//bgColorI = new Color(r,g,b); perfect invert but looks ugly
		//choosing white or black depending on average saturation >/< 1/2 the color scale (0.5*255)
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
	
	public void setHistoryGradient(boolean g){
		simstate.setHistoryGradient(g);
	}
	
	public void printEnergyError(){
		double last = energy.get(energy.size()-1);
		double initial = energy.get(0);
		System.out.println("energy error: " + (last-initial));
		System.out.println("% energy error: " + Math.abs(((last-initial)/initial)));
	}
}
