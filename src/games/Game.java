package games;

import gui.StatisticsGUI;

import java.util.*;

/**
 * Games superclass
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public abstract class Game implements Observer {
	private static boolean DEBUG = false;
	protected static String runningGameName;
	public static boolean running;
	protected String gameName;
	protected int gameID;
	protected static int difficulty;
	protected static int gamesPlayed = 0;
	protected int sizeX;
	protected int sizeY;

	/**
	 * Implementation should initialize a game
	 */
	abstract public void startGame();

	/**
	 * Implementation should reset a game
	 */
	abstract public void resetGame();

	/**
	 * Implementation should save game details to Database
	 */
	abstract public void saveToDatabase();

	/**
	 * Implementation should retrieve game details from Database
	 */
	abstract public void retreiveFromDatabase();

	/**
	 * Implementation should process Launchpad Input
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	abstract public void update(Observable arg0, Object arg1);

	/**
	 * Sets a flag used to determine whether a game is running or not
	 * 
	 * @param status
	 *            Flag status
	 */
	protected static synchronized void setRunning(boolean status) {
		running = status;
	}

	/**
	 * Returns true if there is a game running
	 * 
	 * 
	 * @return running
	 */
	public static boolean isRunning() {
		return running;
	}

	/**
	 * Sets the name of the game that's running
	 * 
	 * @param name
	 *            Name of the game that's running
	 */
	protected static void setRunningGame(String name) {
		runningGameName = name;
		if (name == "none")
			setRunning(false);
		else
			setRunning(true);
		if (DEBUG)
			System.out.println("Now running " + name);
	}

	/**
	 * Increments the current session's number of games played
	 */
	public static void incrementGamesPlayed() {
		gamesPlayed++;
		StatisticsGUI.setCurrentGamesPlayed(gamesPlayed);
	}

	/**
	 * Displays the total number of games played
	 */
	public static void displayNumberOfGamesPlayed() {
		System.out.println("\nTotal games played: " + gamesPlayed);
		return;
	}

	/**
	 * Returns the number of games played during the last session
	 * 
	 * 
	 * @return Games played
	 */
	public static int getNumberOfGamesPlayed() {
		return gamesPlayed;
	}

	/**
	 * To be used when initiating a game. Disables Launchpad self listen,
	 * disables console display of input and clears all pads that could be still
	 * on
	 */
	public void prepareGame() {
		// Disabling self listening so pressing any random button
		// doesn't turn that pad on.
		// This would otherwise cause the already lit up pad to clear
		// if the user lights it up and releases
		// Do not forget to enableSelfListen() when quitting the game
		launchpad_s_plus.Launchpad.disableSelfListen();
		launchpad_s_plus.Input.disableConsoleEcho();

		// Making sure the pads are all cleared and giving the program
		// some time to make sure all is good before proceeding
		launchpad_s_plus.Display.clearPads();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
