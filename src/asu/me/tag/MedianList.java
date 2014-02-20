package asu.me.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianList {
	
	public static final Integer X = 0;
	public static final Integer Y = 1;
	
	//added noise lists, this may help later with filtering out errant readings
	private List<Float> noise_x;
	private List<Float> noise_y;
	private int bufferSize;
	private List<Float> x;
	private List<Float> y;
	
	public MedianList(int buffer) {
		this.bufferSize = buffer;
		this.noise_x = new ArrayList<Float>();
		this.noise_y = new ArrayList<Float>();
		this.x = new ArrayList<Float>();
		this.y = new ArrayList<Float>();
	}
	
	public float getMedian(int axis){
		if(axis == X){
			this.addnoise(getMedian(x),0);
			return getMedian(x);
		} else {
			this.addnoise(getMedian(y),1);
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
	
	private void addnoise(float val, int index){
		if(index == X){
			if(noise_x.size() == bufferSize){
				noise_x.remove(0);
			}
			noise_x.add(val);
		}
		else{
			if(noise_x.size() == bufferSize){
				noise_x.remove(0);
			}
			noise_y.add(val);
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
	
}
