package alieninvasion;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;

import alieninvasion.Framework.GameState;

/**
 * The actual game is controlled here.
 * 
 * @author Revauthore
 * 
 */
public class Game {

	private Craft craft;
	private int level;
	private int score;
	private boolean gameOver;
	private Set<Alien> aliens;
	private boolean finishLoaded;

	/**
	 * key code
	 */
	private final int SPACE = KeyEvent.VK_SPACE;
	private final int LEFT = KeyEvent.VK_LEFT;
	private final int RIGHT = KeyEvent.VK_RIGHT;
	private final int UP = KeyEvent.VK_UP;
	private final int DOWN = KeyEvent.VK_DOWN;

	/**
	 * game speed
	 * 
	 */
	private static int GAME_SPEED = 1;

	private Image bg;

	/**
	 * total aliens at the beginning
	 */
	private final int INIT_ALIENS = 30;

	/**
	 * delay time before the screen turns into grayscale
	 */
	private final int DELAY_TO_GAME_OVER = 10;

	public Game() {

		// Sets variables and objects for the game.
		init();
		// Load game files (images, sounds, ...)
		loadContent();

		Thread threadForInitGame = new Thread() {
			@Override
			public void run() {
				finishLoaded = true;
			}
		};
		threadForInitGame.start();
	}

	public boolean isFinishLoaded() {
		return finishLoaded;
	}

	/**
	 * Set variables and objects for the game.
	 */
	private void init() {
		craft = new Craft();
		level = 1;

		initAliens(INIT_ALIENS);
	}

	private void initAliens(int total) {
		aliens = Collections.synchronizedSet(new HashSet<Alien>());

		for (int i = 0; i < total; i++) {
			int alienX = (int) (3000000 / (Framework.getFrameWidth()) * (1.1 + Math
					.random()));
			int alienY = (int) (Framework.getFrameHeight()
					* (Math.random() + 0.2) * 0.8);
			aliens.add(new Alien(alienX, alienY));
		}
	}

	private void levelUp() {
		Framework.gameState = GameState.GAME_CONTENT_LOADING;
		craft.reset();
		level++;
		initAliens(INIT_ALIENS + level * 5);
		Framework.gameState = GameState.PLAYING;
	}

	public static int getGameSpeed() {
		return GAME_SPEED;
	}

	/**
	 * Load game files - images, sounds, ...
	 */
	private void loadContent() {
		bg = new ImageIcon(this.getClass().getResource("images/game/bg.jpg"))
				.getImage();
	}

	/**
	 * Restart game - reset some variables.
	 */
	public void restartGame() {

	}

	/**
	 * Update game logic.
	 * 
	 * @param gameTime
	 *            gameTime of the game.
	 */
	public synchronized void updateGame(long gameTime) {
		checkCollision();
		if (!gameOver) {
			if (aliens.isEmpty()) {
				levelUp();
			} else {

				craft.move();

				// missiles
				Set<Missile> ms = craft.getMissiles();
				for (Missile m : ms) {
					m.move();
				}

				// aliens
				for (Alien a : aliens) {
					a.move();
				}
			}
		}
	}

	public void keyPressed(int key) {
		if (Framework.keyboardKeyState(SPACE)) {
			craft.fire();
		}
		if (Framework.keyboardKeyState(LEFT)
				&& Framework.keyboardKeyState(RIGHT)) {
			if (key == LEFT) {
				craft.setDirX(-1);
			} else if (key == RIGHT) {
				craft.setDirX(1);
			}
		} else if (Framework.keyboardKeyState(LEFT)) {
			craft.setDirX(-1);
		} else if (Framework.keyboardKeyState(RIGHT)) {
			craft.setDirX(1);
		}

		if (Framework.keyboardKeyState(UP) && Framework.keyboardKeyState(DOWN)) {
			if (key == UP) {
				craft.setDirX(-1);
			} else if (key == DOWN) {
				craft.setDirX(1);
			}
		} else if (Framework.keyboardKeyState(UP)) {
			craft.setDirY(-1);
		} else if (Framework.keyboardKeyState(DOWN)) {
			craft.setDirY(1);
		}
	}

