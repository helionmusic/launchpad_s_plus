package games;

import java.util.*;

import database.Database;
import launchpad_s_plus.*;
import gui.Main;
import gui.SimonGUI;

/**
 * This class will implement the well know Simon game.
 * 
 * @author Helion
 */

/*
 * Description and rules: taken from: https://en.wikipedia.org/wiki/Simon_(game)
 * 
 * The device has four colored buttons, each producing a particular tone when it
 * is pressed or activated by the device. A round in the game consists of the
 * device lighting up one or more buttons in a random order, after which the
 * player must reproduce that order by pressing the buttons. As the game
 * progresses, the number of buttons to be pressed increases. The player loses
 * if a wrong pattern is entered.
 */

public class Simon extends Game implements Observer {
	private static boolean DEBUG = false;
	private static boolean NEWSOUNDS = false;
	private static String playerName = "Player";
	private static int playerScore = 0;
	private static boolean isPlaying = false;
	private static boolean hasLost = false;
	private static ArrayList<Integer> sequence = new ArrayList<Integer>();
	private static String color1 = "red";
	private static String color2 = "green";
	private static String color3 = "orange";
	private static String color4 = "lime";
	private static int waitFor;
	private static int slowestDelay = 700;
	private static int fastestDelay = 250;
	private static int delayDecrement = 10;
	private static int bounceDelay = 100;

	private static boolean forceQuitting;
	private static boolean resettingGame;
	private static boolean quittingGame;

	/**
	 * Constructor
	 */
	public Simon() {
		gameID = 5;
		gameName = "Simon";
	}

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            Unused
	 */
	public Simon(boolean b) {
		gameID = 5;
		gameName = "Simon";
	}

