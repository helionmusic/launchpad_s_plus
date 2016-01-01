package games;

import gui.Main;
import gui.SnakeGUI;

import java.awt.event.KeyEvent;
import java.util.*;

import database.Database;
import launchpad_s_plus.*;

/**
 * Implements Snake game
 * 
 * @author Helion
 */

/*
 * Description of the game and rules: taken from:
 * https://en.wikipedia.org/wiki/Snake_(video_game)
 * 
 * The player controls a dot, square, or object on a bordered plane. As it moves
 * forward, it leaves a trail behind, resembling a moving snake.
 * 
 * The player player attempts to eat objects by running into them with the head
 * of the snake. Each object eaten makes the snake longer, so maneuvering is
 * progressively more difficult.
 * 
 * The player loses when the snake runs into the screen border, a trail, or
 * another obstacle
 */

public class Snake extends Game implements Observer{
	private static boolean DEBUG = false;
	private static int direction;
	private static int nextDifficulty;
	private static String playerName = "Player";
	private static boolean lightsLit = false;
	private static boolean listeningToKeyboard = false;
	private int score;
	private int snakeLength;
	private int eaten;
	private int travelled;
	private boolean hasLost = false;
	private int[][] snakeStatus = new int[8][8];
	private int snakeHeadLocation[] = new int[2];
	private String snakeHeadColor = "green";
	private String snakeBodyColor = "yellow";
	private String snakeTailColor = "red";
	private String controlColor = "orange";
	private String fruitColor = "red";
	private int snakeIntensity = 3;
	private int quitX = 0;
	private int quitY = 7;

	private static boolean forceQuitting;
	private boolean resettingGame;
	private static boolean quittingGame;
	private static boolean quittingGameQuery;

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            False
	 */
	public Snake(boolean b) {
	}

	/**
	 * Constructor
	 */
	public Snake() {
		gameID = 6;
		gameName = "Snake";
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

		gameName = "Snake";
		setRunningGame(gameName);
		sizeX = 8;
		sizeY = 8;
		setRunning(true);
		System.out.println("\nStarting new game of Snake.");
		resetGame();
	}

	/**
	 * Resets the game
	 */
	public void resetGame() {
		super.prepareGame();

		// Setting resetting and quitting booleans to false so the game doesn't
		// restart or quit automatically if this was called previously
		resettingGame = false;
		quittingGame = false;
		forceQuitting = false;

		setRunningGame(gameName);
		resetValues();
		enableKeyboardListener();
		begin(); // Start the game
		disableKeyboardListener();
		
		stopAllBlinking();

		if (resettingGame)
			resetGame();
		quitGame();
	}

	/**
	 * Resets the values of variables susceptible to change during the course of
	 * the game
	 */
	private void resetValues() {
		lightsLit = true;
		difficulty = nextDifficulty;
		direction = -1;
		snakeLength = 1;
		eaten = 0;
		travelled = 0;
		hasLost = false;
		listeningToKeyboard = false;
	}

	/**
	 * Sets the next round's difficulty
	 * 
	 * @param value
	 *            Difficulty
	 */
	public static void setDifficulty(int value) {
		if (DEBUG)
			System.out.println("Next difficulty set: " + value);
		nextDifficulty = value;
	}

	/**
	 * Returns the current difficulty
	 * 
	 * @return Difficulty
	 */
	public static int getDifficulty() {
		return difficulty;
	}
	
	/**
	 * Allows the keyboard listener to function
	 */
	private void enableKeyboardListener(){
		listeningToKeyboard = true;
	}
	
