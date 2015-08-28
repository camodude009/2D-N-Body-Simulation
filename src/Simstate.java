import java.awt.Graphics;
import java.util.ArrayList;


public class Simstate {

	private ArrayList<Planet> thePlanets;
	private ArrayList<int[][]> history;
	private static int algorithm = 0;
	
	public Simstate(){
		thePlanets = new ArrayList<Planet>();
		history = new ArrayList<int[][]>();
	}
	
	public void render(Graphics g) {
		//rendering planets
		for(Planet p : thePlanets){
			p.render(g);
		}
		//for every step
		for(int i = 1; i < history.size()-1; i++){
			int[][]t = history.get(i);
			//drawing lines between past positions of planets
			for (int j = 0; j < t.length; j++){
				g.drawLine(	history.get(i-1)[j][0],
							history.get(i-1)[j][1],
							history.get(i)[j][0],
							history.get(i)[j][1]
						);
			}
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
		default:
			tickEuler(t);
			break;
		}
		history.add(getPlanetPositions());
	}
	
	public void tickEuler(double t){
		for(Planet p: thePlanets) p.updateAccelerationEuler();
		for(Planet p: thePlanets) p.updatePositionEuler(t);
		for(Planet p: thePlanets) p.updateVelocityEuler(t);	
	}
	
	public void tickVerlet(double t){
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
		for(Planet p: thePlanets) p.updatePositionVerlet(t);
		for(Planet p: thePlanets) p.updateAccelerationVerlet();
		for(Planet p: thePlanets) p.updateVelocityVerlet(t);
	}
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p){
		thePlanets.add(new Planet(x, y, vX, vY, m, p, this));
		history = new ArrayList<int[][]>();
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
	
	public int[][] getPlanetPositions(){
		int[][]r = new int[thePlanets.size()][2];
		for(int i = 0; i < thePlanets.size(); i++){
			r[i][0] = (int) thePlanets.get(i).getX();
			r[i][1] = (int) thePlanets.get(i).getY();
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
			default:
				System.out.println("invalid algorithm");
				break;
		}
	}
}
