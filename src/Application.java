import processing.core.*;
import py4j.GatewayServer;
import oscP5.*;

public class Application extends PApplet {

	private static final long serialVersionUID = 8412123833928508734L;

	Calibrate calibrate;
	OscP5 osc;

	public static final String OSC_IR_1_DATA_STRING = "/wii/1/ir/xys/1";//"/wii/2/ir/xys/1";
	public static final String OSC_IR_2_DATA_STRING = "/wii/2/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_3_DATA_STRING = "/wii/3/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_4_DATA_STRING = "/wii/4/ir/xys/1";//"/wii/2/ir/xys/2";
	
	public static final int OSC_PORT= 9000;
	public PVector[] ir_points = {null, null};
	
	public static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
	
	public void setup() {
		// Define size of output canvas
		size(displayWidth, displayHeight);
		
		osc = new OscP5(this, OSC_PORT);

		osc.plug(this, "parseIRMessage1", OSC_IR_1_DATA_STRING);
		osc.plug(this, "parseIRMessage2", OSC_IR_2_DATA_STRING);
		osc.plug(this, "parseIRMessage3", OSC_IR_3_DATA_STRING);
		osc.plug(this, "parseIRMessage4", OSC_IR_4_DATA_STRING);
		
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
		PApplet.main(new String[] { "Application" });

		// Starting communication channel with python application
		GatewayServer server = new GatewayServer(PositionProvider.getInstance());
		server.start();
	}

	public void keyPressed() {

		if (key == 'C' || key == 'c') {
			calibrate.in_progress = true;
			calibrate.complete = false;
		}

	}
	
	public PVector mapIRPoint(float _x, float _y) {
		float x; 
		float y;
		
		if(_x >= 1 && _y >= 1){
			x = OUT_OF_BOUNDS;
			y = OUT_OF_BOUNDS;
		} else {
			x = map(_x, 0, 1, 2, width/2 - 2); // Maps _x from the range 0,1 to the range 2,width/2-2
			y = map(_y, 0, 1, height - 2, 2);
		}
		
		return new PVector(x,y);
	}
	
	/*
	 * Each one of the parseIRMessageX is for its quadrant. They're meant to send context
	 * (which quadrant it's on) to the 
	 */
	public void parseIRMessage1(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 1);
	}
	public void parseIRMessage2(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 2);
	}
	public void parseIRMessage3(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 3);
	}
	public void parseIRMessage4(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 4);
	}
	
	public void parseIRMessage(float _x, float _y, float _size, int type) {	
		PVector p = mapIRPoint(_x, _y);
		
		if (calibrate.in_progress) {
			calibrate.setCalibrationPoint(p);
			System.out.println(calibrate.calibration_points);
		}
		calibrate.drawCurrentLocation(p, type);
		
		ir_points[0] = p;
	}
	
//	public void parseIRMessage2(float _x, float _y, float _size) {
//		PVector p = mapIRPoint(_x, _y);
//		PVector r = new PVector();
//		
//		ir_points[1] = p;
//		
//		if (ir_points[0] != null) {
//			r = PVector.add(ir_points[0], ir_points[1]);
//			stroke(0);
//			//line(width/4,height/2, r.x, r.y);
//		}
//		ir_points[0] = ir_points[1] = null;
//	}
}
