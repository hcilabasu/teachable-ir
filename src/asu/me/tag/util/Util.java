package asu.me.tag.util;

import processing.core.PVector;

public class Util {
	
	public static PVector getMidPoint(PVector[] p){
		return new PVector((p[0].x+p[1].x)/2, (p[0].y+p[1].y)/2);
	}
	
}
