package asu.me.tag;

public class PositionProvider {
	
	private static PositionProvider instance = null;
	
	private Float x = 0f;
	private Float y = 0f;
	
	private PositionProvider(){}
	
	public static PositionProvider getInstance(){
		if(instance == null) {
			instance = new PositionProvider();
		}
		return instance;
	}
	
	public Float getX() {
		return x;
	}

	public void setX(Float x) {
		this.x = x;
	}

	public Float getY() {
		return y;
	}

	public void setY(Float y) {
		this.y = y;
	}
	
	public void setXAndY(Float x, Float y){
		this.x = x;
		this.y = y;
	}
	
	public Float[] getXAndY(){
		return new Float[]{ x, y };
	}
	
}
