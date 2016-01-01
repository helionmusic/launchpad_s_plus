package games;

import gui.CheckersGUI;
import gui.Main;
import java.util.*;
import launchpad_s_plus.*;

// TODO Finish working on the game. Fix bugs, Fix kings

// TODO Moving backwards for kings does not work. 
// TODO Fix random faster blinking when selecting illegal move
// TODO Finish working on player 1 first then move to player 2

// TODO This was the first game I started working on. No real previous
// programming experience. Have to rewrite it entirely and properly.

/**
 * This class implements the game Checkers (also known as Draughts).
 * 
 * 
 * @author Helion
 * @version $Revision: 0.8 $
 */

/*
 * Description and rules: taken from: https://en.wikipedia.org/wiki/Draughts
 * 
 * Checkers is a strategy board game for two players which involve diagonal
 * moves of uniform game pieces and mandatory captures by jumping over opponent
 * pieces.
 * 
 * Uncrowned pieces (men) move one step diagonally forward, and capture an
 * opponent's piece by moving two consecutive steps in the same line, jumping
 * over the piece on the first step. Multiple opposing pieces may be captured in
 * a single turn provided this is done by successive jumps made by a single
 * piece; the jumps do not need to be in the same line but may "zigzag" (change
 * diagonal direction). Men can capture only forward.
 * 
 * When a man reaches the crownhead or kings row (the farthest row forward), it
 * becomes a king, and is marked by placing an additional piece on top of the
 * first man, and acquires additional powers including the ability to move and
 * capture backwards. As with non-king men, a king may make successive jumps in
 * a single turn provided that each jump captures an opponent man or king.
 */

public class Checkers extends Game implements Observer {
	static boolean DEBUG = true; // For debugging purposes only. Set to true to
									// output
	// everything happening to console
	boolean waitingForInput = false; // If we're not waiting for input, do
										// nothing with the update() function
	boolean hasCaptured = false; // Becomes true whenever a player has captured
									// a soldier during his turn
	int waitingForPlayer = 0; // 0 for none - 1 for player 1 - 2 for player 2
	int[][] checkersStatus = new int[8][8]; // status will be -1 for unreachable
											// slots
											// 0 for non occupied slots
											// 1 for slots occupied by Player 1
											// 2 for slots occupied by Player 2
	private String player1Color = "green"; // Presetting colors
	private String player2Color = "red"; //
	private String emptyColor = "orange"; //
	private String player1KingColor = "yellow";
	private String player2KingColor = "orange";
	private int playerIntensity = 3; // Color intensity
	private int emptyIntensity = 1;
	// Setting amount of remaining pieces for both players
	private int player1Remaining = 12;
	private int player2Remaining = 12;

	// These are for the various animations
	private static int controlAnimSpeed = 50;
	private static int boardColorSpeed = 35;
	private static int playerColorSpeed = 30;
	private static int boardWait = 500;

	// These are booleans used to check if we are resetting or quitting the game
	private static boolean forceQuitting = false;
	private boolean resettingGame = false;
	private boolean quittingGame = false;

	// These are just to relocate the Quit and Reset buttons
	private int quitX = 0;
	private int quitY = 7;
	private int resetX = 0;
	private int resetY = 4;

	// TODO - Maybe add a way to resume game after quitting it?

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            Unused
	 */
	public Checkers(boolean b) {
	}

	/**
	 * Constructor
	 */
	public Checkers() {
		gameID = 1;
		gameName = "Checkers";
	}

	/**
	 * Initiator for the Checkers game.
	 */
	@Override
	public void startGame() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		forceQuitting = false;

