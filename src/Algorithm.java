import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class holds all algorithms to solve Convex hulls
 * 
 * @author jbonita
 */
public class Algorithm {

	public static void start() {

		Main.lines.clear();
		for (Point p : Main.points) {
			p.setColor(Color.black);
		}
		if (Main.points.size() <= 1)
			return;
		else if (Main.points.size() == 2) {
			Main.lines.add(new Line(Main.points.get(0), Main.points.get(1)));
			Main.display.repaint();
		} else if (Main.points.size() == 3) {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (j != i)
						Main.lines.add(new Line(Main.points.get(i), Main.points
								.get(j)));
			Main.display.repaint();
		} else if (!Main.athread.equals(null))
			Main.athread.start();
	}

	public static class DivideandConquer extends NotifyingThread{
		
		@Override
		public void doRun(){
			
		}
	}
	
	public static class Ultimate extends NotifyingThread{
		
		@Override
		public void doRun(){
			// find leftmost and rightmost points
			Main.setInfoText("Finding leftmost and rightmost points");
			Point lowest, highest;
			if (Main.points.get(0).get_x() < Main.points.get(1).get_x()) {
				lowest = Main.points.get(0);
				highest = Main.points.get(1);
			} else {
				highest = Main.points.get(0);
				lowest = Main.points.get(1);
			}
			for (int i = 2; i < Main.points.size(); i++) {
				lowest.setColor(Color.magenta);
				highest.setColor(Color.orange);
				Point p = Main.points.get(i);
				p.setColor(Color.cyan);
				step();
				if (p.get_x() < lowest.get_x() || p.get_x() > highest.get_x())
					if (p.get_x() < lowest.get_x()) {
						lowest.setColor(Color.LIGHT_GRAY);
						lowest = p;
					} else {
						highest.setColor(Color.LIGHT_GRAY);
						highest = p;
					}
				else {
					p.setColor(Color.LIGHT_GRAY);
				}
			}
			lowest.setColor(Color.green);
			highest.setColor(Color.green);
			connect(lowest, highest, Main.points);
			//connectlower(lowest, highest, Main.points);
		}
		
		private void connectlower(Point l, Point r,
				ArrayList<Point> T) {
			Main.setInfoText("Finding vertical line at median");
			Line lbound = new ExtendedLine(l, new Point(l.get_x(), l.get_x()+1), Color.pink);
			Line rbound = new ExtendedLine(r, new Point(r.get_x(), r.get_x()+1), Color.pink);
			Main.lines.add(lbound);
			Main.lines.add(rbound);
			double a = findMedian(T);
			Line vertical = new ExtendedLine(new Point(a, 0), new Point(a, 1), Color.LIGHT_GRAY);
			Main.lines.add(vertical);
			step();
			//find the bridge
			//Main.setInfoText("Finding bridge across median on lower hull");
			Line lowerbridge = findlowerBridge(T, a);
			Main.lines.add(lowerbridge);

			//partition
			Main.setInfoText("Partitioning points around median");
			ArrayList<Point> left = new ArrayList<Point>(),
					right = new ArrayList<Point>();
			for(Point p : T){
				step();
				if(p.get_x() <= lowerbridge.getStart().get_x()){
					p.setColor(Color.orange);
					left.add(p);
				}else if(p.get_x() >= lowerbridge.getEnd().get_x()){
					p.setColor(Color.magenta);
					right.add(p);
				}else p.setColor(Color.lightGray);
			}
			step();
			
			Main.lines.remove(lbound);
			Main.lines.remove(rbound);
			Main.lines.remove(vertical);
			step();
			
			if(lowerbridge.getStart().equals(l)) return;
			else connect(l, lowerbridge.getStart(), left);
			if(lowerbridge.getEnd().equals(r)) return;
			else connect(lowerbridge.getEnd(), r, right);
		}

