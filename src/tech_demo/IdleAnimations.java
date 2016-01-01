package tech_demo;

import java.util.*;

import launchpad_s_plus.*;

/**
 * This class will implement subtle animations that will play when the program
 * has been running on the main GUI screen with no game or tech demo running for
 * a while (probably 30 seconds). Just simple, not too bright and slow
 * animations.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

public class IdleAnimations extends TechDemo implements Observer {
	private boolean DEBUG = true;
	private static boolean isRunning = false;

	/**
	 * Constructor for Idle Animations
	 */
	public IdleAnimations() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (DEBUG)
			System.out.println("\nStarting Idle Animations");
		isRunning = true;
		name = "idle animations";
		super.setRunningDemoName(name);
		begin();
	}

	/**
	 * Used for setting the link to the Observable
	 * 
	 * @param b
	 *            False
	 */
	public IdleAnimations(boolean b) {
	}

	/**
	 * Randomly generates an animation from the available programmed ones after
	 * a delay between 1 second and 4 seconds
	 */
	private void begin() {
		while (isRunning) {
			int choice = Calculate.randInt(0, Animation.choicesAvailable() - 1);
			String selection = Animation.getChoice(choice);

			idleDelay();
			if (!isRunning) { // Just a check in case there was an input that
								// terminates the idle state
				return; // while waiting for the idle delay method to return
			}
			if (DEBUG)
				System.out.println("New Idle Animation: "
						+ Animation.getChoice(choice));
			Animation.setRunning(true);
			new Animation(selection);
		}
	}

	/**
	 * Returns after a delay calculated between 1 and 4 seconds
	 */
	private void idleDelay() {
		try {
			Thread.sleep(randomIdleDelay());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns after a value in ms to be used as a delay between animations. <br>
	 * Value returned is contained between 1 and 4 seconds
	 * 
	 * 
	 * @return int
	 */
	private int randomIdleDelay() {
		return Calculate.randInt(1000, 4000);
	}

	/**
	 * Terminates the idle animations if any button on the launchpad is pressed.
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public void update(Observable arg0, Object arg1) {
		if (isRunning)
			quit();
	}

	public void resetDemo() {
	}

	/**
	 * Terminates the idle animations
	 */
	private void quit() {
		if (DEBUG)
			System.out.println("Quitting Idle Animations");
		isRunning = false;
		Animation.setRunning(false);
		super.setRunningDemoName("none");
	}

	/**
	 * Method accessable publicly to terminate the idle animations.
	 */
	public void terminateIdle() {
		quit();
	}

	/**
	 * Returns true if the idle animations are still being generated, false
	 * otherwise
	 * 
	 * 
	 * @return boolean
	 */
	public static boolean isRunning() {
		return isRunning;
	}

}
