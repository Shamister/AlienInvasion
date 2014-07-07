package alieninvasion;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

/**
 * 
 * @author Revauthore
 */
public class Craft extends Sprite {

	private int dx, dy;
	private Set<Missile> missiles;

	/**
	 * minimum speed of craft
	 */
	private final int MIN_SPEED = 4;

	/**
	 * maximum speed of craft
	 */
	private final int MAX_SPEED = 8;

	/**
	 * margin of the craft towards the screen
	 */
	private final int marginX = 2;
	private final int marginY = 2;

	public Craft() {
		image = new ImageIcon(this.getClass().getResource(
				"images/game/craft.png")).getImage();

		visible = true;
		missiles = Collections.synchronizedSet(new HashSet<Missile>());

		double scale = Math.min((double) Framework.getFrameWidth() / 1360,
				(double) Framework.getFrameHeight() / 760);
		width = (int) (image.getWidth(null) * scale);
		height = (int) (image.getHeight(null) * scale);

		x = Framework.getFrameWidth() / 8;
		y = Framework.getFrameHeight() / 2;
		speed = Game.getGameSpeed()
				+ (int) Math.min(Math.max(Math.max(
						(double) Framework.getFrameWidth() / 400,
						(double) Framework.getFrameHeight() / 300), MIN_SPEED),
						MAX_SPEED);

		// get pixel bounds of the sprite
		computeBounds();
	}

	public void reset() {
		x = Framework.getFrameWidth() / 8;
		y = Framework.getFrameHeight() / 2;
		missiles = Collections.synchronizedSet(new HashSet<Missile>());
	}

	public void move() {
		x += dx * speed;
		y += dy * speed;

		checkBounds();
	}

	private void checkBounds() {
		int centerX = width / 2;
		int centerY = height / 2;
		if (x < marginX + centerX) {
			x = marginX + centerX;
		} else if (x > Framework.getFrameWidth() - centerX - marginX) {
			x = Framework.getFrameWidth() - centerX - marginX;
		}
		if (y < marginY + centerY) {
			y = marginY + centerY;
		} else if (y > Framework.getFrameHeight() - centerY - marginY) {
			y = Framework.getFrameHeight() - centerY - marginY;
		}
	}

	public Set<Missile> getMissiles() {
		return missiles;
	}

	public void setDirX(int dx) {
		this.dx = dx;
	}

	public void setDirY(int dy) {
		this.dy = dy;
	}

	public void resetMove() {
		dx = 0;
		dy = 0;
	}

	public void fire() {
		if (visible) {
			missiles.add(new Missile(x, y));
		}
	}
}