	public void keyReleased(int key) {
		if (key == SPACE) {
			craft.fire();
		}
		if (!Framework.keyboardKeyState(KeyEvent.VK_LEFT)
				&& !Framework.keyboardKeyState(KeyEvent.VK_RIGHT)) {
			craft.setDirX(0);
		}
		if (!Framework.keyboardKeyState(KeyEvent.VK_UP)
				&& !Framework.keyboardKeyState(KeyEvent.VK_DOWN)) {
			craft.setDirY(0);
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public synchronized void checkCollision() {
		// craft with aliens
		Rectangle rc = craft.getOuterBounds();

		Iterator<Alien> alienIt = aliens.iterator();
		while (alienIt.hasNext()) {
			try {
				Alien a = alienIt.next();
				if (a != null) {
					Rectangle ra = a.getOuterBounds();

					if (rc.intersects(ra)) {
						// check the inner bounds
						Rectangle interCraftAlien = Sprite.getIntersection(rc,
								ra);
						int minX = interCraftAlien.x;
						int maxX = interCraftAlien.x + interCraftAlien.width
								- 1;
						int minY = interCraftAlien.y;
						int maxY = interCraftAlien.y + interCraftAlien.height
								- 1;
						for (int y = minY; y <= maxY; y++) {
							for (int x = minX; x <= maxX; x++) {
								if (craft.contains(x - rc.x, y - rc.y)
										&& a.contains(x - ra.x, y - ra.y)
										&& craft.isVisible() && a.isVisible()) {
									try {
										Thread.sleep(DELAY_TO_GAME_OVER);
									} catch (InterruptedException e) {
									}
									gameOver = true;
								}
							}
						}
					}

					// if game over, then exit from this method immediately
					if (gameOver) {
						break;
					}

					Set<Missile> m = craft.getMissiles();
					Iterator<Missile> missileIt = m.iterator();
					while (missileIt.hasNext()) {
						Missile ms = missileIt.next();
						if (ms != null && a != null) {
							Rectangle rm = ms.getOuterBounds();

							if (rm.intersects(ra)) {
								// check the inner bounds
								Rectangle interMissileAlien = Sprite
										.getIntersection(rm, ra);
								int minX = interMissileAlien.x;
								int maxX = interMissileAlien.x
										+ interMissileAlien.width - 1;
								int minY = interMissileAlien.y;
								int maxY = interMissileAlien.y
										+ interMissileAlien.height - 1;

								boolean found = false;
								for (int y = minY; y <= maxY; y++) {
									for (int x = minX; x <= maxX; x++) {
										if (ms.contains(x - rm.x, y - rm.y)
												&& a.contains(x - ra.x, y
														- ra.y)
												&& ms.isVisible()
												&& a.isVisible()) {
											score += a.getScore();

											alienIt.remove();
											a = null;

											missileIt.remove();
											ms = null;

											found = true;
											break;
										}
									}
									if (found) {
										break;
									}
								}
							}
						}
					}
				}
			} catch (ConcurrentModificationException e) {
			}
		}
	}

	/**
	 * Draw the game to the screen.
	 * 
	 * @param g2d
	 *            Graphics2D
	 */
	public synchronized void draw(Graphics2D g2d) {
		// draw background
		g2d.drawImage(bg, 0, 0, Framework.getFrameWidth(),
				Framework.getFrameHeight(), null);

		// craft
		if (craft.isVisible()) {
			int craftX = craft.getX() - craft.getWidth() / 2;
			int craftY = craft.getY() - craft.getHeight() / 2;
			g2d.drawImage(craft.getImage(), craftX, craftY, craft.getWidth(),
					craft.getHeight(), null);
		}

		// missiles
		Set<Missile> ms = craft.getMissiles();
		Iterator<Missile> missileIt = ms.iterator();
		while (missileIt.hasNext()) {
			Missile m = missileIt.next();
			if (m != null) {
				if (m.isVisible()) {
					int missileX = m.getX() - m.getWidth() / 2;
					int missileY = m.getY() - m.getHeight() / 2;
					g2d.drawImage(m.getImage(), missileX, missileY,
							m.getWidth(), m.getHeight(), null);
				}
			}
		}

		// aliens
		Iterator<Alien> alienIt = aliens.iterator();
		while (alienIt.hasNext()) {
			Alien alien = alienIt.next();
			if (alien != null) {
				if (alien.isVisible()) {
					int alienX = alien.getX() - alien.getWidth() / 2;
					int alienY = alien.getY() - alien.getHeight() / 2;
					g2d.drawImage(alien.getImage(), alienX, alienY,
							alien.getWidth(), alien.getHeight(), null);
				}
			}
		}

		// soften the string image
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		// level
		String msg = "LEVEL " + level;
		int fontSize = (int) Math.min(Framework.getFrameWidth() * 0.021,
				Framework.getFrameHeight() * 0.036);
		Font f = new Font("Times New Roman", Font.BOLD, fontSize);
		FontMetrics metr = g2d.getFontMetrics(f);
		g2d.setFont(f);
		int fontX = (int) ((Framework.getFrameWidth() - metr.stringWidth(msg)) / 2);
		int fontY = (int) (metr.getHeight() + Framework.getFrameHeight() / 40);
		g2d.setColor(Color.WHITE);
		g2d.drawString(msg, fontX, fontY);

		// score
		String scoreString = "SCORE : " + score;
		Font s = new Font("Times New Roman", Font.BOLD, fontSize);
		FontMetrics scoreMetr = g2d.getFontMetrics(s);
		int scoreX = (int) (Framework.getFrameWidth()
				- scoreMetr.stringWidth(scoreString) - Framework
				.getFrameWidth() / 20);
		int scoreY = fontY;
		g2d.drawString(scoreString, scoreX, scoreY);
	}
}
