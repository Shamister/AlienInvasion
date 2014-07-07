package alieninvasion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class OptionsMenu implements Menu {

	private int fontSize;
	private int titleFontSize;
	private int onSelect;
	private float menuFontStroke;
	private float fontStroke;
	private int maxStrLength;
	private boolean selected;
	private Rectangle outerBounds;

	private String[] choices = { "800 x 600", "1024 x 768", "1280 x 768",
			"1360 x 768", "1366 x 768", "Full Screen" };

	public OptionsMenu() {
		resetBounds();
	}

	public void checkBounds(Point p) {
		if (outerBounds.contains(p)) {
			int space = fontSize + Framework.getFrameHeight() / 75;
			for (int i = 0; i < choices.length; i++) {
				double maxX = outerBounds.getMinX() + fontSize * 0.62
						* choices[i].length();
				double marginY = outerBounds.getMinY() + space * i;
				if (p.getY() > marginY && p.getY() <= marginY + fontSize
						&& p.getX() < maxX) {
					onSelect = i + 1;
					break;
				}
			}
		} else {
			onSelect = -1;
		}
	}

	public void resetBounds() {
		fontSize = (int) Math.min(
				(Framework.getFrameHeight() / (2.5 * choices.length)),
				Framework.getFrameWidth() / 20);

		// set outer bounds
		titleFontSize = (int) (1.2 * fontSize);
		int minX = (int) (Framework.getFrameWidth() * 0.13);
		// get the max length string among choices menu
		for (int i = 0; i < choices.length; i++) {
			if (choices[i].length() > maxStrLength) {
				maxStrLength = choices[i].length();
			}
		}
		int minY = (int) (int) (Framework.getFrameHeight() * 0.27) + 2
				* titleFontSize - fontSize;
		int width = (int) (fontSize * 0.62 * maxStrLength);
		int height = (int) (choices.length * (fontSize + Framework
				.getFrameHeight() / 75));
		outerBounds = new Rectangle(minX, minY, width, height);
		fontStroke = Math.min((float) Framework.getFrameWidth() / 700,
				(float) Framework.getFrameHeight() / 400);
		menuFontStroke = Math.min((float) Framework.getFrameWidth() / 180,
				(float) Framework.getFrameHeight() / 100);
		selected = false;
	}

	public int getSelected() {
		return onSelect;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		int marginX = Framework.getFrameWidth() / 12;
		int topY = (int) (Framework.getFrameHeight() * 0.27);

		// display
		String msg = "Display";
		Font f = new Font("Algerian", Font.BOLD, titleFontSize);
		FontRenderContext frc = g2d.getFontRenderContext();
		AffineTransform transform = g2d.getTransform();
		TextLayout tl = new TextLayout(msg, f, frc);
		Shape outline = tl.getOutline(null);

		int fontX = marginX;
		int fontY = topY + fontSize;

		transform.translate(fontX, fontY);
		g2d.setColor(Color.YELLOW);
		g2d.setFont(f);
		g2d.drawString(msg, fontX, fontY);
		g2d.transform(transform);
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(fontStroke));
		g2d.draw(outline);

		// translate back to origin
		transform.translate(-fontX, -fontY);
		g2d.setTransform(transform);

		// menu choice
		topY = topY + 2 * titleFontSize;
		int space = fontSize + Framework.getFrameHeight() / 75;

		f = new Font("Algerian", Font.PLAIN, fontSize);
		for (int i = 0; i < choices.length; i++) {
			msg = choices[i];
			frc = g2d.getFontRenderContext();
			tl = new TextLayout(msg, f, frc);
			outline = tl.getOutline(null);

			fontX = (int) (Framework.getFrameWidth() / 16 + marginX);
			fontY = (int) (topY + space * (i));

			transform.translate(fontX, fontY);
			g2d.transform(transform);
			if (i + 1 == onSelect) {
				g2d.setColor(Color.GRAY);
			} else {
				g2d.setColor(new Color(53, 53, 0));
			}
			g2d.setStroke(new BasicStroke(menuFontStroke));
			g2d.draw(outline);

			// translate back to origin
			transform.translate(-fontX, -fontY);
			g2d.setTransform(transform);

			g2d.setColor(Color.WHITE);
			g2d.setFont(f);
			g2d.drawString(msg, fontX, fontY);
		}

		Toolkit.getDefaultToolkit().sync();
	}
}
