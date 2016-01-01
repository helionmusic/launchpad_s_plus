package launchpad_s_plus;

/**
 * This class contains the Launchpad S specific MIDI velocity values used for
 * output.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

public class Colors {
	// Full intensity
	private static int yellow = 126;
	private static int green = 124;
	private static int red = 79;
	private static int orange = 95;
	private static int lime = 125;
	// Slightly dimmed
	private static int dimyellow = 57;
	private static int dimgreen = 108;
	private static int dimred = 78;
	private static int dimorange = 22;
	private static int dimlime = 109;
	// Very dimmed
	private static int weakyellow = 45;
	private static int weakgreen = 92;
	private static int weakred = 77;
	private static int weakorange = 93;
	private static int weaklime = 33;

	public static int[] strong = { green, yellow, red, lime, orange };
	public static int[] mid = { dimgreen, dimyellow, dimred, dimlime, dimorange };
	public static int[] weak = { weakgreen, weakyellow, weakred, weaklime,
			weakorange };

	/**
	 * Returns the integer index of the required color
	 * 
	 * @param name
	 *            Name of the color
	 * @return int Index of the color
	 */
	public static int colorValue(String name) {
		if (name.equals("green"))
			return 0;
		else if (name.equals("yellow"))
			return 1;
		else if (name.equals("red"))
			return 2;
		else if (name.equals("lime"))
			return 3;
		else if (name.equals("orange"))
			return 4;
		else
			return 1;
	}

	/**
	 * Returns the name of the color located at the given index
	 * 
	 * @param value
	 *            Index of the color
	 * @return String Name of the color
	 */
	public static String stringFromValue(int value) {
		switch (value) {
		case 0:
			return "green";
		case 1:
			return "yellow";
		case 2:
			return "red";
		case 3:
			return "lime";
		case 4:
			return "orange";
		default:
			return "green";
		}
	}

	/**
	 * Returns a random color to be used with Display.on methods
	 * 
	 * @return Random color
	 */
	public static String randomColor() {
		return stringFromValue(Calculate.randInt(0, 4));
	}
}
