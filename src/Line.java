import java.awt.*;

/**
 * Represents a line segment between two points
 * @author jbonita
 *
 */
public class Line {
	private Point start, end;
	private Color my_c;
	private double y_intercept, slope;
	
	public Line(Line l){
		start = l.getStart();
		end = l.getEnd();
		my_c = l.getColor();
		y_intercept = l.getY_intercept();
		slope = l.getSlope();
	}
	
	public Line(Line l, Color c){
		this(l);
		my_c = c;
	}
	
	public Line(Point p1, Point p2){
		setStart(p1);
		setEnd(p2);
		setColor(Color.black);
		
		slope = p1.get_x() != p2.get_x() ? (p1.get_y() - p2.get_y()) / (p1.get_x() - p2.get_x()) : 100000;
		y_intercept = (getSlope() * -1 * p1.get_x() + p1.get_y());
	}
	
	public Line(Point p1, Point p2, Color c){
		this(p1, p2);
		setColor(c);
	}

	public Color getColor() {
		return my_c;
	}

	public void setColor(Color c) {
		my_c = c;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public double getY_intercept() {
		return y_intercept;
	}

	public double getSlope() {
		return slope;
	}
	
	@Override
	public boolean equals(Object l){
		if(l == this) return true;
		if(l instanceof Line)
			return (this.slope == ((Line) l).getSlope() && this.y_intercept == ((Line)l).getY_intercept());
		else return false;
	}

	/**
	 * Finds the more convex route following counter-clockwise from the most recently found edge defined by lastslope
	 * @param ik another line to compare to
	 * @param lastslope the slope of the most recently found edge on the hull
	 * @return true if ij deserves to be the next edge of the hull more than ik; false otherwise
	 */
	public boolean isMoreConvexThan(Line l, double lastslope) {
		if(this.getSlope() < lastslope && l.getSlope() < lastslope)
			return this.getSlope() > l.getSlope();
		if(this.getSlope() < lastslope) return true;
		if(l.getSlope() < lastslope) return false;
		return (this.getSlope() > l.getSlope());
	}
}
