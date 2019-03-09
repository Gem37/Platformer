package levelEditor;

import java.awt.Image;

import javax.swing.ImageIcon;

import game.Gmae;
public class ImageLoader {
	
	
	
	static ImageIcon air = new ImageIcon("air.png");
	static ImageIcon singlePlatform = new ImageIcon("slice01_01.png");
	static ImageIcon left = new ImageIcon("slice02_02.png");
	static ImageIcon middle = new ImageIcon("slice03_03.png");
	static ImageIcon right = new ImageIcon("slice04_04.png");
	static ImageIcon dirt = new ImageIcon("slice27_27.png");
	static ImageIcon bottom = new ImageIcon("Bottom.png");
	static ImageIcon leftSide = new ImageIcon("LeftSide.png");
	static ImageIcon rightSide = new ImageIcon("RightSide.png");
	static ImageIcon topLeft = new ImageIcon("TopLeft.png");
	static ImageIcon topRight = new ImageIcon("TopRight.png");
	static ImageIcon topLeftDot = new ImageIcon("TopLeftDot.png");
	static ImageIcon topRightDot = new ImageIcon("TopRightDot.png");
	static ImageIcon bottomRight = new ImageIcon("slice29_29.png");
	static ImageIcon bottomLeft = new ImageIcon("slice28_28.png");
	static ImageIcon bottomLeftDot = new ImageIcon("slice30_30.png");
	static ImageIcon bottomRightDot = new ImageIcon("slice31_31.png");
	static ImageIcon goal = new ImageIcon("goal.png");
	
	static ImageIcon[] images = {air, singlePlatform, left, middle, right, dirt, bottom, leftSide, rightSide, topLeft, topRight, bottomLeft, bottomRight, topLeftDot, topRightDot, bottomLeftDot, bottomRightDot, goal};
	

	public static ImageIcon[] getImages() {
		for(int i = 0; i < images.length; i++) {
			images[i].setImage(images[i].getImage().getScaledInstance(Gmae.tileSize, Gmae.tileSize, Image.SCALE_DEFAULT));
		}
		
		return images;
	}
}


