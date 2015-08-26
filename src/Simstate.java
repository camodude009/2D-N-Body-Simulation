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
			for(Planet p : thePlanets){
				p.tick(simMode, 1.0/(double)stepsPerTick);
			}
		}
	}
	
	public ArrayList<Planet> getThePlanets() {
		return thePlanets;
	}
	
	public void addPlanet(Planet p){
		thePlanets.add(p);
	}

}
