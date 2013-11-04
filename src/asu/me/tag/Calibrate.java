package asu.me.tag;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.jai.PerspectiveTransform;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import py4j.GatewayServer;

public class Calibrate {
	
	// Defines in which stage calibration is currently in
	public int calibrationStage = CalibrationStage.READY;
	
	PApplet parent;
 
	Boolean in_progress = false;
	Boolean complete = false;
	Polygon bounding_box = null;

	ArrayList<PVector> calibration_points = new ArrayList<PVector>();
	ArrayList<PVector> destination_points = new ArrayList<PVector>();
	
	public Quadrant[] quadrants = {
			new Q1(), 
			new Q2(),
			new Q3(),
			new Q4()
	};
	
	public CartesianPlane plane;

	PerspectiveTransform transform = null;

	public Calibrate(PApplet p) {
		this.parent = p;
		
		/*
			On a resolution of 1280x800, The first point is (642,2) and it's at the TOP. 
			The last point (642,798) is at the BOTTOM. Therefore, y increases as you go 
			down on the display.
		*/
		destination_points.add(new PVector(parent.width / 2 + 2, 0+2)); // Top left corner
		destination_points.add(new PVector(parent.width - 2, 0+2)); // Top right corner
		destination_points.add(new PVector(parent.width - 2, parent.height - 2)); // Bottom right corner
		destination_points.add(new PVector(parent.width / 2 + 2, parent.height - 2)); // Bottom left corner
		
		plane = new CartesianPlane(quadrants, new PVector(parent.width*0.75f, parent.height*0.5f), parent.width, parent.height);
		// Sharing position
		GatewayServer server = new GatewayServer(plane);
		server.start();
		
	}

	public void drawDestinationBoundingBox(ArrayList<PVector> dp) {
		/*
		 * Calculating the offset for each line in the grid. 
		 */
		int quadrantSize = 5;
		int parentWidthOffset = parent.width / 2 / (quadrantSize * 2); // parent.width / 2 / 10
		int parentHeightOffset = parent.height / (quadrantSize * 2); // parent.height / 10
		
		parent.stroke(0);
		parent.noFill();

		parent.quad(dp.get(0).x, dp.get(0).y, dp.get(1).x, dp.get(1).y,
				dp.get(2).x, dp.get(2).y, dp.get(3).x, dp.get(3).y);
		
		parent.stroke(200,200,200);
		parent.beginShape(PConstants.LINES);
		for (int i = 1; i < 10; i++) {
			// Drawing X lines
			parent.vertex(dp.get(0).x + (i*parentWidthOffset), dp.get(0).y);
			parent.vertex(dp.get(3).x + (i*parentWidthOffset), dp.get(3).y);
			// Drawing Y lines
			parent.vertex(dp.get(0).x, dp.get(0).y + (i*parentHeightOffset));
			parent.vertex(dp.get(1).x, dp.get(1).y + (i*parentHeightOffset));
		}
		parent.endShape();
		
	}

	public void calculateTransform(ArrayList<PVector> dp, ArrayList<PVector> cp) {
		System.out.println("Calculating perspective transform...");
		this.transform = PerspectiveTransform.getQuadToQuad(
				// Original points
				cp.get(0).x, cp.get(0).y, 
				cp.get(1).x, cp.get(1).y, 
				cp.get(2).x, cp.get(2).y, 
				cp.get(3).x, cp.get(3).y,
				// Target points
				dp.get(0).x, dp.get(0).y, 
				dp.get(1).x, dp.get(1).y, 
				dp.get(2).x, dp.get(2).y, 
				dp.get(3).x, dp.get(3).y);

		System.out.println(transform);
	}
	
	public void calculateTransforms() {
		for (Quadrant q : quadrants) {
			q.calculateTransform(destination_points);
		}
	}

