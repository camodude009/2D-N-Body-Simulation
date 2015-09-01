import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class Simstate {

	private ArrayList<Planet> thePlanets;
	private ArrayList<double[][]> history;
	private double historyLength = 0;
	private static int algorithm = 0;
	
	public Simstate(){
		thePlanets = new ArrayList<Planet>();
		history = new ArrayList<double[][]>();
	}
	
	public void render(Graphics g, int xOffset, int yOffset, double scale) {
		//for every step
		for(int i = 1; i < history.size()-1; i++){
			double[][]t = history.get(i);
			double[][]tOld = history.get(i-1);
			//drawing lines between past positions of planets
			for (int j = 0; j < t.length; j++){
				//mixing colors for paths
				Color p = thePlanets.get(j).getColor(); //planet color
				Color b = new Color(80,80,153); //grey-blue color
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
				double rf = r1*mix + r2*mix2;
				double gf = g1*mix + g2*mix2;
				double bf = b1*mix + b2*mix2;
				Color fc = new Color((int)rf, (int)gf, (int)bf);
				//setting the color and drawing lines from point to point
				g.setColor(fc);				
				g.drawLine(	(int)(tOld[j][0]*scale)+xOffset,
							(int)(tOld[j][1]*scale)+yOffset,
							(int)(t[j][0]*scale)+xOffset,
							(int)(t[j][1]*scale)+yOffset
						);
			}
			//rendering planets
			for(Planet p : thePlanets){
				p.render(g, xOffset, yOffset, scale);
			}
		}
		g.setColor(Sim.bgColorI);
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
		if(historyLength == -1 || history.size() < historyLength/t){
			history.add(getPlanetPositions());
		}else if(historyLength == 0){
			while(history.size() > 0){
				history.remove(0);
			}
		}else{
			while(history.size() > historyLength/t){
				history.remove(0);
			}
			history.add(getPlanetPositions());
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
	
	public void addPlanet(double x, double y, double vX, double vY, double m, double p, Color c){
		thePlanets.add(new Planet(x, y, vX, vY, m, p, c, this));
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
}
