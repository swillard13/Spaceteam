package spaceteam.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class TimeBar extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2014548265265118231L;
	
	private int currentTime, totalTime;
	/**
	 * Constructor
	 */
	TimeBar() {
		super();
		currentTime = 0;
		totalTime = 0;
	}
	
	/**
	 * paint a rectangle proportional to the timer
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = this.getWidth();
		int timeRemaining = currentTime/totalTime * width;
		
		g.setColor(Color.YELLOW);
		g.drawRect(0, 0, timeRemaining, this.getHeight());
	}
	
	
	/**
	 * Will input the current and total time from the GUI
	 * @param current
	 * @param total
	 */
	public void currentTimeRemaining(int current, int total) {
		currentTime = current;
		totalTime = total;
		repaint();
	}
}
