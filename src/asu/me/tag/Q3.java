package asu.me.tag;

import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Q3 extends Quadrant {

	@Override
	public PVector[] getQuadrantPoints(List<PVector> points) {
		PVector[] dPoints = new PVector[4];
		
		float half = points.get(0).x/2;
		
		dPoints[0] = new PVector(points.get(0).x, points.get(0).y + half);
		dPoints[1] = new PVector(points.get(1).x - half, points.get(1).y + half);
		dPoints[2] = new PVector(points.get(2).x - half, points.get(2).y);
		dPoints[3] = points.get(3);
		
		return dPoints;
	}

	@Override
	public PVector mapPoint(PVector point){
		float x = PApplet.map(point.x, points[EDGE].x, points[CENTER].x, boundaryPoints[0].x, boundaryPoints[1].x);
		float y = PApplet.map(point.y, points[EDGE].y, points[CENTER].y, boundaryPoints[2].y, boundaryPoints[0].y);
		return new PVector(x, y);
	}
	
}
