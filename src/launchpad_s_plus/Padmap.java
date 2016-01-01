package launchpad_s_plus;

/**
 * Creates a pad map continaing the values needed to determine positions of the
 * Launchpad S pads
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Padmap {
	boolean displayMap = true;

	public Padmap() {
		setPadmap();
	}

	/**
	 * Sets the integer values of the Midi notes for every pad. <br>
	 * These values will be used by DisplayPad.java to determine what Midi
	 * Message to send
	 */
	public void setPadmap() {
		if (displayMap)
			System.out.println("Displaying Pad mapping:\n");

		for (int i = 0; i < 8; i++) {
			Launchpad.map[0][i] = 104 + 24 + i; // These are CC Message based
			if (displayMap)
				System.out.print("CC " + Launchpad.map[0][i] + "\t");
		}
		Launchpad.map[0][8] = -1;
		if (displayMap) {
			System.out.print(Launchpad.map[0][8]);
			System.out.println();
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 9; j++) {
				Launchpad.map[i + 1][j] = (16 * i) + j;
				if (displayMap)
					System.out.print(((16 * i) + j) + "\t");
			}
			if (displayMap)
				System.out.println();
		}
		if (displayMap)
			System.out.println();
	}

	/**
	 * Returns X position based on the integer value given
	 * 
	 * @param value
	 *            MIDI value of the position
	 * @param isCC
	 *            True if the key is a MIDI Control Change
	 * 
	 * @return X position
	 */
	public static int getxposition(int value, boolean isCC) {
		if (!isCC)
			return (value / 16) + 1;
		else
			return 0;
	}

	/**
	 * Returns Y position based on the integer value given
	 * 
	 * @param value
	 *            MIDI value of the position
	 * @param isCC
	 *            True if the key is a MIDI Control Change
	 * 
	 * @return X position
	 */
	public static int getyposition(int value, boolean isCC) {
		if (!isCC)
			return (value % 16);
		else
			return (value - 104);
	}
}
