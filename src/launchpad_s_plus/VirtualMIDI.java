package launchpad_s_plus;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

/**
 * Contains the simplified methods needed to send Virtual MIDI messages
 * 
 * @author Helion
 */
public class VirtualMIDI extends SetMidiMessage {
	static MidiDevice virtualin;
	static MidiDevice virtualout;
	static Transmitter vrtin;
	static Transmitter midiFromvrt;
	static Receiver midiTovrt;
	static Receiver vrtout;
	static Sequencer sequencer;

	/**
	 * Checks if there is the ability to use Virtual MIDI Messages
	 * 
	 * @return True if Virtual MIDI is available, False otherwise
	 */
	public static boolean found() {
		return SetupProcess.virtualMidiDetected;
	}

	/**
	 * Sends the virtual MIDI message based on the given parameters
	 * 
	 * @param value
	 *            MIDI location
	 * @param velocity
	 *            MIDI velocity
	 */
	public static void sendMessage(int value, int velocity) {
		try {
			SetMidiMessage vmessage = new SetMidiMessage("MIDI_ON", value,
					velocity, true);
			vmessage.sendVirtual();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}
}
