package alieninvasion;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.BitSet;

/**
 * AlphaPixels class provides toolkit to convert an image to an array of Bitset
 * objects. Each Bitset contains a row of image pixels. The bit value is true if
 * the pixel has transparency more than 60% (below 153), or false otherwise.
 * 
 * @author Revauthore
 */
public class ImageToolkit {

	private static final int TRANSPARENCY_VALUE = 153;

	private ImageToolkit() {
		// cannot instantiate new object from here
	}

	/**
	 * Resize ARGB image to size specified in the parameters of this method and
	 * return the resized image in the form of BufferedImage.
	 * 
	 * @param originalImage
	 * @param width
	 *            resize this image to this width
	 * @param height
	 *            resize this image to this height
	 * @return resized image in the form of BufferedImage
	 */
	public static BufferedImage resizeImage(Image originalImage, int width,
			int height) {
		BufferedImage resizedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}

	public static BitSet[] getAlphaValues(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();

		BitSet[] data = new BitSet[height];

		for (int j = 0; j != height; ++j) {
			BitSet bs = new BitSet(width);
			for (int i = 0; i != width; ++i) {
				if ((img.getRGB(i, j) >> 24 & 0xff) < TRANSPARENCY_VALUE) {
					bs.set(i, true);
				} else
					bs.set(i, false);
			}
			data[j] = bs;
		}
		return data;
	}

}
