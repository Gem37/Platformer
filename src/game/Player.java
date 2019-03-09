package game;

import javax.swing.ImageIcon;

public class Player extends Entity {
	static ImageIcon image = new ImageIcon("PlayerImage.png");
	public Player(double playerX, double playerY) {
		super(image);
		this.x = playerX;
		this.y = playerY;
	}
}
