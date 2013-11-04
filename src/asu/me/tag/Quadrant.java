package asu.me.tag;

import java.util.List;

import javax.media.jai.PerspectiveTransform;

import processing.core.PVector;

public abstract class Quadrant {
	
	/*
	 * 
	 * COMPL1 ------------ EDGE
	 * |					  |
	 * |					  |
	 * |					  |
	 * |					  |
	 * |					  |
	 * CENTER (0,0)----- COMPL2
	 * 
	 */
	
	public static int CENTER = 0;
	public static int EDGE = 1;
	public static int COMPL1 = 2;
	public static int COMPL2 = 3;

	public PVector currentPoint;
	public PVector[] points;
	public PVector[] boundaryPoints;
	public PerspectiveTransform transform;
	public double certainty;
	
	public Quadrant(){
		points = new PVector[4];
		currentPoint = new PVector(0,0);
	}
	
	public void setCenterPoint(PVector point){
		points[CENTER] = point;
	}
	
	public void setEdgePoint(PVector point){
		points[EDGE] = point;
		setComplementaryPoints();
	}
	
	public void calculateTransform(List<PVector> dPoints){
		boundaryPoints = getQuadrantPoints(dPoints);
		
//		this.transform = PerspectiveTransform.getQuadToQuad(
//				// Original points
//				points[0].x, points[0].y, 
//				points[1].x, points[1].y, 
//				points[2].x, points[2].y, 
//				points[3].x, points[3].y,
//				// Target points
//				qPoints.get(0).x, qPoints.get(0).y, 
//				qPoints.get(1).x, qPoints.get(1).y, 
//				qPoints.get(2).x, qPoints.get(2).y, 
//				qPoints.get(3).x, qPoints.get(3).y);
	}
	
	public abstract PVector mapPoint(PVector point);
	
	public abstract PVector[] getQuadrantPoints(List<PVector> points);
	
	private void setComplementaryPoints(){
		if(points[CENTER] != null && points[EDGE] != null){
			// Setting COMPL1
			points[COMPL1] = new PVector();
			points[COMPL1].x = points[CENTER].x;
			points[COMPL1].y = points[EDGE].y;
			// Setting COMPL2
			points[COMPL2] = new PVector();
			points[COMPL2].x = points[EDGE].x;
			points[COMPL2].y = points[CENTER].y;
		}
		
		System.out.println("CENTER: " + points[CENTER]);
		System.out.println("EDGE  : " + points[EDGE]);
		System.out.println("COMPL1: " + points[COMPL1]);
		System.out.println("COMPL2: " + points[COMPL2]);
	}
 
}
