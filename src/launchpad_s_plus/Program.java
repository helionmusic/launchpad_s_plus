package launchpad_s_plus;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import tech_demo.*;
import games.*;
import gui.StatisticsGUI;

/**
 * Program initiation and termination
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Program {

	/**
	 * Initiates the program: sets the connection to the Launchpad S
	 * 
	 * @throws MidiUnavailableException
	 *             Midi Unavailable Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws IOException
	 *             IO Exception
	 */
	public static void initiation() throws MidiUnavailableException,
			InterruptedException, InvalidMidiDataException, IOException {
		Launchpad.startLaunchpad();
		if (!SetupProcess.lpdetected)
			return;

		midiInDump.MidiInDump incoming = new midiInDump.MidiInDump();
		incoming.join();

		Animation.setRunning(false);

		StatisticsGUI.getStats();
	}

	/**
	 * Closes the connections to the Launchpad S, saves statistics to the
	 * database and exits the program
	 */
	public static void closeProgram() {
		Game.displayNumberOfGamesPlayed();
		Animation.displayNumberOfAnimationsCalled();
		TechDemo.displayNumberOfTechDemosRan();

		Display.stopText();
		if (SetupProcess.lpdetected) {
			StatisticsGUI.saveStats();
			try {
				Launchpad.stopLaunchpad();
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("\nSummary of input:");
		midiInDump.MidiInDump.displayAndCloseInput();
		System.out.println("\nTerminating...");
		System.exit(0);
	}
}
