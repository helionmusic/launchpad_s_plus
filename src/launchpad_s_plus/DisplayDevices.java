package launchpad_s_plus;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * This class is used to display available MIDI devices on the console
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class DisplayDevices {
	private static MidiDevice.Info[] midilist = MidiSystem.getMidiDeviceInfo();

	/**
	 * Change the string choice to either: <br>
	 * - input: display only MIDI ports that allow input <br>
	 * - output: display only MIDI ports that allow output <br>
	 * - display: display all MIDI ports available
	 */
	DisplayDevices() {
		String choice = "both";
		try {
			display(choice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays all available MIDI devices.<br>
	 * Input parameter chooses whether to display only input MIDI devices,
	 * output or both
	 * 
	 * @param InorOut
	 *            Change to either "input" or "output" or "both"
	 */
	DisplayDevices(String InorOut) {
		try {
			display(InorOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays available MIDI devices
	 * 
	 * @param InorOut
	 *            "input" or "output" or "both"
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 */
	private void display(String InorOut) throws MidiUnavailableException {
		resetMidilist();
		if (InorOut.compareTo("input") == 0) {
			System.out.println("Available MIDI In systems and ports:");
			for (int i = 0; i < midilist.length; i++) {
				MidiDevice device = MidiSystem.getMidiDevice(midilist[i]);
				if (device.getMaxTransmitters() != 0)
					System.out.println("\t" + i + "\t" + midilist[i]);
			}
		} else if (InorOut.compareTo("output") == 0) {
			System.out.println("Available MIDI Out systems and ports:");
			for (int i = 0; i < midilist.length; i++) {
				MidiDevice device = MidiSystem.getMidiDevice(midilist[i]);
				if (device.getMaxReceivers() != 0)
					System.out.println("\t" + i + "\t" + midilist[i]);
			}
		} else if (InorOut.compareTo("together") == 0) {
			display("input");
			display("output");
		} else {
			System.out.println("Available MIDI systems and ports:");
			for (int i = 0; i < midilist.length; i++) {
				System.out.println("\t" + i + "\t" + midilist[i]);
			}
		}
	}

	/**
	 * Refreshes the list of available MIDI Devices
	 */
	public static void resetMidilist() {
		midilist = MidiSystem.getMidiDeviceInfo();
	}
}
