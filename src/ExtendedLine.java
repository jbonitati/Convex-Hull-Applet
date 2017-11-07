import java.awt.Color;

/**
 * Represents a line that passes through two points
 * @author jbonita
 *
 */
public class ExtendedLine extends Line {

	/**
	 * @param p1
	 * @param p2
	 */
	public ExtendedLine(Point p1, Point p2) {
		super(p1, p2);
		//find the points on the edge of the display which this line intersects
		//y = slope(x - p1x) + p1y
		setStart(new Point(0, getY_intercept()));
		setEnd(new Point(1.0, getSlope()*(1.0 - p1.get_x()) + p1.get_y()));
	}

	public ExtendedLine(Point p1, Point p2, Color c){
		this(p1, p2);
		setColor(c);
	}
	
	@Override
	public boolean equals(Object l){
		if(this == l) return true;
		else if(l instanceof Line){
			return (this.getSlope() == ((Line)l).getSlope() && this.getY_intercept() == ((Line) l).getY_intercept());
		}
		return false;
	}
	
	public ExtendedLine(Line l){
		this(l.getStart(), l.getEnd());
	}
	
	public ExtendedLine(Line l, Color c){
		this(l);
		setColor(c);
	}
}
