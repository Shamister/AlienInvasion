package alieninvasion;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.BitSet;

/**
 * 
 * @author Revauthore
 */
public abstract class Sprite {

	Image image;
	int x, y, width, height;
	boolean visible;
	int speed;

	/**
	 * an array which contains the pixels of the sprite. This is used to check
	 * the bounds. The array represents the pixels lying vertically on the
	 * sprite, while each element of Bitset represents the pixels lying
	 * horizontally.
	 */
	BitSet[] bounds;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Image getImage() {
		return image;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	protected void computeBounds() {
		BufferedImage imageBounds = ImageToolkit.resizeImage(image, width,
				height);
		BitSet[] temp = ImageToolkit.getAlphaValues(imageBounds);

		bounds = new BitSet[height];

		for (int j = 0; j < height; j++) {
			BitSet bs = temp[j];
			BitSet newData = new BitSet();
			for (int i = 0; i < width; i++) {
				newData.set(i, (bs.get(i)) ? false : true);
			}
			bounds[j] = newData;
		}
	}

	/**
	 * Get outer bounds of the image in the rectangle form
	 * 
	 * @return a rectangle which is outer bound of the image
	 */
	public Rectangle getOuterBounds() {
		return new Rectangle(x - width / 2, y - height / 2, width, height);
	}

	/**
	 * Check whether the pixel at specified location (not relative position of
	 * this sprite) is not transparent.
	 * 
	 * @return true if it is not transparent and false otherwise
	 */
	public boolean contains(int x, int y) {
		try {
			return bounds[y].get(x);
		} catch (IndexOutOfBoundsException e) {
		}
		return false;
	}

	public static Rectangle getIntersection(Rectangle rect1, Rectangle rect2) {
		int minX = Math.max(rect1.x, rect2.x);
		int maxX = Math.min(rect1.x + rect1.width - 1, rect2.x + rect2.width
				- 1);
		int minY = Math.max(rect1.y, rect2.y);
		int maxY = Math.min(rect1.y + rect1.height - 1, rect2.y + rect2.height
				- 1);
		int width = maxX - minX;
		int height = maxY - minY;
		return new Rectangle(minX, minY, width, height);
	}
}