		private void connect(Point l, Point r,
				ArrayList<Point> T) {
			Main.setInfoText("Finding vertical line at median");
			Line lbound = new ExtendedLine(l, new Point(l.get_x(), l.get_x()+1), Color.pink);
			Line rbound = new ExtendedLine(r, new Point(r.get_x(), r.get_x()+1), Color.pink);
			Main.lines.add(lbound);
			Main.lines.add(rbound);
			double a = findMedian(T);
			Line vertical = new ExtendedLine(new Point(a, 0), new Point(a, 1), Color.LIGHT_GRAY);
			Main.lines.add(vertical);
			step();
			//find the bridge
			//Main.setInfoText("Finding bridge across median on lower hull");
			Line upperbridge = findBridge(T, a);
			Main.lines.add(upperbridge);

			//partition
			Main.setInfoText("Partitioning points around edges of bridge");
			ArrayList<Point> left = new ArrayList<Point>(),
					right = new ArrayList<Point>();
			for(Point p : T){
				step();
				if(p.get_x() <= upperbridge.getStart().get_x()){
					p.setColor(Color.orange);
					left.add(p);
				}else if(p.get_x() >= upperbridge.getEnd().get_x()){
					p.setColor(Color.magenta);
					right.add(p);
				}else p.setColor(Color.lightGray);
			}
			step();
			
			Main.lines.remove(lbound);
			Main.lines.remove(rbound);
			Main.lines.remove(vertical);
			step();
			
			if(upperbridge.getStart().equals(l)) return;
			else connect(l, upperbridge.getStart(), left);
			if(upperbridge.getEnd().equals(r)) return;
			else connect(upperbridge.getEnd(), r, right);
		}
		
		/**
		 * finds and returns the line between two points in T that
		 * forms the upper hull bridge across the vertical line at x = a
		 * @param T
		 * @param a
		 * @return
		 */
		private Line findBridge(ArrayList<Point> T,
				double a) {
			if(T.size() == 1) return new Line(T.get(0), T.get(0));
			if(T.size() == 2) 
				if(T.get(0).get_x() < T.get(1).get_x()) return new Line(T.get(0), T.get(1));
				else return new Line(T.get(1), T.get(0));
			ArrayList<Line> pairs = new ArrayList<Line>();
			ArrayList<Point> candidates = new ArrayList<Point>();
			Main.setInfoText("Pairing random points in the subset");
			for(int i = 0; i+1 < T.size(); i+=2){
				if(T.get(i).get_x() < T.get(i+1).get_x())
					pairs.add(new Line(T.get(i), T.get(i+1), Color.cyan));
				else
					pairs.add(new Line(T.get(i+1), T.get(i), Color.cyan));
				Main.lines.add(pairs.get(pairs.size()-1));
				step();
			}
			if(T.size()%2 == 1) candidates.add(T.get(T.size()-1));
			//determine the median of the slopes of lines in pairs
			//putting the slopes in a new arraylist of points so I don't
			//have to rewrite the algorithm
			ArrayList<Point> temp = new ArrayList<Point>();
			for(Line l : pairs){
				temp.add(new Point(l.getSlope(), 0));
			}
			double medianSlope = findMedian(temp);
			ArrayList<Line> small = new ArrayList<Line>(),
					equal = new ArrayList<Line>(),
					large = new ArrayList<Line>();
			for(Line l : pairs){
				if(l.getSlope() < medianSlope) small.add(l);
				else if(l.getSlope() > medianSlope) large.add(l);
				else equal.add(l);
			}
			//find pk and pm, one or two points which have all of the other
			//points below the line passing through them with slope medianSlope
			Point pk = T.get(0);
			Point pm = pk;
			Main.setInfoText("Finding the \"supporting line\" with slope = median of slopes");
			double maximum = pk.get_y() - (medianSlope*pk.get_x());
			Line supportingLine = new ExtendedLine(pk, new Point(0, maximum), Color.green);
			step();
			for(int i = 1; i < T.size(); i++){
				Point pi = T.get(i);
				double intercept = pi.get_y() - (medianSlope*pi.get_x());
				Line testLine = new ExtendedLine(pi, new Point(0, intercept), Color.yellow);
				Main.lines.add(testLine);
				step();
				if(intercept < maximum + .000001 && intercept > maximum - .000001){
					if(pi.get_x() < pk.get_x()){
						pk = pi;
					}if(pi.get_x() > pm.get_x()){
						pm = pi;
					}
				}else if(intercept > maximum){
					//change maximum to intercept
					maximum = intercept;
					pk = pi;
					pm = pi;
					testLine.setColor(Color.green);
					supportingLine = testLine;
				}
				Main.lines.remove(testLine);
			}
			Main.lines.add(supportingLine);
			Main.setInfoText("Pruning points that can't be on the lower bridge");
			for(Line l : pairs) Main.lines.remove(l);
			step();
			//determine if the supporting line contains the bridge
			if(pk.get_x() <= a && pm.get_x() > a){
				for(Point p : T) p.setColor(Color.LIGHT_GRAY);
				Main.lines.remove(supportingLine);
				return new Line(pk, pm); 
			}
			if(pm.get_x() <= a){
				for(Line l : large){
					l.getStart().setColor(Color.red);
					step();
					candidates.add(l.getEnd());
				}
				for(Line l : equal){
					l.getStart().setColor(Color.red);
					step();
					candidates.add(l.getEnd());
				}
				for(Line l : small){
					candidates.add(l.getStart());
					candidates.add(l.getEnd());
				}
			}
			else if(pk.get_x() > a){
				for(Line l : small){
					l.getEnd().setColor(Color.red);
					step();
					candidates.add(l.getStart());
				}
				for(Line l : equal){
					l.getEnd().setColor(Color.red);
					step();
					candidates.add(l.getStart());
				}
				for(Line l : large){
					candidates.add(l.getStart());
					candidates.add(l.getEnd());
				}
			}
			Main.lines.remove(Main.lines.size()-1);
			return findBridge(candidates, a);
		}

