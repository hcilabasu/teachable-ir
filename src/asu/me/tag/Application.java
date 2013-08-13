package asu.me.tag;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PVector;

public class Application extends PApplet {

	private static final long serialVersionUID = 8412123833928508734L;

	Calibrate calibrate;
	OscP5 osc;

	public static final String OSC_IR_1_DATA_STRING = "/wii/1/ir/xys/1";//"/wii/2/ir/xys/1";
	public static final String OSC_IR_2_DATA_STRING = "/wii/2/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_3_DATA_STRING = "/wii/3/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_4_DATA_STRING = "/wii/4/ir/xys/1";//"/wii/2/ir/xys/2";
	
	public static final int OSC_PORT= 9000;
	public PVector[] ir_points = {null, null, null, null};
	
	public static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
	
	public void setup() {
		// Define size of output canvas
		size(1200, 600);
		
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
        
//		calibrate.drawCalibrationPoints();
		calibrate.drawDestinationBoundingBox(calibrate.destination_points);
//		
		if (calibrate.calibrationStage == CalibrationStage.COMPLETE) {
			// Drawing calculated point
			PVector point = calibrate.plane.getCurrentPoint();
			ellipse(point.x, point.y, 10, 10);
			// "("+x+","+y+")"
			float x = map(point.x, width/2, width, -5, 5);
			float y = map(point.y, height, 0, -5, 5);
			text("("+x+","+y+")", point.x + 10, point.y + 10);
			
			// Drawing all points; good for debugging
//			fill(0,0,0,63);
//			for (Quadrant q : calibrate.quadrants) {
//				ellipse(q.currentPoint.x,q.currentPoint.y, 10, 10);
//			}
		}
		
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "asu.me.tag.Application" });

		// Starting communication channel with python application
//		GatewayServer server = new GatewayServer(PositionProvider.getInstance());
//		server.start();
	}

	public void keyPressed() {

		if (key == 'C' || key == 'c') {
			
			// Increment calibration stage
			calibrate.calibrationStage++;
			
			switch(calibrate.calibrationStage){
				case CalibrationStage.CENTER:
					for(int i = 0; i < 4; i++){
						System.out.println("Setting quadrant " + (i+1) + ": " + ir_points[i]);
						calibrate.quadrants[i].setCenterPoint(ir_points[i]);
					}
					break;
				case CalibrationStage.Q1:
					System.out.println("Setting Q1");
					calibrate.quadrants[0].setEdgePoint(ir_points[0]);
					break;
				case CalibrationStage.Q2:
					System.out.println("Setting Q2");
					calibrate.quadrants[1].setEdgePoint(ir_points[1]);
					break;
				case CalibrationStage.Q3:
					System.out.println("Setting Q3");
					calibrate.quadrants[2].setEdgePoint(ir_points[2]);
					break;
				case CalibrationStage.Q4:
					System.out.println("Setting Q4");
					calibrate.quadrants[3].setEdgePoint(ir_points[3]);
					break;
				case CalibrationStage.COMPLETE:
					calibrate.calculateTransforms();
			}
			
		}
		

	}
	
	public PVector mapIRPoint(float _x, float _y) {
		float x; 
		float y;
		
		// Checking if wiimote is picking up the IR signal
//		if(_x >= 1 && _y >= 1){
//			// No signal
//			x = OUT_OF_BOUNDS;
//			y = OUT_OF_BOUNDS;
//		} else {
			// It has a signal
			x = map(_x, 0, 1, 2, width/2 - 2); // Maps _x from the range 0,1 to the range 2,width/2-2
			y = map(_y, 0, 1, height - 2, 2);
//		}
		
		return new PVector(x,y);
	}
	
	/*
	 * Each one of the parseIRMessageX is for its quadrant. They're meant to 
	 * send context (which quadrant it's on) to the parseIRMessage method
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
		// Setting the quadrant current position
		ir_points[type-1] = p;
		// Drawing current point
		calibrate.drawCurrentLocation(p, type);
	}
	
}
