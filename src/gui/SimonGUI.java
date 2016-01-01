package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import games.Simon;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Implements the GUI for Simon
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class SimonGUI {
	private static boolean DEBUG = false;
	private static boolean firstTimeRunning = true;
	private static boolean ready = false;

	private static Simon game;

	private static final JFrame SimonDialog = new JFrame();
	private static final JPanel SimonContainer = new JPanel();
	private static final JPanel leftPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JPanel infoPanel = new JPanel();
	private static final JLabel tutorial = new JLabel();
	private static final JLabel highScores = new JLabel();
	private static final JPanel fillerPanel = new JPanel();

	private static final JPanel playerPanel = new JPanel();
	private static final JLabel playerLabel = new JLabel();
	private static final JTextField playerField = new JTextField();
	private static final JPanel scorePanel = new JPanel();
	private static final JLabel scoreText = new JLabel();
	private static final JPanel highScoresPanel = new JPanel();

	private static String playerName = "Player";
	private static String rowData[][] = new String[3][10];

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
	public SimonGUI(boolean start) {
		if (start) {
			initialize();
			updateDatabase();
			if (firstTimeRunning)
				createAndShowGUI();
			setInteractions();
			SimonDialog.setVisible(true);
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
						game = new Simon();
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
	 * Defines and implements all the interactions for the Game of Life GUI
	 */
	private static void setInteractions() {
		SimonDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quitSimon();
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
	 * Sets the window position and dimenstions
	 */
	private static void initialize() {
		Main.setClosing(false);
		Main.setCardIcon(Main.simonCard, "simon", "selected");

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
		SimonDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		SimonDialog.setTitle("Simon");
		SimonDialog.setResizable(false);
		SimonDialog.setLayout(null);
		SimonDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(10, 0, 200, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(175, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/simonTutorial.png")));

		scoreText.setBounds(0, 0, 100, 100);
		scoreText.setText("Score: 0");

		playerLabel.setText("Player: ");
		playerLabel.setPreferredSize(new Dimension(60, 25));
		playerField.setPreferredSize(new Dimension(130, 25));
		playerField.setMaximumSize(playerField.getPreferredSize());
		playerField.setDocument(new JTextFieldLimit(14));
		playerField.setText(playerName);
		Simon.setPlayerName(playerName);
		playerField.setVisible(true);

		playerPanel.setPreferredSize(new Dimension(160, 25));
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.X_AXIS));
		playerPanel.add(playerLabel);
		playerPanel.add(playerField);

		fillerPanel.setBounds(0, 100, 120, 100);

		infoPanel.setBounds(0, 100, 120, 200);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		highScoresPanel.add(highScores);

		scorePanel.add(scoreText, BorderLayout.WEST);
		scorePanel.setVisible(true);

		SimonContainer.setBounds(0, 0, windowW, windowH);
		SimonContainer.setLayout(null);

		infoPanel.add(scorePanel);
		infoPanel.add(playerPanel);
		infoPanel.add(highScoresPanel);

		rightPanel.add(tutorial);
		leftPanel.add(infoPanel, BorderLayout.WEST);
		SimonContainer.add(leftPanel);
		SimonContainer.add(rightPanel);
		SimonDialog.getContentPane().add(SimonContainer);
	}

	/**
	 * Returns the player's name from the text field
	 * 
	 * @return Player name
	 */
	public static String getPlayer() {
		return playerField.getText();
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
	 * Updates the Leaderboard on the GUI
	 */
	public static void updateDatabase() {
		setHighScores(Simon.getTop10());
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
	 *            ArrayList of String arrays containing SCORE, PLAYERNAME and DATE
	 */
	public static void setHighScores(ArrayList<String[]> topScores) {
		for (int i = 0; i < 10 && i < topScores.size(); i++) {
			rowData[0][i] = topScores.get(i)[1];
			rowData[1][i] = topScores.get(i)[2];
			rowData[2][i] = topScores.get(i)[4];
		}

		String scoresText = "<html>Top 10 Scores:<br>";
		scoresText += "<table><tr>" + "<td>Score</td>"
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
	 * Method called to close the GUI from the Simon class
	 */
	public static void quit() {
		Main.setClosing(true);
		Main.simonRunning = false;
		game = null;
		Main.nothingRunning();
		Main.resetCards();
		SimonDialog.dispose();
		Main.setClosing(false);
		Main.setCardIcon(Main.simonCard, "simon", "default");
	}

	/**
	 * Method called to close the GUI and game. <br>
	 * Should not be called from within Simon class.
	 * 
	 * @return 1
	 */
	public static int quitSimon() {
		Main.setClosing(true);
		Simon.forceQuit();
		game = null;
		Main.simonRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		SimonDialog.dispose();
		Main.setClosing(false);
		Main.setCardIcon(Main.simonCard, "simon", "default");
		return 1;
	}
}
