package gui;

import games.GameOfLife;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Implements the GUI for Game Of Life
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class GameOfLifeGUI {
	private static boolean firstTimeRunning = false;
	private static boolean ready = false;

	private static GameOfLife game;

	private static final JFrame GameOfLifeDialog = new JFrame();
	private static final JPanel GameOfLifeContainer = new JPanel();
	private static final JPanel leftPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JLabel tutorial = new JLabel();

	private static final JLabel ScoreText = new JLabel();

	private static int mainWindowX;
	private static int mainWindowY;
	private static int mainWindowW;
	private static int mainWindowH;

	private static int windowX;
	private static int windowY;
	private static int windowW;
	private static int windowH;

	/**
	 * Constructor for the GUI
	 * 
	 * @param start
	 *            true if the GUI should be displayed, false otherwise
	 */
	GameOfLifeGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();
			setInteractions();
			GameOfLifeDialog.setVisible(true);
			askSettings();
			Main.delay(500);
			while (!ready) {
				Main.delay(1);
			}

			firstTimeRunning = false;

			new Thread() {
				@Override
				public void run() {
					try {
						game = new GameOfLife();
						game.startGame();
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			}.start();

		} else {
			quit();
		}
	}

	/**
	 * Sets the window position and dimenstions
	 */
	private static void initialize() {
		Main.setClosing(false);
		Main.setCardIcon(Main.gameOfLifeCard, "gameOfLife", "selected");

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY);
		windowW = 600;
		windowH = mainWindowH;
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private static void createAndShowGUI() {
		GameOfLifeDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		GameOfLifeDialog.setTitle("Conrad's Game Of Life");
		GameOfLifeDialog.setResizable(false);
		GameOfLifeDialog.setLayout(null);
		GameOfLifeDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(0, 0, 150, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/gameOfLifeTutorial.png")));

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		GameOfLifeContainer.setBounds(0, 0, windowW, windowH);
		GameOfLifeContainer.setLayout(null);

		rightPanel.add(tutorial);
		leftPanel.add(ScoreText);
		GameOfLifeContainer.add(leftPanel, BorderLayout.WEST);
		GameOfLifeContainer.add(rightPanel, BorderLayout.EAST);

		GameOfLifeDialog.getContentPane().add(GameOfLifeContainer);
		GameOfLifeDialog.setVisible(true);

	}

	/**
	 * Defines and implements all the interactions for the Game of Life GUI
	 */
	private static void setInteractions() {
		GameOfLifeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quitGameOfLife();
			}
		});
	}

	/**
	 * Asks for user input before initializing the game.
	 */
	private static void askSettings() {
		ready = true;
	}

	/**
	 * Method called to close the GUI from the GameOfLife class
	 */
	public static void quit() {
		Main.setClosing(true);
		Main.gameOfLifeRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		GameOfLifeDialog.dispose();
		Main.setCardIcon(Main.gameOfLifeCard, "gameOfLife", "default");
		game = null;
		Main.gameOfLifeRunning = false;

	}

	/**
	 * Method called to close the GUI and game. <br>
	 * Should not be called from within GameOfLife class.
	 * 
	 * @return 1
	 */
	public static int quitGameOfLife() {
		GameOfLife.forceQuit();
		game = null;
		Main.setClosing(true);
		Main.gameOfLifeRunning = false;
		Main.setCurrentlyRunning("none");
		Main.resetCards();
		GameOfLifeDialog.dispose();
		return 1;
	}
}
