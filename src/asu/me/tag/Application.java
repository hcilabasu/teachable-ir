package asu.me.tag;
import java.util.HashMap;
import java.util.Map;

import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PVector;
import asu.me.tag.util.Util;

public class Application extends PApplet {

	private static final long serialVersionUID = 8412123833928508734L;
	private static final int MEDIAM_BUFFER_SIZE = 200;
	private static final byte UNTRACKED_BUFFER_VAL = 5;

	private static byte untracked[][] = new byte[4][2];
	
	Calibrate calibrate;
	OscP5 osc;

	public static final String OSC_IR_1_1_DATA_STRING = "/wii/1/ir/xys/1";//"/wii/2/ir/xys/1";
	public static final String OSC_IR_1_2_DATA_STRING = "/wii/1/ir/xys/2";//"/wii/2/ir/xys/1";
	public static final String OSC_IR_2_1_DATA_STRING = "/wii/2/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_2_2_DATA_STRING = "/wii/2/ir/xys/2";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_3_1_DATA_STRING = "/wii/3/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_3_2_DATA_STRING = "/wii/3/ir/xys/2";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_4_1_DATA_STRING = "/wii/4/ir/xys/1";//"/wii/2/ir/xys/2";
	public static final String OSC_IR_4_2_DATA_STRING = "/wii/4/ir/xys/2";//"/wii/2/ir/xys/2";
	
	public static final int OSC_PORT= 9000;
	public PVector[][] ir_points = {{null, null}, {null, null}, {null, null}, {null, null}};
	
	public static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
	
	public Map<Integer, MedianList[]> values;
	
	public PVector[][] sensor_values;
	
	public void setup() {
		frameRate(12);
		// Define size of output canvas
		size(1200, 600);
		
		
		calibrate = new Calibrate(this);
		smooth();
		
		values = new HashMap<Integer, MedianList[]>();
		
		sensor_values = new PVector[4][2];
	
		System.out.println(sensor_values);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				sensor_values[i][j] = new PVector(0,0);
				ir_points[i][j] = new PVector(0,0);
			}
		}
		
		osc = new OscP5(this, OSC_PORT);
		
		osc.plug(this, "parseIRMessage11", OSC_IR_1_1_DATA_STRING);
		osc.plug(this, "parseIRMessage12", OSC_IR_1_2_DATA_STRING);
		osc.plug(this, "parseIRMessage21", OSC_IR_2_1_DATA_STRING);
		osc.plug(this, "parseIRMessage22", OSC_IR_2_2_DATA_STRING);
		osc.plug(this, "parseIRMessage31", OSC_IR_3_1_DATA_STRING);
		osc.plug(this, "parseIRMessage32", OSC_IR_3_2_DATA_STRING);
		osc.plug(this, "parseIRMessage41", OSC_IR_4_1_DATA_STRING);
		osc.plug(this, "parseIRMessage42", OSC_IR_4_2_DATA_STRING);
		
	}

	public void draw() {
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 2; j++) {
				readValues(i, j);
			}
		}
		noStroke();
        
		fill( 255, 255, 255, 50);
        rect(0, 0, width, height);
        
//		calibrate.drawCalibrationPoints();
		calibrate.drawDestinationBoundingBox(calibrate.destination_points);
