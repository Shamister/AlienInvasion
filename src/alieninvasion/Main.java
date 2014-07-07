package alieninvasion;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;

public class Main {

	Window window;
	Framework fw;

	public Main() {
		// get display size to full screen
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		fw = new Framework((int) d.getWidth(), (int) d.getHeight(), false) {

			private static final long serialVersionUID = -3602372537837497202L;

			@Override
			protected void setScreenSize(int width, int height, boolean win) {
				super.setScreenSize(width, height, win);
				window.setVisible(false);
				window.dispose();
				if (win) {
					window = new Window(this, width, height);
				} else {
					window = new Window(this);
				}
			}

		};

		window = new Window(fw);
	}

	public static void main(String[] args) {
		// Use the event dispatch thread to build the UI for thread-safety.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main();
			}
		});
	}
}
