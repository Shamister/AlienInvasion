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

public class MainMenu implements Menu {

	private int fontSize;
	private int onSelect;
	private float fontStroke;
	private boolean selected;
	private Rectangle outerBounds;

	private String[] choices = { "New Game", "How To Play", "Options", "Exit" };

	public MainMenu() {
		resetBounds();
	}

	public void checkBounds(Point p) {
		if (outerBounds.contains(p)) {
			for (int i = 0; i < choices.length; i++) {
				double marginY = outerBounds.getMinY() + fontSize * i;
				if (p.getY() > marginY && p.getY() <= marginY + fontSize) {
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
		int minY = (int) (Framework.getFrameHeight() * 0.68 - fontSize);
		int height = fontSize * choices.length;
		outerBounds = new Rectangle(0, minY, Framework.getFrameWidth(), height);
		fontStroke = Math.min((float) Framework.getFrameWidth() / 700,
				(float) Framework.getFrameHeight() / 400);
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

		// menu choice
		Font f = new Font("Algerian", Font.BOLD, fontSize);
		FontRenderContext frc = g2d.getFontRenderContext();
		AffineTransform transform = g2d.getTransform();

		for (int i = 0; i < choices.length; i++) {
			String msg = choices[i];
			TextLayout tl = new TextLayout(msg, f, frc);
			Shape outline = tl.getOutline(null);
			Rectangle outBounds = outline.getBounds();

			int fontX = (int) ((Framework.getFrameWidth() - outBounds
					.getWidth()) / 2);
			int fontY = (int) (Framework.getFrameHeight() * 0.68 + fontSize * i);

			transform.translate(fontX, fontY);
			g2d.setColor(Color.YELLOW);
			g2d.setFont(f);
			g2d.drawString(msg, fontX, fontY);
			g2d.transform(transform);
			if (i + 1 == onSelect) {
				g2d.setColor(Color.MAGENTA);
			} else {
				g2d.setColor(Color.RED);
			}
			g2d.setStroke(new BasicStroke(fontStroke));
			g2d.draw(outline);

			// translate back to origin
			transform.translate(-fontX, -fontY);
			g2d.setTransform(transform);
		}

		Toolkit.getDefaultToolkit().sync();
	}
}