		/**
		 * finds and returns the line between two points in T that
		 * forms the lower hull bridge across the vertical line at x = a
		 * @param T
		 * @param a
		 * @return
		 */
		private Line findlowerBridge(ArrayList<Point> T,
				double a) {
			if(T.size() == 1) return new Line(T.get(0), T.get(0));
			if(T.size() == 2) 
				if(T.get(0).get_x() < T.get(1).get_x()) return new Line(T.get(0), T.get(1));
				else return new Line(T.get(1), T.get(0));
			ArrayList<Line> pairs = new ArrayList<Line>();
			ArrayList<Point> candidates = new ArrayList<Point>();
			Main.setInfoText("Pairing random points in the subset");
			for(int i = 0; i+1 < T.size(); i+=2){
				if(T.get(i).get_x() < T.get(i+1).get_x())
					pairs.add(new Line(T.get(i), T.get(i+1), Color.cyan));
				else
					pairs.add(new Line(T.get(i+1), T.get(i), Color.cyan));
				Main.lines.add(pairs.get(pairs.size()-1));
				step();
			}
			if(T.size()%2 == 1) candidates.add(T.get(T.size()-1));
			//determine the median of the slopes of lines in pairs
			//putting the slopes in a new arraylist of points so I don't
			//have to rewrite the algorithm
			ArrayList<Point> temp = new ArrayList<Point>();
			for(Line l : pairs){
				temp.add(new Point(l.getSlope(), 0));
			}
			double medianSlope = findMedian(temp);
			ArrayList<Line> small = new ArrayList<Line>(),
					equal = new ArrayList<Line>(),
					large = new ArrayList<Line>();
			for(Line l : pairs){
				if(l.getSlope() < medianSlope) small.add(l);
				else if(l.getSlope() > medianSlope) large.add(l);
				else equal.add(l);
			}
			//find pk and pm, one or two points which have all of the other
			//points below the line passing through them with slope medianSlope
			Point pk = T.get(0);
			Point pm = pk;
			Main.setInfoText("Finding the \"supporting line\" with slope = median of slopes");
			double minimum = pk.get_y() - (medianSlope*pk.get_x());
			Line supportingLine = new ExtendedLine(pk, new Point(0, minimum), Color.green);
			step();
			for(int i = 1; i < T.size(); i++){
				Point pi = T.get(i);
				double intercept = pi.get_y() - (medianSlope*pi.get_x());
				Line testLine = new ExtendedLine(pi, new Point(0, intercept), Color.yellow);
				Main.lines.add(testLine);
				step();
				if(intercept < minimum + .000001 && intercept > minimum - .000001){
					if(pi.get_x() < pk.get_x()){
						pk = pi;
					}if(pi.get_x() > pm.get_x()){
						pm = pi;
					}
				}else if(intercept < minimum){
					//change maximum to intercept
					minimum = intercept;
					pk = pi;
					pm = pi;
					testLine.setColor(Color.green);
					supportingLine = testLine;
				}
				Main.lines.remove(testLine);
				
			}
			Main.lines.add(supportingLine);
			Main.setInfoText("Pruning points that can't be on the upper bridge");
			for(Line l : pairs) Main.lines.remove(l);
			step();
			//determine if the supporting line contains the bridge
			if(pk.get_x() <= a && pm.get_x() > a){
				for(Point p : T) p.setColor(Color.LIGHT_GRAY);
				Main.lines.remove(supportingLine);
				return new Line(pk, pm); 
			}
			if(pm.get_x() <= a){
				for(Line l : large){
					l.getStart().setColor(Color.red);
					step();
					candidates.add(l.getEnd());
				}
				for(Line l : equal){
					l.getStart().setColor(Color.red);
					step();
					candidates.add(l.getEnd());
				}
				for(Line l : small){
					candidates.add(l.getStart());
					candidates.add(l.getEnd());
				}
			}
			else if(pk.get_x() > a){
				for(Line l : small){
					l.getEnd().setColor(Color.red);
					step();
					candidates.add(l.getStart());
				}
				for(Line l : equal){
					l.getEnd().setColor(Color.red);
					step();
					candidates.add(l.getStart());
				}
				for(Line l : large){
					candidates.add(l.getStart());
					candidates.add(l.getEnd());
				}
			}
			Main.lines.remove(Main.lines.size()-1);
			return findBridge(candidates, a);
		}
		
