package piano;

import piano.Piano.CONTROLS;
import launchpad_s_plus.DefaultMIDI;
import launchpad_s_plus.Display;
import launchpad_s_plus.PlaySound;
import launchpad_s_plus.VirtualMIDI;

/**
 * Implements methods to play a Piano key based on given parameters
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class PianoKey extends Thread {
	private boolean DEBUG = false;
	private int x, y;
	private int keyValue;
	private boolean playOrStop;
	private static boolean[][] isPlaying = new boolean[9][9];
	private static boolean sustained;

	private boolean announcePlayed = false;
	private boolean announceStopped = false;
	
	private boolean usingRecorded = false;

	/**
	 * Constructor for PianoKey.
	 * 
	 * @param posx
	 *            X location of the key
	 * @param posy
	 *            Y location of the key
	 * @param status
	 *            True if the key should be played, False if it should be
	 *            stopped
	 */
	public PianoKey(int posx, int posy, boolean status) {
		keyValue = -1;
		x = posx;
		y = posy;
		playOrStop = status;
		this.start();
	}

	/**
	 * Constructor for PianoKey.
	 * 
	 * @param value
	 *            int MIDI location of the key
	 * @param status
	 *            True if the key should be played, False if it should be
	 *            stopped
	 */
	public PianoKey(int value, boolean status) {
		keyValue = value;
		playOrStop = status;
		this.start();
	}

	/**
	 * Initiates the Thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (playOrStop) {
			if (sustained && isPlaying[x][y]) {
				if (keyValue == -1)
					stopKey(x, y, true);
				else
					stopKey(keyValue, true);
			} else {
				if (keyValue == -1)
					playKey(x, y);
				else
					playKey(keyValue);
			}
		} else {
			waitForSustain();
			if (keyValue == -1)
				stopKey(x, y, false);
			else
				stopKey(keyValue, false);
		}
	}

	/**
	 * Plays the key at the given coordinates
	 * 
	 * @param x
	 *            X location of the key
	 * @param y
	 *            Y location of the key
	 */
	void playKey(int x, int y) {
		if (DEBUG && announcePlayed)
			System.out.println("Playing key at " + x + " " + y + " - "
					+ Piano.getKeyName(x, y) + Piano.getKeyOctave(x, y));
		isPlaying[x][y] = true;
		Display.on(x, y, "yellow", 3);
		playNote(Piano.getKeyName(x, y), Piano.getKeyOctave(x, y), true);
	}

	/**
	 * Plays the key at the given MIDI location
	 * 
	 * @param value
	 *            MIDI location of the key
	 */
	void playKey(int value) {
		if (DEBUG && announcePlayed)
			System.out.println("Playing key " + keyValue);
		isPlaying[x][y] = true;
		Display.on(x, y, "yellow", 3);
		playNote(keyValue, true);
	}

	/**
	 * Stops the key at the given coordinates
	 * 
	 * @param x
	 *            X location of the key
	 * @param y
	 *            Y location of the key
	 * @param forced
	 *            True if the sustain is not on, false otherwise
	 */
	void stopKey(int x, int y, boolean forced) {
		if (!isPlaying[x][y])
			return;
		if (DEBUG && announceStopped)
			System.out.println("Stopping key at " + x + " " + y + " - "
					+ Piano.getKeyName(x, y) + Piano.getKeyOctave(x, y));
		isPlaying[x][y] = false;
		if (!forced)
			waitForSustain();
		if (x % 2 == 0) { // White key stopped
			Display.on(x, y, Piano.whiteKeysColor, Piano.whiteKeysIntensity);
			playNote(Piano.getKeyName(x, y), Piano.getKeyOctave(x, y), false);
		} else { // Black key or blank key stopped
			if (y == 0 || y == 3 || y == 7) { // Blank key stopped
				Display.off(x, y);
				playNote(Piano.getKeyName(x, y), Piano.getKeyOctave(x, y),
						false);
			} else { // Black key stopped
				Display.on(x, y, Piano.blackKeysColor, Piano.blackKeysIntensity);
				playNote(Piano.getKeyName(x, y), Piano.getKeyOctave(x, y),
						false);
			}
		}
	}

	/**
	 * Stops the key at the given MIDI location
	 * 
	 * @param value
	 *            MIDI location of the key
	 * @param forced
	 *            True if the sustain is not on, false otherwise
	 */
	void stopKey(int value, boolean forced) {
		if (!isPlaying[x][y])
			return;
		if (DEBUG && announceStopped)
			System.out.println("Stopping key " + keyValue);
		isPlaying[x][y] = false;
		if (!forced)
			waitForSustain();
		if (x % 2 == 0) { // White key stopped
			Display.on(x, y, Piano.whiteKeysColor, Piano.whiteKeysIntensity);
			playNote(keyValue, false);
		} else { // Black key or blank key stopped
			if (y == 0 || y == 3 || y == 7) { // Blank key stopped
				Display.off(x, y);
				playNote(keyValue, false);
			} else { // Black key stopped
				Display.on(x, y, Piano.blackKeysColor, Piano.blackKeysIntensity);
				playNote(keyValue, false);
			}
		}
	}

	/**
	 * Plays the key determined by the name and octaves given
	 * 
	 * @param key
	 *            Name of the key
	 * @param octave
	 *            Octave of the key
	 * @param playOrStop
	 *            True if the key should be played, False if it should be
	 *            stopped
	 */
	private void playNote(String key, int octave, boolean playOrStop) {
		int value = 0;
		int velocity = Piano.velocity;
		value += octave * 12;

		switch (key) {
		case "A":
			value += 9;
			break;
		case "A#":
			value += 10;
			break;
		case "B":
			value += 11;
			break;
		case "C":
			break;
		case "C#":
			value += 1;
			break;
		case "D":
			value += 2;
			break;
		case "D#":
			value += 3;
			break;
		case "E":
			value += 4;
			break;
		case "F":
			value += 5;
			break;
		case "F#":
			value += 6;
			break;
		case "G":
			value += 7;
			break;
		case "G#":
			value += 8;
			break;
		}

		if (Piano.vMIDIFound && Piano.useVMIDI)
			sendVMidiMessage(value, velocity, playOrStop);
		else
			playSample(value, velocity, playOrStop);
	}

	/**
	 * Plays the key determined by the MIDI location value
	 * 
	 * @param value
	 *            MIDI location of the key
	 * @param playOrStop
	 *            True if the key should be played, False if it should be
	 *            stopped
	 */
	void playNote(int value, boolean playOrStop) {
		int velocity = Piano.velocity;
		if (Piano.useVMIDI)
			sendVMidiMessage(value, velocity, playOrStop);
		else
			playSample(value, velocity, playOrStop);
	}

	/**
	 * Plays the sample given by name at the given velocity
	 * 
	 * @param value
	 *            MIDI location of the key
	 * @param velocity
	 *            Velocity to play the key at. Range: 0 to 127
	 * @param status
	 *            True if the key should be played, False if it should be
	 *            stopped
	 */
	private void playSample(int value, int velocity, boolean status) {
		if (!status)
			velocity = 0;
		if (usingRecorded)
			new PlaySound("piano_" + Piano.getKey(value), velocity, status);
		else
			sendDefaultMidiMessage(value, velocity, status);
	}

	/**
	 * Sends the virtual MIDI Message based on given parameters
	 * 
	 * @param value
	 *            MIDI location
	 * @param velocity
	 *            MIDI velocity
	 * @param status
	 *            True if it should be a Note_On, False if it should be a
	 *            Note_Off
	 */
	private void sendVMidiMessage(int value, int velocity, boolean status) {
		if (status)
			VirtualMIDI.sendMessage(value, velocity);
		else
			VirtualMIDI.sendMessage(value, 0);
	}
	
	/**
	 * Sends the default MIDI Message based on given parameters
	 * 
	 * @param value
	 *            MIDI location
	 * @param velocity
	 *            MIDI velocity
	 * @param status
	 *            True if it should be a Note_On, False if it should be a
	 *            Note_Off
	 */
	private void sendDefaultMidiMessage(int value, int velocity, boolean status){
		if (status)
			playDefaultNote(value, velocity);
		else
			playDefaultNote(value, 0);
	}
	
	private void playDefaultNote(int note, int velocity){
		DefaultMIDI.sendMessage(note, velocity);
	}

	/**
	 * Enables or disables Sustain
	 * 
	 * @param status
	 *            True if Sustain should be enabled, False otherwise
	 * @param cut
	 *            True if the sustain should be temporarily disabled
	 */
	static synchronized void setSustain(boolean status, boolean cut) {
		if (cut)
			sustained = status;
		else if (status) {
			if (Piano.longSustained)
				Display.on(CONTROLS.SUSTAIN.positionX,
						CONTROLS.SUSTAIN.positionY, "yellow", 3);
			else
				Display.on(CONTROLS.SUSTAIN.positionX,
						CONTROLS.SUSTAIN.positionY, "green", 3);
			sustained = status;
		} else {
			Display.on(CONTROLS.SUSTAIN.positionX, CONTROLS.SUSTAIN.positionY,
					CONTROLS.SUSTAIN.color, CONTROLS.SUSTAIN.intensity);
			sustained = status;
		}
	}

	/**
	 * Waits for the sustain to be released before returning
	 */
	private synchronized void waitForSustain() {
		while (sustained) {
			delay(10);
			if (!sustained)
				break;
		}
		return;
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param duration
	 *            Time in MS
	 */
	private void delay(int duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
