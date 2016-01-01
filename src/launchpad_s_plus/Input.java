package launchpad_s_plus;

import piano.Piano;
import gui.Main;
import gui.StatisticsGUI;

/**
 * Class used to determine input from the Launchpad S
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Input {
	private static boolean DEBUG = false;
	private static boolean DISPLAYPAD = false;
	private static boolean DISPLAYON = true;
	private static boolean DISPLAYOFF = false;
	private static boolean tempDISPLAYON = DISPLAYON;
	private static boolean tempDISPLAYOFF = DISPLAYOFF;
	public static boolean[][] isPressed = new boolean[9][9];
	private static boolean waitingForInput = false;
	private static String lastKeyPressed = "none";
	private static String lastKeyReleased = "none";
	private static int lastKeyPressedPosition = 0;
	private static int lastKeyReleasedPosition = 0;
	private static int lastKeyPressedX = 0;
	private static int lastKeyPressedY = 0;

	/**
	 * Displays every key press on the console if DISPLAYON is true
	 */
	public static void displayOn() {
		if (DISPLAYON)
			System.out.println("\tLast key pressed: " + lastKeyPressed);
	}

	/**
	 * Displays every key release on the console if DISPLAYOFF is true
	 */
	public static void displayOff() {
		if (DISPLAYOFF)
			System.out.println("\tLast key released: " + lastKeyReleased);
	}

	/**
	 * Clears the status of the key press map. Use this if your implementing
	 * function uses the isPressed[][] map to determine actions that follow
	 */
	public static void clearPressed() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				isPressed[i][j] = false;
			}
		}
		lastKeyPressed = "none";
		lastKeyReleased = "none";
		lastKeyPressedX = -1;
		lastKeyPressedY = -1;
		lastKeyPressedPosition = -1;
		lastKeyReleasedPosition = -1;
		ReceiveMidiMessage.received = false;
	}

	/**
	 * Either sets a pad to pressed or not based on the input parameters
	 * 
	 * @param i
	 *            X location of the pad
	 * @param j
	 *            Y location of the pad
	 * @param status
	 *            true: Pressed - false: Released
	 */
	public static void setPressed(int i, int j, boolean status) {
		if (DEBUG)
			if (status)
				System.out.println("Pressing " + i + " - " + j);
			else
				System.out.println("Releasing " + i + " - " + j);
		if (!status) {
			setUnpressed(i, j);
			displayOff();
		} else {
			ReceiveMidiMessage.received = true;
			isPressed[i][j] = true;
			setLastKeyPressedPosition(Launchpad.map[i][j]);
			lastKeyPressedX = i;
			lastKeyPressedY = j;
			displayOn();
			ReceiveMidiMessage incoming = new ReceiveMidiMessage(i, j, true);
			incoming.notifyPressed();
		}
		if (DISPLAYPAD) {
			displayStatus();
			System.out.println("");
		}
	}

	/**
	 * Sets the pressed flag of a pad to false
	 * 
	 * @param i
	 *            X location of the pad
	 * @param j
	 *            Y location of the pad
	 */
	public static void setUnpressed(int i, int j) {
		if (DEBUG) {
			System.out.println("Releasing" + i + " - " + j);
			displayStatus();
		}
		isPressed[i][j] = false;
		setLastKeyReleasedPosition(Launchpad.map[i][j]);
		ReceiveMidiMessage incoming = new ReceiveMidiMessage(i, j, false);
		incoming.notifyPressed();
	}

	/**
	 * Displays the map of the current pressed pads on the console 1 for
	 * pressed, 0 for released
	 */
	public static void displayStatus() {
		System.out.println();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(((isPressed[i][j]) ? 1 : 0) + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Sets string value of last pressed key
	 * 
	 * @param key
	 *            String value of the last key pressed
	 */
	public static void setLastKeyPressed(String key) {
		ReceiveMidiMessage.received = true;
		lastKeyPressed = key;
		if (!Piano.isRunning())
			Main.setLastReceivedInput(key);
	}

	/**
	 * Sets integer value of last pressed key
	 * 
	 * @param value
	 *            int value of the last key pressed
	 */
	public static void setLastKeyPressedPosition(int value) {
		ReceiveMidiMessage.received = true;
		lastKeyPressedPosition = value;
	}

	/**
	 * Sets string value of last released key
	 * 
	 * @param key
	 *            String value of the last key released
	 */
	public static void setLastKeyReleased(String key) {
		lastKeyReleased = key;
	}

	/**
	 * Sets integer value of last released key
	 * 
	 * @param value
	 *            int MIDI position of the last key released
	 */
	public static void setLastKeyReleasedPosition(int value) {
		lastKeyReleasedPosition = value;
	}

	/**
	 * Returns the integer value of last released key
	 * 
	 * 
	 * @return int int MIDI position of the last key released
	 */
	public static int getLastKeyReleasedPosition() {
		return lastKeyReleasedPosition;
	}

	/**
	 * Returns String value of last key pressed
	 * 
	 * 
	 * @return String Name of the last key pressed
	 */
	public static String getLastKeyPressed() {
		return lastKeyPressed;
	}

	/**
	 * Returns integer value of last key pressed
	 * 
	 * 
	 * @return int MIDI position of the last key pressed
	 */
	public static int getLastKeyPosition() {
		return lastKeyPressedPosition;
	}

	/**
	 * Returns X position of last key pressed
	 * 
	 * 
	 * @return int Launchpad X position of the last key pressed
	 */
	public static int getLastKeyI() {
		return lastKeyPressedX;
	}

	/**
	 * Returns Y position of last key pressed
	 * 
	 * 
	 * @return int Launchpad Y position of the last key pressed
	 */
	public static int getLastKeyJ() {
		return lastKeyPressedY;
	}

	/**
	 * Does nothing until there is a key pressed after the method is called.
	 * This is used to hold the thread of the calling method until there is
	 * input.
	 * 
	 * <br>
	 * <br>
	 * Generally for use with while(!waitForInput()){ do stuff; } or with
	 * waitForInput(); do stuff after;
	 * 
	 * <br>
	 * <br>
	 * Returns true when there is input.
	 * 
	 * 
	 * @return boolean True when there was an input
	 */
	public static boolean waitForInput() {
		ReceiveMidiMessage.received = false;
		waitingForInput = true;
		while (!ReceiveMidiMessage.received && waitingForInput) {
			if (Main.isClosing())
				return true;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * Stops waiting for input. Allows the waitForInput() function to terminate
	 */
	public static void stopWaitForInput(){
		waitingForInput = false;
	}

	/**
	 * Returns if the last input received was a key press or release
	 * 
	 * 
	 * @return boolean Returns True if the last key changed was a press, False
	 *         if it was released
	 */
	public static boolean getStatus() {
		return ReceiveMidiMessage.getStatus();
	}

	/**
	 * Returns X position of last input
	 * 
	 * 
	 * @return int Launchpad X position of the last update received
	 */
	public static int getPositionX() {
		return ReceiveMidiMessage.getPositionX();
	}

	/**
	 * Returns Y position of last input
	 * 
	 * 
	 * @return int Launchpad Y position of the last update received
	 */
	public static int getPositionY() {
		return ReceiveMidiMessage.getPositionY();
	}

	/**
	 * Turns off displaying of key presses and releases on the console
	 */
	public static void disableConsoleEcho() {
		tempDISPLAYON = DISPLAYON;
		tempDISPLAYOFF = DISPLAYOFF;
		DISPLAYON = DISPLAYOFF = false;
	}

	/**
	 * Turns off displaying of key presses on console
	 */
	public static void disableEchoNotesOn() {
		DISPLAYON = false;
	}

	/**
	 * Turns off displaying of key receives on console
	 */
	public static void disableEchoNotesOff() {
		DISPLAYOFF = false;
	}

	/**
	 * Resets console output to what it was before disableConsoleEcho() was
	 * called
	 */
	public static void reenableConsoleEcho() {
		DISPLAYON = tempDISPLAYON;
		DISPLAYOFF = tempDISPLAYOFF;
	}

	/**
	 * Turns on displaying of key presses on console
	 */
	public static void enableEchoNotesOn() {
		DISPLAYON = true;
	}

	/**
	 * Turns on displaying of key releases on console
	 */
	public static void enableEchoNotesOff() {
		DISPLAYOFF = true;
	}

	/**
	 * Returns the number of user pad presses during the current session
	 * 
	 * 
	 * @return Number of pads pressed
	 */
	public static int getNumberOfPadsPressed() {
		return ReceiveMidiMessage.getNumberOfPadsPressed();
	}

	/**
	 * Returns the amount of bytes received from the Launchpad S during the
	 * current session
	 * 
	 * 
	 * @return Bytes received
	 */
	public static int getDataReceived() {
		return ReceiveMidiMessage.getDataReceived();
	}

	/**
	 * Updates pads pressed statistics
	 */
	public static void updatePadsPressed() {
		StatisticsGUI.setCurrentPadsPressed(getNumberOfPadsPressed());
		StatisticsGUI.setCurrentDataReceived(getDataReceived());
	}
}
