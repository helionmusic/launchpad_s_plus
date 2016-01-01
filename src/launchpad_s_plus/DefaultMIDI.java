package launchpad_s_plus;

/**
 * Contains the simplified methods needed to send Virtual MIDI messages
 * 
 * @author Helion
 */
public class DefaultMIDI {
	/**
	 * Sends the default MIDI message based on the given parameters
	 * 
	 * @param value
	 *            MIDI location
	 * @param velocity
	 *            MIDI velocity
	 */
	public static void sendMessage(int value, int velocity) {
        SetupProcess.defaultChannels[1].noteOn(value, velocity);
	}
}
