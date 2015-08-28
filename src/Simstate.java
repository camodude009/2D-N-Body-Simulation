import java.awt.Graphics;
import java.util.ArrayList;


public class Simstate {

	private ArrayList<Planet> thePlanets;
	private ArrayList<double[][]> history;
	private static int algorithm = 0;
	
	public Simstate(){
		thePlanets = new ArrayList<Planet>();
		history = new ArrayList<double[][]>();
	}
	
	public void render(Graphics g, int xOffset, int yOffset, double scale) {
		//rendering planets
		for(Planet p : thePlanets){
			p.render(g, xOffset, yOffset, scale);
		}
		//for every step
		for(int i = 1; i < history.size()-1; i++){
			double[][]t = history.get(i);
			//drawing lines between past positions of planets
			for (int j = 0; j < t.length; j++){
				g.drawLine(	(int)(history.get(i-1)[j][0]*scale)+xOffset,
							(int)(history.get(i-1)[j][1]*scale)+yOffset,
							(int)(history.get(i)[j][0]*scale)+xOffset,
							(int)(history.get(i)[j][1]*scale)+yOffset
						);
			}
		}
		switch (algorithm){
			case 0:
				g.drawString("Euler", 20, 40);
				break;
			case 1:
				g.drawString("Verlet", 20, 40);
				break;
		}
	}
	
	public void tick(double t) { //updates planets
		history.add(getPlanetPositions());
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
		history = new ArrayList<double[][]>();
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
			default:
				System.out.println("invalid algorithm");
				break;
		}
	}
}
