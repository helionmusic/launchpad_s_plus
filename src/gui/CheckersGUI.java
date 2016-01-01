package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import games.Checkers;
import games.Checkers2;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Implements the GUI for Checkers
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class CheckersGUI {
	private static boolean firstTimeRunning = false;
	private static boolean ready = false;

	private static Checkers2 game;

	private static final JFrame CheckersDialog = new JFrame();
	private static final JPanel CheckersContainer = new JPanel();
	private static final JPanel leftPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JLabel tutorial = new JLabel();

	private static final JLabel ScoreText = new JLabel();

	private static String defaultPlayer1 = "Player 1";
	private static String defaultPlayer2 = "Player 2";

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
	CheckersGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();
			setInteractions();
			askSettings();
			Main.delay(500);
			while (!ready) {
				Main.delay(1);
			}

			firstTimeRunning = true;

			Thread CheckersGame = new Thread() {
				@Override
				public void run() {
					try {
						game = new Checkers2();
						game.startGame();
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			};
			CheckersGame.setName("Checkers Game");
			CheckersGame.start();
			
		} else {
			quit();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		Main.setCardIcon(Main.checkersCard, "checkers", "selected");

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private static void createAndShowGUI() {
		CheckersDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		CheckersDialog.setTitle("Checkers Demo");
		CheckersDialog.setResizable(false);
		CheckersDialog.setLayout(null);

		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY);
		windowW = 600;
		windowH = mainWindowH;
		CheckersDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(0, 0, 150, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/checkersTutorial.png")));

		rightPanel.add(tutorial);
		leftPanel.add(ScoreText);
		CheckersContainer.add(leftPanel, BorderLayout.WEST);
		CheckersContainer.add(rightPanel, BorderLayout.EAST);

		CheckersDialog.getContentPane().add(CheckersContainer);
		CheckersDialog.setVisible(true);
	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private static void setInteractions() {
		CheckersDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quitCheckers();
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
	 * Quits the game and closes the GUI
	 */
	public static void quit() {
		Main.setClosing(true);
		Main.checkersRunning = false;
		game = null;
		Main.nothingRunning();
		Main.resetCards();
		CheckersDialog.dispose();
		Main.setCardIcon(Main.checkersCard, "checkers", "default");
	}

	/**
	 * Method called to close the GUI and game. <br>
	 * Should not be called from within Checkers class.
	 * 
	 * @return 1
	 */
	public static int quitCheckers() {
		Main.setClosing(true);
		Checkers.forceQuit();
		game = null;
		Main.checkersRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		CheckersDialog.dispose();
		return 1;
	}
}
