package asu.me.tag;

import processing.core.PApplet;
import processing.core.PVector;

public class CartesianPlane {
	
	private Quadrant[] quadrants;
	private byte currentQuadrant = 0; // 0 is for quadrant I, 1 for II, etc...
	private PVector origin;
	private int width;
	private int height;
	
	public CartesianPlane(Quadrant[] quadrants, PVector origin, int width, int height) {
		this.quadrants = quadrants;
		this.origin = origin;
		this.width = width;
		this.height = height;
	}
	
	public PVector getCurrentPoint(){
		PVector point = quadrants[currentQuadrant].currentPoint;
		float x = point.x;
		float y = point.y;
		if(x >= origin.x && y <= origin.y){
			// Quadrant I
			currentQuadrant = 0;
		} else if(x < origin.x && y <= origin.y){
			// Quadrant II
			currentQuadrant = 1;
		} else if(x < origin.x && y > origin.y){
			// Quadrant IV
			currentQuadrant = 2;
		} else if(x >= origin.x && y > origin.y){
			// Quadrant IV
			currentQuadrant = 3;
		}
		return point;
	}
	
	public PVector getCurrentTransformedPoint(){
		PVector point = getCurrentPoint();
		float x = PApplet.map(point.x, width/2, width, -5, 5);
		float y = PApplet.map(point.y, height, 0, -5, 5);
		return new PVector(x,y);
	}
	
	// Same as 'getCurrentTransformedPoint()', but returning result as array
	public float[] getCurrentPositionAsArray(){
		PVector pos = getCurrentTransformedPoint();
		float[] ret = { pos.x, pos.y };
		return ret;
	}
	
}
