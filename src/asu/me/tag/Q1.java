package asu.me.tag;

import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Q1 extends Quadrant {

	@Override
	public PVector[] getQuadrantPoints(List<PVector> points) {
		PVector[] dPoints = new PVector[4];
		
		dPoints[0] = new PVector(points.get(0).x + (points.get(0).x/2), points.get(0).y);
		dPoints[1] = points.get(1);
		dPoints[2] = new PVector(points.get(2).x, points.get(2).y - (points.get(2).y / 2));
		dPoints[3] = new PVector(points.get(3).x + (points.get(3).x/2), points.get(2).y - (points.get(2).y / 2));
		
		return dPoints;
	}

	@Override
	public PVector mapPoint(PVector point){
		float x = PApplet.map(point.x, points[CENTER].x, points[EDGE].x, boundaryPoints[0].x, boundaryPoints[1].x);
		float y = PApplet.map(point.y, points[CENTER].y, points[EDGE].y, boundaryPoints[2].y, boundaryPoints[0].y);
		return new PVector(x, y);
	}
	
}
