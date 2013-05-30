import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.*;
import javax.media.jai.*;
import processing.core.*;

public class Calibrate {

	PApplet parent;

	Boolean in_progress = false;
	Boolean complete = false;
	Polygon bounding_box = null;

	ArrayList<PVector> calibration_points = new ArrayList<PVector>();
	ArrayList<PVector> destination_points = new ArrayList<PVector>();

	PerspectiveTransform transform = null;

	public Calibrate(PApplet p) {
		this.parent = p;

		destination_points.add(new PVector(parent.width / 2 + 2, 0+2));
		destination_points.add(new PVector(parent.width - 2, 0+2));
		destination_points.add(new PVector(parent.width - 2, parent.height - 2));
		destination_points.add(new PVector(parent.width / 2 + 2, parent.height - 2));
	}

	public void drawCalibrationBoundingBox(ArrayList<PVector> cp) {
		parent.stroke(0);
		parent.noFill();

		parent.quad(cp.get(0).x, cp.get(0).y, cp.get(1).x, cp.get(1).y,
				cp.get(2).x, cp.get(2).y, cp.get(3).x, cp.get(3).y);
	}

	public void drawDestinationBoundingBox(ArrayList<PVector> dp) {
		parent.stroke(0);
		parent.noFill();

		parent.quad(dp.get(0).x, dp.get(0).y, dp.get(1).x, dp.get(1).y,
				dp.get(2).x, dp.get(2).y, dp.get(3).x, dp.get(3).y);
	}

	public void calculateTransform(ArrayList<PVector> dp, ArrayList<PVector> cp) {
		System.out.println("Calculating perspective transform...");
		this.transform = PerspectiveTransform.getQuadToQuad(cp.get(0).x,
				cp.get(0).y, cp.get(1).x, cp.get(1).y, cp.get(2).x,
				cp.get(2).y, cp.get(3).x, cp.get(3).y, dp.get(0).x,
				dp.get(0).y, dp.get(1).x, dp.get(1).y, dp.get(2).x,
				dp.get(2).y, dp.get(3).x, dp.get(3).y);

		System.out.println(transform);
	}

	public PVector transformPoint(PVector _src_pt, PerspectiveTransform t) {
		Point2D.Float src_pt = new Point2D.Float(_src_pt.x, _src_pt.y);
		Point2D.Float dst_pt = new Point2D.Float();
		t.transform(src_pt, dst_pt);
		return new PVector(dst_pt.x, dst_pt.y);
	}

	public void setCalibrationPoint(PVector p) {
		System.out.println("Adding calibration point...");
		System.out.println(p);

		this.in_progress = false;

		if (calibration_points.size() == 4) {
			calibration_points.clear();
			bounding_box = null;
		}
		calibration_points.add(p);

		if (calibration_points.size() == 4) {
			this.complete = true;
			int[] x = {(int)calibration_points.get(0).x, (int)calibration_points.get(1).x, (int)calibration_points.get(2).x, (int)calibration_points.get(3).x};
			int[] y = {(int)calibration_points.get(0).y, (int)calibration_points.get(1).y, (int)calibration_points.get(2).y, (int)calibration_points.get(3).y};
			bounding_box = new Polygon(x,y,4);
			calculateTransform(this.destination_points, this.calibration_points);
		}
	}

	public void drawCalibrationPoints() {
		parent.fill(255, 0, 0);
		
		for (int i = 0; i < calibration_points.size(); i++) {
			parent.ellipse(calibration_points.get(i).x,
					calibration_points.get(i).y, 10, 10);
		}

	}

	public void drawCurrentLocation(PVector p) {
		parent.fill(0, 0, 255);

		parent.ellipse(p.x, p.y, 10, 10);
		
		if ( this.bounding_box != null && this.bounding_box.contains(p.x,p.y) ) {
			drawTransformedLocation(p);
		}
	}
	
	public void drawTransformedLocation(PVector p) {
		PVector dst_pt = transformPoint(p, this.transform);
		parent.fill(0, 255, 0);
		parent.ellipse(dst_pt.x, dst_pt.y, 10, 10);
	}

}
