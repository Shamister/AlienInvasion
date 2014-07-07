package alieninvasion;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * Window class where we can set the dimension, title, and other window
 * settings.
 * 
 * @author Revauthore
 * 
 */
public class Window extends JFrame {

	private static final long serialVersionUID = -8376470784391042314L;

	public Window(Framework fw) {
		setFullScreenSize();
		init();
		this.setContentPane(fw);
		this.setVisible(true);
	}

	public Window(Framework fw, int width, int height) {
		setWindowSize(width, height);
		init();
		this.setContentPane(fw);
		this.setVisible(true);
	}

	private void init() {
		// Set the title for this frame.
		this.setTitle("Alien Invasion v" + Framework.getVersion());

		// Dispose the application when user close frame.
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void setFullScreenSize() {
		this.setVisible(false);
		// get display size to full screen
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		// set size of the frame.
		this.setSize((int) d.getWidth(), (int) d.getHeight());
		// disable decorations for this frame.
		this.setUndecorated(true);
		// put the frame to full screen.
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	public void setWindowSize(int windowWidth, int windowHeight) {
		this.setVisible(false);
		// set size of the frame.
		this.setSize(windowWidth, windowHeight);
		// put frame to center of the screen.
		this.setLocationRelativeTo(null);
		// so that frame cannot be resizable by the user.
		this.setResizable(false);
		this.setVisible(true);
	}
}
