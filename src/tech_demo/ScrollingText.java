package tech_demo;

import gui.Main;
import gui.TextScrollGUI;

import java.util.*;

import launchpad_s_plus.*;

/**
 * This class implements a way to display text on the Launchpad S.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class ScrollingText extends TechDemo implements Observer {
	private static boolean DEBUG = true;
	static boolean looping = false;
	static boolean stopping = false;
	static boolean forceQuitting = false;

	static String message;
	static String color;
	static int intensity;
	static int speed;

	/**
	 * Empty Constructor
	 */
	public ScrollingText() {
		demoName = "Scrolling Text";
	}

	/**
	 * Increments the total tech demos ran and starts the demo
	 */
	public void begin() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("\nStarting Scrolling Text Demo");
		setRunningDemoName(demoName);
		setRunning(true);
		incrementDemosRan();
		resetValues();
		while (!stopping)
			scroll();
	}

	/**
	 * Resets all the variables used for the demo
	 */
	private static void resetValues() {
		stopping = false;
		forceQuitting = false;
		Input.setLastKeyPressed("None");

		message = TextScrollGUI.getText();
		color = TextScrollGUI.getColor();
		intensity = TextScrollGUI.getIntensity();
		speed = TextScrollGUI.getSpeed();
		looping = TextScrollGUI.getLooping();
	}

	/**
	 * Displays the text on the Launchpad S
	 */
	public static void scroll() {
		resetValues();
		System.out.println("\nDisplaying message: " + message);
		Display.text(message, color, intensity, speed);
		looping = true;
		int loopcount = 1;
		Input.clearPressed();

		while (looping) {
			delay(10);
			if (forceQuitting || !looping) {
				stopping = true;
				continue;
			}
			if (stopping || Input.isPressed[1][8]) {
				System.out.println("Stopping text early");
				break;
			}
			if (Input.getLastKeyPressed().equals("CC 0")) {
				if (TextScrollGUI.getLooping()) {
					loopcount++;
					Input.setLastKeyPressed("None");
				} else {
					stopping = true;
				}
			}
		}
		if (!looping) {
			if (!forceQuitting)
				Input.waitForInput();
		}
		delay(10);
		Display.stopText();
		System.out.println(loopcount + " loops performed");
	}

	/**
	 * Stops scrolling text
	 */
	public static void stopScroll() {
		stopping = true;
	}

	/**
	 * Restarts the scrolling text
	 */
	public static void restartScroll() {
		stopping = true;
		delay(10);
		stopping = false;
		delay(10);
		scroll();
	}

	/**
	 * Sets the text to display
	 * 
	 * @param text
	 *            Text to display
	 */
	public static void setText(String text) {
		message = text;
		if (DEBUG)
			System.out.println("Text set to " + message);
	}

	/**
	 * Sets the speed at which to scroll the text at
	 * 
	 * @param value
	 *            Speed of the text
	 */
	public static void setSpeed(int value) {
		System.out.println("New speed " + value);
		if (value < 1)
			speed = 1;
		else if (value > 7)
			speed = 7;
		else
			speed = value;

		if (DEBUG)
			System.out.println("Speed set to " + speed);
	}

	/**
	 * Sets the color of the text
	 * 
	 * @param col
	 *            Color of the text
	 */
	public static void setColor(String col) {
		color = col;
		if (DEBUG)
			System.out.println("Color set to " + color);
	}

	/**
	 * Sets the intensity of the text
	 * 
	 * @param value
	 *            Intensity of the text. Range: 0 to 3
	 */
	public static void setIntensity(int value) {
		intensity = value;
		if (DEBUG)
			System.out.println("Intensity set to " + intensity);
	}

	/**
	 * Sets the looping status of the text
	 * 
	 * @param status
	 *            True if the text should loop, False otherwise
	 */
	public static void setLoop(boolean status) {
		looping = status;
		if (DEBUG)
			System.out.println("Looping set to " + looping);
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param time
	 *            Time in MS
	 */
	private static void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unused
	 */
	@Override
	public void resetDemo() {
	}

	/**
	 * Processes input from the Launchpad. Unused in this demo
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
	}
	
	private static void quitDemo(){
		if (stopping){
			setRunningDemoName("none");
			System.out.println("Quitting Scrolling Text Demo");
			setRunning(false);
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(300);
			Main.setClosing(false);
			TextScrollGUI.quit();
		}
		stopping = false;
	}

	/**
	 * Stops the text from scrolling and quits
	 */
	public static void quit() {
		stopping = true;
	}

	/**
	 * Forcefully stops the text from scrolling and quits
	 */
	public static void forceQuit() {
		stopping = true;
		forceQuitting = true;
		looping = false;
	}

}
