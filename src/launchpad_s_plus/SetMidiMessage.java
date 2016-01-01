package launchpad_s_plus;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Receiver;

/**
 * Class used to set and send the MIDI Messages to the Launchpad S. <br>
 * No need to use this class directly as it is implemented by DisplayPad.java
 * for simplicity.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class SetMidiMessage {
	static ShortMessage midiMessage = new ShortMessage();

	/**
	 * Unused Constructor
	 */
	SetMidiMessage() {
		System.out.println("Test");
	}

	/**
	 * Sets the MIDI Message based on input parameters
	 * 
	 * @param type
	 *            MIDI or MIDI_ON for note on; MIDI_OFF for note off; CC for
	 *            control change
	 * @param lpout
	 *            Launchpad S output port
	 * @param location
	 *            Integer value of the position. Determined by Padmap[x][y]
	 * @param color
	 *            Integer value of the color. Determined by Colors class
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 */
	SetMidiMessage(String type, Receiver lpout, int location, int color)
			throws InvalidMidiDataException {
		if (type.equals("MIDI") || type.equals("MIDI_ON")) {
			midiMessage.setMessage(ShortMessage.NOTE_ON, 0, location, color);
		} else if (type.equals("MIDI_OFF")) {
			midiMessage.setMessage(ShortMessage.NOTE_OFF, 0, location, 0);
		} else if (type.equals("CC")) {
			midiMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, location,
					color);
		} else {
			System.out.println("Wrong Midi Message set");
		}
	}

	/**
	 * Sets the MIDI Message based on input parameters
	 * 
	 * @param type
	 *            MIDI or MIDI_ON for note on; MIDI_OFF for note off; CC for
	 *            control change
	 * @param location
	 *            Integer value of the position. Determined by Padmap[x][y]
	 * @param velocity
	 *            Integer value of the color. Determined by Colors class
	 * @param virtual
	 *            True if the message should be sent through Virtual MIDI (if
	 *            available)
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 */
	SetMidiMessage(String type, int location, int velocity, boolean virtual)
			throws InvalidMidiDataException {
		if (!virtual)
			return;
		if (type.equals("MIDI") || type.equals("MIDI_ON")) {
			midiMessage.setMessage(ShortMessage.NOTE_ON, 0, location, velocity);
		} else if (type.equals("MIDI_OFF")) {
			midiMessage.setMessage(ShortMessage.NOTE_OFF, 0, location, 0);
		} else if (type.equals("CC")) {
			midiMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, location,
					velocity);
		} else {
			System.out.println("Wrong Midi Message set");
		}
	}

	/**
	 * Sends the MIDI message that was created through Virtual MIDI
	 */
	public void sendVirtual() {
		VirtualMIDI.vrtout.send(midiMessage, -1);
	}

	/**
	 * Sends the MIDI message that was created to the Launchpad S
	 */
	public void send() {
		Launchpad.lpout.send(midiMessage, -1);
	}
}
