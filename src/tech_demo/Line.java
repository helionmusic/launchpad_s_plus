package tech_demo;

import launchpad_s_plus.*;

import javax.sound.midi.InvalidMidiDataException;

/**
 * Implements Line animations
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Line extends Thread {
	boolean DEBUG = false;
	int initPadX, initPadY, dur, col, intensity, speed, direction;

	/**
	 * Displays a single line based on input parameters
	 * 
	 * @param dir
	 *            Direction (1 to 8 inclusive) 1:up - 2:down - 3:left - 4:right
	 *            - 5:up+left - 6:up+right - 7:down+left - 8:down+right
	 * @param x
	 *            Starting X location
	 * @param y
	 *            Starting Y location
	 * @param duration
	 *            Duration for which a single pad is lit up
	 * @param colr
	 *            Color integer value
	 * @param intense
	 *            Color intensity value
	 * @param spd
	 *            Speed at which the line moves across
	 */
	public Line(int dir, int x, int y, int duration, int colr, int intense,
			int spd) {
		direction = dir;
		initPadX = x;
		initPadY = y;
		dur = duration;
		col = colr;
		intensity = intense;
		speed = spd;
		this.start();
	}

	/**
	 * Initiates Thread. Displays the line
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * Direction values: 1 up, 2 down, 3 left, 4 right 5 diagonal up-left, 6
		 * diagonal up-right, 7 diagonal down-left, 8 diagonal down-right
		 */
		if (DEBUG)
			System.out.print("\nNew " + Colors.strong[col] + " line");
		boolean onlyOn = false;
		if (initPadX > 8 || initPadX < 0 || initPadY > 8 || initPadY < 0)
			return;

		switch (direction) {
		case 1: // Going up
			if (DEBUG)
				System.out.print(" going up from " + initPadX + " " + initPadY);
			for (int i = initPadX; i >= 0; i--) {
				int j = initPadY;

				if (!onlyOn)
					Display.on(i, j, Colors.strong[col], intensity, dur);
				else
					Display.on(i, j, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 2: // Going down
			if (DEBUG)
				System.out.print(" going down from " + initPadX + " "
						+ initPadY);
			for (int i = initPadX; i < 9; i++) {
				int j = initPadY;
				if (!onlyOn)
					Display.on(i, j, Colors.strong[col], intensity, dur);
				else
					Display.on(i, j, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 3: // Going left
			if (DEBUG)
				System.out.print(" going left from " + initPadX + " "
						+ initPadY);
			for (int j = initPadY; j >= 0; j--) {
				int i = initPadX;
				if (!onlyOn)
					Display.on(i, j, Colors.strong[col], intensity, dur);
				else
					Display.on(i, j, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 4: // Going right
			if (DEBUG)
				System.out.print(" going right from " + initPadX + " "
						+ initPadY);
			for (int j = initPadY; j < 9; j++) {
				int i = initPadX;
				if (!onlyOn)
					Display.on(i, j, Colors.strong[col], intensity, dur);
				else
					Display.on(i, j, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;

		case 5:
			for (int i = 0; i < 9; i++) {
				int x = initPadX;
				int y = initPadY;
				if (!onlyOn)
					Display.on(x - i, y - i, Colors.strong[col], intensity, dur);
				else
					Display.on(x - i, y - i, Colors.strong[col], intensity);

				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 6:
			for (int i = 0; i < 9; i++) {
				int x = initPadX;
				int y = initPadY;
				if (!onlyOn)
					Display.on(x + i, y - i, Colors.strong[col], intensity, dur);

				else

					Display.on(x + i, y - i, Colors.strong[col], intensity);

				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 7:
			for (int i = 0; i < 9; i++) {
				int x = initPadX;
				int y = initPadY;
				if (!onlyOn)

					Display.on(x - i, y + i, Colors.strong[col], intensity, dur);

				else
					Display.on(x - i, y + i, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case 8:
			for (int i = 0; i < 9; i++) {
				int x = initPadX;
				int y = initPadY;
				if (!onlyOn)
					Display.on(x + i, y + i, Colors.strong[col], intensity, dur);
				else
					Display.on(x + i, y + i, Colors.strong[col], intensity);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			return;
		}
	}

	/**
	 * Calculates random parameters and calls a new line
	 * 
	 * @param x
	 *            X location to initiate the line from
	 * @param y
	 *            Y location to initiate the line from
	 * @param withDiagonal
	 *            True if the line should be allowed to be diagonal
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void RandomLine(int x, int y, boolean withDiagonal)
			throws InvalidMidiDataException, InterruptedException {
		int direction, duration, colr;
		if (withDiagonal)
			direction = Calculate.randInt(1, 8);
		else
			direction = Calculate.randInt(1, 4);
		duration = Calculate.randInt(100, 500);
		colr = Calculate.randInt(0, 4);
		new Line(direction, x, y, duration, colr, 3, 50);
	}

	/**
	 * Displays a single line based on input parameters
	 * 
	 * @param x
	 *            Starting X location
	 * @param y
	 *            Starting Y location
	 * @param direction
	 *            Direction of the animation
	 * @param duration
	 *            Duration every pad lights up during the animation
	 * @param color
	 *            Integer value of the color
	 */
	public static void lineWithDirection(int x, int y, int direction,
			int duration, int color) {
		try {
			new Line(direction, x, y, duration, color, 3, 50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
