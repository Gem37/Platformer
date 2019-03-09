package game;
import javax.swing.ImageIcon;

public class Entity {
	public double x;
	public double y;
	double fallingSpeed;
	ImageIcon image;
	public int width;
	public int height;
	
	public Entity(ImageIcon image) {
		height = image.getIconHeight();
		width = image.getIconWidth();
	}
	
	public double scrolledX() {
		return x + Gmae.scrollX;
	}
	public double scrolledY() {
		return y + Gmae.scrollY;
	}
	
	@Override
	public String toString() {
		return x + "," + y;
	}
	
}
