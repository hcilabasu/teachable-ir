package asu.me.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianList {
	
	public static final Integer X = 0;
	public static final Integer Y = 1;
	public static final int DROP_OFFSET = 1;

	
	private int bufferSize;
	private List<Float> x;
	private List<Float> y;
	
	public MedianList(int buffer) {
		this.bufferSize = buffer;
		this.x = new ArrayList<Float>();
		this.y = new ArrayList<Float>();
	}
	
	public float getMedian(int axis){
		if(axis == X){
			return getMedian(x);
		} else {
			return getMedian(y);
		}
	}
	
	private float getMedian(List<Float> array){
		if(array.size() == 0){
			return 0f;
		} else {
			// TODO optimize
			// Duplicating array
			List<Float> a = new ArrayList<Float>();
			for (Float f : array) {
				a.add(f);
			}
			// Sorting
			Collections.sort(a);
			return (Float) a.toArray()[(int) Math.floor(array.size()/2)];
		}
	}
	
	public void add(Float f, int axis){
		if(axis == X){
			add(f, x);
		} else {
			add(f, y);
		}
	}
	
	private void add(Float f, List<Float> array){
		if(array.size() == bufferSize){
			array.remove(0);
		}
		array.add(f);
	}
	
	public float getLast(int axis){
		if(axis == X){
			return getLast(x);
		}
		else{
			return getLast(y);
		}
	}
	private float getLast(List<Float> array){
		return array.indexOf(array.size()-1);
	}
	
	//drop a value from the list (to compensate for some noise)
	public void drop(int axis){
		if(axis == X){
			drop(x);
		}
		else{
			drop(y);
		}
	}
	
	private void drop(List<Float> array){
		for(int i = 0; i<DROP_OFFSET; i++){
			array.remove(getLast(array));
		}
	}
	
}
