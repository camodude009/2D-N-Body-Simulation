import java.awt.Color;
import java.awt.Graphics2D;

public class Planet {

	private double x, y, vX, vY, aX, aY, aXOld, aYOld, m, p, r;
	// x,y coordinates, speed, mass, density, radius
	private Color c;
	private Simstate simstate;

	public Planet(double x, double y, double vX, double vY, double m,
			double p, Color c, Simstate simstate) {
		this.x = x;
		this.y = y;
		this.vX = vX;
		this.vY = vY;
		this.m = m;
		this.p = p;
		this.c = c;
		this.simstate = simstate;
		calculateRadius();
	}

	public void updateAccelerationEuler() {
		aX = 0;
		aY = 0;
		for (Planet p : simstate.getPlanets()) {
			if (p != this) {
				double a = Sim.g * p.getM() / getDS(p);
				aX += (getDX(p) / getD(p) * a);
				aY += (getDY(p) / getD(p) * a);
			}
		}
	}

	public void updateVelocityEuler(double t) {
		vX += aX * t;
		vY += aY * t;
	}

	public void updatePositionEuler(double t) {
		x += vX * t;
		y += vY * t;
	}

	public void updateAccelerationVerlet() {
		aXOld = aX;
		aYOld = aY;
		aX = 0;
		aY = 0;
		for (Planet p : simstate.getPlanets()) {
			if (p != this) {
				double a = Sim.g * p.getM() / getDS(p);
				aX += (getDX(p) / getD(p) * a);
				aY += (getDY(p) / getD(p) * a);
			}
		}
	}

	public void updatePositionVerlet(double t) {
		x += t * (vX + (0.5 * aX * t));
		y += t * (vY + (0.5 * aY * t));
	}

	public void updateVelocityVerlet(double t) {
		vX += 0.5 * (aX + aXOld) * t;
		vY += 0.5 * (aY + aYOld) * t;
	}

	public void updateRK4(Vector drdt, Vector dvdt, double t){
		// aktualisiert x, y, vX, vY um die Ableitungen
		// der Position und Geschwindigkeit * Zeitschritt
		x += drdt.x * t;
		y += drdt.y * t;
		vX += dvdt.x * t;
		vY += dvdt.y * t;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getvX() {
		return vX;
	}

	public void setvX(double vX) {
		this.vX = vX;
	}

	public double getvY() {
		return vY;
	}

	public void setvY(double vY) {
		this.vY = vY;
	}

	public double getVS() {
		return (vX * vX) + (vY * vY);
	}

	public double getV() {
		return Math.sqrt(getVS());
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public double getR() {
		return r;
	}

	public void calculateRadius() {
		r = Math.pow((Math.abs(
				(3.0 * m * Sim.g) / (4.0 * Math.PI * p))),
				(1.0 / 3.0));
	}

	public double getDX(Planet p) {
		return p.getX() - this.x;
	}

	public double getDY(Planet p) {
		return p.getY() - this.y;
	}

	public double getDS(Planet p) {
		return (getDX(p) * getDX(p) + getDY(p) * getDY(p));
	}

	public double getD(Planet p) {
		return Math.sqrt(getDS(p));
	}

	public double getEKin() {
		// 0.5* Masse * (Betrag der Geschwindigkeit)^2
		return 0.5 * m * getVS();
	}

	public double getEPot(Planet p) {
		// (-1 * G * Rigenmasse * Fremdmasse)/Distanz
		return -1 * Sim.g * m * p.getM() / getD(p);
	}

	public double getMomentumX() {
		return m * vX;
	}

	public double getMomentumY() {
		return m * vY;
	}

	public Color getColor() {
		return c;
	}

	public void render(Graphics2D g2d, int xOffset, int yOffset, double scale) {
		// draws a circle
		g2d.setColor(c);
		double rNew = r * scale;
		g2d.fillOval((int) ((x * scale) - rNew) + xOffset,
				(int) ((y * scale) - rNew) + yOffset, (int) (rNew * 2),
				(int) (rNew * 2));
	}

}
