package games;

import gui.GameOfLifeGUI;
import gui.Main;

import java.util.*;

import launchpad_s_plus.*;

/**
 * Implements Conway's "Game of Life" for use with the launchpad S
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

/*
 * Rules of the simulation: taken from:
 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 * 
 * The universe of the Game of Life is an infinite two-dimensional orthogonal
 * grid of square cells, each of which is in one of two possible states, alive
 * or dead. Every cell interacts with its eight neighbours, which are the cells
 * that are horizontally, vertically, or diagonally adjacent. At each step in
 * time, the following transitions occur:
 * 
 * 1- Any live cell with fewer than two live neighbours dies, as if caused by
 * under-population 2- Any live cell with two or three live neighbours lives on
 * to the next generation 3- Any live cell with more than three live neighbours
 * dies, as if by overcrowding 4- Any dead cell with exactly three live
 * neighbours becomes a live cell, as if by reproduction
 * 
 * The initial pattern constitutes the seed of the system. The first generation
 * is created by applying the above rules simultaneously to every cell in the
 * seed—births and deaths occur simultaneously, and the discrete moment at which
 * this happens is sometimes called a tick (in other words, each generation is a
 * pure function of the preceding one). The rules continue to be applied
 * repeatedly to create further generations
 */
public class GameOfLife extends Game implements Observer {
	private static boolean DEBUG = false;
	private static boolean DEBUG2 = false;
	private static boolean paused;
	private static int tickLength;
	private static int sizeX = 8;
	private static int sizeY = 8;
	private static boolean[][] cellStatus = new boolean[sizeX][sizeY];
	private static int[][] cellNeighbours = new int[sizeX][sizeY];
	private static String cellColor = "yellow";
	private static int cellIntensity = 3;
	private static int generations = 0;
	private static int population = 0;
	private static int speedIncrement = 50;
	private int maxTickLength = 1000;
	private int minTickLength = 50;
	private int knownShapes = 12;
	private static int animationDelaySpeed;
	private static int animSpeed;

	private static boolean forceQuitting = false;
	private static boolean quittingGame;
	private static boolean quittingGameQuery;

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            False
	 */
	public GameOfLife(boolean b) {
	}

	/**
	 * Empty Constructor
	 */
	public GameOfLife() {
		gameID = 2;
		gameName = "Game Of Life";
	}

