import java.awt.Color;
import java.awt.Graphics;


public class Planet {

	private double x,y,vX,vY,aX,aY,aXOld,aYOld,m,p,r,f; //x,y coordinates, speed, mass, density, radius
	private Simstate simstate;
	
	public Planet(double x, double y, double vX, double vY, double m, double p, Simstate simstate){
		this.x = x;
		this.y = y;
		this.vX = vX;
		this.vY = vY;
		this.m = m;
		this.p = p;
		this.simstate = simstate;
		calculateRadius();
	}
	
	public void updateAccelerationEuler(){
		aX = 0;
		aY = 0;
		for(Planet p : simstate.getPlanets()){
			if(p != this){
				double a = Sim.G*p.getM()/getDS(p);
				aX += (getDX(p)/getD(p)*a);
				aY += (getDY(p)/getD(p)*a);
			}
		}
	}
	
	public void updateVelocityEuler(double t){
		vX += aX*t;
		vY += aY*t;
	}
	
	public void updatePositionEuler(double t){
		x += vX*t;
		y += vY*t;
	}
	
	public void updateAccelerationVerlet(){	
		aXOld = aX;
		aYOld = aY;
		aX = 0;
		aY = 0;
		for(Planet p : simstate.getPlanets()){
			if(p != this){
				double a = Sim.G*p.getM()/getDS(p);
				aX += (getDX(p)/getD(p)*a);
				aY += (getDY(p)/getD(p)*a);
			}
		}
	}
	
	public void updatePositionVerlet(double t){
		x += t*(vX+(0.5*aX*t));
		y += t*(vY+(0.5*aY*t));
	}
	
	public void updateVelocityVerlet(double t){
		vX += 0.5*(aX + aXOld)*t;
		vY += 0.5*(aY + aYOld)*t;
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
	private double getVS(){
		return (vX*vX)+(vY*vY);
	}
	private double getV(){
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
	private void calculateRadius() {
		r = Math.pow((Math.abs((4*m*Sim.G)/(3*Math.PI*p))), (1.0/3.0));
	}
	private double getDX(Planet p){
		return p.getX()-this.x;
	}
	private double getDY(Planet p){
		return p.getY()-this.y;
	}
	private double getDS(Planet p){
		return (getDX(p)*getDX(p) + getDY(p)*getDY(p));
	}
	private double getD(Planet p){
		return Math.sqrt(getDS(p));
	}
	private double getEKin(){
		return 0.5*m*getVS();
	}
	private double getEPot(Planet p){
		return Sim.G*m*p.getM()/getD(p);
	}
	
	public void render(Graphics g) { //draws a circle
		g.setColor(Color.black);
	    g.fillOval((int)(x-r),(int)(y-r),(int)(r*2),(int)(r*2));
	}
	
}
