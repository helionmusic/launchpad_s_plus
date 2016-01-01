package launchpad_s_plus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This class is used: <br>
 * - to obtain the X or Y position of a pad based on its MIDI note value <br>
 * - to obtain the MIDI note value of a pad based on its X and Y positions <br>
 * - to return a random integer based on a min and max values
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Calculate {
	/**
	 * Unused Constructor
	 */
	public Calculate() {

	}

	/**
	 * Returns a randomized number between a min and max, included.
	 * 
	 * @param min
	 *            Minimum number
	 * @param max
	 *            Maximum number
	 * 
	 * @return Random integer between both min and max inclusive
	 */
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/**
	 * Returns the X position of a pad based on its MIDI note value
	 * 
	 * @param value
	 *            MIDI note value
	 * @param isCC
	 *            If the MIDI message is a Control Change message, this needs to
	 *            be true
	 * 
	 * @return int X location
	 */
	public static int positionX(int value, boolean isCC) {
		return Padmap.getxposition(value, isCC);
	}

	/**
	 * Returns the Y position of a pad based on its MIDI note value
	 * 
	 * @param value
	 *            MIDI note value
	 * @param isCC
	 *            If the MIDI message is a Control Change message, this needs to
	 *            be true
	 * 
	 * @return int Y location
	 */
	public static int positionY(int value, boolean isCC) {
		return Padmap.getyposition(value, isCC);
	}

	/**
	 * Returns the MIDI note value of the pad based on the given X and Y
	 * positions
	 * 
	 * @param x
	 *            X position of the pad
	 * @param y
	 *            Y position of the pad
	 * 
	 * @return MIDI note value
	 */
	public static int position(int x, int y) {
		return Launchpad.map[x][y];
	}

	/**
	 * Returns current date as String
	 * 
	 * 
	 * @return Current Date
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		return dateFormat.format(date);
	}
}