	/**
	 * Disables the keyboard listener from functioning
	 */
	private void disableKeyboardListener(){
		listeningToKeyboard = false;
	}
	/**
	 * Handles keyboard input
	 */
    public static void keyPressed(KeyEvent e) {
    	if (DEBUG){
    		System.out.println("Received key press: " + e.getKeyCode());
    	}
    	if(!listeningToKeyboard)
    		return;
		int prevDirection = direction;

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        	Input.stopWaitForInput();
			if (prevDirection != 3)
				direction = 4;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        	Input.stopWaitForInput();
			if (prevDirection != 4)
				direction = 3;
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
        	Input.stopWaitForInput();
			if (prevDirection != 2)
				direction = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        	Input.stopWaitForInput();
			if (prevDirection != 1)
				direction = 2;
        }
    }

	/**
	 * Increments the total games played and starts a new round
	 */
	private void begin() {

		System.out.println("Previous Top 10 Scores");
		retreiveFromDatabase();

		System.out.println("\nNew Round");

		incrementGamesPlayed();
		fillStatus();
		lightControls();
		spawnSnake();
		findNewFruit();
		if (DEBUG)
			displayStatus();
		Input.waitForInput();
		if (resettingGame || quittingGame || forceQuitting)
			return;
		while (direction == -1) {
			if (DEBUG)
				System.out.println("Waiting for input ");
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resettingGame || quittingGame || forceQuitting)
				return;
		}

		difficulty = SnakeGUI.getDifficulty();

		while (!hasLost) {
			if (resettingGame || quittingGame || forceQuitting)
				return;
			moveSnake(direction);
			if (DEBUG)
				displayStatus();
			recolor();
			if (!hasLost)
				moveDelay();
		}
		snakeDied();
		saveToDatabase();
		SnakeGUI.updateDatabase();
		restartGameQuery();
		if (resettingGame || quittingGame || forceQuitting)
			return;
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
		ArrayList<String[]> topScores = Database.getHighScores(6);
		return topScores;
	}

	/**
	 * Stops all possible blinking pads
	 */
	private void stopAllBlinking() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Display.blinkoff(i + 1, j);
			}
		}
	}

	/**
	 * Player has lost
	 */
	private void snakeDied() {
		int blinkspeed = 400;
		int score = calculateScore();
		System.out.println("Player lost. Score: " + score);
		playerName = SnakeGUI.getPlayerName();
		if (playerName == "")
			playerName = "Player";
		for (int k = snakeLength; k > 0; k--) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (snakeStatus[i][j] == k) {
						if (k == snakeLength)
							Display.blink(i + 1, j, snakeHeadColor, 3,
									blinkspeed);
						else if (k == 1)
							Display.blink(i + 1, j, snakeTailColor, 3,
									blinkspeed);
						else if (k > 0)
							Display.blink(i + 1, j, snakeBodyColor, 3,
									blinkspeed);
						try {
							Thread.sleep(50);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		int animdelay = 50;
		Display.on(0, 0, "red", 1);
		try {
			Thread.sleep(animdelay);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Display.on(0, 1, "red", 1);
		try {
			Thread.sleep(animdelay);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Display.on(0, 2, "red", 1);
		try {
			Thread.sleep(animdelay);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Display.on(0, 3, "red", 1);
		try {
			Thread.sleep(animdelay);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the user's score
	 * 
	 * @return Score
	 */
	private int calculateScore() {
		double temp = (((double) difficulty * (double) difficulty * 20) + ((double) (eaten * 20)));
		if (difficulty != 5 || difficulty != 4)
			temp -= ((double) travelled / 3);

		if (temp < 0)
			temp = 0;

		score = (int) temp;

		return (int) temp;
	}

	/**
	 * Recolors the board just in case a color is off
	 */
	private void recolor() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (snakeStatus[i][j] == -3)
					Display.on(i + 1, j, fruitColor, 1);
				else if (snakeStatus[i][j] == snakeLength)
					Display.on(i + 1, j, snakeHeadColor, snakeIntensity);
				else if (snakeStatus[i][j] == 1)
					Display.on(i + 1, j, snakeTailColor, snakeIntensity);
				else if (snakeStatus[i][j] > 0)
					Display.on(i + 1, j, snakeBodyColor, snakeIntensity);
				else if (snakeStatus[i][j] == -1)
					Display.off(i + 1, j);
			}
		}
	}

	/**
	 * Lights up control pads
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
		lightsLit = true;
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
	 * Initiates an empty int[][] to be used for the snake
	 */
	private void fillStatus() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				snakeStatus[i][j] = 0;
			}
		}
	}

	/**
	 * Displays the status of the board on the console
	 */
	private void displayStatus() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				System.out.print(snakeStatus[i][j] + "\t");
			}
			System.out.println();
		}

		System.out.println("\n\n");
	}

	/**
	 * Spawns a snake
	 */
	private void spawnSnake() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int spawnX = 3;
		int spawnY = 3;
		snakeStatus[spawnX][spawnY] = 1;
		snakeHeadLocation[0] = spawnX;
		snakeHeadLocation[1] = spawnY;
		Display.on(spawnX + 1, spawnY, snakeHeadColor, snakeIntensity);
	}

	/**
	 * Moves the snake into the given direction
	 * 
	 * @param Direction
	 *            Direction headed: <br>
	 *            - 1: Going up<br>
	 *            - 2: Going down<br>
	 *            - 3: Going left<br>
	 *            - 4: Going right
	 */
	private void moveSnake(int Direction) {
		if (forceQuitting)
			return;
		int goingToStatus;
		switch (direction) {
		case 1: // Going up
			if ((snakeHeadLocation[0] - 1) == -1) {
				hasLost = true;
				return;
			}
			goingToStatus = snakeStatus[snakeHeadLocation[0] - 1][snakeHeadLocation[1]];

			if (!checkNextMove(goingToStatus)) {
				hasLost = true;
				return;
			}
			// If none of the above, snake moved to an empty location

			snakeStatus[snakeHeadLocation[0] - 1][snakeHeadLocation[1]] = snakeLength + 1;
			snakeHeadLocation[0]--;

			break;
		case 2: // Going dowm
			if (snakeHeadLocation[0] + 1 == 8) { // Snake is going to hit a wall
				hasLost = true;
				return;
			}

			goingToStatus = snakeStatus[snakeHeadLocation[0] + 1][snakeHeadLocation[1]];
			if (!checkNextMove(goingToStatus)) {
				hasLost = true;
				return;
			}
			// If none of the above, snake will move to an empty location

			snakeStatus[snakeHeadLocation[0] + 1][snakeHeadLocation[1]] = snakeLength + 1;
			snakeHeadLocation[0]++;

			break;
		case 3: // Going left
			if (snakeHeadLocation[1] - 1 == -1) { // Snake is going to hit a
													// wall
				hasLost = true;
				return;
			}

			goingToStatus = snakeStatus[snakeHeadLocation[0]][snakeHeadLocation[1] - 1];

			if (!checkNextMove(goingToStatus)) {
				hasLost = true;
				return;
			}

			// If none of the above, snake will move to an empty location

			snakeStatus[snakeHeadLocation[0]][snakeHeadLocation[1] - 1] = snakeLength + 1;
			snakeHeadLocation[1]--;

			break;
		case 4: // Going right
			if (snakeHeadLocation[1] + 1 == 8) { // Snake is going to hit a wall
				hasLost = true;
				return;
			}

			goingToStatus = snakeStatus[snakeHeadLocation[0]][snakeHeadLocation[1] + 1];

			if (!checkNextMove(goingToStatus)) {
				hasLost = true;
				return;
			}

			// If none of the above, snake will move to an empty location
			snakeStatus[snakeHeadLocation[0]][snakeHeadLocation[1] + 1] = snakeLength + 1;
			snakeHeadLocation[1]++;

			break;
		}

		movingSnake();
	}

	/**
	 * Delays for a duration determined by the difficulty and snake length
	 */
	private void moveDelay() {
		int speed;
		if (difficulty == 1)
			speed = 600;
		else if (difficulty == 2)
			speed = 450;
		else if (difficulty == 3)
			speed = 300;
		else if (difficulty == 4)
			speed = 200;
		else
			speed = 80;

		// if (DEBUG) speed = 2000;
		speed -= 10 * snakeLength;
		if (speed < 80)
			speed = 80;
		if (DEBUG)
			System.out.println("New speed: " + speed);

		try {
			Thread.sleep(speed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether the snake will hit anything next frame
	 * 
	 * @param goingTo
	 *            Direction headed to
	 * @return True if the snake is safe, False if the player will lose
	 */
	private boolean checkNextMove(int goingTo) {
		if (goingTo > 1) { // Snake hit a part of its body
			return false;
		} else if (goingTo == 2) { // Snake will hit its tail
			return false;
		} else if (goingTo == -3) { // Snake ate an fruit
			eaten++;
			snakeLength++;
			findNewFruit();
		}
		return true;
	}

	/**
	 * Moves the snake, updates the board, increments the amount of times moved
	 * and updates the GUI with the new score
	 */
	private void movingSnake() {
		travelled++;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (snakeStatus[i][j] == -3)
					continue;
				else if (snakeStatus[i][j] == -1)
					snakeStatus[i][j] = 0;
				else if (snakeStatus[i][j] > 0) {
					if (snakeStatus[i][j] == 1) {
						Display.off(i + 1, j);
						snakeStatus[i][j] = 0;
					}
					snakeStatus[i][j]--;
				}
			}
		}
		SnakeGUI.setScoreText(calculateScore());
	}

	/**
	 * Spawns a new fruit for the snake to eat
	 */
	private void findNewFruit() {
		if (DEBUG)
			System.out.println("Searching for new fruit");
		int x = Calculate.randInt(0, 7);
		int y = Calculate.randInt(0, 7);
		while (snakeStatus[x][y] != 0) {
			x = Calculate.randInt(0, 7);
			y = Calculate.randInt(0, 7);
		}
		snakeStatus[x][y] = -3;
		Display.on(x + 1, y, fruitColor, 1);
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
			System.out.println(topScore + " " + topPlayer + " " + topDate);
		}
	}

	/**
	 * Processes input from the Launchpad S.
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable arg0, Object arg1) {
		if (runningGameName == "Snake") {
			int prevDirection = direction;
			int positionX, positionY;
			if (Input.getStatus()) {
				positionX = Input.getPositionX();
				positionY = Input.getPositionY();
				if (positionX != 0 && positionY != 7) {
					quittingGameQuery = false;
					notQuitting();
				}

				if (positionX == 0 && positionY == 0) {
					Display.onThenChange(positionX, positionY, "yellow",
							controlColor, 3, 1, 300);
					if (prevDirection != 2)
						direction = 1;
					return;
				} else if (positionX == 0 && positionY == 1) {
					Display.onThenChange(positionX, positionY, "yellow",
							controlColor, 3, 1, 300);
					if (prevDirection != 1)
						direction = 2;
					return;
				} else if (positionX == 0 && positionY == 2) {
					Display.onThenChange(positionX, positionY, "yellow",
							controlColor, 3, 1, 300);
					if (prevDirection != 4)
						direction = 3;
					return;
				} else if (positionX == 0 && positionY == 3) {
					Display.onThenChange(positionX, positionY, "yellow",
							controlColor, 3, 1, 300);
					if (prevDirection != 3)
						direction = 4;
					return;
				} else if (positionX == quitX && positionY == quitY) {
					// Quitting Game
					if (!quittingGameQuery) {
						quittingGameQuery = true;
						Display.blink(quitX, quitY, "red", 2, 400);
						System.out
								.println("Are you sure you want to quit Snake?");
					} else {
						quittingGameQuery = false;
						Display.blinkoff(quitX, quitY);
						quittingGame = true;
						return;
					}
				}
			}
		}
	}

	/**
	 * Stops blinking the Quit control pad in case the user cancelled a quit
	 */
	private void notQuitting() {
		Display.blinkoff(0, 7);
		try {
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.on(0, 7, "red", 1);
	}

	/**
	 * Asks the user if the game should be restarted
	 */
	private void restartGameQuery() {
		Input.clearPressed();
		Display.blink(0, 4, "orange", 1, 300);
		Display.blink(0, 5, "orange", 1, 300);
		Display.blink(0, 6, "orange", 1, 300);
		Display.blink(0, 7, "orange", 1, 300);
		System.out.println("Do you want to play another game?");
		int inX = -2;
		Input.waitForInput();
		inX = Input.getLastKeyI();
		if (inX == 0)
			resettingGame = true;
		else
			quittingGame = true;
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.blinkoff(0, 4);
		Display.blinkoff(0, 5);
		Display.blinkoff(0, 6);
		Display.blinkoff(0, 7);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		System.out.println("Quitting Snake.\n");
		if (!lightsLit)
			delay(2000);
		forceQuitting = true;
		quittingGame = true;
		setRunningGame("none");
		setRunning(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
		Main.setClosing(false);
	}

	/**
	 * Quits the game and closes GUI
	 */
	public static void quitGame() {
		if (quittingGame) {
			System.out.println("Quitting Snake.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(100);
			SnakeGUI.quit();
			Main.setClosing(false);
		}
		quittingGame = false; // So it does not run through this method again if
								// the player played a couple of rounds
	}
}
