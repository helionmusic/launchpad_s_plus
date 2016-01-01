package launchpad_s_plus;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

/**
 * Determines the connections to the Launchpad S. <br>
 * Terminates the program if no Launchpad is connected.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Launchpad {
	static MidiDevice launchpadin;
	static MidiDevice launchpadout;
	static Transmitter lpin;
	static Transmitter midiFromLP;
	static Receiver midiToLP;
	static Receiver lpout;
	static Sequencer sequencer;
	public static int[][] map = new int[9][9];
	private static SetupProcess setup;

	/**
	 * Checks the a Launchpad S is connected
	 * 
	 * @return boolean True if the Launchpad has been found, False otherwise
	 * 
	 * @throws Exception
	 *             Exception
	 */
	public static boolean found() throws Exception {
		try {
			SetupProcess.resetMIDIList();
			boolean temp = SetupProcess.searchMidiDevice("Launchpad S", false);
			SetupProcess.lpdetected = temp;
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
			SetupProcess.lpdetected = false;
			return false;
		}
	}

	/**
	 * Creates a [9][9] map of int values representing the MIDI notes used by
	 * the Launchpad S, then attempts to setup the connections.
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 */
	public static void startLaunchpad() throws MidiUnavailableException,
			InterruptedException, InvalidMidiDataException {
		new Padmap();
		new DisplayDevices("together");
		setup = new SetupProcess();
	}

	/**
	 * Clears all pads that may be turned on and closes the connection.
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void stopLaunchpad() throws InvalidMidiDataException,
			InterruptedException {
		try {
			setup.clearPads();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(500);
		System.out.println("\nClosing Launchpad connection.");
		try {
			SetupProcess.closeLaunchpad();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the MidiDevice entry representative of the Launchpad S Input port
	 * 
	 * @return MidiDevice
	 */
	public static MidiDevice getInputDevice() {
		return launchpadin;
	}

	/**
	 * Returns the Receiver entry representative of the Launchpad S Output port
	 * 
	 * @return Receiver
	 */
	public static Receiver getOutputDevice() {
		return lpout;
	}

	/**
	 * Disables self-listening on the Launchpad, meaning pressing any pad on the
	 * launchpad will not automatically color the same pad. <br>
	 * Use this when you need to implement games or animations that require
	 * colors to be displayed and kept even when the user presses it.
	 */
	public static void disableSelfListen() {
		lpin.close();
	}

	/**
	 * Re-enables self-listening on the Launchpad, meaning pressing any pad will
	 * instantly color it with its default value: (Yellow color, strong
	 * intensity)
	 */
	public static void enableSelfListen() {
		try {
			lpin.setReceiver(lpout);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
