package asu.me.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PVector;
import asu.me.tag.util.Util;

public class CartesianPlane {
	
	private Quadrant[] quadrants;
	private int currentQuadrant = 0; // 0 is for quadrant I, 1 for II, etc...
//	private PVector origin;
	private int width;
	private int height;
//	private float orientationDelta = 0;
	List<Float> previousAngles = new ArrayList<Float>();
	private static final int MEDIAM_BUFFER_SIZE = 100;
	
	
	
	public CartesianPlane(Quadrant[] quadrants, PVector origin, int width, int height) {
		this.quadrants = quadrants;
//		this.origin = origin;
		this.width = width;
		this.height = height;
	}
	
	public int getCurrentQuadrant(){
		return currentQuadrant;
	}
	
	public PVector[] getCurrentPoint(){
//		PVector point = quadrants[currentQuadrant].currentPoint;
//		float x = point.x;
//		float y = point.y;
//		if(x >= origin.x && y <= origin.y){
//			// Quadrant I
//			currentQuadrant = 0;
//		} else if(x < origin.x && y <= origin.y){
//			// Quadrant II
//			currentQuadrant = 1;
//		} else if(x < origin.x && y > origin.y){
//			// Quadrant III
//			currentQuadrant = 2;
//		} else if(x >= origin.x && y > origin.y){
//			// Quadrant IV
//			currentQuadrant = 3;
//		}
		//reassign the current quadrant based off of average certainty (held in 0)
		Quadrant current = quadrants[currentQuadrant];
		for (int i = 0; i < 3; i++) {
			if((int)(current.certainty[0]*10) >= (int)(quadrants[i].certainty[0]*10)){
				current = quadrants[i];
				currentQuadrant = i;
			}
		}
//		System.out.println("Current QUAD: " + currentQuadrant);
		return current.currentPoint;
	}
	
	public PVector[] getCurrentTransformedPoint(){
		Quadrant q = quadrants[currentQuadrant]; 
		PVector[] point = getCurrentPoint(); // TODO
//		float x = PApplet.map(point.x, width/2, width, -5, 5);
//		float y = PApplet.map(point.y, height, 0, -5, 5);
		return new PVector[]{q.mapPoint(point[0]), q.mapPoint(point[1])};
	}
	
	// Same as 'getCurrentTransformedPoint()', but returning result as array
	public float[] getCurrentPositionAsArray(){
		PVector[] pos = getCurrentTransformedPoint(); // TODO make work with array
		PVector mid = Util.getMidPoint(pos);
		float x = Application.map(mid.x, width/2, width, -5, 5);
		float y = Application.map(mid.y, height, 0, -5, 5);
		
		float[] ret = { x, y };
		return ret;
	}
	
	public float getCurrentOrientation(){
		PVector[] point = getCurrentTransformedPoint();
		int first = 0;
		int second = 1;
		boolean swap;
		float angle = Float.MAX_VALUE;
		float median = 0f;
		
		if(!(width - point[first].x < 0 || width - point[second].x < 0)){
			
			float dX = point[first].x - point[second].x;
			float dY = point[second].y - point[first].y;
			angle = (float) ((Math.atan2(dY, dX) * 180) / Math.PI);
			if (angle < 0){
				angle = angle + 360;
			}
			// Calculating angle median
//			addAngle(angle);
//			median = getAngleMedian();			
//			orientationDelta = Math.abs(Math.abs(angle) - median);
//			swap = orientationDelta > 170;
//			if(swap){
//				first = Math.abs(first - 1);
//				second = Math.abs(second - 1); 
//				System.out.println("SWAP");
//			} else if (orientationDelta != 0){
////				System.out.println(orientationDelta);			
//			} 
			
//			System.out.println(median);
			
			
//			System.out.println(angle + (swap ? ", SWAP" : ""));
		} else {
			// Handle missing point
		}
		return angle;
	}
	
	private void addAngle(Float angle){
		if(previousAngles.size() == MEDIAM_BUFFER_SIZE){
			previousAngles.remove(0);
		}
		previousAngles.add(angle);
		
	}
	
	private Float getAngleMedian(){
		if(previousAngles.size() == 0){
			return 0f;
		} else {
			// TODO optimize
			// Duplicating array
			List<Float> a = new ArrayList<Float>();
			for (Float f : previousAngles) {
				a.add(f);
			}
			// Sorting
			Collections.sort(a);
			return (Float) a.toArray()[(int) Math.ceil(a.size()/2)];
		}
	}
}
