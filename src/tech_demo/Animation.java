package tech_demo;

import gui.AnimationGUI;
import gui.StatisticsGUI;

import java.util.Observable;
import java.util.Observer;

import launchpad_s_plus.*;

/**
 * Implements a couple of simple animations to be displayed on the Launchpad S. <br>
 * Includes a few command specific pads which are: <br>
 * - [0][2] Previous Animation <br>
 * - [0][3] Next Animation <br>
 * - [0][5] Reduce amount of simultaneous animations <br>
 * - [0][6] Increases amount of simultaneous animations <br>
 * - [0][7] Quit Animations <br>
 * 
 * <br>
 * Also includes a counter to know how many total animations have been
 * displayed.
 * 
 * <br>
 * <br>
 * Animations available: <br>
 * - Waves <br>
 * - Fireworks <br>
 * - Random lines <br>
 * - Big single wave
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Animation extends TechDemo implements Observer {
	private static boolean isRunning = false;
	private static boolean DEBUG = false;
	public static int chosen = 0;
	private static int choices = 4;

	private int wavecol = 0;
	private String animation;
	private static int totalCalls = 0;
	private static int simultaneous = 1;
	private static int maxSimultaneous = 5;
	private static int minSimultaneous = 1;

	private int quitX = 0;
	private int quitY = 7;

	/**
	 * Empty Constructor
	 */
	public Animation() {
	}

	/**
	 * Constructor for Animation.
	 * 
	 * @param a
	 *            Name of the animation to run
	 */
	public Animation(String a) {
		chosen = getChosen(a);
		animation = a;
		if (!a.equals("none"))
			incrementAnimationsCalled();
		if (isRunning)
			this.start();
	}

	/**
	 * Constructor for Animation.
	 * 
	 * @param a
	 *            Index of the animation to run
	 */
	public Animation(int a) {
		chosen = a;
		animation = getChoice();
		if (!animation.equals("none"))
			incrementAnimationsCalled();
		if (isRunning)
			this.start();
	}

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            False
	 */
	public Animation(boolean b) {
	}

	/**
	 * Returns the number of available different programmed animations
	 * 
	 * @return available animations
	 */
	public static int choicesAvailable() {
		return choices;
	}

	/**
	 * Implements the control buttons. This is instantly called when a pad is
	 * pressed. The method then checks which button is pressed and determines
	 * the action to be performed if any is needed.
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable arg0, Object arg1) {
		if (isRunning) {
			if (DEBUG)
				System.out.println("Starting animation");

			if (Input.getStatus()) {
				AnimationGUI.setLastInput(Input.getLastKeyPressed());

				int X = Input.getPositionX();
				int Y = Input.getPositionY();
				if (X == 0 && Y == 2) {
					chosen = (chosen + (choices - 1)) % choices;
					String animationChoice = getChoice();
					System.out.println("Animation choice is now: "
							+ animationChoice);
					AnimationGUI.setAnimationName(animationChoice);
					return;
				} else if (X == 0 && Y == 3) {
					chosen = (chosen + 1) % choices;
					String animationChoice = getChoice();
					System.out.println("Animation choice is now: "
							+ animationChoice);
					AnimationGUI.setAnimationName(animationChoice);
					return;
				} else if (X == quitX && Y == quitY) {
					setRunning(false);
					AnimationGUI.quitAnimation();
					if (DEBUG)
						System.out.println("Stopping animations");
					return;
				} else if (X == 0 && Y == 5) {
					if (simultaneous > minSimultaneous)
						simultaneous--;
					System.out.println("Simultaneous animations: "
							+ simultaneous);
				} else if (X == 0 && Y == 6) {
					if (simultaneous < maxSimultaneous)
						simultaneous++;
					System.out.println("Simultaneous animations: "
							+ simultaneous);
				} else
					return;
			} else
				return;
		}
	}

	/**
	 * Enables or disables animations
	 * 
	 * @param b
	 *            True if the animations should run, false otherwise
	 */
	public static void setRunning(boolean b) {
		isRunning = b;
	}

	/**
	 * Returns the currently playing animation as a string
	 * 
	 * @return String Name of the currently running animation
	 */
	public static String getChoice() {
		switch (chosen) {
		case 0:
			return "waves";
		case 1:
			return "fireworks";
		case 2:
			return "random lines";
		case 3:
			return "all rows";
		default:
			return "none";
		}
	}

	/**
	 * Returns the animation name based on input value
	 * 
	 * @param value
	 *            number value of the animation
	 * 
	 * @return String Name of the animation at the index given
	 */
	public static String getChoice(int value) {
		switch (value) {
		case 0:
			return "waves";
		case 1:
			return "fireworks";
		case 2:
			return "random lines";
		case 3:
			return "all rows";
		default:
			return "none";
		}
	}

	/**
	 * Gets the int value of the animation in its list based on an input String.
	 * Returns 4 if the animation has not been found
	 * 
	 * @param name
	 *            String with the animation to look for
	 * 
	 * @return int Index of the chosen animation
	 */
	private int getChosen(String name) {
		if (name.compareTo("waves") == 0 || name.compareTo("wave") == 0)
			return 0;
		else if (name.compareTo("fireworks") == 0)
			return 1;
		else if (name.compareTo("random lines") == 0)
			return 2;
		else if (name.compareToIgnoreCase("all rows") == 0)
			return 3;
		else
			return 4;
	}
	
	/**
	 * Increments the number of simultaneous particles for the animation
	 */
	public static void incrementSimultaneous(){
		if (simultaneous < maxSimultaneous)
			simultaneous++;
	}
	
	
	/**
	 * Decrements the number of simultaneous particles for the animation
	 */
	public static void decrementSimultaneous(){
		if (simultaneous > minSimultaneous)
			simultaneous--;
	}

	/**
	 * Returns the current numer of simultaneous particles for the animation
	 * @return simultaneous particles
	 */
	public static int getSimultaneousNumber(){
		return simultaneous;
	}
	
	/**
	 * Returns the current running animation's int list value
	 * 
	 * @return int Index of the currently selected animation
	 */
	public static int getCurrent() {
		return chosen;
	}

	/**
	 * Initiates the Thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		this.animate();
	}

	/**
	 * Calls the animation currently selected and replays it after a delay
	 */
	public void animate() {
		if (chosen == 0)
			Wave();
		else if (chosen == 1)
			Fireworks();
		else if (chosen == 2) {
			RandomLines();
			try {
				Thread.sleep(RandomLinesDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (chosen == 3) {
			AllRows();
		} else if (chosen == 7)
			new Line(1, launchpad_s_plus.Calculate.positionX(116, false),
					launchpad_s_plus.Calculate.positionY(116, false), 100,
					launchpad_s_plus.Colors.strong[launchpad_s_plus.Colors
							.colorValue("red")], 3, 50);
		else
			return;
	}

	/**
	 * Calls the wave animation
	 */
	public void Wave() {
		if (DEBUG)
			System.out.println("New wave animation");
		int tick;
		int duration = 150;
		wavecol = Calculate.randInt(0, 4);
		tick = 200;
		try {
			Line.lineWithDirection(0, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(1, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(2, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(3, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(4, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(5, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(6, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(7, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
			Line.lineWithDirection(8, 0, 4, duration, wavecol);
			Thread.sleep(tick / 3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Calls the random lines animation
	 */
	public void RandomLines() {
		if (DEBUG)
			System.out.println("New random lines animation");
		int direction, padi, padj, duration, colr, delay;
		delay = Calculate.randInt(0, 500);
		for (int i = 0; i < simultaneous; i++) {
			direction = Calculate.randInt(1, 4);
			padi = Calculate.randInt(0, 8);
			padj = Calculate.randInt(0, 8);
			duration = Calculate.randInt(100, 500);
			colr = Calculate.randInt(0, 4);
			new Line(direction, padi, padj, duration, colr, 3, 50);
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Returns a random number used for delaying in the Random Lines method
	 * 
	 * @return int
	 */
	public int RandomLinesDelay() {
		return Calculate.randInt(100, 600);
	}

	/**
	 * Displays a single firework emerging from an X and Y given
	 * 
	 * @param posX
	 *            X position of the firework
	 * @param posY
	 *            Y position of the firework
	 */
	public static void Firework(int posX, int posY) {
		int x = posX;
		int y = posY;
		int col = Calculate.randInt(0, 4);
		int dur = Calculate.randInt(100, 200);

		Firework(x, y, col, dur);
	}

	/**
	 * Calculate a random position, color and speed and displays a single
	 * firework with those settings
	 */
	public void Fireworks() {
		if (DEBUG)
			System.out.println("New fireworks animation");
		int delay = Calculate.randInt(100, 1000);
		for (int i = 0; i < simultaneous; i++) {
			int x = Calculate.randInt(0, 8);
			int y = Calculate.randInt(0, 8);
			int col = Calculate.randInt(0, 4);
			int dur = Calculate.randInt(100, 200);

			Firework(x, y, col, dur);
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a single firework based on input parameters
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * @param col
	 *            Color integer value
	 * @param dur
	 *            Speed at which the firework blasts
	 */
	public static void Firework(int x, int y, int col, int dur) {
		String fireworkColor = Colors.stringFromValue(col);
		Display.on(x, y, fireworkColor, 3, dur);
		try {
			Thread.sleep(dur);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Display.on(x + 1, y, fireworkColor, 3, dur);
		Display.on(x - 1, y, fireworkColor, 3, dur);
		Display.on(x, y + 1, fireworkColor, 3, dur);
		Display.on(x, y - 1, fireworkColor, 3, dur);
		Display.on(x + 1, y + 1, fireworkColor, 3, dur);
		Display.on(x + 1, y - 1, fireworkColor, 3, dur);
		Display.on(x - 1, y + 1, fireworkColor, 3, dur);
		Display.on(x - 1, y - 1, fireworkColor, 3, dur);
		try {
			Thread.sleep(dur);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Display.on(x + 2, y + 2, fireworkColor, 3, dur);
		Display.on(x + 2, y + 1, fireworkColor, 3, dur);
		Display.on(x + 2, y, fireworkColor, 3, dur);
		Display.on(x + 2, y - 1, fireworkColor, 3, dur);
		Display.on(x + 2, y - 2, fireworkColor, 3, dur);
		Display.on(x + 1, y + 2, fireworkColor, 3, dur);
		Display.on(x + 1, y - 2, fireworkColor, 3, dur);
		Display.on(x, y + 2, fireworkColor, 3, dur);
		Display.on(x, y - 2, fireworkColor, 3, dur);
		Display.on(x - 1, y + 2, fireworkColor, 3, dur);
		Display.on(x - 1, y - 2, fireworkColor, 3, dur);
		Display.on(x - 2, y + 2, fireworkColor, 3, dur);
		Display.on(x - 2, y + 1, fireworkColor, 3, dur);
		Display.on(x - 2, y, fireworkColor, 3, dur);
		Display.on(x - 2, y - 1, fireworkColor, 3, dur);
		Display.on(x - 2, y - 2, fireworkColor, 3, dur);
		return;
	}

	/**
	 * Turns on all pads at the same time for a different duration. Looks like a
	 * big single wave
	 */
	public void AllRows() {
		int colored = totalCalls % 5;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int dur = (200 + ((2 * i) * ((2 * j) + 50)));
				Display.on(j, i, Colors.stringFromValue(colored), 3, dur);
			}
		}
		return;
	}

	/**
	 * Increments the current session's number of Animations called
	 */
	public static void incrementAnimationsCalled() {
		totalCalls++;
		StatisticsGUI.setCurrentAnimationsCalled(totalCalls);
	}

	/**
	 * Displays the total number of animations called
	 */
	public static void displayNumberOfAnimationsCalled() {
		System.out.println("Total animations called: " + totalCalls);
		return;
	}

	/**
	 * Returns the number of animations called during the current session
	 * 
	 * @return Animations called
	 */
	public static int getNumberOfAnimationsCalled() {
		return totalCalls;
	}

	/**
	 * Checks there is an animation running
	 * 
	 * @return True if there is an animation running, False otherwise
	 */
	public static boolean isRunning() {
		return isRunning;
	}

	/**
	 * Sets isRunning to false so that no more animations are displayed
	 */
	public static void quitAnimation() {
		instanceRunning = false;
	}

	/**
	 * Resets the Animations
	 */
	public void resetDemo() {
		animation = "none";
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		launchpad_s_plus.Display.clearPads();
	}
}
