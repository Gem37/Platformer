package game;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import levelEditor.ImageLoader;

@SuppressWarnings("serial")
public class Gmae extends JPanel implements KeyListener {
	public final static int playerHeight = 18;
	public final static int playerWidth = playerHeight;
	final double playerSpeed = 20;

	static double scrollX = 0;
	static double scrollY = 0;
	final double scrollBoundsX = 200;
	final double scrollBoundsY = 200;
	
	static final int screenHeight = 400;
	static final int screenWidth = 512;
	
	static List<Enemy> enemies = Collections.synchronizedList(new ArrayList<Enemy>());
	
	static List<Projectile> projectiles = Collections.synchronizedList(new ArrayList<Projectile>());
	
	double bulletSpeed = 25;
	double bulletCooldown = 1.0/3.0;
	double bulletCooldownTimer = 0;
	
	static Player player = new Player(300, 300);

	double jumpMomentum = 0;

	double delta;

	public static final int tileSize = 32;

	ImageIcon[] images = ImageLoader.getImages();
	
	ImageIcon projectileImage = new ImageIcon("Projectile.png");
	
	public static String[] levelFileNames = {"file1.txt", "file2.txt", "file3.txt"};
	
	static int currentLevel = 0;
	
	static int[][] level;
	
	boolean loading = false;
	
	HashMap<Character, Boolean> keysPressed = new HashMap<Character, Boolean>();

	static ArrayList<Integer> passableTiles = new ArrayList<Integer>();
	
	static JFrame frame = new JFrame();
	ImageIcon playerImage = new ImageIcon("PlayerImage.png");
	
