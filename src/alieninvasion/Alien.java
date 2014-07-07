package alieninvasion;

import javax.swing.ImageIcon;

/**
 * 
 * @author Revauthore
 */
public class Alien extends Sprite {
	/**
	 * minimum speed of alien
	 */
	private final int MIN_SPEED = 3;

	/**
	 * maximum speed of alien
	 */
	private final int MAX_SPEED = 10;

	/**
	 * score for killing this alien
	 */
	private final int SCORE = 20;

	private final int marginX = -50;

	public Alien(int x, int y) {
		this.x = x;
		this.y = y;

		speed = Game.getGameSpeed()
				+ (int) Math.max(MIN_SPEED, Math.random() * MAX_SPEED);

		// type of alien is based on its speed
		int type;
		if (speed > 8) {
			type = 5;
		} else if (speed > 6) {
			type = 4;
		} else if (speed > 5) {
			type = 3;
		} else if (speed > 4) {
			type = 2;
		} else {
			type = 1;
		}

		image = new ImageIcon(this.getClass().getResource(
				"images/game/alien-" + type + ".png")).getImage();

		double scale = Math.min((double) Framework.getFrameHeight() / 700,
				(double) Framework.getFrameWidth() / 1300);
		width = (int) (image.getWidth(null) * scale);
		height = (int) (image.getHeight(null) * scale);
		visible = true;

		// set pixel bounds of the sprite
		computeBounds();
	}

	public int getScore() {
		return SCORE;
	}

	public void move() {
		x -= speed;
		if (x < -1 * (width - marginX)) {
			x = Framework.getFrameWidth() - marginX;
		}
	}
}
