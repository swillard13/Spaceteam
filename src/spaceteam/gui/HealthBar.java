package spaceteam.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class HealthBar extends JPanel{
	Image backgroundImage, shipImage, cometImage;
	private int health;

	public HealthBar(){
		try {
			backgroundImage = ImageIO.read(new File("src/spaceteam/gui/spacestrip.jpeg"));
			shipImage = ImageIO.read(new File ("src/spaceteam/gui/space-ship.png"));
			cometImage = ImageIO.read(new File ("src/spaceteam/gui/comet.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		health = 10;
	}
	
	/**
	* Update the health of a player visually by drawing images closer to one another
	*/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.drawImage(backgroundImage, 0, 0, this);
		
		
		g.drawImage(cometImage, (10 - health) * 45, 15, this);

		g.drawImage(shipImage, getWidth() - shipImage.getWidth(null), 0, this);
	}

	/**
	 * update the health of the player and show it visually
	 * @param h the player's updated health
	 */
	public void updateHealthBar (int h) {
		health = h;
		repaint();
	}
}
