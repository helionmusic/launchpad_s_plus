package games;

import gui.Main;
import gui.ReactionGameGUI;

import java.util.*;

import launchpad_s_plus.*;

/**
 * Implements Reaction timing game - Whack a mole-like game
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

/*
 * The goal of the game is to test a player's reaction timing. A particular pad
 * or group of pads would light up and initiate a timer. The player then has to
 * press any of the pads quickly and will be able to get his reaction time
 * displayed. We can get the player to tap 10 or 20 times before we display the
 * average of all reaction times accumulated in the play session
 * 
 * 
 * Difficulty 1: a 3x3 pad lights up Difficulty 2: a 2x2 pad lights up
 * Difficulty 3: a single pad lights up
 */
public class ReactionGame extends Game implements Observer {
	private static boolean DEBUG = true;
	private static String playerName = "Player";
	private static int playerScore = 0;
	private static boolean isPlaying = false;
	private static String color = "green";
	private static int difficulty = 2;
	private static double timeSpawned;
	private static int minDelay = 500;
	private static int maxDelay = 1500;
	private static int maxOnTime = 1000;
	private static int minMissedDelay = 300;
	private static int maxMissedDelay = 1500;
	private static int blockX = 0;
	private static int blockY = 0;
	private static int startX = 0;
	private static int startY = 0;
	private static int turns = 20;
	private static int currentTurn = 0;
	private static int correctTurns = 0;
	private static boolean hasPressed = false;
	private static double lastPressTime = 0;
	private static double bounceTime = 250;
	private static double averageTime = 0;
	private static double sumTime = 0;

	private static int volume = 90;
	private static int animSpeed = 50;
	private static int quitX = 0;
	private static int quitY = 7;

	private static boolean[][] padStatus = new boolean[8][8];

	private static boolean forceQuitting;
	private static boolean resettingGame;
	private static boolean quittingGame;
	private static boolean quittingGameQuery;

	/**
	 * Constructor
	 */
	public ReactionGame() {
		gameID = 4;
		gameName = "Reaction Game";
	}