	public PVector transformPoint(PVector _src_pt, PerspectiveTransform t) {
		Point2D.Float src_pt = new Point2D.Float(_src_pt.x, _src_pt.y);
		Point2D.Float dst_pt = new Point2D.Float();
		t.transform(src_pt, dst_pt);
		return new PVector(dst_pt.x, dst_pt.y);
	}
	
	public void drawCurrentLocation(PVector[] ir_points, int quadrant, int id, double c) {
		switch(quadrant){
			case 1: 
				parent.fill(0, 0, 255);
				break;
			case 2:
				parent.fill(0, 0, 192);
				break;
			case 3:
				parent.fill(0, 0, 128);
				break;
			case 4:
				parent.fill(0, 0, 64);
				break;
		}
		
		parent.ellipse(ir_points[0].x, ir_points[0].y, 10, 10);
		parent.ellipse(ir_points[1].x, ir_points[1].y, 10, 10);
		// Drawing line between them
		parent.stroke(200,200,200);
        parent.line(ir_points[0].x, ir_points[0].y, ir_points[1].x, ir_points[1].y);
		
//		if(p.x != Application.OUT_OF_BOUNDS && p.y != Application.OUT_OF_BOUNDS){
//			System.out.println("Quadrant " + quadrant + ": (" + p.x + "," + p.y + ")");
//		} else {
//			System.out.println("Quadrant " + quadrant + ": OUT OF BOUNDS");
//		}
		
		// Updating position provider
		// PositionProvider.getInstance().setXAndY(p.x, p.y);
		
		if(calibrationStage == CalibrationStage.COMPLETE){
//			PVector point = quadrants[quadrant-1].mapPoint(ir_points);
			quadrants[quadrant-1].currentPoint = ir_points[0];
			quadrants[quadrant-1].certainty = c;
//			parent.ellipse(point.x, point.y, 10, 10);
			
			for (int i = 0; i < quadrants.length; i++) {
				if(i == 3){
					System.out.println("Q" + (i+1) + ": " + (int)(quadrants[i].certainty*10));
				} else {
					System.out.print("Q" + (i+1) + ": " + (int)(quadrants[i].certainty*10) + " ||| ");
				}
			}
		}
	}
	
	public void drawTransformedLocation(PVector p, Quadrant q) {
		PVector dst_pt = transformPoint(p, q.transform);
		parent.fill(0, 255, 0);
		parent.ellipse(dst_pt.x, dst_pt.y, 10, 10);
	}

//	public void setCalibrationPoint(PVector p) {
//		System.out.println("Adding calibration point...");
//		System.out.println(p);
//
//		this.in_progress = false;
//
//		if (calibration_points.size() == 4) {
//			calibration_points.clear();
//			bounding_box = null;
//		}
//		calibration_points.add(p);
//
//		if (calibration_points.size() == 4) {
//			this.complete = true;
//			int[] x = {(int)calibration_points.get(0).x, (int)calibration_points.get(1).x, (int)calibration_points.get(2).x, (int)calibration_points.get(3).x};
//			int[] y = {(int)calibration_points.get(0).y, (int)calibration_points.get(1).y, (int)calibration_points.get(2).y, (int)calibration_points.get(3).y};
//			bounding_box = new Polygon(x,y,4);
//			calculateTransform(this.destination_points, this.calibration_points);
//		}
//	}

//	public void drawCalibrationPoints() {
//		parent.fill(255, 0, 0);
//		
//		for (int i = 0; i < calibration_points.size(); i++) {
//			parent.ellipse(calibration_points.get(i).x,
//					calibration_points.get(i).y, 10, 10);
//		}
//
//	}
	
//	public void drawCalibrationBoundingBox(ArrayList<PVector> cp) {
//		parent.stroke(0);
//		parent.noFill();
//
//		parent.quad(cp.get(0).x, cp.get(0).y, cp.get(1).x, cp.get(1).y,
//				cp.get(2).x, cp.get(2).y, cp.get(3).x, cp.get(3).y);
//	}


}
