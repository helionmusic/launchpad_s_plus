package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import games.ReactionGame;

/**
 * Implements the GUI for Reaction Game
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class ReactionGameGUI {
	private static boolean firstTimeRunning = true;
	private static boolean ready = false;

	private static ReactionGame game;

	private static final JFrame ReactionGameDialog = new JFrame();
	private static final JPanel ReactionGameContainer = new JPanel();
	private static final JPanel leftPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JPanel infoPanel = new JPanel();
	private static final JLabel tutorial = new JLabel();
	private static final JPanel fillerPanel = new JPanel();

	private static final JPanel playerPanel = new JPanel();
	private static final JLabel playerLabel = new JLabel();
	private static final JTextField playerField = new JTextField();
	private static final JPanel scorePanel = new JPanel();
	private static final JLabel scoreText = new JLabel();
	private static final JPanel timePanel = new JPanel();
	private static final JLabel timeText = new JLabel();
	private static final JPanel difficultyPanel = new JPanel();
	private static final JLabel difficultyText = new JLabel();
	private static final JComboBox<String> difficultyBox = new JComboBox<String>();

	private static String playerName = "Player";
	private static int difficulty = 2;

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
	public ReactionGameGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();
			setInteractions();
			ReactionGameDialog.setVisible(true);
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
						game = new ReactionGame();
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
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		Main.setClosing(false);
		Main.setCardIcon(Main.reactionGameCard, "reactionGame", "selected");
		Main.reactionGameRunning = true;

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
		ReactionGameDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		ReactionGameDialog.setTitle("Reaction Game");
		ReactionGameDialog.setResizable(false);
		ReactionGameDialog.setLayout(null);
		ReactionGameDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(0, 0, 150, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/reactionGameTutorial.png")));

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		ReactionGameContainer.setBounds(0, 0, windowW, windowH);
		ReactionGameContainer.setLayout(null);

		rightPanel.add(tutorial);
		leftPanel.add(scoreText);
		ReactionGameContainer.add(leftPanel, BorderLayout.WEST);
		ReactionGameContainer.add(rightPanel, BorderLayout.EAST);

		ReactionGameDialog.getContentPane().add(ReactionGameContainer);
		ReactionGameDialog.setVisible(true);

	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private static void setInteractions() {
		ReactionGameDialog
				.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						quitReactionGame();
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
	 * Returns the currently selected difficulty setting
	 * 
	 * @return difficulty
	 */
	public static int getDifficulty() {
		return difficultyBox.getSelectedIndex() + 1;
	}

	/**
	 * Returns the player's name from the text field
	 * 
	 * @return Player name
	 */
	public static String getPlayerName() {
		return playerField.getText();
	}

	/**
	 * Updates the average text.
	 * 
	 * @param timeTaken
	 *            Average time
	 */
	public static void setAverage(double timeTaken) {
		timeText.setText("Average reaction time: " + (int) timeTaken);
	}

	/**
	 * Updates the score text
	 * 
	 * @param score
	 *            Score
	 */
	public static void setScore(int score) {
		scoreText.setText("Score: " + score);
	}

	/**
	 * Method called to close the GUI from the ReactionGame class
	 */
	public static void quit() {
		Main.setClosing(true);
		Main.reactionGameRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		ReactionGameDialog.dispose();
		Main.setCardIcon(Main.reactionGameCard, "reactionGame", "default");
		game = null;
	}

	/**
	 * Method called to close the GUI and game. <br>
	 * Should not be called from within GameOfLife class.
	 * 
	 * 
	 * @return 1
	 */
	public static int quitReactionGame() {
		Main.setClosing(true);
		Main.reactionGameRunning = false;
		Main.nothingRunning();
		ReactionGame.forceQuit();
		game = null;
		Main.setCurrentlyRunning("none");
		Main.resetCards();
		ReactionGameDialog.dispose();
		return 1;
	}
}
