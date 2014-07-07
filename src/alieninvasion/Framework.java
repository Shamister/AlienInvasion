package alieninvasion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Framework that controls the game (Game.java) that created it, update it and
 * draw it on the screen.
 * 
 * @author Revauthore
 * 
 */

public class Framework extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2538624918711645412L;
	/**
	 * Width of the frame.
	 */
	public static int frameWidth;
	/**
	 * Height of the frame.
	 */
	public static int frameHeight;
	/**
	 * True for windowed mode and false for full screen.
	 */
	private boolean windowed;

	/**
	 * Time of one second in nanoseconds. 1 second = 1 000 000 000 nanoseconds
	 */
	public static final long secInNanosec = 1000000000L;

	/**
	 * Time of one millisecond in nanoseconds. 1 millisecond = 1 000 000
	 * nanoseconds
	 */
	public static final long milisecInNanosec = 1000000L;

	/**
	 * game version
	 */
	private static final String version = "2.1";

	/**
	 * FPS - Frames per second How many times per second the game should update?
	 */
	private static final int GAME_FPS = 60;
	/**
	 * Pause between updates. It is in nanoseconds.
	 */
	private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
	/**
	 * Time of displaying black screen while changing screen resolution. It is
	 * in milliseconds.
	 */
	private final long CHANGING_RESOLUTION_PERIOD = 300L;
	/**
	 * Display time of game over screen. It is in milliseconds.
	 */
	private final long GAME_OVER_PERIOD = 2000L;
	/**
	 * Display time of loading screen. It is in milliseconds.
	 */
	private final long LOADING_GAME_PERIOD = 2000L;

	/**
	 * Possible states of the game
	 */
	public static enum GameState {
		STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, OPTIONS, INSTRUCTIONS, CHANGING_RESOLUTION, PLAYING, GAMEOVER, DESTROYED
	}

	/**
	 * Current state of the game
	 */
	public static GameState gameState;

	/**
	 * Elapsed game time in nanoseconds.
	 */
	private long elapsedTime;
	// It is used for calculating elapsed time.
	private long lastTime;

	// sub-directory of images,..
	private String subdir = "images/";

	// title font size and stroke
	private static int titleFontSize;
	private static int titleFontStroke;

	// menu background
	private Image menuBg;

	// game over image screen
	private Image gameOverSc;

	// menu and options menu are controlled by some instances
	private Menu mainMenu, optionsMenu;

	// The actual game
	private Game game;

	// remove this later
	long begin;

	public Framework(int wWidth, int wHeight, boolean win) {
		super();

		// set screen size and mode
		frameWidth = wWidth;
		frameHeight = wHeight;
		windowed = win;

		gameState = GameState.VISUALIZING;

		// We start game in new thread.
		Thread gameThread = new Thread() {
			@Override
			public void run() {
				gameLoop();
			}
		};
		gameThread.start();
	}

	/**
	 * Set variables and objects. This method is intended to set the variables
	 * and objects for this class, variables and objects for the actual game can
	 * be set in Game.java.
	 */
	private void init() {
		setTitleFont();
	}

	private void setTitleFont() {
		titleFontSize = (int) Math.min(Framework.getFrameHeight() / 5,
				Framework.getFrameWidth() / 9);
		titleFontStroke = Math.max(
				Math.min(Framework.getFrameWidth() / 650,
						Framework.getFrameHeight() / 350), 1);
	}

	/**
	 * Load files - images, sounds, ... This method is intended to load files
	 * for this class, files for the actual game can be loaded in Game.java.
	 */
	private void loadContent() {
		String menuSubdir = subdir + "main/";
		menuBg = new ImageIcon(this.getClass().getResource(
				menuSubdir + "bg.jpg")).getImage();

		mainMenu = new MainMenu();
		optionsMenu = new OptionsMenu();
	}

	/**
	 * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is
	 * updated and then the game is drawn on the screen.
	 */
	private void gameLoop() {
		// This two variables are used in VISUALIZING and CHANGING_RESOLUTION
		// state of the game. We used
		// them to wait some time so that we get correct frame/window
		// resolution.
		long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

		// This variables are used for calculating the time that defines for how
		// long we should put threat to sleep to meet the GAME_FPS.
		long beginTime, timeTaken, timeLeft;

		while (true) {
			beginTime = System.nanoTime();
			// remove this later
			begin = beginTime;

			switch (gameState) {
			case PLAYING:
				elapsedTime += System.nanoTime() - lastTime;

				game.updateGame(elapsedTime);

				lastTime = System.nanoTime();

				if (game.isGameOver()) {
					// reset the elapsed time and last time
					elapsedTime = 0;
					lastTime = System.nanoTime();
					gameState = gameState.GAMEOVER;
				}
				break;
			case GAMEOVER:
				// display game over screen for specified time
				// if it is over, go back to main menu
				if (elapsedTime > GAME_OVER_PERIOD * milisecInNanosec) {
					// When we get size of frame we change status
					game = null;
					gameState = GameState.MAIN_MENU;
				} else {
					elapsedTime += System.nanoTime() - lastTime;
					lastTime = System.nanoTime();
				}
				break;
			case MAIN_MENU:
				setCursor();
				if (mainMenu.isSelected()) {
					int choice = mainMenu.getSelected();
					mainMenu.setSelected(false);
					switch (choice) {
					case 1:
						// reset the elapsed time and last time
						elapsedTime = 0;
						lastTime = System.nanoTime();
						gameState = GameState.GAME_CONTENT_LOADING;
						break;
					case 2:
						gameState = GameState.INSTRUCTIONS;
						break;
					case 3:
						gameState = GameState.OPTIONS;
						break;
					case 4:
						System.exit(0);
						break;
					}
					break;
				}
				mainMenu.checkBounds(mousePosition());
				break;
			case OPTIONS:
				setCursor();
				if (optionsMenu.isSelected()) {
					int choice = optionsMenu.getSelected();
					optionsMenu.setSelected(false);
					// reset the elapsed time and last time
					elapsedTime = 0;
					lastTime = System.nanoTime();

					switch (choice) {
					case 1:
						setScreenSize(800, 600, true);
						break;
					case 2:
						setScreenSize(1024, 768, true);
						break;
					case 3:
						setScreenSize(1280, 768, true);
						break;
					case 4:
						setScreenSize(1360, 768, true);
						break;
					case 5:
						setScreenSize(1366, 768, true);
						break;
					case 6:
						Dimension d = Toolkit.getDefaultToolkit()
								.getScreenSize();
						setScreenSize((int) d.getWidth(), (int) d.getHeight(),
								false);
						break;
					}
					break;
				}
				optionsMenu.checkBounds(mousePosition());
				break;
			case INSTRUCTIONS:
				// ...
				break;
			case CHANGING_RESOLUTION:
				// reset the window size and wait until new size has been set
				if (this.getWidth() > 1
						&& elapsedTime > CHANGING_RESOLUTION_PERIOD
								* milisecInNanosec) {
					// We set gameTime to zero and lastTime to current time for
					// later
					// calculations.
					resetBounds();
					// When we get size of frame and wait enough time we go back
					// to "OPTIONS" state
					gameState = GameState.OPTIONS;
				} else {
					elapsedTime += System.nanoTime() - lastTime;
					lastTime = System.nanoTime();
				}
				break;
			case GAME_CONTENT_LOADING:
				if (game != null && game.isFinishLoaded()
						&& elapsedTime > LOADING_GAME_PERIOD * milisecInNanosec) {
					// reset the elapsed time and last time
					elapsedTime = 0;
					lastTime = System.nanoTime();
					// When we game is loaded and we wait enough then we change
					// status to "PLAYING"
					gameState = GameState.PLAYING;
				} else {
					if (game == null) {
						newGame();
					}
					elapsedTime += System.nanoTime() - lastTime;
					lastTime = System.nanoTime();
				}
				break;
			case STARTING:
				// Sets variables and objects.
				init();
				// Load files - images, sounds, ...
				loadContent();

				// When all things that are called above finished, we change
				// game status to main menu.
				gameState = GameState.MAIN_MENU;
				break;
			case VISUALIZING:
				// Wait one second for the window/frame to be set to its
				// correct size. Just in case we
				// also insert 'this.getWidth() > 1' condition in case when the
				// window/frame size wasn't set in time,
				// so that we although get approximately size.
				if (this.getWidth() > 1 && visualizingTime > secInNanosec) {
					frameWidth = this.getWidth();
					frameHeight = this.getHeight();

					// When we get size of frame we change status.
					gameState = GameState.STARTING;
				} else {
					visualizingTime += System.nanoTime() - lastVisualizingTime;
					lastVisualizingTime = System.nanoTime();
				}
				break;
			}

			// Repaint the screen.
			repaint();

			// Here we calculate the time that defines for how long we should
			// put threat to sleep to meet the GAME_FPS.
			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In
																			// milliseconds
			// If the time is less than 10 milliseconds, then we will put thread
			// to sleep for 10 millisecond so that some other thread can do some
			// work.
			if (timeLeft < 10)
				timeLeft = 10; // set a minimum
			try {
				// Provides the necessary delay and also yields control so that
				// other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * method to set the screen size and it should be implemented when framework
	 * is created
	 */
	protected void setScreenSize(int width, int height, boolean win) {
		frameWidth = width;
		frameHeight = height;
		windowed = win;
		gameState = GameState.CHANGING_RESOLUTION;

		// other stuff will be done when the instance of this class created
	}

	/**
	 * Get the frame width
	 */
	public static int getFrameWidth() {
		return frameWidth;
	}

	/**
	 * Get the frame height
	 * 
	 * @return
	 */
	public static int getFrameHeight() {
		return frameHeight;
	}

	/**
	 * Set the frame width
	 */
	public static void setFrameWidth(int width) {
		frameWidth = width;
	}

	/**
	 * Set the frame height
	 */
	public static void setFrameHeight(int height) {
		frameHeight = height;
	}

	/**
	 * get version
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * Set cursor image
	 */
	public void setCursor() {
		// set cursor
		Image cursorIcon = new ImageIcon(this.getClass().getResource(
				"images/cursorIcon.png")).getImage();
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon,
				new Point(getX(), getY()), "Cursor"));
	}

	/**
	 * Reset all bounds (text, image, etc..)
	 */
	public void resetBounds() {
		switch (gameState) {
		case PLAYING:
			// ...
			break;
		case GAMEOVER:
			// ...
			break;
		case MAIN_MENU:
			// ...
			break;
		case OPTIONS:
			// ...
			break;
		case INSTRUCTIONS:
			// ...
			break;
		case CHANGING_RESOLUTION:
			setTitleFont();
			mainMenu.resetBounds();
			optionsMenu.resetBounds();
			break;
		case GAME_CONTENT_LOADING:
			// ...
			break;
		}
	}

	/**
	 * Draw the game to the screen. It is called through repaint() method in
	 * GameLoop() method.
	 */
	@Override
	public void draw(Graphics2D g2d) {
		switch (gameState) {
		case PLAYING:
			game.draw(g2d);
			break;
		case GAMEOVER:
			game.draw(g2d);
			drawGameOver(g2d);
			break;
		case MAIN_MENU:
			g2d.drawImage(menuBg, 0, 0, frameWidth, frameHeight, null);
			drawTitle(g2d);
			drawVersion(g2d);
			mainMenu.draw(g2d);
			break;
		case OPTIONS:
			g2d.drawImage(menuBg, 0, 0, frameWidth, frameHeight, null);
			drawTitle(g2d);
			drawVersion(g2d);
			optionsMenu.draw(g2d);
			break;
		case INSTRUCTIONS:
			g2d.drawImage(menuBg, 0, 0, frameWidth, frameHeight, null);
			drawTitle(g2d);
			drawVersion(g2d);
			drawInstructions(g2d);
			break;
		case CHANGING_RESOLUTION:
			// ...
			break;
		case GAME_CONTENT_LOADING:
			g2d.setBackground(Color.BLACK);
			drawLoading(g2d);
			break;
		}
		g2d.dispose();
	}

	private void drawTitle(Graphics2D g2d) {
		// title
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		String msg = "ALIEN INVASION";
		Font f = new Font("Algerian", Font.BOLD, titleFontSize);
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl = new TextLayout(msg, f, frc);

		Shape outline = tl.getOutline(null);
		Rectangle outBounds = outline.getBounds();

		int fontX = (int) ((frameWidth - outBounds.getWidth()) / 2);
		int fontY = (int) (frameHeight * 0.21);

		AffineTransform transform = g2d.getTransform();
		transform.translate(fontX, fontY);

		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setFont(f);
		g2d.drawString(msg, fontX, fontY);
		g2d.transform(transform);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(titleFontStroke));
		g2d.draw(outline);

		// translate back to origin
		transform.translate(-fontX, -fontY);
		g2d.setTransform(transform);
	}

	private void drawInstructions(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		String[] msgs = Instructions.getMessages();
		int fontSize = (int) Math.min(frameHeight * 0.024, frameWidth * 0.013);
		float menuFontStroke = Math.min((float) frameWidth / 273,
				(float) frameHeight / 153);
		int topX = (int) (frameWidth * 0.1);
		int topY = (int) (frameHeight * 0.29);
		int space = fontSize + frameHeight / 25;

		Font f = new Font("Verdana", Font.PLAIN, fontSize);
		g2d.setFont(f);
		FontRenderContext frc = g2d.getFontRenderContext();
		AffineTransform transform = g2d.getTransform();
		for (int i = 0; i < msgs.length; i++) {
			String msg = msgs[i].toUpperCase();
			int y = topY + space * i;
			TextLayout tl = new TextLayout(msg, f, frc);
			Shape outline = tl.getOutline(null);
			g2d.setColor(new Color(53, 53, 0));

			transform.translate(topX, y);
			g2d.transform(transform);

			g2d.setStroke(new BasicStroke(menuFontStroke));

			g2d.draw(outline);

			// translate back to origin
			transform.translate(-topX, -y);
			g2d.setTransform(transform);

			g2d.setColor(Color.WHITE);
			g2d.drawString(msgs[i].toUpperCase(), topX, y);
		}
	}

	private void drawGameOver(Graphics2D g2d) {
		if (gameOverSc == null) {
			gameOverSc = new BufferedImage(Framework.getFrameWidth(),
					Framework.getFrameHeight(), BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = gameOverSc.getGraphics();
			this.paint(g);
		} else {
			g2d.drawImage(gameOverSc, 0, 0, frameWidth, frameHeight, null);

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			// draw game over string
			String msg = "GAME OVER";
			int fontSize = (int) Math
					.min(frameWidth * 0.07, frameHeight * 0.16);
			Font f = new Font("Times New Roman", Font.BOLD, fontSize);
			FontRenderContext frc = g2d.getFontRenderContext();
			TextLayout tl = new TextLayout(msg, f, frc);

			Shape outline = tl.getOutline(null);
			Rectangle outBounds = outline.getBounds();

			int fontX = (int) ((frameWidth - outBounds.getWidth()) / 2);
			int fontY = (int) ((frameHeight + outBounds.getHeight()) / 2);

			AffineTransform transform = g2d.getTransform();
			transform.translate(fontX, fontY);

			g2d.setColor(Color.WHITE);
			g2d.setFont(f);
			g2d.drawString(msg, fontX, fontY);
			g2d.transform(transform);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			g2d.draw(outline);
		}
	}

	private void drawVersion(Graphics2D g2d) {
		String sver = version + ".0.0";
		int fontSize = (int) Math.min(frameWidth * 0.018, frameHeight * 0.031);
		Font v = new Font("Times New Roman", Font.BOLD, fontSize);
		g2d.setFont(v);
		FontMetrics versionMetr = g2d.getFontMetrics(v);
		int versionX = (int) (frameWidth * 0.98 - versionMetr.stringWidth(sver));
		int versionY = (int) (frameHeight * 0.98);
		if (windowed) {
			versionX -= 5;
			versionY -= 25;
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(sver, versionX, versionY);
	}

	private void drawLoading(Graphics2D g2d) {
		String s = "LOADING...";
		int fontSize = (int) Math.min(frameWidth * 0.03, frameHeight * 0.65);
		Font v = new Font("Times New Roman", Font.BOLD, fontSize);
		g2d.setFont(v);
		FontMetrics versionMetr = g2d.getFontMetrics(v);
		int posX = (int) (frameWidth * 0.98 - versionMetr.stringWidth(s));
		int posY = (int) (frameHeight * 0.98);
		if (windowed) {
			posX -= 5;
			posY -= 25;
		}
		g2d.setColor(Color.WHITE);
		g2d.drawString(s, posX, posY);
	}

	/**
	 * Starts new game.
	 */
	private void newGame() {
		gameOverSc = null;

		game = new Game();
	}

	/**
	 * Restart game - reset game time and call RestartGame() method of game
	 * object so that reset some variables.
	 */
	private void restartGame() {
		// We set gameTime to zero and lastTime to current time for later
		// calculations.
		elapsedTime = 0;
		lastTime = System.nanoTime();

		game.restartGame();

		// We change game status so that the game can start.
		gameState = GameState.PLAYING;
	}

	/**
	 * Returns the position of the mouse pointer in game frame/window. If the
	 * mouse position is null, then wait until it gets the position.
	 * 
	 * @return Point of mouse coordinates.
	 */
	private Point mousePosition() {
		Point mp = this.getMousePosition();
		while (mp == null) {
			mp = this.getMousePosition();
		}
		return mp;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		switch (gameState) {
		case PLAYING:
			game.keyPressed(e.getKeyCode());
			break;
		case GAMEOVER:
			// ...
			break;
		case MAIN_MENU:
			// ...
			break;
		case OPTIONS:
			// ...
			break;
		case CHANGING_RESOLUTION:
			// ...
			break;
		case GAME_CONTENT_LOADING:
			// ...
			break;
		}
	}

	/**
	 * This method is called when keyboard key is released.
	 * 
	 * @param e
	 *            KeyEvent
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		switch (gameState) {
		case PLAYING:
			game.keyReleased(e.getKeyCode());
			break;
		case GAMEOVER:
			// ...
			break;
		case MAIN_MENU:
			// ...
			break;
		case OPTIONS:
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = gameState.MAIN_MENU;
			}
			break;
		case INSTRUCTIONS:
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = gameState.MAIN_MENU;
			}
			break;
		case CHANGING_RESOLUTION:
			// ...
			break;
		case GAME_CONTENT_LOADING:
			// ...
			break;
		}
	}

	/**
	 * This method is called when mouse button is released.
	 * 
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		switch (gameState) {
		case PLAYING:
			// ...
			break;
		case GAMEOVER:
			// ...
			break;
		case MAIN_MENU:
			mainMenu.setSelected(true);
			break;
		case OPTIONS:
			optionsMenu.setSelected(true);
			break;
		case GAME_CONTENT_LOADING:
			// ...
			break;
		}
	}
}