	public static void main(String[] args) {
		Gmae g = new Gmae();
		
		try {
			level = g.loadLevel(levelFileNames[currentLevel]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.addKeyListener(g);
		frame.add(g);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(screenWidth, screenHeight);
		frame.setVisible(true);
		frame.setResizable(false);
		
		passableTiles.add(0);
		passableTiles.add(17);
		
		System.out.println("Level " + (currentLevel + 1));
		
		g.run();
	}

	public void run() {
		double last = System.nanoTime();
		while(true) {
			delta = (System.nanoTime() - last) / 100000000; // When delta equals 10, one second will have passed
			last = System.nanoTime();
			bulletCooldownTimer += delta / 10;
			
			if(keysPressed.containsKey('r') && keysPressed.get('r')) {
				keysPressed.put('r', false);
				currentLevel--;
				nextLevel();
			}
			
			if(keysPressed.containsKey('a') && keysPressed.get('a') && !leftCollision(player)) {
				player.x -= playerSpeed * delta;
			}

			if(keysPressed.containsKey('d') && keysPressed.get('d') && !rightCollision(player)) {
				player.x += playerSpeed * delta;
			}
			
			if(keysPressed.containsKey(' ') && keysPressed.get(' ') && bulletCooldownTimer >= bulletCooldown) {
				shoot();
				bulletCooldownTimer = 0;
			}
			
			Player player = this.player;
			
			if(!isTouchingGround(player)) {
				player.fallingSpeed += 4 * delta;
			} else if(player.fallingSpeed > 0) {
				player.fallingSpeed = 0;
			}
			
			if(topCollision(player) && player.fallingSpeed < 0) {
				player.fallingSpeed = 0;
			}
			
			if(player.fallingSpeed > 75) {
				player.fallingSpeed = 75;
			}
			
			player.y += player.fallingSpeed * delta;
			
			player.y -= distanceInGround(player);
			if(distanceInGround(player) != 0) {
				System.out.println(distanceInGround(player));
			}
			// System.out.println(player.fallingSpeed);
			
//			if(isTouchingGround(player)) {
//				while(isTouchingGround(player)) {
//					player.y -= 1;
//				}
//				player.y += 1;
//			}
			
			if(isTouchingGround(player) && keysPressed.containsKey('w') && keysPressed.get('w')) {
				player.fallingSpeed = -25;
			}
			
			Iterator<Projectile> i = projectiles.iterator();
			while(i.hasNext()) {
				Projectile p = i.next();
				p.x += p.xV * delta;
				p.y += p.yV * delta;
				if(touchingSolid(p)) {
					i.remove();
				}
			}
			
			if(player.scrolledX() <= scrollBoundsX) {
				scrollX += 0.5;
			}
			if(player.scrolledX() >= screenWidth - scrollBoundsX) {
				scrollX -= 0.5;
			}
			if(player.scrolledY() <= scrollBoundsY) {
				scrollY += 0.5;
			}
			if(player.scrolledY() >= screenHeight - scrollBoundsY) {
				scrollY -= 0.5;
			}
			
			for(int j = 0; j < enemies.size(); j++) {
				Enemy e = enemies.get(j);
				if(rightCollision(e) || leftCollision(e)) {
					e.isMovingRight = !e.isMovingRight;
				} else if(passableTiles.contains(level[(int) ((e.y + e.height) / tileSize)][(int) ((e.x - 1) / tileSize)]) && !e.isMovingRight) {
					e.isMovingRight = !e.isMovingRight;
				} else if(passableTiles.contains(level[(int) ((e.y + e.height) / tileSize)][(int) ((e.x + e.width) / tileSize)]) && e.isMovingRight) {
					e.isMovingRight = !e.isMovingRight;
				}

				if(e.isMovingRight) {
					e.x += e.speed * delta;
				} else {
					e.x -= e.speed * delta;
				}

				if(!isTouchingGround(e)) {
					e.fallingSpeed += 0.00075;
					e.y += e.fallingSpeed * delta;
				} else {
					e.fallingSpeed = 0;
				}
				
				Iterator<Projectile> it = projectiles.iterator();
				while(it.hasNext()) {
					Projectile p = it.next();
					if(aTouchingB(p, e)) {
						e.health -= 1;
						it.remove();
					}
				}
				
				if(e.health < 1) {
					enemies.remove(e);
				}
				
				if(aTouchingB(e, player)) {
					currentLevel--;
					nextLevel();
					break;
				}
			}
			
			
			
			if(touchingTile(17, player) && !loading) {
				nextLevel();
			}
			
			repaint();
		}
	}

	public int[][] loadLevel(String fileName) throws IOException {
		loading = true;
		ArrayList<int[]> levelArrayList = new ArrayList<int[]>();
		FileReader f = new FileReader(fileName);
		BufferedReader b = new BufferedReader(f);
		
		String line = b.readLine();
		
		player.x = (Integer.parseInt(line.substring(0, line.indexOf(","))) + 0.5) * tileSize;
		player.y = (Integer.parseInt(line.substring(line.indexOf(",") + 1)) + 0.5) * tileSize;
		
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
		loading = false;
		return (int[][]) levelArrayList.toArray(new int[levelArrayList.size()][]);
	}
	
	public void shoot() {
		Point mousePos = frame.getMousePosition();
		if(mousePos != null) {
			double dist = Math.sqrt(Math.pow(mousePos.x - (player.scrolledX() + player.width / 2), 2) + Math.pow((mousePos.y - 20) - (player.scrolledY() + player.height / 2), 2));
			double xV = (((mousePos.x) - (player.scrolledX() + player.width / 2)) / dist) * bulletSpeed + (Math.random() - 0.5) * 3;
			double yV = (((mousePos.y - 20) - (player.scrolledY() + player.height / 2)) / dist) * bulletSpeed + (Math.random() - 0.5) * 3;
			synchronized(projectiles) {
				projectiles.add(new Projectile(projectileImage, player.x + player.width / 2 - projectileImage.getIconWidth() / 2, player.y + player.height / 2 - projectileImage.getIconHeight() / 2, xV, yV));
			}
		}
	}
	
	public boolean isInsideGround(Entity e) {
		int tile1 = level[(int) ((e.y + e.height - 1) / tileSize)][(int) (e.x / tileSize)];
		int tile2 = level[(int) ((e.y + e.height - 1) / tileSize)][(int) ((e.x + e.width - 1) / tileSize)];
		return !passableTiles.contains(tile1) || !passableTiles.contains(tile2);
	}
	
	public int distanceInGround(Entity e) {
		Point blockBelow = new Point((int) (e.x / tileSize), (int) ((e.y + e.height - 1) / tileSize));
		return (int) (!passableTiles.contains(level[blockBelow.y][blockBelow.x]) ? ((e.y + e.height) - blockBelow.y * 32) : 0);
	}
	
	public boolean isTouchingGround(Entity e) {
		int tile1 = level[(int) ((e.y + e.height) / tileSize)][(int) (e.x / tileSize)];
		int tile2 = level[(int) ((e.y + e.height) / tileSize)][(int) ((e.x + e.width - 1) / tileSize)];
		return !passableTiles.contains(tile1) || !passableTiles.contains(tile2);
	}

	public boolean topCollision(Entity e) {
		int tile1 = level[(int) ((e.y - 1) / tileSize)][(int) (e.x / tileSize)];
		int tile2 = level[(int) ((e.y - 1) / tileSize)][(int) ((e.x + e.width - 1) / tileSize)];
		return !passableTiles.contains(tile1) || !passableTiles.contains(tile2);
	}

	public boolean leftCollision(Entity e) {
		int tile1 = level[(int) (e.y / tileSize)][(int) ((e.x - 1) / tileSize)];
		int tile2 = level[(int) ((e.y + e.height - 1) / tileSize)][(int) ((e.x - 1) / tileSize)];
		return !passableTiles.contains(tile1) || !passableTiles.contains(tile2);
	}

	public boolean rightCollision(Entity e) {
		int tile1 = level[(int) (e.y / tileSize)][(int) ((e.x + e.width) / tileSize)];
		int tile2 = level[(int) ((e.y + e.height - 1) / tileSize)][(int) ((e.x + e.width) / tileSize)];
		return !passableTiles.contains(tile1) || !passableTiles.contains(tile2);
	}
	
	public boolean touchingTile(int tile, Entity e) {
		int topLeft = level[(int) ((e.y) / tileSize)][(int) (e.x / tileSize)];
		int topRight = level[(int) ((e.y) / tileSize)][(int) ((e.x + e.width - 1) / tileSize)];
		int bottomLeft = level[(int) ((e.y + e.height - 1) / tileSize)][(int) (e.x / tileSize)];
		int bottomRight = level[(int) ((e.y + e.height - 1) / tileSize)][(int) ((e.x + e.width - 1) / tileSize)];
		return topLeft == tile || topRight == tile || bottomLeft == tile || bottomRight == tile;
	}
	
	public boolean touchingSolid(Entity e) {
		return rightCollision(e) || leftCollision(e) || isTouchingGround(e) || topCollision(e);
	}
	
	public boolean aTouchingB(Entity a, Entity b) {
		return a.x > (b.x - a.width) && a.x < (b.x + b.width) && a.y > (b.y - a.height) && a.y < (b.y + b.height);
	}
	
	@SuppressWarnings("static-access")
	public void paintComponent(Graphics g) {
		for(int y = 0; y < level.length; y++) {
			for(int x = 0; x < level[0].length; x++) {
				g.drawImage(images[level[y][x]].getImage(), x * tileSize + (int) scrollX, y * tileSize + (int) scrollY, null);
			}
		}
		g.drawImage(playerImage.getImage(), (int) player.scrolledX(), (int) player.scrolledY(), this);
		
		synchronized (enemies) {
			for(Enemy e : enemies) {
				g.drawImage(e.image.getImage(), (int) e.scrolledX(), (int) e.scrolledY(), this);
			}
		}
		synchronized (projectiles) {
			for(Projectile e : projectiles) {
				g.drawImage(e.image.getImage(), (int) e.scrolledX(), (int) e.scrolledY(), this);
			}
		}
	}
	
	public void nextLevel() {
		if(currentLevel + 1 != levelFileNames.length) {
			currentLevel++;
		} else {
			System.out.println("You Won!");
			currentLevel = 0;
		}
		
		player.fallingSpeed = 0;
		
		enemies.clear();
		projectiles.clear();
		
		try {
			level = loadLevel(levelFileNames[currentLevel]);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Level " + (currentLevel + 1));
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysPressed.put(e.getKeyChar(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.put(e.getKeyChar(), false);
	}
}