		/**
		 * finds the median of the set of points T using 
		 * the median of medians algorithm
		 * @param t
		 * @return
		 */
		private double findMedian(ArrayList<Point> t) {
			return select(t, t.size()/2).get_x();
		}

		private Point select(ArrayList<Point> t, int k) {
			if(t.size() <= 10){
				t = sort(t);
				return k != 0 ? t.get(k-1) : t.get(0);
			}
			//partition into subsets of 5 elements
			ArrayList<ArrayList<Point>> subsets = new ArrayList<ArrayList<Point>>();
			for(int i = 0; i < t.size(); ){
				ArrayList<Point> temp = new ArrayList<Point>();
				for(int j = 0; j < 5; j++)
					if(i < t.size()) temp.add(t.get(i++));
				subsets.add(temp);
			}
			ArrayList<Point> medians = new ArrayList<Point>();
			for(ArrayList<Point> a : subsets){
				medians.add(select(a, a.size()/2)); //find median of each subset
			}
			Point med = select(medians, medians.size()/2);//median of medians
			//partition t into 3 sets around med
			ArrayList<Point> smaller = new ArrayList<Point>(),
					equal = new ArrayList<Point>(),
					larger = new ArrayList<Point>();
			for(int i = 0; i < t.size(); i++){
				if(t.get(i).get_x() < med.get_x()) smaller.add(t.get(i));
				else if(t.get(i).get_x() > med.get_x()) larger.add(t.get(i));
				else equal.add(t.get(i));
			}
			if(k < smaller.size()) return select(smaller, k);
			else if(k <= smaller.size() + equal.size()) return med;
			else return select(larger, k - smaller.size() - equal.size());
		}

		private ArrayList<Point> sort(ArrayList<Point> T){
			if(T.size() == 1) return T;
			Point highest = T.get(0);
			for(int i = 0; i < T.size(); i++){
				if(T.get(i).get_x() > highest.get_x()){
					highest = T.get(i);
				}
			}
			T.remove(highest);
			T = sort(T);
			T.add(highest);
			return T;
		}
	}

