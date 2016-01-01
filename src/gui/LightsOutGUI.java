package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import games.LightsOut;
import games.Snake;

/**
 * Implements the GUI for Lights Out
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class LightsOutGUI {
	private static boolean DEBUG = false;
	private static boolean firstTimeRunning = true;
	private static boolean ready = false;

	private static LightsOut game;

	private static final JFrame LightsOutDialog = new JFrame();
	private static final JPanel LightsOutContainer = new JPanel();
	private static final JPanel leftPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JPanel infoPanel = new JPanel();
	private static final JLabel tutorial = new JLabel();
	private static final JPanel fillerPanel = new JPanel();
	private static final JLabel highScores = new JLabel();

	private static final JPanel playerPanel = new JPanel();
	private static final JLabel playerLabel = new JLabel();
	private static final JTextField playerField = new JTextField();
	private static final JPanel turnsPanel = new JPanel();
	private static final JLabel turnsText = new JLabel();
	private static final JPanel remainingPanel = new JPanel();
	private static final JLabel remainingText = new JLabel();
	private static final JPanel scorePanel = new JPanel();
	private static final JLabel scoreText = new JLabel();
	private static final JPanel difficultyPanel = new JPanel();
	private static final JComboBox<String> difficulty = new JComboBox<String>();
	private static final JLabel difficultyText = new JLabel();
	private static final JPanel highScoresPanel = new JPanel();

	private static String defaultDifficulty = "Medium";
	private static String playerName = "Player";
	private static String rowData[][] = new String[3][10];
	private static int currentDifficulty;

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
	public LightsOutGUI(boolean start) {
		if (start) {
			initialize();
			updateDatabase();
			if (firstTimeRunning)
				createAndShowGUI();
			setInteractions();
			LightsOutDialog.setVisible(true);
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
						game = new LightsOut();
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
		Main.setCardIcon(Main.lightsOutCard, "lightsOut", "selected");
		LightsOut.setDifficulty(defaultDifficulty);

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 600;
		windowH = mainWindowH;
		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY);
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private static void createAndShowGUI() {
		LightsOutDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		LightsOutDialog.setTitle("Lights Out");
		LightsOutDialog.setResizable(false);
		LightsOutDialog.setLayout(null);
		LightsOutDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(20, 0, 200, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(175, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/lightsOutTutorial.png")));

		turnsText.setBounds(0, 0, 100, 30);
		turnsText.setText("Turn 0");
		remainingText.setBounds(0, 10, 100, 30);
		remainingText.setText("Remaining lights: 0");
		scoreText.setBounds(0, 20, 100, 15);
		scoreText.setText("Score: 0");

		if (firstTimeRunning) {
			difficulty.addItem("Easy");
			difficulty.addItem("Medium");
			difficulty.addItem("Hard");
			difficulty.addItem("Extreme");
		}
		difficulty.setSelectedItem(defaultDifficulty);
		difficulty.setPreferredSize(new Dimension(115, 25));
		difficulty.setMaximumSize(difficulty.getPreferredSize());

		difficultyText.setPreferredSize(new Dimension(57, 25));
		difficultyText.setMaximumSize(difficultyText.getPreferredSize());
		difficultyText.setText("Difficulty:");

		difficultyPanel.setLayout(new BoxLayout(difficultyPanel,
				BoxLayout.X_AXIS));
		difficultyPanel.add(difficultyText);
		difficultyPanel.add(difficulty);

		playerLabel.setText("Player: ");
		playerLabel.setPreferredSize(new Dimension(60, 25));
		playerField.setPreferredSize(new Dimension(130, 25));
		playerField.setMaximumSize(playerField.getPreferredSize());
		playerField.setDocument(new JTextFieldLimit(15));
		playerField.setText(playerName);
		Snake.setPlayerName(playerName);
		playerField.setVisible(true);

		playerPanel.setPreferredSize(new Dimension(160, 25));
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.X_AXIS));
		playerPanel.add(playerLabel);
		playerPanel.add(playerField);

		fillerPanel.setBounds(0, 100, 120, 100);

		infoPanel.setBounds(0, 100, 120, 200);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		highScoresPanel.add(highScores);

		turnsPanel.add(turnsText, BorderLayout.WEST);
		turnsPanel.setVisible(true);

		remainingPanel.add(remainingText, BorderLayout.WEST);
		remainingPanel.setVisible(true);

		scorePanel.add(scoreText, BorderLayout.WEST);
		scorePanel.setVisible(true);

		LightsOutContainer.setBounds(0, 0, windowW, windowH);
		LightsOutContainer.setLayout(null);

		infoPanel.add(turnsPanel);
		infoPanel.add(remainingPanel);
		infoPanel.add(scorePanel);
		infoPanel.add(playerPanel);
		infoPanel.add(difficultyPanel);
		infoPanel.add(highScoresPanel);

		rightPanel.add(tutorial);
		leftPanel.add(infoPanel, BorderLayout.WEST);
		LightsOutContainer.add(leftPanel);
		LightsOutContainer.add(rightPanel);
		LightsOutDialog.getContentPane().add(LightsOutContainer);

	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private static void setInteractions() {
		LightsOutDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quitLightsOut();
			}
		});

		difficulty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO make it reset the game and switch difficulty
				// System.out.println(difficulty.getSelectedItem().toString());
				// setDifficulty(difficulty.getSelectedItem().toString());
				difficulty.setSelectedIndex(currentDifficulty);
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
	 * Updates the Turns text
	 * 
	 * @param turn
	 *            Turns played
	 */
	public static void setTurnsText(int turn) {
		turnsText.setText("Turn " + turn);
	}

	/**
	 * Updates the remaining lights text
	 * 
	 * @param remain
	 *            Remaining lights
	 */
	public synchronized static void setRemaining(int remain) {
		remainingText.setText("Remaining lights: " + remain);
	}

	/**
	 * Updates the score
	 * 
	 * @param score
	 *            Score
	 */
	public synchronized static void setScore(int score) {
		scoreText.setText("Score: " + score);
	}

	/**
	 * Sets the difficulty to the given parameter
	 * 
	 * @param diff
	 *            Difficulty
	 */
	private static void setDifficulty(String diff) {
		LightsOut.setDifficulty(diff);
	}

	/**
	 * Returns the player name indicated in the text field
	 * 
	 * @return Player name
	 */
	public static String getPlayerName() {
		return playerField.getText();
	}

	/**
	 * Updates the Leaderboard on the GUI
	 */
	public static void updateDatabase() {
		setHighScores(LightsOut.getTop10());
	}

	/**
	 * Updates the High scores on the GUI with those given as an ArrayList of an
	 * array of Strings. <br>
	 * Only score, player name and date are displayed here although more
	 * information is received.<br>
	 * Every ArrayList entry contains the following:<br>
	 * [Game ID][Score][Player Name][Player 2 Name][Date Played]
	 * 
	 * @param topScores
	 *            ArrayList of String arrays containing SCORE, PLAYERNAME and
	 *            DATE
	 */
	public static void setHighScores(ArrayList<String[]> topScores) {
		for (int i = 0; i < 10 && i < topScores.size(); i++) {
			rowData[0][i] = topScores.get(i)[1];
			rowData[1][i] = topScores.get(i)[2];
			rowData[2][i] = topScores.get(i)[4];
		}

		String scoresText = "<html>Top 10 Scores:<br>";
		scoresText += "<table><tr>" + "<td width=\"50\">Score</td>"
				+ "<td width=\"60\">Name</td>"
				+ "<td width=\"50\">Date</td></tr>";
		for (int i = 0; i < 10; i++) {
			scoresText += "<tr>";
			for (int j = 0; j < 3; j++) {
				scoresText += "<td><FONT FACE=\"Geneva, Arial\" SIZE=2>"
						+ rowData[j][i] + "</FONT></td>";
			}
			scoresText += "</tr>";
		}
		scoresText += "</table></html>";
		if (DEBUG)
			System.out.println(scoresText);
		highScores.setText(scoresText);
	}

	/**
	 * Sets the difficulty to the given parameter
	 * 
	 * @param diff
	 *            Difficulty
	 */
	public static void setDifficulty(int diff) {
		difficulty.setSelectedIndex(diff);
		currentDifficulty = diff;
	}

	/**
	 * Quits the game and closes the GUI
	 */
	public static void quit() {
		Main.setClosing(true);
		Main.lightsOutRunning = false;
		game = null;
		Main.nothingRunning();
		Main.resetCards();
		LightsOutDialog.dispose();
		Main.setCardIcon(Main.lightsOutCard, "lightsOut", "default");
	}

	/**
	 * Method called to close the GUI and game. <br>
	 * Should not be called from within LightsOut class.
	 * 
	 * @return 1
	 */
	public static int quitLightsOut() {
		LightsOut.forceQuit();
		Main.setClosing(true);
		game = null;
		Main.lightsOutRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		LightsOutDialog.dispose();
		return 1;
	}

}
