import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;


public class Simstate {

	private ArrayList<Planet> thePlanets;
	private ArrayList<double[][]> history;
	private double historyLength = 0.0;
	private int tickCount = 0;
	private int historyDetail = 1;
	private int dataDetail = 1;
	private boolean historyGradient = false;
	private boolean collectData = false;
	private ArrayList<Double> momentum;
	private ArrayList<Double> eKin;
	private ArrayList<Double> ePot;
	private ArrayList<Double> energy;
	private ArrayList<Double> time;
	private int algorithm = 0;
	
	public Simstate(){
		thePlanets = new ArrayList<Planet>();
		history = new ArrayList<double[][]>();
	}
	
	public void render(Graphics2D g2d, int xOffset, int yOffset, double scale) {
		//for every step
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); //setting stroke of lines
		for(int i = 1; i < history.size(); i++){
			double[][]t = history.get(i);
			double[][]tOld = history.get(i-1);
			//drawing lines between past positions of planets
			for (int j = 0; j < t.length; j++){
				if(historyGradient){ //gradient paths
					//mixing colors for paths
					Color p = thePlanets.get(j).getColor(); //planet color
					Color b = Sim.bgColor; //bg color
					//gradient function: -(2x)^2+1 -> gradient from c1 to c2 where c1 is full at 0 and c2 is full at 1/2 the length of the array of points
					double mix = -1.0*(Math.pow(2.0*(double)(history.size()-i)/(double)history.size(), 2))+1.0; 
					if(mix < 0) mix = 0; 
					double mix2 = 1.0-mix;
					//getting rgb values of the two colors
					double r1 = p.getRed();
					double g1 = p.getGreen();
					double b1 = p.getBlue();				
					double r2 = b.getRed();
					double g2 = b.getGreen();
					double b2 = b.getBlue();
					//mixing the two based on the the gradient from earlier
					double rf = Math.sqrt(r1*r1*mix + r2*r2*mix2);
					double gf = Math.sqrt(g1*g1*mix + g2*g2*mix2);
					double bf = Math.sqrt(b1*b1*mix + b2*b2*mix2);
					Color fc = new Color((int)rf, (int)gf, (int)bf);
					//setting the color
					g2d.setColor(fc);
				}else{ //non gradient paths
					//setting the color
					g2d.setColor(thePlanets.get(j).getColor());
				}
				//drawing lines from point to point				
				g2d.drawLine(	(int)(tOld[j][0]*scale)+xOffset,
								(int)(tOld[j][1]*scale)+yOffset,
								(int)(t[j][0]*scale)+xOffset,
								(int)(t[j][1]*scale)+yOffset
						);
			}
		}
		//rendering planets
		for(Planet p : thePlanets){
			p.render(g2d, xOffset, yOffset, scale);
		}
		//rendering algorithm type
		g2d.setColor(Sim.bgColorI);
		switch (algorithm){
			case 0:
				g2d.drawString("Euler", 20, 40);
				break;
			case 1:
				g2d.drawString("Verlet", 20, 40);
				break;
		}
	}
	
	public void tick(double t) { //updates planets	
		switch (algorithm){
			case 0:
				tickEuler(t);
				break;
			case 1:
				tickVerlet(t);
				break;
			case 2:
				tickRK4(t);
				break;
			default:
				tickEuler(t);
				break;
		}
		tickCount++;
		if (tickCount % historyDetail == 0){
			if(historyLength == -1 || history.size() < historyLength/(t*(double)historyDetail)){
				history.add(getPlanetPositions());
			}else if(historyLength == 0){
				while(history.size() > 0){
					history.remove(0);
				}
			}else{
				while(history.size() > historyLength/(t*(double)historyDetail)){
					history.remove(0);
				}
				history.add(getPlanetPositions());
			}
		}
		if(collectData && (tickCount % dataDetail == 0)){
			collectData();
		}
	}
	
	public void tickEuler(double t){
		for(Planet p: thePlanets) p.updateAccelerationEuler();
		for(Planet p: thePlanets) p.updateVelocityEuler(t);	
		for(Planet p: thePlanets) p.updatePositionEuler(t);

	}
	
	public void tickVerlet(double t){
		for(Planet p: thePlanets) p.updatePositionVerlet(t);
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
		for(Planet p: thePlanets) p.updateVelocityVerlet(t);
	}
	
	public void tickRK4(double dt){
		//initialising
		State state = new State();
		for(Planet p: thePlanets){
			state.r.add(new Vector(p.getX(), p.getY()));
			state.v.add(new Vector(p.getvX(), p.getvY()));
		}
		Derivative derivative  = new Derivative();
		for(int i = 0; i < state.r.size(); i++){
			derivative.dr.add(state.v.get(i));
		}
		derivative.dv = acceleration(state);
		//rk4
		Derivative a,b,c,d;
		a = evaluate(state, 0.0, derivative);
		b = evaluate(state, dt*0.5, a);
		c = evaluate(state, dt*0.5, b);
		d = evaluate(state, dt, c);
		ArrayList<Vector> drdt = new ArrayList<Vector>();
		ArrayList<Vector> dvdt = new ArrayList<Vector>();
		//drdt = 1/6*(a.dr + 2*(b.dr + c.dr) + d.dr)
		//dvdt = 1/6*(a.vr + 2*(b.vr + c.vr) + d.vr)
		for(int i = 0; i < state.r.size(); i++){
			drdt.add(a.dr.get(i).add(
					 b.dr.get(i).add(c.dr.get(i))
					 	.multiply(2.0))
					 .add(d.dr.get(i))
				.divide(6.0)
			);
			dvdt.add(a.dv.get(i).add(
					 b.dv.get(i).add(c.dv.get(i))
					 	.multiply(2.0))
				 	 .add(d.dv.get(i))
				.divide(6.0)
			);
			thePlanets.get(i).updateRK4(drdt.get(i), dvdt.get(i), dt);
		}
	}
	
	public Derivative evaluate(State init, double dt, Derivative d){
		State state = new State();
		//updating the initial state by d * dt
		//state.r = initial.r + d.dx*dt;
	    //state.v = initial.v + d.dv*dt;
		for(int i = 0; i < init.r.size(); i++){
			state.r.add(init.r.get(i).add(d.dr.get(i).multiply(dt)));
			state.v.add(init.v.get(i).add(d.dv.get(i).multiply(dt)));
		}
		Derivative output = new Derivative();
		//calculating the derivatives from the new state
		//output.dr = state.v;
	    //output.dv = acceleration(state, t+dt);
		for(int i = 0; i < init.r.size(); i++){
			output.dr.add(state.v.get(i));
		}
		output.dv = acceleration(state);
		return output;
	}
	
	public ArrayList<Vector> acceleration(State state){
		ArrayList<Vector> a = new ArrayList<Vector>();
		for(int i = 0; i < state.r.size(); i++){
			Vector r1 = state.r.get(i);
			Vector acceleration = new Vector(0,0);
			for(int j = 0; j < state.r.size(); j++){
				if(i != j){
					Vector r2 = state.r.get(j);
					Vector d = r2.subtract(r1);
					//a = (G*m)/(d*d)
					//ax = dx*(G*m*)/(d*d*d)
					acceleration.addS(new Vector(d.x,d.y)
					.multiply(Sim.g*thePlanets.get(j).getM()/d.length3())
					);
				}
			}
			a.add(acceleration);
		}
		return a;
	}
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p, Color c){
		thePlanets.add(new Planet(x, y, vX, vY, m, p, c, this));
		history = new ArrayList<double[][]>();
		//recalculating initial acceleration
		for(Planet a: thePlanets) a.updateAccelerationVerlet();
	}
	
	public ArrayList<Planet> getPlanets() {
		return thePlanets;
	}
	
	public String[] getPlanetsAsText(){
		String[] r = new String[thePlanets.size()];
		for(int i = 0; i < thePlanets.size(); i++){
			Planet p = thePlanets.get(i);
			r[i] = "addplanet " + p.getX() + " " + p.getY() + " " + p.getvX() + " " + p.getvY() + " " + p.getM() + " " + p.getP();
		}
		return r;
	}
	
	public double[][] getPlanetPositions(){
		double[][]r = new double[thePlanets.size()][2];
		for(int i = 0; i < thePlanets.size(); i++){
			r[i][0] = thePlanets.get(i).getX();
			r[i][1] = thePlanets.get(i).getY();
		}
		return r;
	}

	public void setAlgorithm(int a){
		switch (a){
			case 0:
				algorithm = a;
				System.out.println("using euler");
				break;
			case 1:
				algorithm = a;
				System.out.println("using verlet");
				break;
			case 2:
				algorithm = a;
				System.out.println("using rk4");
				break;
			default:
				System.out.println("invalid algorithm");
				break;
		}
		//update Acceleration in case of Algorithm switch
		//since RK4 does not store accelerations in Planet
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
	}
	
	public void setHistoryLength(double l){
		historyLength = l;
		if(l == -1){
			System.out.println("history set to infinite (not recommended!)");
		}else if(l == 0){
			System.out.println("history turned off");
		}else{
			System.out.println("history set to " + historyLength + "s");
		}
	}
	
	public void setHistoryDetail(int d){
		if(d > 0){
			historyDetail = d;
			System.out.println("history detail set to " + d);
		}else{
			System.out.println("invalid history detail");
		}
	}
	
	public void setHistoryGradient(boolean g){
		historyGradient = g;
		if(historyGradient)System.out.println("gradient on");
		else System.out.println("gradient off");
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
	
	public void collectData(){
		double momentumX = 0;
		double momentumY = 0;
		double momentum = 0;
		double eKin = 0;
		double ePot = 0;
		double energy;
		for(Planet p: thePlanets){
			momentumX += p.getMomentumX();
			momentumY += p.getMomentumY();
			eKin += p.getEKin();
		}
		for(int i = 0; i < thePlanets.size()-1; i++){
			Planet a = thePlanets.get(i);
			for (int j = i+1; j < thePlanets.size(); j++){
				Planet b = thePlanets.get(j);
				ePot += a.getEPot(b);
			}
		}
		energy = eKin + ePot;
		momentum = Math.sqrt(momentumX*momentumX+momentumY*momentumY);
		this.time.add(Sim.totalElapsedTime);
		this.momentum.add(momentum);
		this.eKin.add(eKin);
		this.ePot.add(ePot);
		this.energy.add(energy);
	}
	
	public void saveData(String fileName){
		if(momentum.size() > 0){
			String[] s = new String[momentum.size()+1];
			double eKinAv = 0;
			double ePotAv = 0;
			double energyAv = 0;
			s[0] = "Time Momentum EKin EPot ETotal EKinAverage EPotAverage ETotalAverage";
			int completion = 0;
			for(int i = 0; i < momentum.size(); i++){
				eKinAv += eKin.get(i);
				ePotAv += ePot.get(i);
				energyAv += energy.get(i);
				s[i+1] = time.get(i) + " " + momentum.get(i) + " " + eKin.get(i) + " " + ePot.get(i) + " " + energy.get(i) + " "
						+ (eKinAv/(i+1)) + " " + (ePotAv/(i+1)) + " " + (energyAv/(i+1));
				
				int newCompletion = (int)(100.0*((double)i/(double)momentum.size()));
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
	
	public void setDataDetail(int d){
		if(d > 0){
			dataDetail = d;
			System.out.println("data detail set to " + d);
		}else{
			System.out.println("invalid data detail");
		}
	}
	
	public void printEnergyError(){
		if(collectData){
			double last = energy.get(energy.size()-1);
			double initial = energy.get(0);
			double average = 0;
			for(Double e: energy){
				average += e;
			}
			average /= energy.size();
			System.out.println("Initial/Last");
			System.out.println("  energy error: " + (last-initial));
			System.out.println("  % energy error: " + Math.abs(((last-initial)/initial)));
			System.out.println("Initial/Average");
			System.out.println("  energy error): " + (average-initial));
			System.out.println("  % energy error: " + Math.abs(((average-initial)/initial)));
		}
	}
	
}
