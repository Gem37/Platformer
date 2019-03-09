package game;

public class TankEnemy extends Enemy {
	public TankEnemy(double enemyX, double enemyY) {
		super(enemyX, enemyY);
		health = 10;
		speed = 5;
	}
}