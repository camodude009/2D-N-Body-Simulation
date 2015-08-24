import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class Planet {

	private double x,y,vX,vY,aX,aY,m,p,r,f; //x,y coordinates, speed, mass, density, radius
	private Gamestate gamestate;
	
	public Planet(double x, double y, double vX, double vY, double m, double p, Color color, Gamestate gamestate){
		this.x = x;
		this.y = y;
		this.vX = vX;
		this.vY = vY;
		this.m = m;
		this.p = p;
		this.gamestate = gamestate;
		calculateRadius();
	}
	
	public void tick(int simMode, double t){
		switch (simMode){
			case 0:
				tickEuler(t);
				break;
			case 1:
				tickVerlet(t);
				break;
			case 2:
				break;
		}
	}
	
	public void tickEuler(double t){
		
		//acceleration		
		aX = 0;
		aY = 0;
		for(Planet p : gamestate.getThePlanets()){
			if(p != this){
				double a = p.getM()/getDS(p);
				aX += (getDX(p)/getD(p)*a);
				aY += (getDY(p)/getD(p)*a);
			}
		}
		
		//velocity
		vX += aX*t;
		vY += aY*t;
		
		//position
		x += vX*t;
		y += vY*t;
		
	}
	
	public void tickVerlet(double t){
		
		//acceleration		
		aX = 0;
		aY = 0;
		for(Planet p : gamestate.getThePlanets()){
			if(p != this){
				double a = p.getM()/getDS(p);
				aX += (getDX(p)/getD(p)*a);
				aY += (getDY(p)/getD(p)*a);
			}
		}
		
		//velocity 1/2 way
		vX += 0.5*aX*t;
		vY += 0.5*aY*t;
		
		
		//position
		x += t*vX;
		y += t*vY;
		
		//acceleration		
		aX = 0;
		aY = 0;
		for(Planet p : gamestate.getThePlanets()){
			if(p != this){
				double a = p.getM()/getDS(p);
				aX += (getDX(p)/getD(p)*a);
				aY += (getDY(p)/getD(p)*a);
			}
		}
		
		//velocity 1/2 way
		vX += 0.5*aX*t;
		vY += 0.5*aY*t;	
		
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
		r = Math.pow((Math.abs((4*m)/(3*Math.PI*p))), (1.0/3.0));
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
	
	public void render(Graphics g) { //draws a circle
		g.setColor(Color.black);
	    g.fillOval((int)(x-r),(int)(y-r),(int)(r*2),(int)(r*2));
	}
	
}