	/**
	 * Uses the incremental approach to solve the convex hull
	 * 
	 * @author jbonita
	 */
	public static class Incremental extends NotifyingThread {
		private static ArrayList<Line> upperHull;
		private static ArrayList<Line> lowerHull;
		@Override
		public void doRun() {
			upperHull = new ArrayList<Line>();
			lowerHull = new ArrayList<Line>();
			// sort points by x
			Main.setInfoText("Sorting points by x-coordinate");
			mergeSortByX(0, Main.points.size()-1);
			for (Point p : Main.points) {
				p.setColor(Color.blue);
				step();
			}
			for (Point p : Main.points)
				p.setColor(Color.LIGHT_GRAY);
			Main.setInfoText("Starting with 3 points");
			//adding first point
			//adding second point
			
			//adding third point
			if(ccw(Main.points.get(2), Main.points.get(1), Main.points.get(0))){
				lowerHull.add(new Line(Main.points.get(2), Main.points.get(1)));
				lowerHull.add(new Line(Main.points.get(1), Main.points.get(0)));
				upperHull.add(new Line(Main.points.get(2), Main.points.get(0)));	
			}else{
				upperHull.add(new Line(Main.points.get(2), Main.points.get(1)));
				upperHull.add(new Line(Main.points.get(1), Main.points.get(0)));
				lowerHull.add(new Line(Main.points.get(2), Main.points.get(0)));
			}
			for(Line l : upperHull){
				Main.lines.add(l);
			}
			for(Line l : lowerHull){
				Main.lines.add(l);
			}
			step();
			// insert points one by one and form a convex hull around them
			for (int i = 3; i < Main.points.size(); i++) {
				Point p = Main.points.get(i);
				p.setColor(Color.pink);
				// find upper tangency point
				Main.setInfoText("Adding new point to upper hull");
				step();
				Iterator<Line> itu = upperHull.iterator();
				boolean found = false;
				ArrayList<Line> toRemove = new ArrayList<Line>();
				while (!found && itu.hasNext()) {
					Line hi = itu.next();
					hi.setColor(Color.magenta);
					hi.getStart().setColor(Color.blue);
					hi.getEnd().setColor(Color.blue);
					Line test = new Line(p, hi.getStart(), Color.pink);
					Main.lines.add(test);
					step();
					if(!ccw(p, hi.getStart(), hi.getEnd())){
						//hi is the upper tangency point
						upperHull.add(0, test);
						found = true;
						hi.setColor(Color.black);
						hi.getStart().setColor(Color.orange);
						hi.getEnd().setColor(Color.orange);
					}else{
						//p is not connected to the start of this line
						hi.getStart().setColor(Color.LIGHT_GRAY);
						hi.getEnd().setColor(Color.orange);
						hi.setColor(Color.red);
						toRemove.add(hi);
						itu.remove();
						Main.lines.remove(test);
					}
					test.setColor(Color.black);
				}
				if(!itu.hasNext()){
					//we reached the leftmost point
					upperHull.add(new Line(p, Main.points.get(0)));
					Main.lines.add(upperHull.get(0));
				}
				
				// find lower tangency point
				Main.setInfoText("Adding new point to lower hull");
				step();
				Iterator<Line> itl = lowerHull.iterator();
				found = false;
				while (!found && itl.hasNext()) {
					Line hi = itl.next();
					hi.setColor(Color.orange);
					hi.getStart().setColor(Color.blue);
					hi.getEnd().setColor(Color.blue);
					Line test = new Line(p, hi.getStart(), Color.pink);
					Main.lines.add(test);
					step();
					if(ccw(p, hi.getStart(), hi.getEnd())){
						//hi is the upper tangency point
						lowerHull.add(0, test);
						found = true;
						hi.setColor(Color.black);
						hi.getStart().setColor(Color.magenta);
						hi.getEnd().setColor(Color.magenta);
					}else{
						//p is not connected to the start of this line
						hi.getStart().setColor(Color.lightGray);
						hi.getEnd().setColor(Color.magenta);
						hi.setColor(Color.red);
						toRemove.add(hi);
						itl.remove();
						Main.lines.remove(test);
					}
					test.setColor(Color.black);
				}
				if(!itl.hasNext()){
					//we reached the leftmost point
					lowerHull.add(new Line(p, Main.points.get(0)));
					Main.lines.add(lowerHull.get(0));
				}
				for(Line l : toRemove)Main.lines.remove(l);
				
				p.setColor(Color.black);
			}
		}
	}

	/**
	 * sorts Main.points by increasing distance from vertical axis
	 * 
	 * @param start
	 *            should be 0 when called
	 * @param end
	 *            should be Main.points.size()-1 when called
	 */
	public static void mergeSortByX(int start, int end) {
		if (end == start) {
			return;
		}
		if (end == start + 1) {
			if (Main.points.get(end).get_x() < Main.points.get(start).get_x()) {
				Point t = Main.points.get(start);
				Main.points.set(start, Main.points.get(end));
				Main.points.set(end, t);
			}
			return;
		}
		int mid = (end + start) / 2;
		mergeSortByX(start, mid);
		mergeSortByX(mid, end);
		// merge
		ArrayList<Point> temp = new ArrayList<Point>();
		int i = start, j = mid;
		while (i < mid && j <= end) {
			if (Main.points.get(i).get_x() > Main.points.get(j).get_x())
				temp.add(Main.points.get(j++));
			else
				temp.add(Main.points.get(i++));
		}
		while (i < mid)
			temp.add(Main.points.get(i++));
		while (j <= end)
			temp.add(Main.points.get(j++));
		// copy
		i = 0;
		for (int x = start; x <= end; x++) {
			Main.points.set(x, temp.get(i++));
		}
	}

