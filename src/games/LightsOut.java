package games;

import gui.LightsOutGUI;
import gui.Main;

import java.util.*;

import database.Database;
import launchpad_s_plus.*;

/**
 * Implements Lights Out game
 * 
 * @author Helion
 */

/*
 * Description of the game and rules: taken from:
 * https://en.wikipedia.org/wiki/Lights_Out_%28game%29
 * 
 * The game consists of a grid of lights. When the game starts, a random number
 * or a stored pattern of these lights is switched on. Pressing any of the
 * lights will toggle it and the four adjacent lights. The goal of the puzzle is
 * to switch all the lights off, preferably in as few button presses as
 * possible.
 * 
 * Tips for playing: If a light is on, it must be toggled an odd number of times
 * to be turned off. If a light is off, it must be toggled an even number of
 * times (including none at all) for it to remain off. Several conclusion are
 * used for the game's strategy. Firstly, the order in which the lights are
 * pressed does not matter, as the result will be the same. Secondly, in a
 * minimal solution, each light needs to be pressed no more than once, because
 * pressing a light twice is equivalent to not pressing it at all.
 */
public class LightsOut extends Game implements Observer {
	private static boolean DEBUG = false;
	private static String controlColor = "yellow";
	private static String normColor = "yellow";
	private static String wallColor = "orange";
	private static String difficulty = "medium";
	private static String playerName = "Player";
	private static boolean resettingGame;
	private static boolean quittingGame;
	private static boolean quittingGameQuery;
	private static boolean forceQuitting;
	private static int lightStatus[][] = new int[8][8];
	private static int turns = 0;
	private static int score = 0;
	private int sizeX;
	private int sizeY;
	private int quitX = 0;
	private int quitY = 7;
	private static int remaining;

	/**
	 * Class used to create unique cells containing and X and Y coordinate
	 * 
	 * @author Luke
	 */
	protected class LightCell {
		protected int x;
		protected int y;

		/**
		 * Constructor
		 * 
		 * @param i
		 *            X location of the cell
		 * @param j
		 *            Y location of the cell
		 */
		LightCell(int i, int j) {
			x = i;
			y = j;
		}

		/**
		 * Returns the X position of the cell
		 * 
		 * @return X position of the cell
		 */
		protected int getX() {
			return x;
		}

		/**
		 * Returns the Y position of the cell
		 * 
		 * @return Y position of the cell
		 */
		protected int getY() {
			return y;
		}

		/**
		 * Sets the X position of the cell
		 * 
		 * @param i
		 *            X position of the cell
		 */
		protected void setX(int i) {
			x = i;
		}

		/**
		 * Sets the Y position of the cell
		 * 
		 * @param j
		 *            Y position of the cell
		 */
		protected void setY(int j) {
			y = j;
		}
	}

	/**
	 * Constructor used to create the link with Launchpad Input
	 * 
	 * @param b
	 *            False
	 */
	public LightsOut(boolean b) {
	}

	/**
	 * Constructor
	 */
	public LightsOut() {
		gameID = 3;
		gameName = "Lights Out";
	}

	/**
	 * Resets the game
	 */
	@Override
	public void resetGame() {
		super.prepareGame();
		resettingGame = false;
		quittingGame = false;
		forceQuitting = false;
		setRunningGame(gameName);
		turns = 0;
		remaining = 0;
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
		gameName = "Lights Out";
		setRunningGame(gameName);
		sizeX = 8;
		sizeY = 8;
		setRunning(true);
		System.out.println("\nStarting new game of Lights Out.");
		begin();
	}

	/**
	 * Restarts the game
	 */
	public static void restart() {
		resettingGame = true;
	}

	/**
	 * Increments the total games played and starts a new round
	 */
	public void begin() {
		if (DEBUG)
			System.out.println("Previous Top 10 Scores");
		retreiveFromDatabase();

		incrementGamesPlayed();
		retreiveFromDatabase();
		newGame();
	}

	/**
	 * Starts a new round
	 */
	private void newGame() {
		resetGame();
		lightControls();
		resetBoard(difficulty);
		while (remaining != 0) {
			if (resettingGame || quittingGame || forceQuitting)
				break;

			Input.clearPressed();
			Input.waitForInput();
			int x = Input.getLastKeyI();
			int y = Input.getLastKeyJ();

			if (resettingGame || quittingGame || forceQuitting)
				break;

			if (x == quitX && y == quitY) {
				quittingGame = true;
				continue;
			} else if (x == 0 && y == 0) {
				resetBoard("easy");
			} else if (x == 0 && y == 1) {
				resetBoard("medium");
			} else if (x == 0 && y == 2) {
				resetBoard("hard");
			} else if (x == 0 && y == 3) {
				resetBoard("random");
			} else if (x >= sizeX + 1 || y >= sizeY || x == 0) {
				continue;
			}

			if (DEBUG)
				System.out.println("Toggling " + (x - 1) + " " + y);
			toggleLight(x - 1, y);
			if (DEBUG)
				System.out.println("Turn " + turns + " - Remaining "
						+ remaining);
			LightsOutGUI.setRemaining(remaining);
		}
		System.out.println("Your score is : " + calculateScore());
		if (remaining == 0)
			playerWon();

		clearBoard();

		if (resettingGame) {
			System.out.println("Resetting game");
			resetGame();
		}

		if (DEBUG)
			System.out.println("reset:" + resettingGame + " quitting:"
					+ quittingGame + " force:" + forceQuitting);

		quittingGame = true;
		quitGame();
	}

