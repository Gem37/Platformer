package levelEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.Enemy;
import game.Gmae;

@SuppressWarnings("serial")
public class LevelEditor extends JPanel implements KeyListener, MouseListener {
	static JFrame frame = new JFrame();

	final static int mapSize = 25;
	final static int bufferSize = 2;
	final static int totalTileSize = mapSize + bufferSize * 2;
	final int sideBarWidth = 100;
	final int sideBarItemSize = 50;
	final int sideBarItemSpacing = 35 + sideBarItemSize;
	
	int selectedItem = 2;
	
	int playerX = 2;
	int playerY = 2;
	
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	ImageIcon playerImage = new ImageIcon("PlayerImage.png");
	ImageIcon enemyImage = new ImageIcon("Enemy.png");
	
	static int[][] tiles = new int[totalTileSize][totalTileSize];

	static int tileSize = Gmae.tileSize;

	static ImageIcon[] images = ImageLoader.getImages();
	Image[] sideBarImages =
			{playerImage.getImage().getScaledInstance(sideBarItemSize, sideBarItemSize, Image.SCALE_SMOOTH),
			enemyImage.getImage().getScaledInstance(sideBarItemSize, sideBarItemSize, Image.SCALE_SMOOTH),
			images[1].getImage().getScaledInstance(sideBarItemSize, sideBarItemSize, Image.SCALE_SMOOTH),
			images[17].getImage().getScaledInstance(sideBarItemSize, sideBarItemSize, Image.SCALE_SMOOTH)};
	
	double scrollX = 100;
	double scrollY = 0;

	boolean input = false;

	public static void main(String[] args) {
		
		LevelEditor g = new LevelEditor();
		frame.addKeyListener(g);
		frame.addMouseListener(g);
		frame.add(g);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);

