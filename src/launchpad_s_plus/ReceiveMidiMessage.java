package launchpad_s_plus;

import java.util.*;

import piano.Piano;
import games.*;
import tech_demo.*;

/**
 * This class is used to notify all observers that an input has been received.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class ReceiveMidiMessage extends Observable {
	boolean DEBUG = false;
	private static int positionX;
	private static int positionY;
	private static boolean status;
	public static boolean received = false;

	private static int padsPressed = 0;
	private static int padsReleased = 0;
	private static int dataReceived = 0;

	/**
	 * Constructor called every time there is a received input from the
	 * Launchpad S. Also notifies all observers
	 * 
	 * @param x
	 *            X location of the received input
	 * @param y
	 *            Y location of the received input
	 * @param onOrOff
	 *            True if the received input was a key press, False if it was a
	 *            key release
	 */
	public ReceiveMidiMessage(int x, int y, boolean onOrOff) {
		if (onOrOff)
			incrementPadsPressed();
		else
			padsReleased++;
		positionX = x;
		positionY = y;
		status = onOrOff;
		this.addObserver(new ReactiveDemo(x, y, status));
//		this.addObserver(new Checkers(false));
		this.addObserver(new Checkers2(false));
		this.addObserver(new Animation(false));
		this.addObserver(new Snake(false));
		this.addObserver(new GameOfLife(false));
		this.addObserver(new IdleAnimations(false));
		this.addObserver(new Piano(false));
		this.addObserver(new ReactionGame(false));
	}

	/**
	 * Notifies listeners that an input has been received
	 */
	public void notifyPressed() {
		if (DEBUG)
			System.out.println("Notifying Observers of "
					+ ((status) ? "key press" : "key release"));

		setChanged();
		if (DEBUG)
			System.out.println("Observable flag changed.");

		notifyObservers(status);
		if (DEBUG)
			System.out.println("Observers notified.");
	}

	/**
	 * Returns X position of the last received or released key
	 * 
	 * 
	 * @return int
	 */
	public static int getPositionX() {
		return positionX;
	}

	/**
	 * Returns Y position of the last receive or released key
	 * 
	 * 
	 * @return Y position of the last update received
	 */
	public static int getPositionY() {
		return positionY;
	}

	/**
	 * Returns true if the last action was a key press <br>
	 * Returns false if the last action was a key release
	 * 
	 * 
	 * @return True if the last update was a key press, False if it was a key
	 *         release
	 */
	public static boolean getStatus() {
		return status;
	}

	/**
	 * Increments the number of the current session's Pads Pressed and updates
	 * the statistics
	 */
	private static void incrementPadsPressed() {
		padsPressed++;
		Input.updatePadsPressed();
	}

	/**
	 * Returns the current session's Pads Pressed
	 * 
	 * 
	 * @return Current session's Pads Pressed
	 */
	public static int getNumberOfPadsPressed() {
		return padsPressed;
	}

	/**
	 * Returns the current session's Received Data in Bytes
	 * 
	 * 
	 * @return Amount of Data Received in Bytes
	 */
	public static int getDataReceived() {
		dataReceived = (padsPressed + padsReleased) * 8;
		return dataReceived;
	}
}
