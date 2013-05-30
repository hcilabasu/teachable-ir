import processing.core.*;
import oscP5.*;

public class Application extends PApplet {

	Calibrate calibrate;
	OscP5 osc;

	public static final String OSC_IR_1_DATA_STRING = "/wii/2/ir/xys/1";
	public static final String OSC_IR_2_DATA_STRING = "/wii/2/ir/xys/2";
	public static final int OSC_PORT= 9000;
	public PVector[] ir_points = {null, null};
	
	public void setup() {
		size(displayWidth/2, displayHeight/2);
		
		
		osc = new OscP5(this, OSC_PORT);
		osc.plug(this, "parseIRMessage1", OSC_IR_1_DATA_STRING);
		osc.plug(this, "parseIRMessage2", OSC_IR_2_DATA_STRING);
		calibrate = new Calibrate(this);
		smooth();
				
	}

	public void draw() {
		noStroke();
        fill( 255, 255, 255, 50);
        rect(0, 0, width, height);
		calibrate.drawCalibrationPoints();
		calibrate.drawDestinationBoundingBox(calibrate.destination_points);
		if (calibrate.complete && calibrate.calibration_points.size() == 4) {
			calibrate.drawCalibrationBoundingBox(calibrate.calibration_points);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Application" });
	}

	public void keyPressed() {

		if (key == 'C' || key == 'c') {
			calibrate.in_progress = true;
			calibrate.complete = false;
		}

	}
	
	public PVector mapIRPoint(float _x, float _y) {
		float x = map(_x, 0, 1, 2, width/2 - 2);
		float y = map(_y, 0, 1, 2, height - 2);
		
		return new PVector(x,y);
	}

	public void parseIRMessage1(float _x, float _y, float _size) {	
		PVector p = mapIRPoint(_x, _y);
		
		if (calibrate.in_progress) {
			calibrate.setCalibrationPoint(p);
			System.out.println(calibrate.calibration_points);
		}
		calibrate.drawCurrentLocation(p);
		
		ir_points[0] = p;

		
	}
	
	public void parseIRMessage2(float _x, float _y, float _size) {
		PVector p = mapIRPoint(_x, _y);
		PVector r = new PVector();
		
		ir_points[1] = p;
		
		if (ir_points[0] != null) {
			r = PVector.add(ir_points[0], ir_points[1]);
			stroke(0);
			//line(width/4,height/2, r.x, r.y);
		}
		ir_points[0] = ir_points[1] = null;
	}
}
