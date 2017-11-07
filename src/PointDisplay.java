import java.awt.*;
import javax.swing.*;

public class PointDisplay extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int POINT_RADIUS = 8;
	final int LINE_WIDTH = 3;
	int height, width;
	
	public PointDisplay(){
		super();
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = ((Graphics2D) g);
		height = getHeight();
		width = getWidth();
		//clear display
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);
		//display info at top of screen
		g2.setColor(Color.black);
		g2.setFont(new Font("Calibri", Font.PLAIN, (int)width/75 + (int)height/75));
		g2.drawString(Main.getInfoText(), 5, g2.getFont().getSize());
		//draw all lines
		for(Line l:Main.lines){
			g2.setColor(l.getColor());
			g2.setStroke(new BasicStroke(LINE_WIDTH));
			g2.drawLine((int)(width*l.getStart().get_x()) + POINT_RADIUS/2, (int)(height*l.getStart().get_y()) + POINT_RADIUS/2, (int)(width*l.getEnd().get_x()) + POINT_RADIUS/2, (int)(height*l.getEnd().get_y()) + POINT_RADIUS/2);
		}
		//draw all points
				for(Point p:Main.points){
					g2.setColor(p.getColor());
					g2.fillOval((int)(width*p.get_x()), (int)(height*p.get_y()), POINT_RADIUS, POINT_RADIUS);
				}
	}
}
