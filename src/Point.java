import java.awt.*;

public class Point {
	private double my_x, my_y;
	private Color my_c;
	
	public Point(double x, double y){
		my_x = x;
		my_y = y;
		my_c = Color.black;
	}
	
	public Point(double x, double y, Color c){
		this(x, y);
		setColor(c);
	}
	
	public double get_x(){return my_x;}
	public double get_y(){return my_y;}
	public Color getColor(){return my_c;}
	public void setColor(Color c){my_c = c;}

	/**
	 * determine if two points are on the same side of a line
	 * this happens iff ((y1−y2)(ax−x1)+(x2−x1)(ay−y1))((y1−y2)(bx−x1)+(x2−x1)(by−y1)) > 0
	 * @param p the other point
	 * @param l the line
	 * @return true if this point and p are on the same side of l, false otherwise
	 */
	public boolean on_same_side(Point p, Line l) {
		return ((l.getStart().get_y() - l.getEnd().get_y())*(my_x - l.getStart().get_x()) + 
				(l.getEnd().get_x() - l.getStart().get_x())*(my_y - l.getStart().get_y())) *
				((l.getStart().get_y() - l.getEnd().get_y())*(p.get_x() - l.getStart().get_x()) + 
				(l.getEnd().get_x() - l.getStart().get_x())*(p.get_y() - l.getStart().get_y()))
				> 0;
	}

	/**
	 * Calculates the polar angle from the horizontal to the vector from this point to the given point
	 * @param p the point toward which the angle is to be found
	 * @return the polar angle in radians between 0 and 2*PI
	 */ 
	public double polarAngleTo(Point p) {
		//if(this.equals(p)) return 0;
		double angle = Math.acos( ( p.get_x() - this.get_x()) / this.distanceTo(p));
		if(p.get_y() >= this.get_y())
			return angle;
		else
			return Math.PI*2 - angle;
	}

	public double distanceTo(Point p) {
		return Math.sqrt((my_x - p.get_x())*(my_x - p.get_x()) + (my_y - p.get_y())*(my_y - p.get_y()) ) ;
	}

	@Override
	public boolean equals(Object p){
		if(p == this) return true;
		if(p instanceof Point){
			return (my_x == ((Point) p).get_x() && my_y == ((Point)p).get_y());
		}
		return false;
	}
	
	/**
	 * calculates the distance from a point to a line
	 * @param l line to calculate from
	 * @return the shortest distance from this point to l
	 */
	public double distanceTo(Line l) {
		double normalLength = Math.sqrt((l.getEnd().get_x()-l.getStart().get_x())
				*(l.getEnd().get_x()-l.getStart().get_x())
				+((l.getEnd().get_y()-l.getStart().get_y())
				*(l.getEnd().get_y()-l.getStart().get_y())));
	    return Math.abs((l.getEnd().get_x()-l.getStart().get_x())
	    		*(l.getStart().get_y()-my_y)
	    		- ((l.getStart().get_x() - my_x)
	    		*(l.getEnd().get_y()-l.getStart().get_y()))
	    		/normalLength);
	}
}