	/**
	 * Resets the light cells on the board based on a given difficulty
	 * 
	 * @param diff
	 *            Difficulty
	 */
	private void resetBoard(String diff) {
		clearBoard();
		turns = 0;
		score = 0;

		int amount;
		System.out.println(diff);
		if (diff == "Easy" || diff == "easy") {
			LightsOutGUI.setDifficulty(0);
			amount = 5;
			sizeX = 4;
			sizeY = 3;
		} else if (diff == "Medium" || diff == "medium") {
			LightsOutGUI.setDifficulty(1);
			amount = 10;
			sizeX = 5;
			sizeY = 4;
		} else if (diff == "Hard" || diff == "hard") {
			LightsOutGUI.setDifficulty(2);
			amount = 18;
			sizeX = 6;
			sizeY = 5;
		} else {
			LightsOutGUI.setDifficulty(3);
			amount = Calculate.randInt(0, 64);
			sizeX = 8;
			sizeY = 7;
		}

		delay(100);

		/* Creating the walls for easier difficulties */
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (i >= sizeX || j >= sizeY) {
					lightStatus[i][j] = -1;
					Display.on(i + 1, j, wallColor, 1);
				}
				delay(10);
			}
			delay(20);
		}

		ArrayList<LightCell> cell = new ArrayList<LightCell>();
		for (int i = 0; i < amount; i++) {
			int x = Calculate.randInt(0, sizeX - 1);
			int y = Calculate.randInt(0, sizeY - 1);
			cell.add(new LightCell(x, y));
			for (int j = 0; j < i; j++) {
				while ((cell.get(i).getX() == cell.get(j).getX())
						&& (cell.get(i).getY() == cell.get(j).getY())) {
					j = 0;
					if (DEBUG)
						System.out
								.println("Cell already existant, regenerating.");
					int X = Calculate.randInt(0, sizeX - 1);
					int Y = Calculate.randInt(0, sizeY - 1);
					cell.get(i).setX(X);
					cell.get(i).setY(Y);
				}
			}
			if (DEBUG)
				System.out.println("X:" + cell.get(i).getX() + " Y:"
						+ cell.get(i).getY());
		}

		for (int i = 0; i < amount; i++) {
			setLightStatus(cell.get(i).getX(), cell.get(i).getY(), 1);
			delay(30);
		}

		System.out.println("You are now playing a " + diff
				+ " game. Good Luck!");
		System.out.println(remaining + " lights remaining");

		updateGUI();
	}

	/**
	 * Sets the a given light cell to On
	 * 
	 * @param x
	 *            X location of the light cell on the game's board
	 * @param y
	 *            Y location of the light cell on the game's board
	 * @param status
	 *            1
	 */
	private synchronized static void setLightStatus(int x, int y, int status) {
		Display.on(x + 1, y, normColor, 3);
		lightStatus[x][y] = status;
		incrementRemaining();
	}

	/**
	 * The player has won. Waits for input from the player before proceeding
	 */
	private void playerWon() {
		Display.blink(0, 0, controlColor, 2, 500);
		Display.blink(0, 1, controlColor, 2, 500);
		Display.blink(0, 2, controlColor, 2, 500);
		Display.blink(0, 3, controlColor, 2, 500);
		Display.blink(quitX, quitY, "red", 2, 500);
		saveToDatabase();
		LightsOutGUI.updateDatabase();
		Input.clearPressed();
		Input.waitForInput();
	}

	/**
	 * Clears the board
	 */
	private void clearBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				lightStatus[i][j] = 0;
				Display.off(i + 1, j);
			}
		}
		remaining = 0;
	}

	/**
	 * Sets the game's difficulty
	 * 
	 * @param value
	 *            Easy, Medium, Hard or Random
	 */
	public static void setDifficulty(String value) {
		difficulty = value;
	}
	
	/**
	 * Returns the game's difficulty
	 * 
	 * @return String describing the difficulty
	 */
	public static String getDifficulty(){
		return difficulty;
	}

	/**
	 * Sets the player's name
	 * 
	 * @param name
	 *            Player name
	 */
	public static void setPlayerName(String name) {
		playerName = name;
	}

	/**
	 * Lights up the extra control pads
	 */
	private void lightControls() {
		int animSpeed = 50;
		int times = 4;

		Display.onThenChange(0, 0, "green", controlColor, 3, 1, animSpeed
				* times);
		delay(animSpeed);
		Display.onThenChange(0, 1, "green", controlColor, 3, 1, animSpeed
				* times);
		delay(animSpeed);
		Display.onThenChange(0, 2, "green", controlColor, 3, 1, animSpeed
				* times);
		delay(animSpeed);
		Display.onThenChange(0, 3, "green", controlColor, 3, 1, animSpeed
				* times);
		delay(animSpeed);
		Display.on(0, 4, "green", 3, animSpeed * times);
		delay(animSpeed);
		Display.on(0, 5, "green", 3, animSpeed * times);
		delay(animSpeed);
		Display.on(0, 6, "green", 3, animSpeed * times);
		delay(animSpeed);
		Display.onThenChange(0, 7, "green", "red", 3, 1, animSpeed * times);
		delay(animSpeed);

	}

	/**
	 * Calculates the user's score
	 * 
	 * @return Score
	 */
	private static int calculateScore() {
		double temp = 0;

		if (difficulty == "easy" || difficulty == "Easy")
			temp = 150;
		else if (difficulty == "medium" || difficulty == "Medium")
			temp = 200;
		else if (difficulty == "hard" || difficulty == "Hard")
			temp = 300;
		else
			temp = 400;

		temp -= (double) (turns);

		if (temp < 0)
			temp = 0;

		score = (int) temp;
		return (int) temp;
	}

	/**
	 * Saves details of the round into the database
	 */
	@Override
	public void saveToDatabase() {
		Database.saveScore(playerName, "", score, gameID);
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
			if (DEBUG)
				System.out.println(topScore + " " + topPlayer + " " + topDate);
		}
	}

	/**
	 * Returns the top 10 saved rounds from the database as an ArrayList of an
	 * array of Strings<br>
	 * Every ArrayList entry contains the following:<br>
	 * [Game ID][Score][Player Name][Player 2 Name][Date Played]
	 * 
	 * @return ArrayList of String arrays containing SCORE, PLAYERNAME, and DATE
	 */
	public static ArrayList<String[]> getTop10() {
		ArrayList<String[]> topScores = Database.getHighScores(3);
		return topScores;
	}

	/**
	 * Processes Input from the Launchpad S immediately. Unused in this game
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
	}

	/**
	 * Toggles a single light cell
	 * 
	 * @param x
	 *            X location of the cell to toggle
	 * @param y
	 *            Y location of the cell to toggle
	 */
	public static void toggle(int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7)
			return;
		if (lightStatus[x][y] == -1) {
			return;
		}

		if (lightStatus[x][y] == 1) {
			Display.off(x + 1, y);
			lightStatus[x][y] = 0;
			decrementRemaining();
		} else {
			Display.on(x + 1, y, normColor, 3);
			lightStatus[x][y] = 1;
			incrementRemaining();
		}
	}

	/**
	 * Toggles a light cell and all its neighbors
	 * 
	 * @param x
	 *            X location of the middle light cell
	 * @param y
	 *            Y location of the middle light cell
	 */
	private static void toggleLight(int x, int y) {
		toggle(x, y);
		toggle(x + 1, y);
		toggle(x - 1, y);
		toggle(x, y + 1);
		toggle(x, y - 1);
		incrementTurns();
	}

	/**
	 * Increments the amount of turns taken by the player
	 */
	private static void incrementTurns() {
		turns++;
		updateGUI();
	}

	/**
	 * Updates the GUI
	 */
	private static void updateGUI() {
		LightsOutGUI.setTurnsText(turns);
		LightsOutGUI.setRemaining(remaining);
		LightsOutGUI.setScore(calculateScore());
	}

	/**
	 * Increments the remaining ON light cells
	 */
	private synchronized static void incrementRemaining() {
		remaining++;
	}

	/**
	 * Decrements the remaining ON light cells
	 */
	private synchronized static void decrementRemaining() {
		remaining--;
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param duration
	 *            Time in MS
	 */
	public static void delay(int duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the quitting flag to false in case the user did not confirm a quit
	 */
	private void notQuitting() {
		Display.blinkoff(0, 7);
		quittingGameQuery = false;
		quittingGame = false;
		try {
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.on(0, 7, "red", 1);
	}

	/**
	 * Quits the game and closes the GUI
	 */
	public static void quitGame() {
		if (quittingGame) {
			System.out.println("Quitting Lights Out.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(100);
			LightsOutGUI.quit();
			Main.setClosing(false);
			quittingGame = false;
			resettingGame = false;
			forceQuitting = false;
		}
		quittingGame = false;
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		forceQuitting = true;
		System.out.println("Quitting Lights Out.\n");
		setRunningGame("none");
		setRunning(false);
		delay(100);
		Main.setClosing(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
	}
}