	/**
	 * Determines if three points form a counterclockwise turn
	 * 
	 * @return true if the points from a couterclockwise turn
	 */
	public static boolean ccw(Point p1, Point p2, Point p3) {
		return (p2.get_x() - p1.get_x()) * (p3.get_y() - p1.get_y())
				- (p2.get_y() - p1.get_y()) * (p3.get_x() - p1.get_x()) > 0;
	}

	/**
	 * Sorts Main.points by increasing polar angle from a given reference point
	 * 
	 */
	public static void mergeSortbyAngle(Point ref, int start, int end) {
		if (end <= start) {
			return;
		}
		if (end == start + 1) {
			if (ref.polarAngleTo(Main.points.get(end)) < ref
					.polarAngleTo(Main.points.get(start))) {
				Point t = Main.points.get(start);
				Main.points.set(start, Main.points.get(end));
				Main.points.set(end, t);
			}
			return;
		}
		int mid = (end + start) / 2;
		mergeSortbyAngle(ref, start, mid);
		mergeSortbyAngle(ref, mid, end);
		// merge
		ArrayList<Point> temp = new ArrayList<Point>();
		int i = start, j = mid;
		while (i < mid && j <= end) {
			if (ref.polarAngleTo(Main.points.get(i)) > ref
					.polarAngleTo(Main.points.get(j)))
				temp.add(Main.points.get(j++));
			else
				temp.add(Main.points.get(i++));
		}
		while (i < mid)
			temp.add(Main.points.get(i++));
		while (j <= end)
			temp.add(Main.points.get(j++));
		// copy
		i = 0;
		for (int x = start; x <= end; x++) {
			Main.points.set(x, temp.get(i++));
		}
	}

	/**
	 * Graham Scan by Ronald Graham
	 * 
	 * @author jbonita
	 */
	public static class Graham extends NotifyingThread {

		@Override
		public void doRun() {
			// find point with highest y-coordinate (lowest point on display);
			// this point is on the hull
			Main.setInfoText("Finding lowest point");
			Point lowest = Main.points.get(0);
			lowest.setColor(Color.green);
			for (Point p : Main.points) {
				p.setColor(Color.orange);
				step();
				if (p.get_y() > lowest.get_y()) {
					lowest.setColor(Color.LIGHT_GRAY);
					lowest = p;
					lowest.setColor(Color.green);
				} else {
					p.setColor(Color.LIGHT_GRAY);
				}
			}
			lowest.setColor(Color.magenta);
			int index = Main.points.indexOf(lowest);
			Point temp = Main.points.get(0);
			Main.points.set(0, lowest);
			Main.points.set(index, temp);
			// sort points by polar angle from lowest point
			Main.setInfoText("Sorting points by polar angle");
			mergeSortbyAngle(lowest, 1, Main.points.size() - 1);
			for (int i = 0; i < Main.points.size(); i++) {
				Main.lines.add(new Line(lowest, Main.points.get(i),
						Color.magenta));
				step();
				Main.lines.clear();
			}
			Main.lines.add(new ExtendedLine(lowest, Main.points.get(1),
					Color.pink));
			Main.lines.add(new Line(lowest, Main.points.get(1)));
			Main.points.get(1).setColor(Color.black);
			step();
			Point pi = Main.points.get(2);
			int m = 1; // # of points on the hull so far
			for (int i = 2; i < Main.points.size(); i++) {
				Main.setInfoText("Connecting points in order");
				pi = Main.points.get(i);
				Main.lines.set(0, new ExtendedLine(lowest, pi, Color.pink));
				pi.setColor(Color.blue);
				Main.lines.add(new Line(Main.points.get(m), pi, Color.green));
				step();
				// cut off segments that won't be on the hull
				while (!ccw(Main.points.get(m - 1), Main.points.get(m), pi)) {
					Main.setInfoText("Deleting points that cause left turns");
					Main.lines.remove(1 + m--);
					Main.lines.remove(1 + m);
					Main.lines.add(new Line(Main.points.get(m), pi, Color.red));
					Main.points.get(m+1).setColor(Color.red);
					step();

				}
				Main.lines.get(1 + m).setColor(Color.black);
				m++;
				// swap point i with point m
				Main.points.set(i, Main.points.get(m));
				Main.points.set(m, pi);
				pi.setColor(Color.black);
			}
			Main.lines.add(new Line(pi, lowest));
			Main.lines.remove(0);
		}

	}

