package asu.me.tag.orientation;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import asu.me.tag.Calibrate;

public class Application extends PApplet {

	private static final long serialVersionUID = 8412123833928508734L;

	Calibrate calibrate;
	OscP5 osc;
	
	PVector[] points = {null, null}; 

	public static final String OSC_IR_1_DATA_STRING = "/wii/1/ir/xys/1";//"/wii/2/ir/xys/1";
	public static final String OSC_IR_2_DATA_STRING = "/wii/1/ir/xys/2";//"/wii/2/ir/xys/2";
	
	public static final int OSC_PORT= 9000;
	public PVector[] ir_points = {null, null, null, null};
	
	public static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
	
	public void setup() {
		// Define size of output canvas
		size(600, 600);
		
		osc = new OscP5(this, OSC_PORT);

		osc.plug(this, "parseIRMessage1", OSC_IR_1_DATA_STRING);
		osc.plug(this, "parseIRMessage2", OSC_IR_2_DATA_STRING);
		
		smooth();
		
		
				
	}

	public void draw() {
		// Drawing the background
		noStroke();
		fill( 255, 255, 255, 50);
        rect(0, 0, width, height);
        
        // Drawing each point
        fill(0, 0, 255);
        for (PVector p : points) {
            ellipse(p.x, p.y, 10, 10);
		}
        
        // Drawing line between points
        stroke(200,200,200);
		beginShape(PConstants.LINES);
		for (PVector p : points){
			vertex(p.x, p.y);
		}
		endShape();
		
		// Calculate angle
		float dX = points[1].x - points[0].x;
		float dY = points[1].y - points[0].y;
		Double angle = Math.atan2(dY, dX) * 180 / Math.PI;
		text(angle.toString(), 10, 15);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "asu.me.tag.orientation.Application" });
	}
	
	public void parseIRMessage1(float _x, float _y, float _size) {
		System.out.println("#1: " + _x + "," + _y);
		float x = map(_x, 0, 1, 0, width);
        float y = map(_y, 0, 1, 0, width);
		points[0] = new PVector(x, y);
	}
	
	public void parseIRMessage2(float _x, float _y, float _size) {
		System.out.println("#2: " + _x + "," + _y);
		float x = map(_x, 0, 1, 0, width);
        float y = map(_y, 0, 1, 0, width);
		points[1] = new PVector(x, y);
	}
	
}