//		
		if (calibrate.calibrationStage == CalibrationStage.COMPLETE) {
			// Drawing calculated point
			PVector[] tPoint = calibrate.plane.getCurrentTransformedPoint();
			fill( 0, 0, 255);
//			ellipse(tPoint[0].x, tPoint[0].y, 10, 10);
//			ellipse(tPoint[1].x, tPoint[1].y, 10, 10);
			PVector midPoint = Util.getMidPoint(new PVector[]{tPoint[0], tPoint[1]});
			ellipse(midPoint.x, midPoint.y, 10, 10);
			stroke(200,200,200);
	        line(tPoint[0].x, tPoint[0].y, tPoint[1].x, tPoint[1].y);
			
	        float[] coord = calibrate.plane.getCurrentPositionAsArray();
	        float orientation = calibrate.plane.getCurrentOrientation();
	        
			text(calibrate.plane.getCurrentQuadrant() + " ("+coord[0]+","+coord[1]+") O: " + orientation, midPoint.x + 20, midPoint.y + 20);
			
			// Drawing all points; good for debugging
//			fill(0,0,0,63);
//			for (Quadrant q : calibrate.quadrants) {
//				ellipse(q.currentPoint.x,q.currentPoint.y, 10, 10);
//			}
		}
		else{
			String infoMessage = "";
			int stage = calibrate.calibrationStage;
			switch(stage){
				case 0:
					infoMessage = "Calibrate center";
					break;
				case 1:
					infoMessage = "Center calibrated, calibrate upper right corner";
					break;
				case 2:
					infoMessage = "Upper right corner calibrated, calibrate upper left corner";
					break;
				case 3:
					infoMessage = "Upper left corner calibrated, calibrate lower left corner";
					break;
				case 4:
					infoMessage = "Lower left corner calibrated, calibrate lower right corner";
					break;
				case 5: 
					infoMessage = "Calibration complete, press \"C\" ";
					break;
				case 6:
					//technically impossible case
					infoMessage = "";
					break;
			}
			fill(10,10,10);
			int y = 15;
			int x = this.getSize().width/2 + 2;
			text(infoMessage, x, y);
			
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
			
//			// Increment calibration stage
			calibrate.calibrationStage++;
			
			switch(calibrate.calibrationStage){
				case CalibrationStage.CENTER:
					for(int i = 0; i < 4; i++){
						System.out.println("Setting quadrant " + (i+1) + ": " + ir_points[i]);
						calibrate.quadrants[i].setCenterPoint(Util.getMidPoint(ir_points[i]));
					}
					break;
				case CalibrationStage.Q1:
					System.out.println("Setting Q1");
					calibrate.quadrants[0].setEdgePoint(Util.getMidPoint(ir_points[0]));
					break;
				case CalibrationStage.Q2:
					System.out.println("Setting Q2");
					calibrate.quadrants[1].setEdgePoint(Util.getMidPoint(ir_points[1]));
					break;
				case CalibrationStage.Q3:
					System.out.println("Setting Q3");
					calibrate.quadrants[2].setEdgePoint(Util.getMidPoint(ir_points[2]));
					break;
				case CalibrationStage.Q4:
					System.out.println("Setting Q4");
					calibrate.quadrants[3].setEdgePoint(Util.getMidPoint(ir_points[3]));
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
	public void parseIRMessage11(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 1, 1);
		//System.out.print("1-1 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage12(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 1, 2);
		//System.out.print("1-2 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage21(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 2, 1);
		//System.out.print("2-1 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage22(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 2, 2);
		//System.out.print("2-2 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage31(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 3, 1);
		//System.out.print("3-1 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage32(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 3, 2);
		//System.out.print("3-2 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage41(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 4, 1);
		//System.out.print("4-1 ");
		//System.out.println(_x +" , " + _y);
	}
	public void parseIRMessage42(float _x, float _y, float _size){
		parseIRMessage(_x, _y, _size, 4, 2);
		//System.out.print("4-2 ");
		//System.out.println(_x +" , " + _y);
	}
	
	public void parseIRMessage(float _x, float _y, float _size, int type, int id) {
		sensor_values[type-1][id-1] = new PVector(_x, _y);
	}

	public void readValues(int type, int id) {
		if(sensor_values[type-1][id-1] != null){
				
			float _x = sensor_values[type-1][id-1].x;
			float _y = sensor_values[type-1][id-1].y;
			
			double c = Double.MAX_VALUE;
			PVector p;
			
			MedianList[] l = values.get(type-1);
			if(l == null){
				l = new MedianList[] {new MedianList(MEDIAM_BUFFER_SIZE), new MedianList(MEDIAM_BUFFER_SIZE)};
			}
			
			if(_x >= 1 || _y >= 1){
				// TODO handle untracked IR sources
				//System.out.println("UNTRACKED " + type + " " + id);
				if(untracked[type-1][id-1] < UNTRACKED_BUFFER_VAL){
					l[id-1].drop(MedianList.X);
					l[id-1].drop(MedianList.Y);
					untracked[type-1][id-1]++;
				}
				return;
				/*
				p = new PVector(l[id-1].getLast(MedianList.X), l[id-1].getLast(MedianList.Y));
				l[id-1].add(_x, MedianList.X);
				l[id-1].add(_y, MedianList.Y);
			*/
			} 
			else if(untracked[type-1][id-1] <= 0){
				untracked[type-1][id-1] = 0;
				//check to see if the pattern makes sense
				//if the pattern is too big, then try dropping values to get rid of the problem
				
				//good (probably) value
				l[id-1].add(_x, MedianList.X);
				l[id-1].add(_y, MedianList.Y);
				_x = l[id-1].getMedian(MedianList.X);
				_y = l[id-1].getMedian(MedianList.Y);
				c = Math.sqrt(Math.pow(_x - 0.5, 2) + Math.pow(_y - 0.5, 2)); 
				//System.out.print (type + " ");
				//System.out.println(_x + "," + _y);
				// Calculating certainty
				
				
				
				//System.out.println("c: " + c);
				p = mapIRPoint(_x, _y);
			
			// Setting the quadrant current position
			ir_points[type-1][id-1] = p;
			// Drawing current point on calibration panel
			calibrate.drawCurrentLocation(ir_points[type-1], type, id, c);
			return;
			}
			//buffer area, ignoring values for now
			else{
				untracked[type-1][id-1]--;
				return;
			}
		}
	}
	
}