	/**
	 * Quickhull - quicksort-like algorithm for finding a convex hull by W. Eddy
	 * 
	 * @author jbonita
	 */
	public static class Quick extends NotifyingThread {

		@Override
		public void doRun() {
			// find lowest and highest points
			Main.setInfoText("Finding lowest and highest points");
			Point lowest, highest;
			if (Main.points.get(0).get_y() < Main.points.get(1).get_y()) {
				lowest = Main.points.get(0);
				highest = Main.points.get(1);
			} else {
				highest = Main.points.get(0);
				lowest = Main.points.get(1);
			}
			for (int i = 2; i < Main.points.size(); i++) {
				lowest.setColor(Color.magenta);
				highest.setColor(Color.orange);
				Point p = Main.points.get(i);
				p.setColor(Color.cyan);
				step();
				if (p.get_y() < lowest.get_y() || p.get_y() > highest.get_y())
					if (p.get_y() < lowest.get_y()) {
						lowest.setColor(Color.LIGHT_GRAY);
						lowest = p;
					} else {
						highest.setColor(Color.LIGHT_GRAY);
						highest = p;
					}
				else {
					p.setColor(Color.LIGHT_GRAY);
				}
			}
			lowest.setColor(Color.green);
			highest.setColor(Color.green);
			Line line1 = new Line(lowest, highest, Color.yellow);
			Main.lines.add(line1);
			step();

			// partition the remaining points into two sets
			ArrayList<Point> s1 = new ArrayList<Point>();
			ArrayList<Point> s2 = new ArrayList<Point>();
			Main.setInfoText("Dividing points into two sets");
			for (Point p : Main.points) {
				if (p != lowest && p != highest) {
					if (ccw(lowest, highest, p)) {
						s1.add(p);
						p.setColor(Color.orange);
					} else {
						s2.add(p);
						p.setColor(Color.magenta);
					}
					step();
				}
			}

			// starting algorithm
			Main.lines.remove(line1);
			quickhull(line1, s1);
			quickhull(line1, s2);
		}

		private void quickhull(Line l, ArrayList<Point> s) {
			if (s.size() == 0) {
				Main.lines.add(l);
				step();
				l.setColor(Color.black);
				return;
			}
			l.setColor(Color.green);
			Main.lines.add(l);
			Main.setInfoText("Finding farthest point from line");
			Point farthest = s.get(0);
			farthest.setColor(Color.blue);
			step();
			for (Point p : s) {
				p.setColor(Color.cyan);
				if (p.distanceTo(l) >= farthest.distanceTo(l)) {
					farthest.setColor(Color.LIGHT_GRAY);
					farthest = p;
					step();
					p.setColor(Color.blue);
				} else {
					step();
					p.setColor(Color.LIGHT_GRAY);
				}
			}
			farthest.setColor(Color.green);
			Line triangle1 = new Line(l.getEnd(), farthest, Color.yellow);
			Line triangle2 = new Line(l.getStart(), farthest, Color.yellow);
			Main.lines.add(triangle1);
			Main.lines.add(triangle2);
			ArrayList<Point> s1 = new ArrayList<Point>();
			ArrayList<Point> s2 = new ArrayList<Point>();
			Main.setInfoText("Sorting remaining points into subsets");
			for (Point p : s) {
				if (p != farthest) {
					if (!p.on_same_side(l.getStart(), triangle1)) {
						p.setColor(Color.orange);
						s1.add(p);
					} else if (!p.on_same_side(l.getEnd(), triangle2)) {
						p.setColor(Color.magenta);
						s2.add(p);
					} else {
						p.setColor(Color.red);
					}
					step();
				}
			}
			quickhull(triangle1, s1);
			quickhull(triangle2, s2);
			Main.lines.remove(triangle1);
			Main.lines.remove(triangle2);
			Main.lines.remove(l);
		}
	}

	/**
	 * Jarvis March A.K.A "Gift-Wrapping" by R.A. Jarvis
	 * 
	 * @author jbonita
	 */
	public static class Jarvis extends NotifyingThread {

