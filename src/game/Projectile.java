package game;

import javax.swing.ImageIcon;

public class Projectile extends Entity {
	double angle;
	double xV;
	double yV;
	
	public Projectile(ImageIcon image, double x, double y, double xV, double yV) {
		super(image);
		this.image = image;
		this.x = x;
		this.y = y;
		this.xV = xV;
		this.yV = yV;
	}
}