		gameName = "Checkers";
		difficulty = 0;
		sizeX = sizeY = 8;
		setRunning(true);
		System.out.println("\nStarting Checkers.");
		resetGame();
	}

	/**
	 * Initializes Launchpad settings and sets initial variables for the game
	 */
	public void resetGame() {
		super.prepareGame();

		// Resetting animation speeds in case a force quit was previously called
		controlAnimSpeed = 50;
		boardColorSpeed = 35;
		playerColorSpeed = 30;
		boardWait = 500;

		// Setting resetting and quitting booleans to false so the game doesn't
		// restart or quit automatically if this was called previously
		resettingGame = false;
		quittingGame = false;

		// Resetting the amount of remaining pieces in case a game reset was
		// previously called
		player1Remaining = 12;
		player2Remaining = 12;

		begin(); // Start the game

		if (resettingGame)
			resetGame();
		quitGame();
	}

	/**
	 * Starts the game. First colors boards and then asks for input from each
	 * player until one loses or the game is reset/quit. When a player wins it
	 * will ask for a rematch. If it is refused, the game quits
	 */
	private void begin() {
		System.out.println("Starting new game of Checkers.");
		incrementGamesPlayed(); // Incrementing the games played counter
		lightControls(); // Light up extra controls for (player 1/2 turn,
		// reset game, quit game)
		colorBoard(); // Coloring the board backdrop and setting the map status
		if (forceQuitting || quittingGame)
			return;
		fillPlayer1(); // Coloring Player 1 (green) slots and setting map status
		fillPlayer2(); // Coloring Player 2 (red) slots and setting map status
		if (forceQuitting || quittingGame)
			return;

		// Looping the game until one player runs out of pieces, or until a
		// reset or quit has been called
		while (player1Remaining != 0 && player2Remaining != 0) {
			if (forceQuitting)
				return;

			if (quittingGame || resettingGame)
				break;
			inPlayer1(); // Player 1 input. Pad [9][9] blinks green
			if (forceQuitting)
				return;
			if (!quittingGame && !resettingGame)
				displayRemaining();
			if (!quittingGame && !resettingGame)
				recolorBoard(); // Just in case something broke and the colors
								// are off
			if (player2Remaining == 0 || player1Remaining == 0)
				break;
			inPlayer2(); // Player 2 input. Pad [1][9] blinks green
			if (forceQuitting)
				return;
			if (!quittingGame && !resettingGame)
				displayRemaining();
			if (!quittingGame && !resettingGame)
				recolorBoard();
		}

		// At this point, one of the players has lost. Checking which one
		if (player1Remaining == 0) {
			playerwon(2);
			System.out.println("Player 2 wins");
			restartGameQuery();
		} else if (player2Remaining == 0) {
			playerwon(1);
			System.out.println("Player 1 wins");
			restartGameQuery();
		}

		// Game is over. If the player has accepted to play again, this method
		// will complete and the initial resetGame() will call itself again

		if (DEBUG)
			displayStatusOnConsole(); // Just to check the board on the console.
										// Status shows all slots and if they're
										// occupied or not

		// Disabling all the blinking lights before returning. This saves for
		// many many headaches later on
		disablelightControls();
	}

	/**
	 * Turns on the extra 'settings' lights. [0][0] is used to quit the game
	 * [0][1] is used to reset the game [1][8] is used to show if player 2 needs
	 * to play [8][8] is used to show if player 1 needs to play
	 */
	private void lightControls() {
		if (forceQuitting)
			return;

		int times = 4;

		Display.onThenChange(quitX, quitY, "green", "red", 3, 1,
				controlAnimSpeed * times);
		Display.onThenChange(1, 8, "green", "red", 3, 1, controlAnimSpeed
				* times);
		delay(controlAnimSpeed);
		Display.on(0, 6, "green", 3, controlAnimSpeed * times);
		Display.on(2, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(0, 5, "green", 3, controlAnimSpeed * times);
		Display.on(3, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(resetX, resetY, emptyColor, 1);
		Display.on(4, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(0, 3, "green", 3, controlAnimSpeed * times);
		Display.on(5, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(0, 2, "green", 3, controlAnimSpeed * times);
		Display.on(6, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(0, 1, "green", 3, controlAnimSpeed * times);
		Display.on(7, 8, "green", 3, controlAnimSpeed * times);
		delay(controlAnimSpeed);
		Display.on(0, 0, "green", 3, controlAnimSpeed * times);
		Display.on(8, 8, "red", 1);
		delay(controlAnimSpeed);
	}

	/**
	 * Returns after a given delay value in ms
	 * 
	 * @param value
	 *            delay duration
	 */
	private static void delay(int value) {
		try {
			Thread.sleep(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disables all blinking control lights
	 */
	public void disablelightControls() {
		Display.blinkoff(quitX, quitY);
		Display.blinkoff(resetX, resetY);
		Display.blinkoff(1, 8);
		Display.blinkoff(8, 8);
		Display.blinkoff(0, 5);
		Display.blinkoff(0, 6);
	}

	// TODO finish with kings

	/**
	 * Asks player 1 to play.
	 */
	private void inPlayer1() {
		hasCaptured = false;
		waitingForInput = true;
		waitingForPlayer = 1;
		delay(50);
		Display.blink(8, 8, "green", 2, 500);
		Input.clearPressed();

		int lastX = -2;
		int lastY = -2;
		// First we have to check if player 1 is choosing his own piece
		while (!checkPlayer(lastX, lastY, 1)) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (DEBUG)
				System.out.println("Waiting for Player 1 input");
			Input.waitForInput();
			lastX = Input.getLastKeyI() - 1;
			lastY = Input.getLastKeyJ();
			if (DEBUG)
				System.out.println("Received input from Player 1: " + lastX
						+ " " + lastY);
		}

		// Then we show legal moves
		if (checkersStatus[lastX][lastY] == 1)
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], true);
		int movetoX = 5;
		int movetoY = 5;
		// Then we have to get the piece to move if the player has selected a
		// legal slot
		while (checkersStatus[movetoX][movetoY] != 0) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (movetoX == lastX && movetoY == lastY) {
				showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY],
						false);
				break;
			}
			System.out.println("Waiting for Player 1 to move piece");
			Input.waitForInput();
			movetoX = Input.getLastKeyI() - 1;
			movetoY = Input.getLastKeyJ();
			delay(50);
			if (outOfBounds(movetoX, movetoY)
					&& isCommand(movetoX + 1, movetoY)) {
				if (movetoX == quitX - 1 && movetoY == quitY)
					quitGameQuery();
				else if (movetoX == resetX - 1 && movetoY == resetY)
					resetGameQuery();
				movetoX = lastX;
				movetoY = lastY;
				break;
			}
			if (DEBUG)
				System.out.println("Received input from Player 1: " + movetoX
						+ " " + movetoY);
		}
		if (movetoX == lastX && movetoY == lastY) {
			if (!resettingGame && !quittingGame)
				System.out
						.println("Player 1 cancelled move. Select piece again");
			disablelightControls();
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			inPlayer1();
		} else if (!isLegalMove(movetoX, movetoY, lastX, lastY,
				checkersStatus[lastX][lastY])) {
			System.out.println("Player 1 illegal move. Select piece again");
			disablelightControls();
			delay(50);
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			inPlayer1();
		} else {
			if (!isKing(lastX, lastY)) {
				if (movetoX - lastX == 2 || movetoX - lastX == -2) {
					if (DEBUG)
						System.out.println("Legal move. Moving and capturing");
					capture((movetoX + lastX) / 2, (movetoY + lastY) / 2, 2);
					movePlayerTo(movetoX, movetoY, lastX, lastY,
							(movetoX + lastX) / 2, (movetoY + lastY) / 2, 1,
							checkersStatus[lastX][lastY] == 3, true);
					showLegalMoves(movetoX, movetoY,
							checkersStatus[lastX][lastY], false); // to stop the
					// blinking. Notice
					// the false in the
					// parameters
					Display.on(movetoX + 1, movetoY, player1Color,
							playerIntensity);
					if (hasCaptured) {
						while (canCapture(movetoX, movetoY, 1)) {
							continueCapture1(movetoX, movetoY);
						}
					}
				} else {
					if (DEBUG)
						System.out.println("Legal move. Moving");
					movePlayerTo(movetoX, movetoY, lastX, lastY, 0, 0, 1,
							checkersStatus[lastX][lastY] == 3, false);
				}
			} else {
				System.out.println("dfghjkklhj");
			}
		}
		if (quittingGame || forceQuitting)
			return;
		if (resettingGame)
			return;

		waitingForInput = false;
		waitingForPlayer = 0;
		Display.blinkoff(8, 8);
		delay(200);
		Display.on(8, 8, "red", 1);

		if (DEBUG)
			displayStatusOnConsole();

		if (hasCaptured) {
			while (canCapture(movetoX, movetoY, 1)) {
				inPlayer1();
			}
		}
	}

	/**
	 * Method called when player 1 can capture multiple another piece
	 * immediately after playing
	 * 
	 * @param lastX
	 *            X location of the player 1's piece
	 * @param lastY
	 *            Y location of the player 1s piece
	 */
	private void continueCapture1(int lastX, int lastY) {
		waitingForInput = true;
		waitingForPlayer = 1;
		Display.blink(8, 8, "green", 2, 500);
		Input.clearPressed();

		showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], true);
		int movetoX = 5;
		int movetoY = 5;
		// We have to get the piece to move if the player has selected a
		// legal slot
		while (checkersStatus[movetoX][movetoY] != 0) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (movetoX == lastX && movetoY == lastY) {
				showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY],
						false);
				break;
			}
			System.out.println("Waiting for Player 1 to move piece");
			Input.waitForInput();
			movetoX = Input.getLastKeyI() - 1;
			movetoY = Input.getLastKeyJ();
			delay(50);
			if (outOfBounds(movetoX, movetoY)
					&& isCommand(movetoX + 1, movetoY)) {
				if (movetoX == quitX - 1 && movetoY == quitY)
					quitGameQuery();
				else if (movetoX == resetX - 1 && movetoY == resetY)
					resetGameQuery();
				movetoX = lastX;
				movetoY = lastY;
				break;
			}
			if (DEBUG)
				System.out.println("Received input from Player 1: " + movetoX
						+ " " + movetoY);
		}
		if (movetoX == lastX && movetoY == lastY) {
			if (!resettingGame && !quittingGame)
				System.out
						.println("Player 1 cancelled move. Select piece again");
			disablelightControls();
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			continueCapture1(lastX, lastY);
		} else if (!isLegalMove(movetoX, movetoY, lastX, lastY, 1)) {
			System.out.println("Player 1 illegal move. Select piece again");
			disablelightControls();
			delay(50);
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			continueCapture1(lastX, lastY);
		} else {
			if (movetoX - lastX == 2 || movetoX - lastX == -2) {
				if (DEBUG)
					System.out.println("Legal move. Moving and capturing");
				capture((movetoX + lastX) / 2, (movetoY + lastY) / 2, 2);
				movePlayerTo(movetoX, movetoY, lastX, lastY,
						(movetoX + lastX) / 2, (movetoY + lastY) / 2, 1,
						checkersStatus[lastX][lastY] == 3, true);
				showLegalMoves(movetoX, movetoY, checkersStatus[lastX][lastY],
						false); // to stop the
				// blinking. Notice
				// the false in the
				// parameters
				Display.on(movetoX + 1, movetoY, player1Color, playerIntensity);
				if (hasCaptured) {
					while (canCapture(movetoX, movetoY, 1)) {
						continueCapture1(movetoX, movetoY);
					}
				}
			} else {
				if (DEBUG)
					System.out.println("Legal move. Moving");
				movePlayerTo(movetoX, movetoY, lastX, lastY, 0, 0, 1,
						checkersStatus[lastX][lastY] == 3, false);
			}
		}
		if (quittingGame || forceQuitting)
			return;
		if (resettingGame)
			return;

		waitingForInput = false;
		waitingForPlayer = 0;
	}

	/**
	 * Returns true if the pad located by the given parameters is a Command pad,
	 * meaning not part of the game area
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * 
	 * @return True if the pad is a Command pad, False otherwise
	 */
	private boolean isCommand(int x, int y) {
		if (x == quitX && y == quitY)
			return true;
		else if (x == resetX && y == resetY)
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the pad located by the given parameters is not in the
	 * game area
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * 
	 * @return True if the pad is in the game area, False otherwise
	 */
	private boolean outOfBounds(int x, int y) {
		if (x < 0 || x > 7)
			return true;
		else if (y < 0 || y > 7)
			return true;
		else
			return false;
	}

	// TODO work on all this in inPlayer1() and adapt afterwards:
	// TODO finish with kings

	/**
	 * Asks player 2 to play
	 */
	private void inPlayer2() {
		waitingForInput = true;
		waitingForPlayer = 2;
		Display.blink(1, 8, "green", 2, 500);
		Input.clearPressed();

		int lastX = -2;
		int lastY = -2;
		// First we have to check if player 1 is choosing his own piece
		while (!checkPlayer(lastX, lastY, 2)) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (DEBUG)
				System.out.println("Waiting for Player 2 input");
			Input.waitForInput();
			lastX = Input.getLastKeyI() - 1;
			lastY = Input.getLastKeyJ();
			if (DEBUG)
				System.out.println("Received input from Player 2: " + lastX
						+ " " + lastY);
		}
		// Then we show legal moves
		showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], true);
		int movetoX = 5;
		int movetoY = 5;
		// Then we have to get the piece to move if the player has selected a
		// legal slot
		while (checkersStatus[movetoX][movetoY] != 0) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (movetoX == lastX && movetoY == lastY) {
				showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY],
						false);
				break;
			}
			System.out.println("Waiting for Player 2 to move piece");
			Input.waitForInput();
			movetoX = Input.getLastKeyI() - 1;
			movetoY = Input.getLastKeyJ();
			delay(50);
			if (outOfBounds(movetoX, movetoY)
					&& isCommand(movetoX + 1, movetoY)) {
				if (movetoX == quitX - 1 && movetoY == quitY)
					quitGameQuery();
				else if (movetoX == resetX - 1 && movetoY == resetY)
					resetGameQuery();
				movetoX = lastX;
				movetoY = lastY;
				break;
			}
			if (DEBUG)
				System.out.println("Received input from Player 2: " + movetoX
						+ " " + movetoY);
		}
		if (movetoX == lastX && movetoY == lastY) {
			if (!resettingGame && !quittingGame)
				System.out
						.println("Player 2 cancelled move. Select piece again");
			disablelightControls();
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			inPlayer2();
		} else if (!isLegalMove(movetoX, movetoY, lastX, lastY, 2)) {
			System.out.println("Player 2 illegal move. Select piece again");
			disablelightControls();
			delay(50);
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			inPlayer2();
		} else {
			if (movetoX - lastX == 2 || movetoX - lastX == -2) {
				if (DEBUG)
					System.out.println("Legal move. Moving and capturing");
				capture((movetoX + lastX) / 2, (movetoY + lastY) / 2, 1);
				movePlayerTo(movetoX, movetoY, lastX, lastY,
						(movetoX + lastX) / 2, (movetoY + lastY) / 2, 2,
						checkersStatus[lastX][lastY] == 4, true);
				showLegalMoves(movetoX, movetoY, checkersStatus[lastX][lastY],
						false); // to stop the
				// blinking. Notice
				// the false in the
				// parameters
				Display.on(movetoX + 1, movetoY, player2Color, playerIntensity);
				if (hasCaptured) {
					while (canCapture(movetoX, movetoY, 2)) {
						continueCapture2(movetoX, movetoY);
					}
				}
			} else {
				if (DEBUG)
					System.out.println("Legal move. Moving");
				movePlayerTo(movetoX, movetoY, lastX, lastY, 0, 0, 2,
						checkersStatus[lastX][lastY] == 4, false);
			}
		}
		if (quittingGame || forceQuitting)
			return;
		if (resettingGame)
			return;

		waitingForInput = false;
		waitingForPlayer = 0;
		Display.blinkoff(1, 8);
		delay(200);
		Display.on(1, 8, "red", 1);

		if (DEBUG)
			displayStatusOnConsole();

		if (hasCaptured) {
			while (canCapture(movetoX, movetoY, 2)) {
				inPlayer2();
			}
		}
	}

	/**
	 * Method called when player 2 can capture multiple another piece
	 * immediately after playing
	 * 
	 * @param lastX
	 *            X location of the player 2's piece
	 * @param lastY
	 *            Y location of the player 2's piece
	 */
	private void continueCapture2(int lastX, int lastY) {
		waitingForInput = true;
		waitingForPlayer = 2;
		Display.blink(1, 8, "green", 2, 500);
		Input.clearPressed();

		showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], true);
		int movetoX = 5;
		int movetoY = 5;
		// We have to get the piece to move if the player has selected a
		// legal slot
		while (checkersStatus[movetoX][movetoY] != 0) {
			if (quittingGame || forceQuitting)
				return;
			if (resettingGame)
				return;
			if (movetoX == lastX && movetoY == lastY) {
				showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY],
						false);
				break;
			}
			System.out.println("Waiting for Player 2 to move piece");
			Input.waitForInput();
			movetoX = Input.getLastKeyI() - 1;
			movetoY = Input.getLastKeyJ();
			delay(50);
			if (outOfBounds(movetoX, movetoY)
					&& isCommand(movetoX + 1, movetoY)) {
				if (movetoX == quitX - 1 && movetoY == quitY)
					quitGameQuery();
				else if (movetoX == resetX - 1 && movetoY == resetY)
					resetGameQuery();
				movetoX = lastX;
				movetoY = lastY;
				break;
			}
			if (DEBUG)
				System.out.println("Received input from Player 2: " + movetoX
						+ " " + movetoY);
		}
		if (movetoX == lastX && movetoY == lastY) {
			if (!resettingGame && !quittingGame)
				System.out
						.println("Player 2 cancelled move. Select piece again");
			disablelightControls();
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			continueCapture2(lastX, lastY);
		} else if (!isLegalMove(movetoX, movetoY, lastX, lastY, 2)) {
			System.out.println("Player 2 illegal move. Select piece again");
			disablelightControls();
			delay(50);
			showLegalMoves(lastX, lastY, checkersStatus[lastX][lastY], false);
			continueCapture2(lastX, lastY);
		} else {
			if (movetoX - lastX == 2 || movetoX - lastX == -2) {
				if (DEBUG)
					System.out.println("Legal move. Moving and capturing");
				capture((movetoX + lastX) / 2, (movetoY + lastY) / 2, 1);
				movePlayerTo(movetoX, movetoY, lastX, lastY,
						(movetoX + lastX) / 2, (movetoY + lastY) / 2, 2,
						checkersStatus[lastX][lastY] == 4, true);
				showLegalMoves(movetoX, movetoY, checkersStatus[lastX][lastY],
						false); // to stop the
				// blinking. Notice
				// the false in the
				// parameters
				Display.on(movetoX + 1, movetoY, player2Color, playerIntensity);
				if (hasCaptured) {
					while (canCapture(movetoX, movetoY, 2)) {
						continueCapture2(movetoX, movetoY);
					}
				}
			} else {
				if (DEBUG)
					System.out.println("Legal move. Moving");
				movePlayerTo(movetoX, movetoY, lastX, lastY, 0, 0, 2,
						checkersStatus[lastX][lastY] == 4, false);
			}
		}
		if (quittingGame || forceQuitting)
			return;
		if (resettingGame)
			return;

		waitingForInput = false;
		waitingForPlayer = 0;
	}

	/**
	 * Used to determine whether a player is able to capture something.
	 * 
	 * @param fromX
	 *            Piece position X
	 * @param fromY
	 *            Piece position Y
	 * @param player
	 *            player 1 or 2 playing
	 * 
	 * @return true if player can capture something by using this piece, false
	 *         otherwise
	 */
	private boolean canCapture(int fromX, int fromY, int player) {
		switch (player) {
		case 1:
			if (fromX < 2 || fromX > 5)
				return false;
			if (checkersStatus[fromX][fromY] == 1) {
				// Piece selected is a normal Player 1 piece

				if (checkersStatus[fromX - 1][fromY - 1] == 2) {
					if (checkersStatus[fromX - 2][fromY - 2] == 0)
						return true;
				} else if (checkersStatus[fromX - 1][fromY + 1] == 2) {
					if (checkersStatus[fromX - 2][fromY + 2] == 0)
						return true;
				} else {
					return false;
				}
			} else if (checkersStatus[fromX][fromY] == 3) {
				for (int i = 0; fromX - i > 1; i++) {
					if (checkersStatus[fromX - i][fromY - i] == 2
							|| checkersStatus[fromX - i][fromY - i] == 4) {
						return (checkersStatus[fromX - i - 1][fromY - i - 1] == 0);
					} else if (checkersStatus[fromX - i][fromY + i] == 2
							|| checkersStatus[fromX - i][fromY + i] == 4) {
						return (checkersStatus[fromX - i - 1][fromY + i + 1] == 0);
					} else if (checkersStatus[fromX + i][fromY - i] == 2
							|| checkersStatus[fromX + i][fromY - i] == 4) {
						return (checkersStatus[fromX + i + 1][fromY - i - 1] == 0);
					} else if (checkersStatus[fromX + i][fromY + i] == 2
							|| checkersStatus[fromX + i][fromY + i] == 4) {
						return (checkersStatus[fromX + i + 1][fromY + i + 1] == 0);
					}
				}
			}
			break;
		case 2:
			if (fromX < 2 || fromX > 5)
				return false;
			if (checkersStatus[fromX][fromY] == 2) {
				if (checkersStatus[fromX + 1][fromY - 1] == 1) {
					if (checkersStatus[fromX + 2][fromY - 2] == 0)
						return true;
				} else if (checkersStatus[fromX + 1][fromY + 1] == 1) {
					return (checkersStatus[fromX + 2][fromY + 2] == 0);
				}
			} else if (checkersStatus[fromX][fromY] == 4) {
				for (int i = 0; fromX - i > 1; i++) {
					if (checkersStatus[fromX - i][fromY - i] == 1
							|| checkersStatus[fromX - i][fromY - i] == 3) {
						return (checkersStatus[fromX - i - 1][fromY - i - 1] == 0);
					} else if (checkersStatus[fromX - i][fromY + i] == 1
							|| checkersStatus[fromX - i][fromY + i] == 3) {
						return (checkersStatus[fromX - i - 1][fromY + i + 1] == 0);
					} else if (checkersStatus[fromX + i][fromY - i] == 1
							|| checkersStatus[fromX + i][fromY - i] == 3) {
						return (checkersStatus[fromX + i + 1][fromY - i - 1] == 0);
					} else if (checkersStatus[fromX + i][fromY + i] == 1
							|| checkersStatus[fromX + i][fromY + i] == 3) {
						return (checkersStatus[fromX + i + 1][fromY + i + 1] == 0);
					}
				}
			}
			break;

		}
		return false;
	}

	/**
	 * Captures the piece at the position determined by the input parameters
	 * 
	 * @param x
	 *            X position of the piece
	 * @param y
	 *            Y position of the piece
	 * @param capturedplayer
	 *            player who's piece has been captured
	 */
	private void capture(int x, int y, int capturedplayer) {
		hasCaptured = true;
		checkersStatus[x][y] = 0;
		Display.on(x + 1, y, emptyColor, emptyIntensity);
		if (capturedplayer == 1)
			player1Remaining--;
		else
			player2Remaining--;

	}

	/**
	 * Used to check whether the selected pad contains the designated player's
	 * piece
	 * 
	 * @param lastX
	 *            Last X pressed
	 * @param lastY
	 *            Last Y pressed
	 * @param player
	 *            Current player
	 * 
	 * @return true if it is
	 */
	private boolean checkPlayer(int lastX, int lastY, int player) {
		if (lastX == quitX - 1 && lastY == quitY) {
			quitGameQuery();
			if (quittingGame)
				return false;
		} else if (lastX == resetX - 1 && lastY == resetY) {
			resetGameQuery();
			if (resettingGame)
				return false;
		} else if (DEBUG) {
			if (lastX == 3 && lastY == 8) {
				playerwon(1);
			} else if (lastX == 2 && lastY == 8) {
				playerwon(2);
			}
		}
		if (lastX < 0 || lastY < 0 || lastX > 7 || lastY > 7)
			return false;
		switch (player) {
		case 1:
			if (checkersStatus[lastX][lastY] == 1)
				return true;
			else if (checkersStatus[lastX][lastY] == 3)
				return true;
			break;
		case 2:
			if (checkersStatus[lastX][lastY] == 2)
				return true;
			else if (checkersStatus[lastX][lastY] == 4)
				return true;
			break;
		}
		return false;
	}

	/**
	 * Move selected player's piece to designated location based on input
	 * parameters
	 * 
	 * @param movetoX
	 *            X location going to
	 * @param movetoY
	 *            Y location going to
	 * @param movefromX
	 *            X location coming from
	 * @param movefromY
	 *            Y location coming from
	 * @param capturedX
	 *            X location of captured piece if any
	 * @param capturedY
	 *            Y location of captured piece if any
	 * @param player
	 *            The player making the move
	 * @param isKing
	 *            True if the piece moved is a King
	 * @param hasCaptured
	 *            True if the player has captured a piece
	 */
	private void movePlayerTo(int movetoX, int movetoY, int movefromX,
			int movefromY, int capturedX, int capturedY, int player,
			boolean isKing, boolean hasCaptured) {
		showLegalMoves(movefromX, movefromY, player, false);

		System.out.println("Moving player " + player + " piece from "
				+ movefromX + " " + movefromY + " to " + movetoX + " "
				+ movetoY);
		int otherplayer;
		if (player == 1)
			otherplayer = 2;
		else
			otherplayer = 1;
		if (hasCaptured)
			System.out.println("Player " + otherplayer
					+ " piece captured from " + capturedX + " " + capturedY);

		switch (player) {
		case 1:
			if (isKing) {
				setChecker(movetoX, movetoY, 3);
			} else {
				if (movetoX == 0)
					setChecker(movetoX, movetoY, 3);
				else
					setChecker(movetoX, movetoY, 1);
			}
			setChecker(movefromX, movefromY, 0);
			break;
		case 2:
			if (isKing)
				setChecker(movetoX, movetoY, 4);
			else {
				if (movetoX == 7)
					setChecker(movetoX, movetoY, 4);
				else
					setChecker(movetoX, movetoY, 2);
			}
			setChecker(movefromX, movefromY, 0);
		}
	}

	/**
	 * Turns on the pad at X and Y given and sets the internal 2D array to the
	 * value (representing the piece and player) given
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * @param value
	 *            piece number. 1 and 3 for Player 1 pieces, 2 and 4 for Player
	 *            2
	 */
	private void setChecker(int x, int y, int value) {
		switch (value) {
		case 0:
			checkersStatus[x][y] = value;
			Display.blinkoff(x + 1, y);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Display.on(x + 1, y, emptyColor, emptyIntensity);
			break;
		case 1:
			checkersStatus[x][y] = value;
			Display.on(x + 1, y, player1Color, playerIntensity);
			break;
		case 3:
			checkersStatus[x][y] = value;
			// Display.blink(x + 1, y, player1KingColor, "yellow",
			// playerIntensity, playerIntensity, 300);
			Display.on(x + 1, y, player1KingColor, playerIntensity);
			break;
		case 2:
			checkersStatus[x][y] = value;
			Display.on(x + 1, y, player2Color, playerIntensity);
			break;
		case 4:
			checkersStatus[x][y] = value;
			// Display.blink(x + 1, y, player2KingColor, "orange",
			// playerIntensity, playerIntensity, 300);
			Display.on(x + 1, y, player2KingColor, playerIntensity);
			break;
		default:
			return;
		}
	}

	/**
	 * Shows all legal moves from the X and Y location given for the player
	 * given. If showoroff is set to false, it will stop blinking all legal
	 * moves available to that pad
	 * 
	 * @param x
	 *            X location of the selected piece
	 * @param y
	 *            Y location of the selected piece
	 * @param player
	 *            Player selecting
	 * @param showoroff
	 *            True if the Legal moves are to be shown, False otherwise
	 */
	private void showLegalMoves(int x, int y, int player, boolean showoroff) {
		if (DEBUG && showoroff)
			System.out.println("Showing legal moves for player "
					+ ((player > 2) ? (player - 2) + "-King" : player)
					+ " - From position " + x + " " + y);

		if (showoroff) {
			if (player == 1 || player == 2)
				Display.blink(x + 1, y, ((player == 1) ? player1Color
						: player2Color), 3, 300);
			else if (player == 3 || player == 4)
				Display.blink(x + 1, y, ((player == 1) ? player1KingColor
						: player2KingColor), 3, 300);
		} else {
			Display.blinkoff(x + 1, y);
			delay(200);
			if (player == 1 || player == 3) {
				if (isKing(x, y))
					setChecker(x, y, 3);
				else
					setChecker(x, y, 1);
			} else if (player == 2 || player == 4) {
				if (isKing(x, y))
					setChecker(x, y, 4);
				else
					setChecker(x, y, 2);
			}
		}

		switch (player) {
		case 1:
			switch (checkersStatus[x][y]) {
			case 1:
				if (isLegalMove(x - 1, y + 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Legal move to " + (x - 1) + " "
								+ (y + 1));
					x++; // Incrementing because the board is shifted down for
							// the gaming area
							// Gaming area is from [x=1][y=0] to [x=8][y=7]
					if (showoroff)
						Display.blink(x - 1, y + 1, ((player == 1) ? "green"
								: "red"), 1, 300);
					else {
						Display.blinkoff(x - 1, y + 1);
						delay(300);
						Display.on(x - 1, y + 1, emptyColor, emptyIntensity);
					}
					x--;
				} else if (isEnemy(x - 1, y + 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Enemy at " + (x - 1) + " "
								+ (y + 1));
					if (isLegalMove(x - 2, y + 2, player)) {
						if (DEBUG && showoroff)
							System.out.println("Legal move to " + (x - 2) + " "
									+ (y + 2));
						x++; // Incrementing because the board is shifted down
								// for the gaming area
						if (showoroff) {
							Display.blink(x - 2, y + 2, "green",
									emptyIntensity, 300);
							Display.blink(x - 1, y + 1, "red", playerIntensity,
									300);
						} else {
							Display.blinkoff(x - 2, y + 2);
							Display.blinkoff(x - 1, y + 1);
							delay(400);
							Display.on(x - 2, y + 2, emptyColor, emptyIntensity);
							Display.on(x - 1, y + 1, player2Color,
									playerIntensity);
						}
						x--;
					}
				}

				if (isLegalMove(x - 1, y - 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Legal move to " + (x - 1) + " "
								+ (y - 1));
					x++; // Incrementing because the board is shifted down for
							// the gaming area
					if (showoroff)
						Display.blink(x - 1, y - 1, "green", emptyIntensity,
								300);
					else {
						Display.blinkoff(x - 1, y - 1);
						try {
							Thread.sleep(200);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Display.on(x - 1, y - 1, emptyColor, emptyIntensity);
					}
					x--;
				} else if (isEnemy(x - 1, y - 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Enemy at " + (x - 1) + " "
								+ (y - 1));
					if (isLegalMove(x - 2, y - 2, player)) {
						if (DEBUG && showoroff)
							System.out.println("Legal move to " + (x - 2) + " "
									+ (y - 2));
						x++; // Incrementing because the board is shifted down
								// for the gaming area
						if (showoroff) {
							Display.blink(x - 2, y - 2, player1Color,
									emptyIntensity, 300);
							Display.blink(x - 1, y - 1, player2Color,
									playerIntensity, 300);
						} else {
							Display.blinkoff(x - 2, y - 2);
							Display.blinkoff(x - 1, y - 1);
							try {
								Thread.sleep(200);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Display.on(x - 2, y - 2, emptyColor, emptyIntensity);
							Display.on(x - 1, y - 1, player2Color,
									playerIntensity);
						}
						x--;
					}
				}
				break;
			case 3:
				if (DEBUG && showoroff)
					System.out.println("Player 1 chose a king piece");
				break;
			}
			break;

		case 2:
			switch (checkersStatus[x][y]) {
			case 2:

				if (isLegalMove(x + 1, y + 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Legal move to " + (x + 1) + " "
								+ (y + 1));
					x++; // Incrementing because the board is shifted down for
							// the gaming area
							// Gaming area is from [x=1][y=0] to [x=8][y=7]
					if (showoroff)
						Display.blink(x + 1, y + 1, player2Color,
								emptyIntensity, 300);
					else {
						Display.blinkoff(x + 1, y + 1);
						try {
							Thread.sleep(200);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Display.on(x + 1, y + 1, emptyColor, emptyIntensity);
					}
					x--;
				} else if (isEnemy(x + 1, y + 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Enemy at " + (x + 1) + " "
								+ (y + 1));
					if (isLegalMove(x + 2, y + 2, player)) {
						if (DEBUG && showoroff)
							System.out.println("Legal move to " + (x + 2) + " "
									+ (y + 2));
						x++; // Incrementing because the board is shifted down
								// for the gaming area
						if (showoroff) {
							Display.blink(x + 2, y + 2, player2Color,
									emptyIntensity, 300);
							Display.blink(x + 1, y + 1, player1Color,
									playerIntensity, 300);
						} else {
							Display.blinkoff(x + 2, y + 2);
							Display.blinkoff(x + 1, y + 1);
							try {
								Thread.sleep(200);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Display.on(x + 2, y + 2, emptyColor, emptyIntensity);
							Display.on(x + 1, y + 1, player1Color,
									playerIntensity);
						}
						x--;
					}
				}

				if (isLegalMove(x + 1, y - 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Legal move to " + (x + 1) + " "
								+ (y - 1));
					x++; // Incrementing because the board is shifted down for
							// the gaming area
					if (showoroff)
						Display.blink(x + 1, y - 1, ((player == 1) ? "green"
								: "red"), 1, 300);
					else {
						Display.blinkoff(x + 1, y - 1);
						try {
							Thread.sleep(200);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Display.on(x + 1, y - 1, emptyColor, emptyIntensity);
					}
					x--;
				} else if (isEnemy(x + 1, y - 1, player)) {
					if (DEBUG && showoroff)
						System.out.println("Enemy at " + (x + 1) + " "
								+ (y - 1));
					if (isLegalMove(x + 2, y - 2, player)) {
						if (DEBUG && showoroff)
							System.out.println("Legal move to " + (x + 2) + " "
									+ (y - 2));
						x++; // Incrementing because the board is shifted down
								// for the gaming area
						if (showoroff) {
							Display.blink(x + 2, y - 2, player2Color,
									emptyIntensity, 300);
							Display.blink(x + 1, y - 1, player1Color,
									playerIntensity, 300);
						} else {
							Display.blinkoff(x + 2, y - 2);
							Display.blinkoff(x + 1, y - 1);
							try {
								Thread.sleep(200);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Display.on(x + 2, y - 2, emptyColor, emptyIntensity);
							Display.on(x + 1, y - 1, player1Color,
									playerIntensity);
						}
						x--;
					}
				}
				break;
			case 4:
				if (DEBUG && showoroff)
					System.out.println("Player 2 chose a king");
				break;
			}
		}
	}

	/**
	 * Returns true if the piece located by the given parameters is a King piece
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * 
	 * @return True if the piece is King, false otherwise
	 */
	private boolean isKing(int x, int y) {
		return (checkersStatus[x][y] == 3 || checkersStatus[x][y] == 4);
	}

	/**
	 * Returns true if the pad at X and Y locations given is enemy to the player
	 * given
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * @param player
	 *            Player we're checking for
	 * 
	 * @return true if the [x][y] is a piece of the other player
	 */
	private boolean isEnemy(int x, int y, int player) {
		if (x > 7 || x < 0)
			return false;
		else if (y > 7 || y < 0)
			return false;
		boolean enemy;
		switch (player) {
		case 1:
			if (checkersStatus[x][y] == 2 || checkersStatus[x][y] == 4)
				enemy = true;
			else
				enemy = false;
			break;
		case 2:
			if (checkersStatus[x][y] == 1 || checkersStatus[x][y] == 3)
				enemy = true;
			else
				enemy = false;
			break;
		default:
			enemy = false;
		}
		return enemy;
	}

	/**
	 * Checks if the location [x][y] is an empty slot and returns true if it is
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * @param player
	 *            Player we're checking for
	 * 
	 * @return true if the pad is empty
	 */
	private boolean isLegalMove(int x, int y, int player) {
		if (x > 7 || x < 0)
			return false;
		else if (y > 7 || y < 0)
			return false;
		return (checkersStatus[x][y] == 0);
	}

	/**
	 * Checks if the player is allowed to move to the designated location from
	 * his initial location.
	 * 
	 * @param movetoX
	 *            X location going to
	 * @param movetoY
	 *            Y location going to
	 * @param lastX
	 *            X location coming from
	 * @param lastY
	 *            Y location coming from
	 * @param player
	 *            Player attempting the move
	 * 
	 * @return True if the move is Legal, false othewise
	 */
	private boolean isLegalMove(int movetoX, int movetoY, int lastX, int lastY,
			int player) {
		if (movetoX > 7 || movetoX < 0)
			return false;
		else if (movetoY > 7 || movetoY < 0)
			return false;

		int x = movetoX;
		int y = movetoY;

		switch (player) {
		case 1:
			if (lastX > movetoX) {
				if ((movetoX - lastX) == 1 || (movetoX - lastX) == -1) {
					if (checkersStatus[movetoX][movetoY] == 0)
						return true;
				} else if ((movetoX - lastX) == 2 || (movetoX - lastX) == -2) {
					return (checkersStatus[(movetoX + lastX) / 2][(movetoY + lastY) / 2] == 2 || checkersStatus[(movetoX + lastX) / 2][(movetoY + lastY) / 2] == 4);
				}
			} else {
				return false;
			}
		case 3:
			System.out.println("King move");
			// TODO
			if (!isEnemy(x, y, player) && !outOfBounds(x, y)) {
				if (isLegalKingMove(x, y, lastX, lastY, 1))
					return true;
			}
		case 2:
			if (lastX < movetoX) {
				if ((movetoX - lastX) == 1 || (movetoX - lastX) == 1) {
					if (checkersStatus[movetoX][movetoY] == 0)
						return true;
				} else if ((movetoX - lastX) == 2 || (movetoX - lastX) == 2) {
					return (checkersStatus[(movetoX + lastX) / 2][(movetoY + lastY) / 2] == 1 || checkersStatus[(movetoX + lastX) / 2][(movetoY + lastY) / 2] == 3);
				}
			} else
				return false;
		case 4:
			System.out.println("King move");
			// TODO
			if (!isEnemy(x, y, player) && !outOfBounds(x, y)) {
				if (isLegalKingMove(x, y, lastX, lastY, 2))
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the player is allowed to move to the designated location from
	 * his initial location. This method is used for king pieces.
	 * 
	 * @param movetoX
	 *            X location coming from
	 * @param movetoY
	 *            Y location coming from
	 * @param lastX
	 *            X location going to
	 * @param lastY
	 *            Y location going to
	 * @param player
	 *            Player moving
	 * 
	 * @return True if the move is legal, false otherwise
	 */
	private boolean isLegalKingMove(int movetoX, int movetoY, int lastX,
			int lastY, int player) {
		return true;

	}

	/**
	 * Colors the board and sets the initial values on the internal 2D array
	 */
	private void colorBoard() {
		if (DEBUG)
			System.out.println("Coloring board background");

		if (forceQuitting)
			return;

		// Gotta animate it to look cool :D
		try {
			for (int j = 0; j < 4; j++) {
				for (int i = 1; i < 9; i++) {
					if (!forceQuitting)
						boardColorSpeed += (i * j) / 2;
					else
						boardColorSpeed = 0;

					if ((i - 4 + j) % 2 == 0) {
						Display.onThenChange(i, 4 + j, "yellow", emptyColor, 3,
								emptyIntensity, 300);
						checkersStatus[i - 1][4 + j] = 0;
					} else
						checkersStatus[i - 1][4 + j] = -1;
					if ((i - 3 - j) % 2 == 0) {
						Display.onThenChange(i, 3 - j, "yellow", emptyColor, 3,
								emptyIntensity, 300);
						checkersStatus[i - 1][3 - j] = 0;
					} else
						checkersStatus[i - 1][3 - j] = -1;
					Thread.sleep(boardColorSpeed);
				}
				Thread.sleep(boardColorSpeed);
			}
			Thread.sleep(boardWait);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Colors the board again in case a color failed to properly display on the
	 * launchpad
	 */
	private void recolorBoard() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				if (checkersStatus[i][j] == 1)
					Display.on(i + 1, j, player1Color, playerIntensity);
				else if (checkersStatus[i][j] == 2)
					Display.on(i + 1, j, player2Color, playerIntensity);
				else if (checkersStatus[i][j] == 3)
					Display.on(i + 1, j, player1KingColor, playerIntensity);
				else if (checkersStatus[i][j] == 4)
					Display.on(i + 1, j, player2KingColor, playerIntensity);
				else if (checkersStatus[i][j] == 0)
					Display.on(i + 1, j, emptyColor, emptyIntensity);
				else
					Display.off(i + 1, j);
			}
		}
	}

	/**
	 * If a player has won, blink a specific pad and clear all the slots that
	 * don't contain the winning player's pieces. This is just to show the
	 * remaining player's pieces' positions since it looks nice
	 * 
	 * @param winner
	 *            Winning player (1 or 2)
	 */
	private void playerwon(int winner) {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (winner == 1)
			Display.blink(0, 5, "yellow", "green", "red", 3, 3, 3, 200);
		else if (winner == 2)
			Display.blink(0, 6, "yellow", "green", "red", 3, 3, 3, 200);

		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				if (((winner == 1) ? checkersStatus[i][j] == 2
						|| checkersStatus[i][j] == 4
						: checkersStatus[i][j] == 1
								|| checkersStatus[i][j] == 3)
						|| checkersStatus[i][j] == 0) {
					int delay = (10 * i) + (20 * j);
					Display.on(i + 1, j, "yellow", 3, delay);
					try {
						Thread.sleep(delay);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Colors player 1's pieces and set the initial values on the internal 2D
	 * array
	 */
	private void fillPlayer1() {
		if (DEBUG)
			System.out.println("Coloring Player 1 slots.");

		if (forceQuitting)
			return;

		try {
			for (int j = 0; j < 8; j++) {
				for (int i = 6; i < 9; i++) {
					if ((j - i) % 2 == 0) {
						Display.on(i, j, player1Color, playerIntensity);
						checkersStatus[i - 1][j] = 1;
					}
					Thread.sleep(playerColorSpeed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Colors player 2's pieces and set the initial values on the internal 2D
	 * array
	 */
	private void fillPlayer2() {
		if (DEBUG)
			System.out.println("Coloring Player 2 slots.");

		if (forceQuitting)
			return;

		try {
			for (int j = 7; j >= 0; j--) {
				for (int i = 1; i < 4; i++) {

					if ((j - i) % 2 == 0) {
						Display.on(i, j, player2Color, playerIntensity);
						checkersStatus[i - 1][j] = 2;
					}
					Thread.sleep(playerColorSpeed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used for debugging: Displays the current checkerboard's status on the
	 * console. Will display . for unreacheable slots, 0 for empty and available
	 * slots, 1 for player 1 regular pieces, 2 for player 2 regular pieces, 3
	 * for player 1 king pieces, 4 for player 2 king pieces.
	 */
	public void displayStatusOnConsole() {
		if (DEBUG) {
			System.out.println("Status of the board:");
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					int temp = checkersStatus[i][j];
					System.out.print(((temp == -1) ? "." : temp) + "\t");
				}
				System.out.println("");
			}
		}
	}

	/**
	 * Displays the remaining amount of pieces available to every player
	 */
	private void displayRemaining() {
		System.out.println("Remaining player 1 pieces: " + player1Remaining);
		System.out.println("Remaining player 2 pieces: " + player2Remaining
				+ "\n");
	}

	/**
	 * Saves names and score to database Or will save game status to database so
	 * we can resume later on
	 */
	public void saveToDatabase() {
		if (DEBUG)
			System.out.println("Saving to Database.");
		// TODO Auto-generated method stub

	}

	/**
	 * TODO
	 */
	public void retreiveFromDatabase() {
		if (DEBUG)
			System.out.println("Retreiving from Database.");
		// TODO Auto-generated method stub

	}

	/**
	 * Not used in this game, but this is called immediately upon receiving an
	 * input. We use this to instantly do something if some pad is tapped
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable arg0, Object arg1) {
		// if (DEBUG)
		// System.out.println("Update received.");
		// Not really using the Observable pattern this time.
	}

	/**
	 * Asks players to confirm if they want to reset the game or not Blinks the
	 * Reset pad (arrow down) while waiting for an answer. Tapping the reset pad
	 * again confirms. Tapping any other pad cancels the reset.
	 */
	public void resetGameQuery() {
		Input.clearPressed();
		waitingForInput = true;
		Display.blink(resetX, resetY, emptyColor, emptyIntensity, 300);
		System.out.println("Are you sure you want to reset game?");
		int inX = -2;
		int inY = -2;
		Input.waitForInput();
		inX = Input.getLastKeyI();
		inY = Input.getLastKeyJ();
		if (inX == resetX && inY == resetY)
			resettingGame = true;
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.blinkoff(resetX, resetY);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.on(resetX, resetY, emptyColor, emptyIntensity);
	}

	/**
	 * Asks players if they want to play another game Blinks the Reset pad
	 * (arrow down) while waiting for an answer. Tapping the reset pad confirms
	 * a rematch. Tapping any other pad cancels the rematch.
	 */
	public void restartGameQuery() {
		Input.clearPressed();
		waitingForInput = true;
		Display.blink(resetX, resetY, emptyColor, emptyIntensity, 300);
		System.out.println("Do you want to play another game?");
		int inX = -2;
		int inY = -2;
		Input.waitForInput();
		inX = Input.getLastKeyI();
		inY = Input.getLastKeyJ();
		if (inX == resetX && inY == resetY)
			resettingGame = true;
		else
			quittingGame = true;
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.blinkoff(resetX, resetY);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.on(resetX, resetY, emptyColor, emptyIntensity);
	}

	/**
	 * Asks players to confirm if they want to quit the game or not. Blinks the
	 * Quit pad (arrow up) while waiting for an answer. Tapping the quit pad
	 * confirms a rematch. Tapping any other pad cancels the rematch.
	 */
	public void quitGameQuery() {
		Input.clearPressed();
		waitingForInput = true;
		Display.blink(quitX, quitY, "red", emptyIntensity, 300);
		System.out.println("Are you sure you want to quit game?");
		int inX = -2;
		int inY = -2;
		Input.waitForInput();
		inX = Input.getLastKeyI();
		inY = Input.getLastKeyJ();
		if (inX == quitX && inY == quitY)
			quittingGame = true;
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.blinkoff(quitX, quitY);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display.on(quitX, quitY, "red", emptyIntensity);
	}

	/**
	 * Quits the game. Clears the pads and resets the Launchpad to it's normal
	 * "out of game" status. It should be ready to be used for other games or
	 * animations after.
	 */
	public static void quitGame() {
		if (DEBUG)
			System.out.println("Quitting Checkers.");
		controlAnimSpeed = 0;
		boardColorSpeed = 0;
		playerColorSpeed = 0;
		boardWait = 0;
		CheckersGUI.quit();
		delay(500);
		setRunning(false);
		Main.setClosing(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		System.out.println("Quitting Checkers.\n");
		forceQuitting = true;
		controlAnimSpeed = 0;
		boardColorSpeed = 0;
		playerColorSpeed = 0;
		boardWait = 0;
		setRunning(false);
		Main.setClosing(false);
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
		launchpad_s_plus.Display.clearPads();
	}
}
