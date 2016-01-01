package launchpad_s_plus;

import javax.sound.midi.*;

/**
 * Sets up the connection to the Launchpad S and to the internal virtual MIDI
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class SetupProcess {
	static MidiDevice.Info[] midilist = MidiSystem.getMidiDeviceInfo();
	static String desc;

	static int lpfound = 0;
	static int inPort = -1;
	static int outPort = -1;
	public static boolean lpdetected;

	static boolean lpAlreadySetup = false;
	static boolean virtualAlreadySetup = false;
	static int virtualfound = 0;
	static int virtualInPort = -1;
	static int virtualOutPort = -1;
	static boolean virtualMidiDetected;
	static Synthesizer defaultSynth;
	static MidiChannel[] defaultChannels;


	/**
	 * Initiates the setup process. Searches for the launchpad and calls the
	 * methods that open input and output ports if one has been found.
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 */
	public SetupProcess() throws MidiUnavailableException,
			InterruptedException, InvalidMidiDataException {
		
		lpdetected = searchMidiDevice("Launchpad S", true);
		if (!lpdetected) {
			System.out.print("Terminating...");
			return;
		}

		if (!virtualAlreadySetup) {
			virtualMidiDetected = searchInternalMidiDevice("Internal MIDI");
			if (!virtualMidiDetected)
				System.out.print("Piano Demo will use default MIDI to play samples");
			else
				System.out
						.println("Piano Demo can use virtual MIDI to play samples");
		}
		

		setupLaunchpad();
		Input.clearPressed();

		if (virtualMidiDetected && !virtualAlreadySetup)
			setupVirtualMIDI();
		
		setupDefaultMIDI();

		clearPads();
		Thread.sleep(200);
	}
	
	private void setupDefaultMIDI(){
		try {
			defaultSynth = MidiSystem.getSynthesizer();
			defaultSynth.open();
			defaultChannels = defaultSynth .getChannels();
            defaultChannels[1].programChange(0);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches for an Internal MIDI device by name
	 * 
	 * @param name
	 *            Name of the MIDI device to look for
	 * @return True if the device has been found, False otherwise
	 */
	private boolean searchInternalMidiDevice(String name) {
		try {
			for (int j = 0; j < midilist.length; j++) {
				desc = midilist[j].toString();
				if (desc.contains(name)) {
					MidiDevice device = MidiSystem.getMidiDevice(midilist[j]);
					if (device.getMaxReceivers() != 0) {
						System.out.println("\n" + desc + " output at position "
								+ j);
						virtualOutPort = j;
					} else {
						System.out.println("\n" + desc + " input at position "
								+ j);
						virtualInPort = j;
					}
					virtualfound = j;
					break;
				}
			}
			for (int j = 0; j < midilist.length; j++) {
				desc = midilist[j].toString();
				if (desc.contains(name)) {
					MidiDevice device = MidiSystem.getMidiDevice(midilist[j]);
					if (device.getMaxTransmitters() == 0
							&& virtualOutPort == -1) {
						System.out.println(desc + " output at position " + j);
						virtualOutPort = j;
					}
					continue;
				}
			}

			if (virtualInPort == -1 && virtualOutPort == -1) { // If no Internal
																// Midi is
																// connected
				System.out.println("\nNo " + name + " found.");
				return false;
			} else if (virtualInPort == -1 && virtualOutPort != -1) { // If
																		// there
																		// is
																		// only
																		// output
				// port found - error
				System.out.println("\n" + name + " Input port not found");
				return false;
			} else if (virtualInPort != -1 && virtualOutPort == -1) { // If
																		// there
																		// is
																		// only
																		// input
				// port found - error
				System.out.println("\n" + name + " Output port not found");
				return false;
			} else
				// Internal Midi ports found
				System.out.println(name + " found. \tInput port: "
						+ virtualInPort + "\tOutput port: " + virtualOutPort);
			return true;
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Displays all available midi ports and the device associated
	 */
	public void displayDevices() {
		System.out.println("Available midi systems:");
		for (int i = 0; i < midilist.length; i++) {
			System.out.println("\t" + midilist[i]);
		}
	}

	/**
	 * Searches a midi device that's description contains the name given
	 * 
	 * @param name
	 *            Name or portion of the name to search for
	 * @param display
	 *            Boolean: true if console output of the positions should be
	 *            displayed, false otherwise
	 * 
	 * @return True if found input and output ports for the device, false
	 *         otherwise
	 */
	public static boolean searchMidiDevice(String name, boolean display) {
		try {
			for (int j = 0; j < midilist.length; j++) {
				desc = midilist[j].toString();
				if (desc.contains(name)) {
					MidiDevice device = MidiSystem.getMidiDevice(midilist[j]);
					if (device.getMaxReceivers() != 0) {
						if (display)
							System.out.println("\n" + desc
									+ " output at position " + j);
						outPort = j;
					} else {
						if (display)
							System.out.println("\n" + desc
									+ " input at position " + j);
						inPort = j;
					}
					lpfound = j;
					break;
				}
			}
			for (int j = lpfound + 1; j < midilist.length; j++) {
				desc = midilist[j].toString();
				if (desc.contains(name)) {
					MidiDevice device = MidiSystem.getMidiDevice(midilist[j]);
					if (device.getMaxTransmitters() != 0) {
						if (display)
							System.out
									.println(desc + " input at position " + j);
						inPort = j;
					} else {
						if (display)
							System.out.println(desc + " output at position "
									+ j);
						outPort = j;
					}
					break;
				}
			}

			if (inPort == -1 && outPort == -1) { // If no Launchpad is connected
				if (display)
					System.out.println("\nNo " + name + " found.");
				return false;
			} else if (inPort == -1 && outPort != -1) { // If there is only
														// output
														// port found - error
				if (display)
					System.out.println("\n" + name + " Input port not found");
				return false;
			} else if (inPort != -1 && outPort == -1) { // If there is only
														// input
														// port found - error
				if (display)
					System.out.println("\n" + name + " Output port not found");
				return false;
			} else
			// Launchpad ports found
			if (display)
				System.out.println(name + " found. \tInput port: " + inPort
						+ "\tOutput port: " + outPort);
			return true;
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sets up all the connections required for the Launchpad
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 */
	public void setupLaunchpad() throws MidiUnavailableException {
		Launchpad.launchpadin = MidiSystem.getMidiDevice(midilist[inPort]);
		Launchpad.launchpadout = MidiSystem.getMidiDevice(midilist[outPort]);
		Launchpad.sequencer = MidiSystem.getSequencer();
		Launchpad.lpin = Launchpad.launchpadin.getTransmitter();
		Launchpad.lpout = Launchpad.launchpadout.getReceiver();
		Launchpad.lpin.setReceiver(Launchpad.lpout);
		Launchpad.sequencer = MidiSystem.getSequencer();
		Launchpad.midiFromLP = Launchpad.launchpadin.getTransmitter();
		Launchpad.midiToLP = Launchpad.launchpadout.getReceiver();

		Launchpad.launchpadin.close();
		Launchpad.launchpadout.close();

		Launchpad.sequencer.open();
		Launchpad.launchpadin.open();
		Launchpad.launchpadout.open();

		lpAlreadySetup = true;
	}

	/**
	 * Sets up all the connection required for the Virtual MIDI
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 */
	private void setupVirtualMIDI() throws MidiUnavailableException {
		VirtualMIDI.virtualout = MidiSystem
				.getMidiDevice(midilist[virtualOutPort]);
		VirtualMIDI.sequencer = MidiSystem.getSequencer();
		VirtualMIDI.vrtout = VirtualMIDI.virtualout.getReceiver();
		VirtualMIDI.sequencer = MidiSystem.getSequencer();
		VirtualMIDI.midiTovrt = VirtualMIDI.virtualout.getReceiver();

		VirtualMIDI.virtualout.close();

		VirtualMIDI.sequencer.open();
		VirtualMIDI.virtualout.open();

		virtualAlreadySetup = true;
	}

	/**
	 * Turns off every single possibly already lit pad
	 */
	public void clearPads() {
		// Make sure no pad is left in a blinking state
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				try {
					DisplayBlink.setBlink(i, j, false);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
			}

		for (int i = 0; i < 8; i++) {
			Display.off(0, i);
		}
		for (int i = 0; i < 128; i++) {
			SetMidiMessage message;
			try {
				message = new SetMidiMessage("MIDI_OFF", Launchpad.lpout, i, 0);
				message.send();
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Re-evalutes the list of available midi devices. Very useful for
	 * reconnecting to a device in case something happened.
	 */
	public static void resetMIDIList() {
		MidiDevice.Info[] newMidiList = midilist;
		try {
			newMidiList = MidiSystem.getMidiDeviceInfo();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		midilist = newMidiList;
	}

	/**
	 * Closes all connections with the Launchpad and releases it for other
	 * applications that might need to connect to it.
	 */
	public static void closeLaunchpad() {
		Launchpad.lpin.close();
		Launchpad.lpout.close();
		Launchpad.launchpadin.close();
		Launchpad.launchpadout.close();
	}

	/**
	 * Closes all connections with the virtual MIDI connection and releases it
	 * for other applications that might need to connect to it.
	 */
	public void closeVirtualMIDI() {
		VirtualMIDI.vrtin.close();
		VirtualMIDI.vrtout.close();
		VirtualMIDI.virtualin.close();
		VirtualMIDI.virtualout.close();
	}
}
