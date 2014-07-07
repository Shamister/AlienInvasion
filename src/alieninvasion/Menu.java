package alieninvasion;

import java.awt.Graphics2D;
import java.awt.Point;

public interface Menu {
	public void draw(Graphics2D g2d);

	public void checkBounds(Point p);

	public void resetBounds();

	public int getSelected();

	public boolean isSelected();

	public void setSelected(boolean b);
}
