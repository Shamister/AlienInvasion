package alieninvasion;

import javax.swing.ImageIcon;

/**
 * 
 * @author Revauthore
 */
public class Missile extends Sprite {
	/**
	 * margin of the missile towards the screen
	 */
	private final int marginX = 5;

	public Missile(int x, int y) {
		image = new ImageIcon(this.getClass().getResource(
				"images/game/missile.png")).getImage();

		this.x = x;
		this.y = y;
		double scale = Math.min((double) Framework.getFrameHeight() / 700,
				(double) Framework.getFrameWidth() / 1300);
		width = (int) (image.getWidth(null) * scale);
		height = (int) (image.getHeight(null) * scale);
		visible = true;

		speed = Game.getGameSpeed()
				+ (int) (Math.max(Math.max(
						(double) Framework.getFrameWidth() / 400,
						(double) Framework.getFrameHeight() / 200), 5));

		// set pixel bounds of the sprite
		computeBounds();
	}

	public void move() {
		x += speed;
		if (x > Framework.getFrameWidth() + width + marginX) {
			visible = false;
		}
	}
}