	/**
	 * Constructor used to create the link with Launchpad Input
	 * 
	 * @param b
	 *            False
	 */
	public ReactionGame(boolean b) {
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

		gameName = "Reaction";
		setRunningGame(gameName);
		sizeX = 8;
		sizeY = 8;
		setRunning(true);
		System.out.println("\nStarting new game of Reaction.");
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
	 * Resets the values of all the variables used in the game
	 */
	private void resetValues() {
		playerName = "Player";
		// playerName = ReactionGameGUI.getPlayerName();
		isPlaying = false;
		currentTurn = 0;
		correctTurns = 0;
		playerScore = 0;
		animSpeed = 50;
		// difficulty = ReactionGameGUI.getDifficulty();
		difficulty = 2;
		Display.clearPads();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				padStatus[i][j] = false;
			}
		}
	}

	/**
	 * Starts the game
	 */
	private void begin() {
		introLights();

		for (int i = 0; i < turns; i++) {
			if (forceQuitting || quittingGame)
				return;
			isPlaying = true;
			currentTurn = i + 1;
			int turnTime = Calculate.randInt(minDelay, maxOnTime);
			delay(50);
			timeSpawned = System.currentTimeMillis();
			spawnBlock();

			hasPressed = false;
			while ((System.currentTimeMillis() < timeSpawned + turnTime)
					&& !hasPressed) {
				if (forceQuitting)
					return;
				delay(5);
			}
			if (!hasPressed) {
				if (forceQuitting)
					return;
				updateScore(-1);
				clearBlock();
				new PlaySound("reaction_missedBlock", volume, true);
				delay(Calculate.randInt(minMissedDelay, maxMissedDelay));
			}
			isPlaying = false;

			while ((System.currentTimeMillis() < timeSpawned
					+ Calculate.randInt(turnTime, maxDelay))) {
				if (forceQuitting)
					return;
				delay(5);
			}
		}
		isPlaying = false;

		restartQuery();
		if (forceQuitting || quittingGame)
			return;
	}

	/**
	 * Clears a spawned block
	 */
	private void clearBlock() {
		for (int i = 0; i < blockX; i++) {
			for (int j = 0; j < blockY; j++) {
				Display.off(startX + i + 1, startY + j);
				padStatus[startX + i][startY + j] = false;
			}
		}
	}

	/**
	 * Spawns a random block based on the difficulty level
	 */
	private void spawnBlock() {
		if (difficulty == 1)
			blockX = blockY = 3;
		else if (difficulty == 2)
			blockX = blockY = 2;
		else if (difficulty == 3)
			blockX = blockY = 1;
		else
			blockX = blockY = 5;

		startX = Calculate.randInt(0, 7 - blockX);
		startY = Calculate.randInt(0, 7 - blockY);

		for (int i = 0; i < blockX; i++) {
			for (int j = 0; j < blockY; j++) {
				Display.on(startX + i + 1, startY + j, color, 3);
				padStatus[startX + i][startY + j] = true;
			}
		}
		new PlaySound("reaction_newBlock", volume, true);
	}

	/**
	 * Changes the color of the block to show that the player did not press in
	 * it
	 */
	private void wrongBlock() {
		for (int i = 0; i < blockX; i++) {
			for (int j = 0; j < blockY; j++) {
				Display.on(startX + i + 1, startY + j, "red", 3, 300);
				padStatus[startX + i][startY + j] = false;
				new PlaySound("reaction_wrongBlock", volume, true);
			}
		}
	}

	/**
	 * Checks if the player input was on the block
	 * 
	 * @param x
	 *            X location of the player input
	 * @param y
	 *            Y location of the player input
	 * 
	 * @return True if the player hit the block, False otherwise
	 */
	private boolean checkInput(int x, int y) {
		hasPressed = true;
		lastPressTime = System.currentTimeMillis();
		if (padStatus[x][y]) {
			correctTurns++;
			clearBlock();
			return true;
		} else {
			wrongBlock();
			return false;
		}
	}

	/**
	 * Updates the player's score on the GUI
	 * 
	 * @param correct
	 *            True if the player correctly tapped a block, False otherwise
	 */
	private void updateScore(int correct) {
		double timeTaken = System.currentTimeMillis() - timeSpawned;
		if (correct == 1) {
			playerScore += (difficulty * difficulty) * (int) (timeTaken / 70);
		} else if (correct == 0) {
			playerScore -= (difficulty * difficulty) * (int) (timeTaken / 90);
			if (playerScore < 0)
				playerScore = 0;
		} else {
			playerScore -= 20 - (20 / (difficulty + 1));
			if (playerScore < 0)
				playerScore = 0;
		}
		if (DEBUG)
			System.out.println("New score is " + playerScore);
		// ReactionGameGUI.setScore(playerScore);
	}

	/**
	 * Updates the player's average timing. If the player hits a wrong pad, the
	 * 
	 * @param correct
	 *            True if the player correctly tapped a block, False otherwise
	 */
	private void updateAverage(boolean correct) {
		if (correct) {
			double currentRound = System.currentTimeMillis() - timeSpawned;
			System.out.println("Last time taken: " + currentRound);
			sumTime += currentRound;
			System.out.println("Total time taken: " + sumTime);
			averageTime = sumTime / correctTurns;
			if (DEBUG)
				System.out.println("New average time is " + averageTime + "\n");
			// ReactionGameGUI.setAverage(averageTime);
		}
	}

	/**
	 * Turns on lights at the beginning of the round
	 */
	private void introLights() {
		Display.on(1, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 7, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(2, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 6, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(3, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 5, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(4, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 4, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(5, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 3, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(6, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 2, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(7, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 1, "yellow", 3, 8 * animSpeed);
		delay(animSpeed);
		Display.on(8, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 0, "yellow", 3, 8 * animSpeed);
		delay(1000);
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param time
	 *            Duration in MS
	 */
	private static void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves score into database
	 */
	@Override
	public void saveToDatabase() {
		// TODO Auto-generated method stub

	}

	/**
	 * Retrieves scores from database
	 */
	@Override
	public void retreiveFromDatabase() {
		// TODO Auto-generated method stub

	}

	/**
	 * Processes input from Launchpad S
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (runningGameName == "Reaction") {
			int positionX, positionY;
			if (Input.getStatus()) {
				positionX = Input.getPositionX();
				positionY = Input.getPositionY();

				if (positionX != quitX && positionY != quitY) {
					quittingGameQuery = false;
					notQuitting();
				}

				if (positionX == quitX && positionY == quitY) {
					// Quitting Game
					if (!quittingGameQuery) {
						quittingGameQuery = true;
						Display.blink(quitX, quitY, "red", 2, 400);
						System.out
								.println("Are you sure you want to quit Reaction?");
					} else {
						quittingGameQuery = false;
						Display.blinkoff(quitX, quitY);
						quittingGame = true;
						return;
					}
				} else if (positionX > 0 && positionY < 8) {
					if (!isPlaying)
						return;
					else {
						if (System.currentTimeMillis() - lastPressTime > bounceTime) {
							boolean correct = checkInput(positionX - 1,
									positionY);
							if (correct) 
								new PlaySound("reaction_rightBlock", volume, true);
							updateScore((correct) ? 1 : 0);
							updateAverage(correct);
						}
					}
				}
			}
		}
	}

	/**
	 * Asks player if the game should be restarted
	 */
	private static void restartQuery() {
		int x = -1;
		int y = -1;

		Input.clearPressed();
		Input.waitForInput();
		if (forceQuitting || quittingGame) {
			resettingGame = false;
			return;
		}
		x = Input.getLastKeyI();
		y = Input.getLastKeyJ();

		if (x > 0 && y < 8)
			resettingGame = true;
	}

	/**
	 * Resets the quitting flag to false in case the user did not confirm a quit
	 */
	private static void notQuitting() {
		quittingGameQuery = false;
	}

	/**
	 * Quits the game and closes the GUI
	 */
	public static void quitGame() {
		if (quittingGame) {
			animSpeed = 0;
			System.out.println("Quitting Reaction Game.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(300);
			ReactionGameGUI.quit();
			Main.setClosing(false);
		}
		quittingGame = false;
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		forceQuitting = true;
		System.out.println("Quitting Reaction Game.\n");
		setRunningGame("none");
		setRunning(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
		Main.setClosing(false);
	}

}
