import java.awt.Graphics;
import java.util.ArrayList;


public class Simstate {

	private ArrayList<Planet> thePlanets;
	private static int simMode = 0;
	private static double stepsPerTick = 1;
	
	public Simstate(){
		thePlanets = new ArrayList<Planet>();
	}
	
	public void render(Graphics g) {	
		for(Planet p : thePlanets){
			p.render(g);
		}
	}
	
	public void tick() { //updates planets "stepsPerTick" times
		for(int i = 0; i < stepsPerTick; i++){
			switch (simMode){
			case 0:
				tickEuler(1.0/(double)stepsPerTick);
				break;
			case 1:
				tickEuler(1.0/(double)stepsPerTick);
				break;
			case 2:
				break;
			}
		}
	}
	
	public void tickEuler(double t){
		for(Planet p: thePlanets) p.updateAccelerationEuler();		
		for(Planet p: thePlanets) p.updateVelocityEuler(t);		
		for(Planet p: thePlanets) p.updatePositionEuler(t);
	}
	
	public void tickVerlet(double t){
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
		for(Planet p: thePlanets) p.updatePositionVerlet(t);
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
		for(Planet p: thePlanets) p.updateVelocityVerlet(t);
	}
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p){
		thePlanets.add(new Planet(x, y, vX, vY, m, p, this));
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

}