		for(int i = 0; i < images.length; i++) {
			images[i].setImage(images[i].getImage().getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT));
		}
		
	}

	public void paintComponent(Graphics g) {
		if(input) {
			updateTiles();
			for(int y = 0; y < tiles.length; y++) {
				for(int x = 0; x < tiles[y].length; x++) {
					g.drawImage(images[tiles[y][x]].getImage(), x * tileSize + (int) scrollX, y * tileSize + (int) scrollY, null);
				}
			}
			g.drawImage(playerImage.getImage(), (int) ((playerX + 0.5) * tileSize - Gmae.playerWidth / 2 + scrollX), (int) ((playerY + 0.5) * tileSize - Gmae.playerHeight / 2 + scrollY), this);
			for(Enemy e : enemies) {
				g.drawImage(e.image.getImage(), (int) (e.x + scrollX), (int) (e.y + scrollY), this);
			}
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, sideBarWidth, 800);
			for(int i = 0; i < sideBarImages.length; i++) {
				g.drawImage(sideBarImages[i], (int) (sideBarWidth / 2 - sideBarItemSize / 2), (int) (sideBarItemSpacing * (i+0.5) - sideBarItemSize / 2), this);
			}
		}
	}

	public void updateTiles() {
		for(int y = 1; y < tiles.length - 1; y++) {
			for(int x = 1; x < tiles[y].length - 1; x++) {
				String tileText = surroundingTiles(x, y);
				if(tiles[y][x] != 0 && tiles[y][x] != 17) {
					if(tileText.equals("11111111")) {
						tiles[y][x] = 5;
					} else if(tileText.equals("10111111") || tileText.equals("00111111") || tileText.equals("00011111") || tileText.equals("10011111")) {
						tiles[y][x] = 7; // Left Side
					} else if(tileText.equals("00000000")) {
						tiles[y][x] = 1; // Single Platform
					} else if(tileText.equals("11101111") || tileText.equals("01101111") || tileText.equals("11101011") || tileText.equals("01101011") || tileText.equals("01000010")) {
						tiles[y][x] = 3; // Middle
					} else if(tileText.equals("11111000") || tileText.equals("11111100") || tileText.equals("11111001") || tileText.equals("11111101")) {
						tiles[y][x] = 8; // Right Side
					} else if(tileText.equals("00000111") || tileText.equals("00000011") || tileText.equals("00000010") || tileText.equals("00000110")) {
						tiles[y][x] = 2; // Left
					} else if(tileText.equals("11100000") || tileText.equals("01100000") || tileText.equals("11000000") || tileText.equals("01000000")) {
						tiles[y][x] = 4; // Right
					} else if(tileText.equals("11010110") || tileText.equals("11110110") || tileText.equals("11010111") || tileText.equals("11110111") ) {
						tiles[y][x] = 6; // Bottom
					} else if(tileText.equals("00001011") || tileText.equals("00101011") || tileText.equals("00001111") || tileText.equals("00101111") ) {
						tiles[y][x] = 9; // Top Left
					} else if(tileText.equals("01101000") || tileText.equals("11101000") || tileText.equals("01101001") || tileText.equals("11101001")) {
						tiles[y][x] = 10; // Top Right
					} else if(tileText.equals("00010110") || tileText.equals("10010110") || tileText.equals("00010111") || tileText.equals("10010111")) {
						tiles[y][x] = 11; // Bottom Left
					} else if(tileText.equals("11010000") || tileText.equals("11110000") || tileText.equals("11010100") || tileText.equals("11110100")) {
						tiles[y][x] = 12; // Bottom Right
					} else if(tileText.equals("01111111")) {
						tiles[y][x] = 13; // Top Left Dot
					} else if(tileText.equals("11111011")) {
						tiles[y][x] = 14; // Top Right Dot
					} else if(tileText.equals("11011111")) {
						tiles[y][x] = 15; // Bottom Left Dot
					} else if(tileText.equals("11111110")) {
						tiles[y][x] = 16; // Bottom Right Dot
					} 
				}
			}
		}
	}

	public String surroundingTiles(int x, int y) {
		String tileText = "";
		tileText += (tiles[y - 1][x - 1] == 0 || tiles[y - 1][x - 1] == 17) ? 0 : 1;
		tileText += (tiles[y][x - 1] == 0 || tiles[y][x - 1] == 17) ? 0 : 1;
		tileText += (tiles[y + 1][x - 1] == 0 || tiles[y + 1][x - 1] == 17) ? 0 : 1;
		tileText += (tiles[y - 1][x] == 0 || tiles[y - 1][x] == 17) ? 0 : 1;
		tileText += (tiles[y + 1][x] == 0 || tiles[y + 1][x] == 17) ? 0 : 1;
		tileText += (tiles[y - 1][x + 1] == 0 || tiles[y - 1][x + 1] == 17) ? 0 : 1;
		tileText += (tiles[y][x + 1] == 0 || tiles[y][x + 1] == 17) ? 0 : 1;
		tileText += (tiles[y + 1][x + 1] == 0 || tiles[y + 1][x + 1] == 17) ? 0 : 1;
		return tileText;
	}

	public void saveLevel(char c) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter writer = new PrintWriter(Gmae.levelFileNames[Integer.parseInt(Character.toString(c)) - 1], "UTF-8");

		writer.println(playerX + "," + playerY);
		
		for(Enemy e : enemies) {
			writer.print(e.toString() + " ");
		}
		
		if(enemies.size() == 0) {
			writer.print(" ");
		}
		
		writer.println();
		
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles[0].length; x++) {
				writer.print(tiles[y][x] + " ");
			}
			writer.println();
		}
		writer.close();
	}

	public void loadLevel(String fileName) {
		try {
			ArrayList<int[]> levelArrayList = new ArrayList<int[]>();
			FileReader f = new FileReader(fileName);
			BufferedReader b = new BufferedReader(f);

			String line = b.readLine();

			playerX = Integer.parseInt(line.substring(0, line.indexOf(",")));
			playerY = Integer.parseInt(line.substring(line.indexOf(",") + 1));
			
			line = b.readLine();
			
			String[] strings = line.split(" ");
			
			for(String s : strings) {
				enemies.add(new Enemy(Double.parseDouble(s.substring(0, s.indexOf(","))), Double.parseDouble(s.substring(s.indexOf(",") + 1))));
			}
			
			while(b.ready()) {
				line = b.readLine();
				if(line == "") {
					break;
				}
				strings = line.split(" ");
				int[] ints = new int[strings.length];
				for(int i = 0; i < strings.length; i++) {
					if(strings[i] != "") {
						ints[i] = Integer.parseInt(strings[i]);
					}
				}
				levelArrayList.add(ints);
			}
			b.close();
			tiles = (int[][]) levelArrayList.toArray(new int[levelArrayList.size()][]);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(Character.isDigit(e.getKeyCode())) {
			if(input) {
				try {
					saveLevel(e.getKeyChar());
					System.out.println("Saving Level");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			} else {
				int numberPressed = Integer.parseInt(Character.toString(e.getKeyChar()));
				input = true;
				if(numberPressed == 0) {
					for(int y = 0; y < 29; y++) {
						for(int x = 0; x < 29; x++) {
							if(y < bufferSize || y > totalTileSize - (bufferSize + 1) || x < bufferSize || x > totalTileSize - (bufferSize + 1)) {
								tiles[y][x] = 5;
							}
						}
					}
				} else {
					loadLevel(Gmae.levelFileNames[numberPressed - 1]);
				}
				updateTiles();
			}
		} else {
			if(e.getKeyChar() == 'w') {
				scrollY += 5;
			}
			if(e.getKeyChar() == 's') {
				scrollY -= 5;
			}
			if(e.getKeyChar() == 'a') {
				scrollX += 5;
			}
			if(e.getKeyChar() == 'd') {
				scrollX -= 5;
			}
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		double x = e.getX() - scrollX;
		double y = e.getY() - 20 - scrollY;
		if(e.getX() <= sideBarWidth) {
			if(e.getX() >= (sideBarItemSize / 2) && e.getX() <= (sideBarWidth - sideBarItemSize / 2)) {
				for(int i = 0; i < sideBarImages.length; i++) {
					if(e.getY() - 20 >= (int) (sideBarItemSpacing * (i+0.5) - sideBarItemSize / 2) && e.getY() - 20 <= (int) (sideBarItemSpacing * (i+0.5) - sideBarItemSize / 2 + sideBarItemSize)) {
						selectedItem = i;
					}
				}
			}
			
		} else {
			if(selectedItem == 0) {
				playerX = (int) (x / tileSize);
				playerY = (int) (y / tileSize);
			} else if(selectedItem == 3) {
				if(tiles[(int) (y / tileSize)][(int) x / tileSize] == 0) {
					tiles[(int) (y / tileSize)][(int) x / tileSize] = 17;
				} else {
					tiles[(int) (y / tileSize)][(int) x / tileSize] = 0;
				}
			} else if(selectedItem == 1) {
				boolean found = false;
				for(Enemy enemy : enemies) {
					if(Math.sqrt((x - 5 - enemy.x) * (x - 5 - enemy.x) + (y - 5 - enemy.y) * (y - 5 - enemy.y)) < 30) {
						found = true;
						enemies.remove(enemy);
						break;
					}
				}
				
				if(!found) {
					enemies.add(new Enemy(x, y - 5));
				}
			} else if(selectedItem == 2) {
				if(tiles[(int) (y / tileSize)][(int) x / tileSize] == 0) {
					tiles[(int) (y / tileSize)][(int) x / tileSize] = 3;
				} else {
					tiles[(int) (y / tileSize)][(int) x / tileSize] = 0;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