	/**
	 * Initializes and starts the game
	 */
	@Override
	public void startGame() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		gameName = "Simon";
		setRunningGame(gameName);
		sizeX = 8;
		sizeY = 8;
		setRunning(true);
		System.out.println("\nStarting new game of Simon.");
		resetGame();
	}

	/**
	 * Resets the game
	 */
	@Override
	public void resetGame() {
		super.prepareGame();
		incrementGamesPlayed();

		// Setting resetting and quitting booleans to false so the game doesn't
		// restart or quit automatically if this was called previously
		resettingGame = false;
		quittingGame = false;
		forceQuitting = false;

		setRunningGame(gameName);
		resetValues();
		begin(); // Start the game

		if (resettingGame)
			resetGame();
		quitGame();
	}

	/**
	 * Resets the values of variables susceptible to change during the course of
	 * the game
	 */
	private void resetValues() {
		playerName = SimonGUI.getPlayer();
		playerScore = 0;
		hasLost = false;
		isPlaying = false;
		sequence.clear();
		for (int i = 0; i < 3; i++)
			sequence.add(Calculate.randInt(1, 4));

	}

	/**
	 * Increments the total games played and starts a new round
	 */
	private void begin() {
		incrementGamesPlayed();
		while (!hasLost) {
			playSequence();
			getInput();

			if (forceQuitting || quittingGame)
				return;

			if (!hasLost)
				addToSequence();
		}

		saveToDatabase();
		SimonGUI.updateDatabase();
		restartQuery();
	}

	/**
	 * Plays back the current sequence of the game
	 */
	private void playSequence() {
		waitFor = slowestDelay - (playerScore * delayDecrement);
		if (waitFor < fastestDelay)
			waitFor = fastestDelay;

		for (int i = 0; i < sequence.size(); i++) {
			if (forceQuitting)
				return;
			playPad(sequence.get(i).intValue());
			delay(waitFor);
		}
	}

	/**
	 * Selects a random number (1 to 4) and increments it to the already
	 * available sequence
	 */
	private void addToSequence() {
		sequence.add(Calculate.randInt(1, 4));
	}

	/**
	 * Illuminates the quadrant of the Launchpad represented by the given
	 * parameter and plays the related sound. <br>
	 * If the given parameter is equal to 5, the "wrong input" sound is played
	 * and the Launchpad illuminates entirely in red
	 * 
	 * @param i
	 *            Selected quadrant
	 */
	private void playPad(int i) {
		int padDuration = waitFor / 2;
		new PlaySound("simon_" + ((NEWSOUNDS) ? "14" : i + ""), 127, true);
		if (i != 5) {
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 4; y++) {
					switch (i) {
					case 1:
						Display.on(x + 1, y, color1, 3, padDuration);
						break;
					case 2:
						Display.on(x + 1, y + 4, color2, 3, padDuration);
						break;
					case 3:
						Display.on(x + 5, y, color3, 3, padDuration);
						break;
					case 4:
						Display.on(x + 5, y + 4, color4, 3, padDuration);
						break;
					default: // Should never happen
						Display.on(x + 3, y + 2, color1, 3, padDuration);
						break;
					}
				}
			}
		} else {
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					Display.on(x + 1, y, "red", 3, 500);
				}
			}
		}
	}

	/**
	 * Gets the user's input for the entire sequence.<br>
	 * The user must reproduce the same pattern earlier played back in order to
	 * advance.
	 */
	private void getInput() {
		isPlaying = true;
		for (int i = 0; i < sequence.size(); i++) {
			int currentSequence = sequence.get(i);
			hasLost = !getPad(currentSequence);
			if (forceQuitting || quittingGame)
				return;
			if (DEBUG)
				System.out.println("Has Lost: " + hasLost);
			if (!hasLost) {
				playPad(currentSequence);
				incrementScore();
				delay(bounceDelay);
			} else {
				playPad(5);
				delay(1000);
				return;
			}
		}
		isPlaying = false;
		delay(1000);
	}

	/**
	 * Gets the user's input for the i'th pad. Checks if the input is correct or
	 * not
	 * 
	 * @param i
	 *            Pad in the sequence to check
	 * @return True if the user selected the proper pad, False if the user lost
	 */
	private boolean getPad(int i) {
		int x = -1;
		int y = -1;
		boolean correct = false;

		if (DEBUG)
			System.out.println("Getting case " + i);

		Input.waitForInput();
		if (forceQuitting || quittingGame)
			return false;
		x = Input.getLastKeyI();
		y = Input.getLastKeyJ();
		if (DEBUG)
			System.out.println("Received X:" + x + " Y:" + y);

		switch (i) {
		case 1:
			if (x > 0 && x < 5 && y >= 0 && y < 4)
				correct = true;
			else
				correct = false;
			break;
		case 2:
			if (x > 0 && x < 5 && y > 3 && y < 8)
				correct = true;
			else
				correct = false;
			break;
		case 3:
			if (x > 4 && x < 9 && y >= 0 && y < 4)
				correct = true;
			else
				correct = false;
			break;
		case 4:
			if (x > 4 && x < 9 && y > 3 && y < 8)
				correct = true;
			else
				correct = false;
			break;
		default:
			correct = false;
		}
		if (DEBUG)
			System.out.println("Input is " + correct);

		return correct;
	}

	/**
	 * Increments the player's score and updates the GUI
	 */
	private void incrementScore() {
		playerScore++;
		SimonGUI.setScore(playerScore);
	}

	/**
	 * Sets the player name
	 * 
	 * @param text
	 *            Player name
	 */
	public static void setPlayerName(String text) {
		playerName = text;
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param duration
	 *            Time in MS to delay for
	 */
	private static void delay(int duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves details of the round into the database
	 */
	@Override
	public void saveToDatabase() {
		Database.saveScore(playerName, "", playerScore, gameID);
	}

	/**
	 * Retrieves details of the best plays from the databse
	 */
	@Override
	public void retreiveFromDatabase() {
		ArrayList<String[]> topScores = Database.getHighScores(gameID);
		String topScore = "";
		String topPlayer = "";
		String topDate = "";
		for (int i = 0; i < 10 && i < topScores.size(); i++) {
			topScore = topScores.get(i)[1];
			topPlayer = topScores.get(i)[2];
			topDate = topScores.get(i)[4];
			System.out.println(topScore + " " + topPlayer + " " + topDate);
		}
	}

	/**
	 * Returns the top 10 saved rounds from the database as an ArrayList of an
	 * array of Strings<br>
	 * Every ArrayList entry contains the following:<br>
	 * [Game ID][Score][Player Name][Player 2 Name][Date Played]
	 * 
	 * @return ArrayList of String arrays containing SCORE, PLAYERNAME and DATE
	 */
	public static ArrayList<String[]> getTop10() {
		ArrayList<String[]> topScores = Database.getHighScores(5);
		return topScores;
	}

	/**
	 * Processes input from the Launchpad S. Unused in this game.
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (!isPlaying)
			return;
	}

	/**
	 * Asks user if the game should be restarted
	 */
	private static void restartQuery() {
		int x = -1;
		int y = -1;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Display.blink(i + 3, j + 2, "green", 2, 500);
			}
		}

		Input.clearPressed();
		Input.waitForInput();
		x = Input.getLastKeyI();
		y = Input.getLastKeyJ();

		Display.clearPads();

		if (x > 2 && x < 7 && y > 1 && y < 6)
			resettingGame = true;
		else
			quittingGame = true;
	}

	/**
	 * Quits the game and closes GUI
	 */
	public static void quitGame() {
		if (quittingGame) {
			System.out.println("Quitting Simon.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(100);
			Main.setClosing(false);
			SimonGUI.quit();
			quittingGame = false;
			resettingGame = false;
		}
		quittingGame = false;
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		System.out.println("Quitting Simon.\n");
		setRunningGame("none");
		setRunning(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
		Main.setClosing(false);
		quittingGame = false;
		resettingGame = false;
		forceQuitting = true;
		isPlaying = false;
	}

}