		@Override
		public void doRun() {
			Main.setInfoText("Finding leftmost point...");
			Point p1 = Main.points.get(0);
			p1.setColor(Color.green);
			int first = 0;
			for (int n = 1; n < Main.points.size(); n++) {
				Point p = Main.points.get(n);
				p.setColor(Color.orange);

				step();

				if (p.get_x() < p1.get_x()) {
					first = n;
					p1.setColor(Color.black);
					p1 = p;
					p1.setColor(Color.green);
				} else
					p.setColor(Color.black);
			}
			p1.setColor(Color.magenta);
			Main.setInfoText("Finding each point on the hull in sequence");
			// "March" around the hull counter-clockwise until p1 is reached
			// again
			Point temp = Main.points.get(0);
			Main.points.set(0, p1);
			Main.points.set(first, temp);
			int i = 1;
			Point pi = p1;
			Point pj;
			double lastslope = Double.POSITIVE_INFINITY; // the slope of the
															// last edge is used
															// to find the next
															// point on the hull
			do {
				if (i >= Main.points.size()) {
					Main.lines.add(new Line(pi, p1));
					break;
				}
				Line ij = new Line(pi, Main.points.get(i), Color.green);
				ij.getEnd().setColor(Color.green);
				Main.lines.add(ij);

				step();

				for (int k = i + 1; k < Main.points.size(); k++) {
					Point pk = Main.points.get(k);
					pk.setColor(Color.orange);
					Line ik = new Line(pi, pk, Color.orange);
					Main.lines.add(ik);

					step();
					if (ik.isMoreConvexThan(ij, lastslope)) {
						ij.getEnd().setColor(Color.black);
						ik.setColor(Color.green);
						ik.getEnd().setColor(Color.green);
						Main.lines.remove(ij);
						ij = ik;
					} else {
						pk.setColor(Color.black);
						Main.lines.remove(ik);
					}
				}
				// check if this is the last edge
				if (i != 1) {
					Line i1 = new Line(pi, p1);
					Main.lines.add(i1);

					step();

					if (i1.isMoreConvexThan(ij, lastslope)) {
						ij.getEnd().setColor(Color.black);
						Main.lines.remove(ij);
						ij = i1;
					} else {
						Main.lines.remove(i1);
					}
				}

				pj = ij.getEnd();
				pj.setColor(Color.red);
				ij.setColor(Color.black);
				lastslope = ij.getSlope();

				// swap this point so the hull is in order in the arraylist
				temp = Main.points.get(i);
				int jindex = Main.points.indexOf(pj);
				Main.points.set(i, pj);
				Main.points.set(jindex, temp);
				i++;
				pi = pj;

				step();

			} while (!pj.equals(p1));
		}

	}

	/**
	 * this algorithm tests all possible point combinations and sees if they are
	 * on the edge by checking if all other points are on one side of it
	 * 
	 * @author jbonita
	 */
	public static class Naive extends NotifyingThread {

		@Override
		public void doRun() {
			Main.setInfoText("Checking all n^2 pairs of points... this may take a while");
			for (Point pi : Main.points) {
				pi.setColor(Color.green);

				for (Point pj : Main.points) {
					if (!pi.equals(pj)) {
						// make a pair of points to check if their segment is on
						// the hull

						pj.setColor(Color.green);
						Line ij = new ExtendedLine(pi, pj, Color.green);

						if (!Main.lines.contains(ij)) {
							Main.lines.add(ij);
							step();
							boolean valid = true;
							// pick an unused point for reference
							int x = 0;
							while (Main.points.get(x).equals(pi)
									|| Main.points.get(x).equals(pj))
								x = (int) (Math.random() * Main.points.size());
							Point px = Main.points.get(x);
							px.setColor(Color.blue);

							ArrayList<Line> temp = new ArrayList<Line>();
							// try to find a point that is on the other side of
							// the line ij from px
							for (int k = 0; valid && k < Main.points.size(); k++) {
								Point pk = Main.points.get(k);
								if (!pk.equals(pi) && !pk.equals(pj)
										&& !pk.equals(px)) {
									pk.setColor(Color.blue);
									Line xk = new Line(px, pk, Color.blue);

									valid = px.on_same_side(pk, ij);
									if (!valid)
										xk.setColor(Color.red);

									Main.lines.add(xk);

									step();
									temp.add(xk);
									pk.setColor(Color.black);
								}
							}
							for (Line l : temp)
								Main.lines.remove(l);
							temp.clear();

							px.setColor(Color.black);
							if (valid) {
								Main.lines.add(new Line(pi, pj));
							}
							Main.lines.remove(ij);

							step();
						}
						pj.setColor(Color.black);
					}
				}
				pi.setColor(Color.black);
			}
		}
	}
}
