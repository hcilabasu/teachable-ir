package asu.me.tag.util;

import processing.core.PVector;

//utility class for solving the robot's location and orientation

public class Util {
	public static final PVector ANGLE_ORIGIN = new PVector(1,0);
	
	public static PVector getMidPoint(PVector[] p){
		return new PVector((p[0].x+p[1].x)/2, (p[0].y+p[1].y)/2);
	}
	
	//new code
	//same as above but solves with 3 points instead of just 
	public static PVector getGeometryMidpoint(PVector[] p){
		//store the legs of the triangle
		//each of the PVectors holds the displacement of a leg.
		PVector[] legs = new PVector[3];
		legs[0] = new PVector(p[0].x - p[1].x , p[0].y - p[1].y);
		legs[1]= new PVector(p[1].x - p[2].x , p[1].y - p[2].y);
		legs[2]= new PVector(p[2].x - p[0].x , p[2].y - p[0].y);
		
		//now that you have each leg, figure out which is the hypotenuse
		int longest = 0;
		for(int i = 0; i < 3; i++){
			if(legs[i].magSq() > legs[longest].magSq()){
				longest = i;
				}
		}
		
		//now figure out which point is the origin point and where the robot is pointing
		//the second longest vector can give the orientation and location of the robot
		int origin = 0;
		int pointto = 0;
		
		switch (longest){
			case 0:	origin = 2;
					if(legs[1].magSq() < legs[2].magSq())
						pointto = 0;
					else
						pointto = 1;
					break;
					
			case 1:	origin = 0;
					if(legs[0].magSq() < legs[2].magSq())
						pointto = 2;
					else
						pointto = 1;
					break;
					
			case 2: origin = 1;
					if(legs[0].magSq() < legs[1].magSq())
						pointto = 2;
					else
						pointto = 0;
					break;
		}
					
		//now return the midpoint of the robot
		return new PVector((p[origin].x+p[pointto].x)/2, (p[origin].y+p[pointto].y)/2);
	}

	//very similar to the code above, except it returns the orientation of the robot
	public static float getOrientation(PVector[] p){
		//store the legs of the triangle
		//each of the PVectors holds the displacement of a leg.
		PVector[] legs = new PVector[3];
		legs[0] = new PVector(p[0].x - p[1].x , p[0].y - p[1].y);
		legs[1]= new PVector(p[1].x - p[2].x , p[1].y - p[2].y);
		legs[2]= new PVector(p[2].x - p[0].x , p[2].y - p[0].y);
		
		//now that you have each leg, figure out which is the hypotenuse
		int longest = 0;
		for(int i = 0; i < 3; i++){
			if(legs[i].magSq() > legs[longest].magSq()){
				longest = i;
				}
		}
		
		//now figure out which point is the origin point and where the robot is pointing
		//the second longest vector can give the orientation and location of the robot
		int origin = 0;
		int pointto = 0;
		
		switch (longest){
			case 0:	origin = 2;
					if(legs[1].magSq() < legs[2].magSq())
						pointto = 0;
					else
						pointto = 1;
					break;
					
			case 1:	origin = 0;
					if(legs[0].magSq() < legs[2].magSq())
						pointto = 2;
					else
						pointto = 1;
					break;
					
			case 2: origin = 1;
					if(legs[0].magSq() < legs[1].magSq())
						pointto = 2;
					else
						pointto = 0;
					break;
		}
		PVector direction = PVector.sub(legs[pointto],legs[origin]);
		
		//probably need to convert from radians to degrees
		return direction.heading();
		
	
	}
	
}