package games;

import gui.CheckersGUI;
import gui.Main;

import java.util.*;
import launchpad_s_plus.*;

// TODO Rewriting Checkers

/**
 * This class implements the game Checkers (also known as Draughts).
 * 
 * 
 * @author Helion
 * @version $Revision: 0.1 $
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

public class Checkers2 extends Game implements Observer {
	static boolean DEBUG = true; // For debugging purposes only. Set to true to
									// output
	// everything happening to console
	// nothing with the update() function
	static boolean hasCaptured = false; // Becomes true whenever a player has
										// captured
	// a soldier during his turn
	static int nextPlayer = 0; // 0 for none - 1 for player 1 - 2 for
								// player 2
	static CheckerCell[][] checkerBoard = new CheckerCell[8][8];
	/*
	 * status will be -1 for unreachable slots 0 for non occupied slots 1 for
	 * slots occupied by Player 1 2 for slots occupied by Player 2
	 */

	private String player1Color = "green"; // Presetting colors
	private String player2Color = "red"; //
	private String emptyColor = "orange"; //
	private String player1KingColor = "yellow";
	private String player2KingColor = "yellow";
	private int playerIntensity = 3; // Color intensity
	private int emptyIntensity = 1;
	// Setting amount of remaining pieces for both players
	private static int player1Remaining = 12;
	private static int player2Remaining = 12;
	private static int winningPlayer = 0;

	// Memory for the selected piece and move
	private static int selectedX = -1;
	private static int selectedY = -1;

	// These are for the various animations
	private static int controlAnimSpeed = 50;
	private static int boardColorSpeed = 35;
	private static int playerColorSpeed = 30;
	private static int boardWait = 500;

	// These are booleans used to check if we are resetting or quitting the game
	private static boolean forceQuitting = false;
	private static boolean resettingGame = false;
	private static boolean resettingGameQuery = false;
	private static boolean quittingGame = false;
	private static boolean quittingGameQuery = false;

	private static boolean waitingForInput = false;
	private static boolean playing = false;

	// These are just to relocate the Quit and Reset buttons
	private static int quitX = 0;
	private static int quitY = 7;
	private static int resetX = 0;
	private static int resetY = 4;

	class CheckerCell {
		int x, y, player;
		boolean isKing;
		ArrayList<int[]> moves;
		ArrayList<int[]> eat;
		boolean alive;

		CheckerCell() {
			x = y = player = 0;
			isKing = alive = false;
			moves = new ArrayList<int[]>();
			eat = new ArrayList<int[]>();
		}

		CheckerCell(int X, int Y, int PLAYER, boolean ISKING) {
			x = X;
			y = Y;
			player = PLAYER;
			isKing = ISKING;
			if (PLAYER > 0)
				alive = true;
			else
				alive = false;
			moves = new ArrayList<int[]>();
			eat = new ArrayList<int[]>();
		}

		void setX(int X) {
			x = X;
		}

		void setY(int Y) {
			y = Y;
		}

		void setPos(int X, int Y) {
			setX(X);
			setY(Y);
		}

		void setEmpty() {
			setPlayer(0);
			setKing(false);
		}

		void setPlayer(int PLAYER) {
			player = PLAYER;
			if (player <= 0)
				setAlive(false);
			else
				setAlive(true);
		}

		int getPlayer() {
			return player;
		}

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

		int[] getPos() {
			int[] pos = new int[2];
			pos[0] = getX();
			pos[1] = getY();
			return pos;
		}

		void setKing(boolean status) {
			isKing = status;
		}

		void setAlive(boolean status) {
			alive = status;
		}

		boolean isEmpty() {
			return (player == 0);
		}

		boolean isAccessible() {
			return !(player == -1);
		}

		void addMove(int[] newMove) {
			if (!moves.contains(newMove))
				moves.add(newMove);
		}

		void addEat(int[] newEat) {
			if (!eat.contains(newEat))
				eat.add(newEat);
		}

		ArrayList<int[]> getLegalMoves() {
			return moves;
		}

		ArrayList<int[]> getEdibleSoldiers() {
			return eat;
		}

		boolean canMoveTo(int x, int y) {
			for (int i = 0; i < moves.size(); i++) {
				if (moves.get(i)[0] == x && moves.get(i)[1] == y)
					return true;
			}
			return false;
		}

		boolean canMove() {
			return (moves.size() > 0);
		}

		boolean canEat() {
			return (eat.size() > 0);
		}

		void kill() {
			setPlayer(0);
			setKing(false);
			moves.clear();
			eat.clear();
		}

		boolean isEnemy(CheckerCell other) {
			return ((this.player == 1 && other.getPlayer() == 2) || (this.player == 2 && other
					.getPlayer() == 1));
		}

		boolean isSameTeam(CheckerCell other) {
			return ((this.player == 1 && other.getPlayer() == 1) || (this.player == 2 && other
					.getPlayer() == 2));
		}
	}

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            Unused
	 */
	public Checkers2(boolean b) {
	}

	/**
	 * Constructor
	 */
	public Checkers2() {
		gameID = 1;
		gameName = "Checkers";
	}

	@Override
	public void startGame() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		gameName = "Checkers";
		difficulty = 0;
		sizeX = sizeY = 8;
		setRunningGame(gameName);
		setRunning(true);
		System.out.println("\nStarting Checkers.");
		resetGame();
	}

	@Override
	public void resetGame() {
		super.prepareGame();
		incrementGamesPlayed();

		setRunningGame(gameName);
		resetValues();
		begin(); // Start the game

		if (resettingGame)
			resetGame();
		quitGame();
	}

	private void resetValues() {
		// Resetting animation speeds in case a force quit was previously called
		controlAnimSpeed = 50;
		boardColorSpeed = 35;
		playerColorSpeed = 30;
		boardWait = 500;

		// Setting resetting and quitting booleans to false so the game
		// doesn't---
		// restart or quit automatically if this was called previously
		resettingGame = false;
		resettingGameQuery = false;
		quittingGame = false;
		quittingGameQuery = false;
		forceQuitting = false;
		playing = false;

		// Clearing board
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				checkerBoard[i][j] = new CheckerCell();
			}
		}

		// Resetting the amount of remaining pieces in case a game reset was
		// previously called
		player1Remaining = 12;
		player2Remaining = 12;
		winningPlayer = 0;
		nextPlayer = 0;

		// Resetting selection
		selectedX = -1;
		selectedY = -1;
	}

	private void begin() {
		System.out.println("Starting new game of Checkers.");
		lightControls();
		colorBoard();
		fillPlayer1(); // Coloring Player 1 (green) slots and setting map status
		fillPlayer2(); // Coloring Player 2 (red) slots and setting map status
		updateMoves();
		if (forceQuitting || quittingGame)
			return;

		playing = true;
		startPlaying();
		if (quittingGame || resettingGame)
			return;
		playerwon(winningPlayer);
	}

	private void startPlaying() {
		nextPlayer = 1;
		boolean rightSelection = false;
		boolean selectedPlayer = false;
		int lastX, lastY, newX, newY = -2;
		while (playing) {
			if (DEBUG)
				displayStatusOnConsole();
			recolorBoard();
			updateMoves();
			lastX = -2;
			lastY = -2;
			newX = -2;
			newY = -2;
			rightSelection = true;
			if (!playerCanMove(nextPlayer)) {
				if (nextPlayer == 1)
					winningPlayer = 2;
				else
					winningPlayer = 1;
				return;
			}
			delay(10);

			waitForPlayer(nextPlayer);
			while (waitingForInput) {
				if (quittingGame || resettingGame)
					return;
				delay(10);
			}

			lastX = selectedX - 1;
			lastY = selectedY;
			if (!selectedPlayer)
				rightSelection = checkPlayer(nextPlayer, selectedX - 1,
						selectedY);

			if (!rightSelection)
				continue;

			showLegalMoves(lastX, lastY, nextPlayer, true);
			selectedPlayer = true;

			waitForPlayer(nextPlayer);
			while (waitingForInput) {
				if (quittingGame || resettingGame)
					return;
				delay(10);
			}
			newX = selectedX - 1;
			newY = selectedY;

			showLegalMoves(lastX, lastY, nextPlayer, false);
			delay(10);

			if (newX == lastX && newY == lastY) {
				System.out.println("Player " + nextPlayer + " cancelled move.");
				selectedPlayer = false;
				continue;
			}

			if (isLegalMove(lastX, lastY, newX, newY, nextPlayer)) {
				System.out.println("Legal move. Moving");
				selectedPlayer = false;
				int[] to = new int[2];
				int[] from = new int[2];
				to[0] = newX;
				to[1] = newY;
				from[0] = lastX;
				from[1] = lastY;
				boolean hasEaten = doMove(from, to, nextPlayer);

				if (hasEaten) {
					if (DEBUG)
						System.out
								.println("Player has eaten. Checking if it is possible to eat again");
					if (checkerBoard[newX][newY].canEat())
						continue;
				}
				if (nextPlayer == 1)
					nextPlayer = 2;
				else
					nextPlayer = 1;
				continue;
			}

			if (quittingGame || resettingGame)
				return;
		}
	}

	private void doControlsCheck() {
		if (checkForReset() && !resettingGameQuery)
			restartQuery();
		else if (checkForReset() && resettingGameQuery) {
			resettingGame = true;
			stopWaitInput();
		} else if (!checkForReset() && resettingGameQuery) {
			notResetting();
		}

		if (checkForQuit() && !quittingGameQuery)
			quitGameQuery();
		else if (checkForQuit() && quittingGameQuery) {
			quittingGame = true;
			stopWaitInput();
		} else if (!checkForQuit() && quittingGameQuery) {
			notQuitting();
		}

	}

	boolean playerCanMove(int player) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (checkerBoard[i][j].getPlayer() == player
						&& !checkerBoard[i][j].getLegalMoves().isEmpty())
					return true;
			}
		}
		return false;
	}

	void updateMoves(int x, int y) {
		if (checkerBoard[x][y].getPlayer() == 1 && x == 0)
			checkerBoard[x][y].setKing(true);
		else if (checkerBoard[x][y].getPlayer() == 2 && x == 7)
			checkerBoard[x][y].setKing(true);

		checkerBoard[x][y].moves = new ArrayList<int[]>();
		checkerBoard[x][y].eat = new ArrayList<int[]>();

		if (checkerBoard[x][y].isKing) {
			updateMovesKing(x, y);
			return;
		}

		if (checkerBoard[x][y].getPlayer() == 1) {
			try {
				if (checkerBoard[x - 1][y - 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y - 1;
					checkerBoard[x][y].addMove(temp);
				} else if (checkerBoard[x][y]
						.isEnemy(checkerBoard[x - 1][y - 1])
						&& checkerBoard[x - 2][y - 2].isEmpty()) {

					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y - 1;
					checkerBoard[x][y].addEat(temp);
					temp = new int[2];
					temp[0] = x - 2;
					temp[1] = y - 2;
					checkerBoard[x][y].addMove(temp);
				}
			} catch (Exception e) {
			}

			try {
				if (checkerBoard[x - 1][y + 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y + 1;
					checkerBoard[x][y].addMove(temp);
				} else if (checkerBoard[x][y]
						.isEnemy(checkerBoard[x - 1][y + 1])
						&& checkerBoard[x - 2][y + 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y + 1;
					checkerBoard[x][y].addEat(temp);
					temp = new int[2];
					temp[0] = x - 2;
					temp[1] = y + 2;
					checkerBoard[x][y].addMove(temp);
				}
			} catch (Exception e) {
			}
		}

		if (checkerBoard[x][y].getPlayer() == 2) {
			try {
				if (checkerBoard[x + 1][y + 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y + 1;
					checkerBoard[x][y].addMove(temp);
				} else if (checkerBoard[x][y]
						.isEnemy(checkerBoard[x + 1][y + 1])
						&& checkerBoard[x + 2][y + 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y + 1;
					checkerBoard[x][y].addEat(temp);
					temp = new int[2];
					temp[0] = x + 2;
					temp[1] = y + 2;
					checkerBoard[x][y].addMove(temp);
				}
			} catch (Exception e) {
			}

			try {
				if (checkerBoard[x + 1][y - 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y - 1;
					checkerBoard[x][y].addMove(temp);
				} else if (checkerBoard[x][y]
						.isEnemy(checkerBoard[x + 1][y - 1])
						&& checkerBoard[x + 2][y - 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y - 1;
					checkerBoard[x][y].addEat(temp);
					temp = new int[2];
					temp[0] = x + 2;
					temp[1] = y - 2;
					checkerBoard[x][y].addMove(temp);
				}
			} catch (Exception e) {
			}
		}
	}

	void updateMovesKing(int x, int y) {
		checkerBoard[x][y].moves.clear();
		checkerBoard[x][y].eat.clear();
		updateMovesKing(checkerBoard[x][y], checkerBoard[x][y], 1);
		updateMovesKing(checkerBoard[x][y], checkerBoard[x][y], 2);
		updateMovesKing(checkerBoard[x][y], checkerBoard[x][y], 3);
		updateMovesKing(checkerBoard[x][y], checkerBoard[x][y], 4);
	}

	void updateMovesKing(CheckerCell original, CheckerCell currentCell,
			int direction) {

		int x = currentCell.getX();
		int y = currentCell.getY();

		switch (direction) {
		case 1:
			try {
				if (checkerBoard[x - 1][y - 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y - 1;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					updateMovesKing(original, checkerBoard[x - 1][y - 1], 1);
				} else if (checkerBoard[original.getX()][original.getY()]
						.isEnemy(checkerBoard[x - 1][y - 1])
						&& checkerBoard[x - 2][y - 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y - 1;
					checkerBoard[original.getX()][original.getY()].addEat(temp);
					temp = new int[2];
					temp[0] = x - 2;
					temp[1] = y - 2;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					return;
				}
			} catch (Exception e) {
				return;
			}
			break;
			
		case 2:
			try {
				if (checkerBoard[x + 1][y - 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y - 1;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					updateMovesKing(original, checkerBoard[x + 1][y - 1], 2);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			try {
				if (checkerBoard[original.getX()][original.getY()]
						.isEnemy(checkerBoard[x + 1][y - 1])
						&& checkerBoard[x + 2][y - 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y - 1;
					checkerBoard[original.getX()][original.getY()].addEat(temp);
					temp = new int[2];
					temp[0] = x + 2;
					temp[1] = y - 2;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			break;

		case 3:
			try {
				if (checkerBoard[x - 1][y + 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y + 1;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					updateMovesKing(original, checkerBoard[x - 1][y + 1], 3);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			try {
				if (checkerBoard[original.getX()][original.getY()]
						.isEnemy(checkerBoard[x - 1][y + 1])
						&& checkerBoard[x - 2][y + 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x - 1;
					temp[1] = y + 1;
					checkerBoard[original.getX()][original.getY()].addEat(temp);
					temp = new int[2];
					temp[0] = x - 2;
					temp[1] = y + 2;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			break;

		case 4:
			try {
				if (checkerBoard[x + 1][y + 1].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y + 1;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					updateMovesKing(original, checkerBoard[x + 1][y + 1], 4);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			try {
				if (checkerBoard[original.getX()][original.getY()]
						.isEnemy(checkerBoard[x + 1][y + 1])
						&& checkerBoard[x + 2][y + 2].isEmpty()) {
					int[] temp = new int[2];
					temp[0] = x + 1;
					temp[1] = y + 1;
					checkerBoard[original.getX()][original.getY()].addEat(temp);
					temp = new int[2];
					temp[0] = x + 2;
					temp[1] = y + 2;
					checkerBoard[original.getX()][original.getY()]
							.addMove(temp);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			break;
		}
	}

	void updateMoves() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				checkerBoard[i][j].moves.clear();
				checkerBoard[i][j].eat.clear();
				updateMoves(i, j);
			}
		}
	}

	boolean doMove(int[] moveFrom, int[] moveTo, int player) {
		boolean hasEaten = false;
		boolean isKing = checkerBoard[moveFrom[0]][moveFrom[1]].isKing;

		if (isKing) {
			if (moveFrom[0] > moveTo[0] && moveFrom[1] > moveTo[1]) {
				if (checkerBoard[moveFrom[0]][moveFrom[1]]
						.isEnemy(checkerBoard[moveTo[0] + 1][moveTo[1] + 1])) {
					checkerBoard[moveTo[0] + 1][moveTo[1] + 1].kill();
					hasEaten = true;
				}
			} else if (moveFrom[0] > moveTo[0] && moveFrom[1] < moveTo[1]) {
				if (checkerBoard[moveFrom[0]][moveFrom[1]]
						.isEnemy(checkerBoard[moveTo[0] + 1][moveTo[1] - 1])) {
					checkerBoard[moveTo[0] + 1][moveTo[1] - 1].kill();
					hasEaten = true;
				}
			} else if (moveFrom[0] < moveTo[0] && moveFrom[1] > moveTo[1]) {
				if (checkerBoard[moveFrom[0]][moveFrom[1]]
						.isEnemy(checkerBoard[moveTo[0] - 1][moveTo[1] + 1])) {
					checkerBoard[moveTo[0] - 1][moveTo[1] + 1].kill();
					hasEaten = true;
				}
			} else if (moveFrom[0] < moveTo[0] && moveFrom[1] < moveTo[1]) {
				if (checkerBoard[moveFrom[0]][moveFrom[1]]
						.isEnemy(checkerBoard[moveTo[0] - 1][moveTo[1] - 1])) {
					checkerBoard[moveTo[0] - 1][moveTo[1] - 1].kill();
					hasEaten = true;
				}
			}
		} else if (Math.abs(moveFrom[0] - moveTo[0]) > 1) {
			checkerBoard[(moveFrom[0] + moveTo[0]) / 2][(moveFrom[1] + moveTo[1]) / 2]
					.kill();
			hasEaten = true;
		}
		if (isKing)
			Display.blinkoff(moveFrom[0] + 1, moveFrom[1]);

		checkerBoard[moveFrom[0]][moveFrom[1]].setEmpty();
		checkerBoard[moveTo[0]][moveTo[1]].setPlayer(player);
		checkerBoard[moveTo[0]][moveTo[1]].setKing(isKing);
		return hasEaten;
	}

	private boolean checkForReset() {
		return ((selectedX == resetX) && (selectedY == resetY));
	}

	private boolean checkForQuit() {
		return ((selectedX == quitX) && (selectedY == quitY));
	}

	private void waitForPlayer(int playingPerson) {
		if (DEBUG)
			System.out.println("Waiting for player " + playingPerson);
		blinkPlayer(playingPerson, true);
		waitInput();
		// if (DEBUG)
		// System.out.println("Clearing input");
		// Input.clearPressed();
		// delay(10);
		if (DEBUG)
			System.out.println("Waiting for input");
		// Input.waitForInput();
	}

	private void waitInput() {
		waitingForInput = true;
	}

	private void stopWaitInput() {
		waitingForInput = false;
	}

	private void blinkPlayer(int player, boolean onOrOff) {
		stopPlayerBlinks();
		if (player == 1 && onOrOff)
			Display.blink(8, 8, "green", 3, 500);
		else if (player == 1 && !onOrOff)
			Display.blinkoff(8, 8);
		else if (player == 2 && onOrOff)
			Display.blink(1, 8, "green", 3, 500);
		else if (player == 2 && !onOrOff)
			Display.blinkoff(1, 8);
	}

	private void stopPlayerBlinks() {
		Display.blinkoff(8, 8);
		Display.blinkoff(1, 8);
		delay(10);
		Display.on(8, 8, "red", 1);
		Display.on(1, 8, "red", 1);
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
	 * Colors the board and sets the initial values on the internal 2D array
	 */
	private void colorBoard() {
		if (DEBUG)
			System.out.println("Coloring board background");

		if (forceQuitting)
			return;

		// Gotta animate it to look cool :D
		for (int j = 0; j < 4; j++) {
			for (int i = 1; i < 9; i++) {
				if (!forceQuitting)
					boardColorSpeed += (i * j) / 2;
				else
					boardColorSpeed = 0;

				if ((i - 4 + j) % 2 == 0) {
					Display.onThenChange(i, 4 + j, "yellow", emptyColor, 3,
							emptyIntensity, 300);
					checkerBoard[i - 1][4 + j].setPlayer(0);
					checkerBoard[i - 1][4 + j].setPos(i - 1, 4 + j);
				} else {
					checkerBoard[i - 1][4 + j].setPlayer(-1);
					checkerBoard[i - 1][4 + j].setPos(i - 1, 4 + j);
				}
				if ((i - 3 - j) % 2 == 0) {
					Display.onThenChange(i, 3 - j, "yellow", emptyColor, 3,
							emptyIntensity, 300);
					checkerBoard[i - 1][3 - j].setPlayer(0);
					checkerBoard[i - 1][3 + j].setPos(i - 1, 3 + j);
				} else {
					checkerBoard[i - 1][3 - j].setPlayer(-1);
					checkerBoard[i - 1][3 + j].setPos(i - 1, 3 + j);
				}
				delay(boardColorSpeed);
			}
			delay(boardColorSpeed);
		}
		delay(boardWait);
	}

	/**
	 * Colors the board again in case a color failed to properly display on the
	 * launchpad
	 */
	private void recolorBoard() {
		stopPlayerBlinks();
		delay(100);

		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				if (checkerBoard[i][j].isKing) {
					Display.blinkoff(i + 1, j);
					delay(100);
				}

				if (checkerBoard[i][j].getPlayer() == 1
						&& !checkerBoard[i][j].isKing)
					Display.on(i + 1, j, player1Color, playerIntensity);
				else if (checkerBoard[i][j].getPlayer() == 2
						&& !checkerBoard[i][j].isKing)
					Display.on(i + 1, j, player2Color, playerIntensity);
				else if (checkerBoard[i][j].getPlayer() == 1
						&& checkerBoard[i][j].isKing)
					Display.blink(i + 1, j, player1KingColor, player1Color,
							playerIntensity, playerIntensity, 1000);
				else if (checkerBoard[i][j].getPlayer() == 2
						&& checkerBoard[i][j].isKing)
					Display.blink(i + 1, j, player2KingColor, player2Color,
							playerIntensity, playerIntensity, 1000);
				else if (checkerBoard[i][j].getPlayer() == 0)
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
		delay(100);

		if (winner == 1)
			Display.blink(0, 5, "yellow", "green", "red", 3, 3, 3, 200);
		else if (winner == 2)
			Display.blink(0, 6, "yellow", "green", "red", 3, 3, 3, 200);

		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				if (((winner == 1) ? checkerBoard[i][j].getPlayer() == 2
						|| checkerBoard[i][j].getPlayer() == 4
						: checkerBoard[i][j].getPlayer() == 1
								|| checkerBoard[i][j].getPlayer() == 3)
						|| checkerBoard[i][j].getPlayer() == 0) {
					int waitFor = (10 * i) + (20 * j);
					Display.on(i + 1, j, "yellow", 3, waitFor);
					delay(waitFor);
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

		for (int j = 0; j < 8; j++) {
			for (int i = 6; i < 9; i++) {
				if ((j - i) % 2 == 0) {
					Display.on(i, j, player1Color, playerIntensity);
					checkerBoard[i - 1][j] = new CheckerCell(i - 1, j, 1, false);
				}
				delay(playerColorSpeed);
			}
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

		for (int j = 7; j >= 0; j--) {
			for (int i = 1; i < 4; i++) {

				if ((j - i) % 2 == 0) {
					Display.on(i, j, player2Color, playerIntensity);
					checkerBoard[i - 1][j] = new CheckerCell(i - 1, j, 2, false);
				}
				delay(playerColorSpeed);
			}
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
					int temp = checkerBoard[i][j].getPlayer();
					if (checkerBoard[i][j].isKing)
						temp += 2;
					System.out.print(((temp == -1) ? "." : temp) + "\t");
				}
				System.out.println("");
			}
		}
		displayRemaining();
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
	 * Saves details of the round into the database
	 */
	@Override
	public void saveToDatabase() {
		// TODO Auto-generated method stub
	}

	@Override
	public void retreiveFromDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (runningGameName == "Checkers") {
			if (Input.getStatus()) {
				selectedX = Input.getPositionX();
				selectedY = Input.getPositionY();
				doControlsCheck();
				if (quittingGame || resettingGame)
					return;
				if (!outOfGameBounds()) {
					if (waitingForInput)
						stopWaitInput();
				}
			}
		}
	}

	private boolean outOfGameBounds() {
		return (selectedX == 0 || selectedY == 8);
	}

	private boolean nextCheckOutOfGameBounds(int x, int y) {
		return (x <= 1 || x >= 6 || y <= 1 || y >= 6);
	}

	private boolean checkPlayer(int whoIs, int x, int y) {
		if (whoIs == 1)
			return (checkerBoard[x][y].getPlayer() == 1);
		else
			return (checkerBoard[x][y].getPlayer() == 2);
	}

	private void showLegalMoves(int x, int y, int player, boolean showoroff) {
		if (DEBUG && showoroff)
			System.out.println("Showing legal moves for player " + player
					+ (checkerBoard[x][y].isKing ? "-King" : "")
					+ " - From position " + x + " " + y);
		else if (DEBUG && !showoroff)
			System.out.println("Turning off legal moves blinking.");

		// Showing the legal moves
		if (showoroff) {
			String selfColor = "";
			String enemyColor = "";
			if (player == 1) {
				if (checkerBoard[x][y].isKing)
					selfColor = player1KingColor;
				else
					selfColor = player1Color;
				enemyColor = player2Color;
			} else {
				if (checkerBoard[x][y].isKing)
					selfColor = player2KingColor;
				else
					selfColor = player2Color;
				enemyColor = player1Color;
			}

			// Blink selected cell
			if (checkerBoard[x][y].isKing) {
				Display.blinkoff(x + 1, y);
				delay(10);
			}
			Display.blink(x + 1, y, selfColor, 3, 300);

			// Blink all available moves
			ArrayList<int[]> legalMoves = checkerBoard[x][y].getLegalMoves();
			ArrayList<int[]> canEat = checkerBoard[x][y].getEdibleSoldiers();

			if (DEBUG)
				System.out.println("Cell " + x + " " + y + " can eat "
						+ canEat.size() + " cells.");
			for (int i = 0; i < canEat.size(); i++) {
				if (DEBUG)
					System.out.println("\t- " + canEat.get(i)[0] + " "
							+ canEat.get(i)[1]);
				Display.blink(canEat.get(i)[0] + 1, canEat.get(i)[1],
						enemyColor, 2, 300);
			}

			if (DEBUG)
				System.out.println("Cell " + x + " " + y + " can move to "
						+ legalMoves.size() + " positions.");
			for (int i = 0; i < legalMoves.size(); i++) {
				if (DEBUG)
					System.out.println("\t- " + legalMoves.get(i)[0] + " "
							+ legalMoves.get(i)[1]);
				Display.blink(legalMoves.get(i)[0] + 1, legalMoves.get(i)[1],
						selfColor, 3, 300);
			}
		} else {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					Display.blinkoff(i + 1, j);
					delay(5);
				}
			}
			delay(100);
			recolorBoard();
		}
	}

	private boolean isLegalMove(int lastX, int lastY, int newX, int newY,
			int player) {
		if (DEBUG)
			System.out.println("Checking legal move for player " + player
					+ " at r" + lastX + " c" + lastY);

		ArrayList<int[]> moves = checkerBoard[lastX][lastY].getLegalMoves();

		for (int i = 0; i < moves.size(); i++) {
			if (DEBUG)
				System.out.println("\tPossible move to r" + moves.get(i)[0]
						+ " c" + moves.get(i)[1]);
			if (moves.get(i)[0] == newX && moves.get(i)[1] == newY) {
				if (DEBUG)
					System.out.println("Move r" + newX + " c" + newY
							+ " is legal.");
				return true;
			}
		}

		if (DEBUG)
			System.out
					.println("Move to r" + newX + " c" + newY + " is illegal");

		return false;
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
	 * Asks user if the game should be restarted
	 */
	private static void restartQuery() {
		resettingGameQuery = true;
		Display.blink(resetX, resetY, "yellow", 2, 500);
	}

	private static void quitGameQuery() {
		quittingGameQuery = true;
		Display.blink(quitX, quitY, "red", 3, 500);
	}

	/**
	 * Quits the game. Clears the pads and resets the Launchpad to it's normal
	 * "out of game" status. It should be ready to be used for other games or
	 * animations after.
	 */
	public static void quitGame() {
		if (quittingGame) {
			System.out.println("Quitting Checkers.\n");
			setRunningGame("none");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(100);
			Main.setClosing(false);
			CheckersGUI.quit();
			quittingGame = false;
			resettingGame = false;
			playing = false;
			waitingForInput = false;
		}
		quittingGame = false;
	}

	/**
	 * Method called when quitting has been cancelled
	 */
	private void notQuitting() {
		Display.blinkoff(quitX, quitY);
		delay(250);
		Display.on(quitX, quitY, "red", 1);
		quittingGameQuery = false;
		quittingGame = false;
	}

	/**
	 * Method called when resetting has been cancelled
	 */
	private void notResetting() {
		Display.blinkoff(resetX, resetY);
		delay(250);
		Display.on(resetX, resetY, "yellow", 1);
		resettingGameQuery = false;
		resettingGame = false;
	}

	/**
	 * Forcefully quits the game
	 */
	public static void forceQuit() {
		System.out.println("Quitting Checkers.\n");
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
		playing = false;
		waitingForInput = false;
	}
}