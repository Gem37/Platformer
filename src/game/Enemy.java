package game;

import javax.swing.ImageIcon;

public class Enemy extends Entity {
	public double speed = 8.5;
	int health = 3;
	boolean isMovingRight = true;
	
	public static ImageIcon image = new ImageIcon("Enemy.png");
	
	public Enemy(double enemyX, double enemyY) {
		super(image);
		this.x = enemyX;
		this.y = enemyY;
	}
	
	public Enemy(double enemyX, double enemyY, String imageName) {
		super(new ImageIcon(imageName));
		this.x = enemyX;
		this.y = enemyY;
		image = new ImageIcon(imageName);
	}
}
