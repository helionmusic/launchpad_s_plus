package tech_demo;

import gui.ReactiveDemoGUI;

import java.util.*;

import javax.sound.midi.InvalidMidiDataException;

import launchpad_s_plus.*;

/**
 * Implements a couple of simple animations to be displayed on the Launchpad S
 * whenever the user presses a pad. <br>
 * Include a few command specific pads which are: <br>
 * - [0][2] Previous Animation <br>
 * - [0][3] Next Animation <br>
 * - [0][7] Quit Reactive Demo
 * 
 * <br>
 * <br>
 * Animations available: <br>
 * - Plus <br>
 * - Star <br>
 * - Fireworks <br>
 * - Random line
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class ReactiveDemo extends Thread implements Observer {
	boolean DEBUG = false;
	private static boolean isRunning;
	private int positionX;
	private int positionY;
	private boolean pressed;
	public static int type = 1;
	private static int types = 4;

	private int quitX = 0;
	private int quitY = 7;

	/**
	 * Unused Constructor
	 */
	public ReactiveDemo() {
	}

	/**
	 * Constructor for ReactiveDemo.
	 * 
	 * @param x
	 *            X location of the input
	 * @param y
	 *            Y location of the input
	 * @param status
	 *            True if the last input was a press, False if it was a release
	 */
	public ReactiveDemo(int x, int y, boolean status) {
		positionX = x;
		positionY = y;
		pressed = status;
	}

	/**
	 * Increments the amount of Demos ran and initiates the Reactive demo.
	 */
	public void begin() {
		TechDemo.incrementDemosRan();

		if (!SetupProcess.lpdetected)
			return;
		Launchpad.disableSelfListen();

		System.out.println("Starting Reactive Animations Demo.");
		isRunning = true;
	}

	/**
	 * Quits the reactive demo and closes the GUI
	 */
	public static void quit() {
		System.out.println("Quitting Reactive Animations Demo.\n");
		isRunning = false;
		ReactiveDemoGUI.quitReactive();
	}

	/**
	 * This method is instantly called whenever an input is received. This will
	 * extract the X and Y coordinates and call the currently selected animation
	 * 
	 * @param received
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable received, Object arg1) {
		if (isRunning) {
			if (DEBUG)
				System.out.println("Reactive Demo");
			if (this.pressed) {
				if (Input.getStatus()) {
					ReactiveDemoGUI.setLastInput(Input.getLastKeyPressed());

					positionX = Input.getPositionX();
					positionY = Input.getPositionY();
					if (positionX == 0 && positionY == 2) {
						type = (type + types - 1) % types;
						ReactiveDemoGUI.setAnimationName(getReaction());
						return;
					} else if (positionX == 0 && positionY == 3) {
						type = (type + 1) % types;
						ReactiveDemoGUI.setAnimationName(getReaction());
						return;
					} else if (positionX == quitX && positionY == quitY) {
						setRunning(false);
						ReactiveDemoGUI.quit();
						return;
					}

					try {
						this.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Starts Thread. Calls the selected animation
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		switch (type) {
		case 0:
			RandomLine(true);
			break;
		case 1:
			Star(false);
			break;
		case 2:
			Star(true);
			break;
		case 3:
			Firework();
		}
	}

	/**
	 * Animations a Plus or a Star based on input parameters
	 * 
	 * @param b
	 *            If false, display only a Plus. Otherwise display a Star
	 */
	private void Star(boolean b) {
		int duration = Calculate.randInt(100, 500);
		int colr = Calculate.randInt(0, 4);
		try {
			tech_demo.Line.lineWithDirection(positionX, positionY, 1, duration,
					colr);
			tech_demo.Line.lineWithDirection(positionX, positionY, 2, duration,
					colr);
			tech_demo.Line.lineWithDirection(positionX, positionY, 3, duration,
					colr);
			tech_demo.Line.lineWithDirection(positionX, positionY, 4, duration,
					colr);
			if (b) {
				tech_demo.Line.lineWithDirection(positionX, positionY, 5,
						duration, colr);
				tech_demo.Line.lineWithDirection(positionX, positionY, 6,
						duration, colr);
				tech_demo.Line.lineWithDirection(positionX, positionY, 7,
						duration, colr);
				tech_demo.Line.lineWithDirection(positionX, positionY, 8,
						duration, colr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a random line based on input parameters
	 * 
	 * @param withDiagonals
	 *            If true, allows line to be diagonal. if false, only horizontal
	 *            and vertical
	 */
	private void RandomLine(boolean withDiagonals) {
		try {
			tech_demo.Line.RandomLine(positionX, positionY, true);
		} catch (InvalidMidiDataException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a firework emerging from positions X and Y
	 */
	private void Firework() {
		Animation.Firework(positionX, positionY);
	}

	/**
	 * Sets the Reactive Demo status to running
	 * 
	 * @param b
	 *            False
	 */
	public static void setRunning(boolean b) {
		isRunning = b;
	}

	/**
	 * Returns true if the Reactive Demo is still running, false otherwise
	 * 
	 * @return boolean True if the demo is still running, false otherwise
	 */
	public static boolean isRunning() {
		return isRunning;
	}

	/**
	 * Returns the name of the currently used reactive animation
	 * 
	 * @return Current Reactive animation
	 */
	public static String getReaction() {
		String reaction = "";
		switch (type) {
		case 0:
			reaction = "Random Line";
			break;
		case 1:
			reaction = "Plus";
			break;
		case 2:
			reaction = "Star";
			break;
		case 3:
			reaction = "Fireworks";
			break;
		}
		return reaction;
	}

	/**
	 * Returns the currently running reactive animation's index
	 * 
	 * @return int currently running animation
	 */
	public static int getChoice() {
		return type;
	}

	/**
	 * Sets the currently running animation to the index given
	 * 
	 * @param value
	 *            index of the animation to select
	 */
	public static void setAnimation(int value) {
		type = value;
	}

	public static int getChoices() {
		return types;
	}
}