	/**
	 * Initiates an instance of Game of Life
	 */
	@Override
	public void startGame() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		gameName = "Game Of Life";
		setRunningGame(gameName);
		setRunning(true);
		System.out.println("\nStarting Game of Life.");
		resetGame();
	}

	/**
	 * Resets the game
	 */
	public void resetGame() {
		super.prepareGame();

		// Setting resetting and quitting booleans to false so the game doesn't
		// quit automatically if this was called previously
		forceQuitting = false;
		quittingGame = false;
		quittingGameQuery = false;

		resetValues();
		begin(); // Start the simulation

		quitGame();
	}

	/**
	 * Resets all the variables to the values used when initiating Game of Life
	 */
	private void resetValues() {
		paused = true;
		generations = 0;
		population = 0;
		tickLength = 200;
		animationDelaySpeed = 50;
		animSpeed = 30;
		if (DEBUG || DEBUG2)
			tickLength = 1000;
		clearCells();
	}

	/**
	 * Initiates the simulation
	 */
	private synchronized void begin() {
		incrementGamesPlayed();
		lightControls();
		animationDelay();
		spawnShape(Calculate.randInt(0, knownShapes - 1));
		while (isRunning()) {
			if (quittingGame || forceQuitting) {
				quitGame();
				return;
			}
			try {
				Thread.sleep(tickLength);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!paused) {
				if (quittingGame || forceQuitting) {
					quitGame();
					return;
				}
				simulate();

				if (DEBUG)
					System.out.println("\tGeneration " + generations);
				if (DEBUG2)
					displayStatus();
			} else {
				showCurrentStats();
				while (paused) {
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (quittingGame || forceQuitting) {
						quitGame();
						return;
					}
				}
			}
		}
	}

	/**
	 * Shows if the simulation is paused, the current population, generation and
	 * tick length
	 */
	private void showCurrentStats() {
		System.out.println("\tSimulation "
				+ (isRunning() ? "running" : "paused")
				+ "\n\tCurrent population: " + populationValue()
				+ "\n\tCurrent generation: " + generationsValue()
				+ "\n\tCurrent tick length: " + tickLength + "\n");
	}

	/**
	 * Displays on the console a map of the simulation containing "." for empty
	 * cells and "1" for live cells
	 */
	private void displayStatus() {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				System.out.print(((cellNeighbours[x][y] == -1) ? "."
						: cellNeighbours[x][y]) + "\t");
			}
			System.out.print("\t\t");
			for (int y = 0; y < sizeY; y++) {
				System.out.print(((cellStatus[x][y]) ? "1" : ".") + "\t");
			}
			System.out.println();
		}
		System.out.println("\n\n");
	}

	/**
	 * Calculates the next generation and generates it
	 */
	private synchronized static void simulate() {
		boolean[][] current = cellStatus;
		boolean[][] next = new boolean[sizeX][sizeY];
		incrementGenerations();

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				int near = nearCells(x, y);
				if (current[x][y] && near < 2)
					next[x][y] = false;
				else if (current[x][y] && (near == 2 || near == 3))
					next[x][y] = true;
				else if (current[x][y] && near > 3)
					next[x][y] = false;
				else if (!current[x][y] && near == 3)
					next[x][y] = true;
			}
		}
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				if (next[x][y]) {
					spawnCell(x, y);
					updateNeighboursPlus(y, x);
				} else {
					killCell(x, y);
					updateNeighboursMinus(x, y);
				}
			}
		}
	}

	/**
	 * Returns whether the cell indicated by the input parameters is live of
	 * dead
	 * 
	 * @param x
	 *            X location of the cell
	 * @param y
	 *            Y location of the cell
	 * 
	 * @return boolean - true if it is alive, false otherwise
	 */
	private synchronized static boolean isAlive(int x, int y) {
		return cellStatus[x][y];
	}

	/**
	 * Increments the live population
	 */
	private synchronized static void incrementPopulation() {
		population++;
	}

	/**
	 * Decrements the live population
	 */
	private synchronized static void decrementPopulation() {
		population--;
	}

	/**
	 * Returns the current value of the population
	 * 
	 * 
	 * @return int - population
	 */
	private synchronized static int populationValue() {
		return population;
	}

	/**
	 * Increments the current generation value
	 */
	private synchronized static void incrementGenerations() {
		generations++;
	}

	/**
	 * Returns the current generation value
	 * 
	 * 
	 * @return int - current generation
	 */
	private synchronized static int generationsValue() {
		return generations;
	}

	/**
	 * Clears all the cells and makes sure none are left alive
	 */
	private synchronized void clearCells() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				cellNeighbours[i][j] = 0;
				cellStatus[i][j] = false;
			}
		}
	}

	/**
	 * Kills all cells
	 */
	private void killAllCells() {
		for (int i = 0; i < sizeX; i++)
			for (int j = 0; j < sizeY; j++)
				killCell(i, j);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Spanws a known shape on the grid based on the input value <br>
	 * If the input value is -1, generates a random pattern on the grid <br>
	 * Ohter input values generate a known shape
	 * 
	 * @param x
	 *            int
	 */
	private void spawnShape(int x) {
		if (x == -1) {
			System.out.println("\tGenerating random pattern");
			for (int i = 0; i < sizeX; i++)
				for (int j = 0; j < sizeY; j++)
					if (Calculate.randInt(0, 1) == 1)
						spawnCell(i, j);
		}
		switch (x) {
		case 0: // Block
			System.out.println("\tSpawning Block");
			spawnCell(3, 3);
			spawnCell(3, 4);
			spawnCell(4, 3);
			spawnCell(4, 4);
			break;
		case 1: // Beehive
			System.out.println("\tSpawning Beehive");
			spawnCell(3, 3);
			spawnCell(3, 4);
			spawnCell(4, 2);
			spawnCell(4, 5);
			spawnCell(5, 3);
			spawnCell(5, 4);
			break;
		case 2: // Loaf
			System.out.println("\tSpawning Loaf");
			spawnCell(2, 3);
			spawnCell(2, 4);
			spawnCell(3, 2);
			spawnCell(3, 5);
			spawnCell(4, 3);
			spawnCell(4, 5);
			spawnCell(5, 4);
			break;
		case 3: // Boat
			System.out.println("\tSpawning Boat");
			spawnCell(2, 2);
			spawnCell(2, 3);
			spawnCell(3, 2);
			spawnCell(3, 4);
			spawnCell(4, 3);
			break;
		case 4: // Ship
			System.out.println("\tSpawning Ship");
			spawnCell(2, 2);
			spawnCell(2, 3);
			spawnCell(3, 2);
			spawnCell(3, 4);
			spawnCell(4, 3);
			spawnCell(4, 4);
			break;
		case 5: // Blinker
			System.out.println("\tSpawning Blinker");
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			break;
		case 6: // Toad
			System.out.println("\tSpawning Toad");
			spawnCell(3, 3);
			spawnCell(3, 4);
			spawnCell(3, 5);
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			break;
		case 7: // Beacon
			System.out.println("\tSpawning Beacon");
			spawnCell(2, 2);
			spawnCell(2, 3);
			spawnCell(3, 2);
			spawnCell(4, 5);
			spawnCell(5, 4);
			spawnCell(5, 5);
			break;
		case 8: // Glider
			System.out.println("\tSpawning Glider");
			spawnCell(2, 3);
			spawnCell(3, 4);
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			break;
		case 9: // Spaceship
			System.out.println("\tSpawning SpaceShip");
			spawnCell(1, 1);
			spawnCell(1, 4);
			spawnCell(2, 5);
			spawnCell(3, 1);
			spawnCell(3, 5);
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			spawnCell(4, 5);
			break;
		case 10:
			System.out.println("\tSpawning 6-Step Oscillator");
			spawnCell(3, 2);
			spawnCell(3, 3);
			spawnCell(3, 4);
			spawnCell(3, 5);
			spawnCell(4, 1);
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			spawnCell(4, 5);
			spawnCell(4, 6);
			spawnCell(5, 2);
			spawnCell(5, 3);
			spawnCell(5, 4);
			spawnCell(5, 5);
			break;
		case 11:
			System.out.println("\tSpawning 24-Step Flip Oscillator");
			spawnCell(1, 3);
			spawnCell(2, 2);
			spawnCell(2, 4);
			spawnCell(4, 3);
			spawnCell(4, 5);
			spawnCell(5, 4);
			break;
		case 12:
			System.out.println("\tSpawning E");
			spawnCell(1, 3);
			spawnCell(1, 4);
			spawnCell(2, 2);
			spawnCell(2, 3);
			spawnCell(3, 2);
			spawnCell(3, 3);
			spawnCell(3, 4);
			spawnCell(4, 2);
			spawnCell(4, 3);
			spawnCell(4, 4);
			spawnCell(5, 2);
			spawnCell(5, 3);
			spawnCell(6, 3);
			spawnCell(6, 4);
			break;
		}
	}

	/**
	 * Clears the grid and generates either a random pattern or a known shape
	 * 
	 * @param random
	 *            true if the pattern should be random, false if a known shape
	 *            should be spawned
	 */
	private void changeSeed(boolean random) {
		killAllCells();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		generations = 0;
		spawnShape((random) ? -1 : Calculate.randInt(0, knownShapes));
		showCurrentStats();
	}

	/**
	 * Makes a given cell live
	 * 
	 * @param x
	 *            X location of the cell
	 * @param y
	 *            Y location of the cell
	 */
	private synchronized static void spawnCell(int x, int y) {
		int near = nearCells(x, y);
		if (isAlive(x, y))
			return;
		if (DEBUG)
			System.out.println("Spawning cell at " + x + " " + y);
		cellStatus[x][y] = true;
		cellNeighbours[x][y] = near;
		Display.on(x + 1, y, cellColor, cellIntensity);
		incrementPopulation();
	}

	/**
	 * Increments the neighbours count on all adjacent cells
	 * 
	 * @param x
	 *            X location of the middle cell
	 * @param y
	 *            Y location of the middle cell
	 */
	private static void updateNeighboursPlus(int x, int y) {
		cellNeighbours[(x + 7) % sizeX][(y + 7) % sizeY]++;
		cellNeighbours[(x + 7) % sizeX][y]++;
		cellNeighbours[(x + 7) % sizeX][(y + 1) % sizeY]++;
		cellNeighbours[x][(y + 7) % sizeY]++;
		cellNeighbours[x][(y + 1) % sizeY]++;
		cellNeighbours[(x + 1) % sizeX][(y + 7) % sizeY]++;
		cellNeighbours[(x + 1) % sizeX][y]++;
		cellNeighbours[(x + 1) % sizeX][(y + 1) % sizeY]++;
	}

	/**
	 * Kills the given cell
	 * 
	 * @param x
	 *            X location of the cell
	 * @param y
	 *            Y location of the cell
	 */
	private synchronized static void killCell(int x, int y) {
		if (!isAlive(x, y))
			return;
		if (DEBUG)
			System.out.println("Killing cell at " + x + " " + y);
		cellStatus[x][y] = false;
		Display.off(x + 1, y);
		decrementPopulation();
	}

	/**
	 * Decrements the neighbours count on all adjacent cells
	 * 
	 * @param x
	 *            X location of the middle cell
	 * @param y
	 *            Y location of the middle cell
	 */
	private static void updateNeighboursMinus(int x, int y) {
		if (cellNeighbours[(x + 7) % sizeX][(y + 7) % sizeY] > 0)
			cellNeighbours[(x + 7) % sizeX][(y + 7) % sizeY]--;
		if (cellNeighbours[(x + 7) % sizeX][y] > 0)
			cellNeighbours[(x + 7) % sizeX][y]--;
		if (cellNeighbours[(x + 7) % sizeX][(y + 1) % sizeY] > 0)
			cellNeighbours[(x + 7) % sizeX][(y + 1) % sizeY]--;
		if (cellNeighbours[x][(y + 7) % sizeY] > 0)
			cellNeighbours[x][(y + 7) % sizeY]--;
		if (cellNeighbours[x][(y + 1) % sizeY] > 0)
			cellNeighbours[x][(y + 1) % sizeY]--;
		if (cellNeighbours[(x + 1) % sizeX][(y + 7) % sizeY] > 0)
			cellNeighbours[(x + 1) % sizeX][(y + 7) % sizeY]--;
		if (cellNeighbours[(x + 1) % sizeX][y] > 0)
			cellNeighbours[(x + 1) % sizeX][y]--;
		if (cellNeighbours[(x + 1) % sizeX][(y + 1) % sizeY] > 0)
			cellNeighbours[(x + 1) % sizeX][(y + 1) % sizeY]--;
	}

	/**
	 * Returns the number of adjacent live cells
	 * 
	 * @param x
	 *            X location of the cell to check
	 * @param y
	 *            Y location of the cell to check
	 * 
	 * @return int Adjacent cells count
	 */
	private static int nearCells(int x, int y) {
		int count = 0;
		if (cellStatus[(x + 7) % sizeX][(y + 7) % sizeY])
			count++;
		if (cellStatus[(x + 7) % sizeX][y])
			count++;
		if (cellStatus[(x + 7) % sizeX][(y + 1) % sizeY])
			count++;
		if (cellStatus[x][(y + 7) % sizeY])
			count++;
		if (cellStatus[x][(y + 1) % sizeY])
			count++;
		if (cellStatus[(x + 1) % sizeX][(y + 7) % sizeY])
			count++;
		if (cellStatus[(x + 1) % sizeX][y])
			count++;
		if (cellStatus[(x + 1) % sizeX][(y + 1) % sizeY])
			count++;

		return count;
	}

	/**
	 * Turns on all the extra control lights
	 */
	private void lightControls() {
		Display.onThenChange(1, 8, "yellow", "orange", 3, 2, 8 * animSpeed);
		Display.onThenChange(0, 7, "yellow", "red", 3, 1, 8 * animSpeed);
		animationDelay();
		Display.onThenChange(2, 8, "yellow", "orange", 3, 2, 8 * animSpeed);
		Display.on(0, 6, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.on(3, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 5, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.on(4, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 4, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.onThenChange(5, 8, "yellow", "lime", 3, 1, 8 * animSpeed);
		Display.on(0, 3, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.onThenChange(6, 8, "yellow", "lime", 3, 1, 8 * animSpeed);
		Display.on(0, 2, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.on(7, 8, "yellow", 3, 8 * animSpeed);
		Display.on(0, 1, "yellow", 3, 8 * animSpeed);
		animationDelay();
		Display.onThenChange(8, 8, "yellow", "red", 3, 2, 8 * animSpeed);
		Display.on(0, 0, "yellow", 3, 8 * animSpeed);
		animationDelay();
	}

	/**
	 * Returns after a slight delay
	 */
	private void animationDelay() {
		try {
			Thread.sleep(animationDelaySpeed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unused in Game Of Life
	 */
	@Override
	public void saveToDatabase() {
	}

	/**
	 * Unused in Game Of Life
	 */
	@Override
	public void retreiveFromDatabase() {
	}

	/**
	 * Processes input from the Launchpad
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable arg0, Object arg1) {
		if (runningGameName == "Game Of Life") {
			int positionX, positionY;
			if (Input.getStatus()) {
				positionX = Input.getPositionX();
				positionY = Input.getPositionY();

				if (positionX != 0 && positionY != 7) {
					quittingGameQuery = false;
					notQuitting();
				}

				if (positionX == 1 && positionY == 8) {
					if (DEBUG)
						System.out.println("Speeding up simulation");
					Display.on(2, 8, "orange", 2);
					tickLength -= speedIncrement;
					if (tickLength < minTickLength)
						tickLength = minTickLength;
					if (tickLength == minTickLength)
						Display.onThenChange(1, 8, "red", "red", 3, 1, 200);
					else
						Display.onThenChange(1, 8, "yellow", "orange", 3, 2,
								200);
				} else if (positionX == 2 && positionY == 8) {
					if (DEBUG)
						System.out.println("Slowing down simulation");
					Display.on(1, 8, "orange", 2);
					tickLength += speedIncrement;
					if (tickLength > maxTickLength)
						tickLength = maxTickLength;
					if (tickLength == maxTickLength)
						Display.onThenChange(2, 8, "red", "red", 3, 1, 200);
					else
						Display.onThenChange(2, 8, "yellow", "orange", 3, 2,
								200);
				} else if (positionX == 8 && positionY == 8) {
					if (paused) {
						if (DEBUG)
							System.out.println("Resuming simulation");
						Display.onThenChange(8, 8, "yellow", "green", 3, 2, 200);
						paused = false;
					} else {
						if (DEBUG)
							System.out.println("Pausing simulation");
						Display.onThenChange(8, 8, "yellow", "red", 3, 2, 200);
						paused = true;
					}
				} else if (positionX == 5 && positionY == 8) {
					// Spawning a known shape
					if (!paused)
						paused = true;
					Display.on(8, 8, "red", 2);
					Display.onThenChange(5, 8, "yellow", "lime", 3, 2, 200);
					changeSeed(false);
				} else if (positionX == 6 && positionY == 8) {
					// Spawning a random seed
					if (!paused)
						paused = true;
					Display.on(8, 8, "red", 2);
					Display.onThenChange(6, 8, "yellow", "lime", 3, 2, 200);
					changeSeed(true);
				} else if (positionX == 0 && positionY == 7) {
					// Quitting Game
					if (!quittingGameQuery) {
						quittingGameQuery = true;
						Display.blink(0, 7, "red", 2, 400);
						System.out
								.println("Are you sure you want to quit Game of Life?");
					} else {
						quittingGameQuery = false;
						Display.blinkoff(0, 7);
						quittingGame = true;
						return;
					}
				} else if (positionX > 0 && positionY < 8) {
					quittingGameQuery = false;
					int x = positionX - 1;
					int y = positionY;
					if (cellStatus[x][y]) {
						killCell(x, y);
					} else {
						spawnCell(x, y);
					}
				}
			}
		}
	}

	/**
	 * Returns after a given delay value in ms
	 * 
	 * @param value
	 *            Time in MS
	 */
	private static void delay(int value) {
		try {
			Thread.sleep(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method called when quitting has been cancelled
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
	 * Quits the game and closes the GUI
	 */
	public static void quitGame() {
		if (quittingGame) {
			animationDelaySpeed = 0;
			animSpeed = 0;
			System.out.println("Quitting Game of Life.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(300);
			Main.setClosing(false);
			GameOfLifeGUI.quit();
		}
		quittingGame = false; // So it does not run through this method again if
								// the player played a couple of rounds
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		quittingGame = true;
		forceQuitting = true;
		animationDelaySpeed = 0;
		animSpeed = 0;
		System.out.println("Quitting Game of Life.\n");
		setRunningGame("none");
		setRunning(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(300);
		Main.setClosing(false);
	}
}
