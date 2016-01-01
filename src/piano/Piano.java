package piano;

import gui.*;
import java.util.*;
import launchpad_s_plus.*;
import tech_demo.*;

/**
 * This class will implement a way to play piano directly from the launchpad,
 * with proper keys displayed on it and instant audio (either will be samples or
 * will send MIDI to an external MIDI compatible piano synthesizer - Pianoteq
 * 2.2 - for even more close to real piano sound).
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Piano extends TechDemo implements Observer {
	// These are for debugging purposes
	private static boolean DEBUG = false;

	private static boolean isRunning = false;
	private static boolean quittingQuery = false;
	private static boolean quitting = false;
	static boolean vMIDIFound = launchpad_s_plus.VirtualMIDI.found();
	static boolean useVMIDI;
	static String whiteKeysColor = "green";
	static String blackKeysColor = "orange";
	static int whiteKeysIntensity = 1;
	static int blackKeysIntensity = 1;

	private Metronome mtr = new Metronome();
	private static boolean isPlaying = false;
	static boolean isSustained = false;
	static boolean longSustained = false;
	private static long doubletapTime = 500;
	private static long lastSustainPressed;
	static int octaveShift = 0;
	static int maxOctaveShift = 2;
	static int minOctaveShift = -1;
	public static int velocity = 80;
	static int maxVelocity = 127;
	static int minVelocity = 0;
	static public int tempo = 120;
	static int maxTempo = 400;
	static int minTempo = 40;

	/**
	 * Class enumerating objects that contain X and Y coordinates for all the
	 * extra Control buttons to be used in the Piano demo, as well as their
	 * color and intensity information
	 * 
	 * @author Fares
	 * 
	 * @version $Revision: 1.0 $
	 */
	public enum CONTROLS {
		TEMPO(0, 4, "green", 0), SUSTAIN(1, 8, "green", 1), CUTSUSTAIN(2, 8,
				"green", 0), OCTAVEUP(0, 0, "orange", 1), OCTAVEDOWN(0, 1,
				"orange", 1), VELOCITYUP(0, 3, "lime", 1), VELOCITYDOWN(0, 2,
				"lime", 1), TEMPOUP(0, 6, "green", 1), TEMPODOWN(0, 5, "green",
				1), PLAY(8, 8, "green", 1), QUIT(0, 7, "red", 1);

		public final int positionX;
		public final int positionY;
		protected final String color;
		protected final int intensity;

		/**
		 * Constructor of the enumerated objects
		 * 
		 * @param x
		 *            X coordinates
		 * @param y
		 *            Y coordinates
		 * @param col
		 *            Color name
		 * @param intense
		 *            Intensity value
		 */
		private CONTROLS(int x, int y, String col, int intense) {
			this.positionX = x;
			this.positionY = y;
			this.color = col;
			this.intensity = intense;
		}
	}

	/**
	 * Constructor for the Piano demo
	 */
	public Piano() {
		System.out.println("\nStarting Piano");
		incrementDemosRan();
		isRunning = true;
		name = "piano";
		super.setRunningDemoName(name);
		begin();
	}

	/**
	 * Used for setting the link to the Observable.
	 * 
	 * @param bool
	 *            Unused
	 */
	public Piano(boolean bool) {
	}

	/**
	 * Initiates the Piano Demo.
	 */
	private void begin() {
		try {
			Launchpad.found();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		launchpad_s_plus.Launchpad.disableSelfListen();
		launchpad_s_plus.Input.disableConsoleEcho();
		launchpad_s_plus.Display.clearPads();
		delay(200);

		lightKeys();
		lightControls();
		setupMetronome();
		loadSamples();
		while (isRunning) {
			if (quitting)
				break;
			delay(10);
		}
		stopMetronome();
		quit();
	}

	/**
	 * Quits the Piano demo.
	 */
	public static void quit() {
		if (quitting) {
			System.out.println("Quitting Piano Demo.\n");
			setRunningDemoName("none");
			Main.setCurrentlyRunning("None");
			isRunning = false;
			launchpad_s_plus.Display.clearPads();
			launchpad_s_plus.Launchpad.enableSelfListen();
			launchpad_s_plus.Input.reenableConsoleEcho();
			delay(100);
			PianoGUI.quit();
		}
		quitting = false;
	}

	/**
	 * Forcefully quits the Piano demo
	 */
	public static void forceQuit() {
		setRunningDemoName("none");
		quitting = true;
		isRunning = false;
		launchpad_s_plus.Display.clearPads();
		launchpad_s_plus.Launchpad.enableSelfListen();
		launchpad_s_plus.Input.reenableConsoleEcho();
		delay(100);
	}

	/**
	 * Enables or disables the use of Virtual MIDI in the demo
	 * 
	 * @param status
	 *            True if Virtual MIDI is to be used, false otherwise
	 */
	public static void setVMIDIUse(boolean status) {
		useVMIDI = status;
	}

	/**
	 * Sets up the Metronome
	 */
	private void setupMetronome() {
		mtr.setTempo(getTempoInMS());
		mtr.setLocation(CONTROLS.TEMPO.positionX, CONTROLS.TEMPO.positionY);
		mtr.setColors("green", "red");
	}

	/**
	 * Starts the Metronome
	 */
	private void playMetronome() {
		if (isPlaying) {
			if (DEBUG)
				System.out.println("Stopping Metronome");
			isPlaying = false;
			Display.on(CONTROLS.PLAY.positionX, CONTROLS.PLAY.positionY,
					CONTROLS.PLAY.color, CONTROLS.PLAY.intensity);
			mtr.stopMetronome();
			Main.setLastReceivedInput("Metronome off");
		} else {
			if (DEBUG)
				System.out.println("Starting Metronome at " + tempo + " bpm");
			isPlaying = true;
			Display.on(CONTROLS.PLAY.positionX, CONTROLS.PLAY.positionY,
					"yellow", CONTROLS.PLAY.intensity);
			mtr = new Metronome(CONTROLS.TEMPO.positionX,
					CONTROLS.TEMPO.positionY, "green", "red", 3, 2,
					getTempoInMS());
			Main.setLastReceivedInput("Metronome: " + tempo);
		}
	}

	/**
	 * Converts BPM (Beats/Minute) to milliseconds
	 * 
	 * @return MS conversion of the BPM
	 */
	public static int getTempoInMS() {
		double delayTime = (60 / (double) tempo);
		delayTime *= 1000;
		return (int) delayTime;
	}

	/**
	 * Stops the Metronome
	 */
	private void stopMetronome() {
		mtr.stopMetronome();
	}

	/**
	 * Initiates all the samples to be used for playback if Virtual MIDI isn't
	 * used. Can be used to change the Piano sound used
	 */
	private void loadSamples() {
		// TODO If there are going to multiple Pianos sampled
	}

	/**
	 * Returns the key name, including octave number, of the pad located by the
	 * given parameters
	 * 
	 * @param x
	 *            X location of the pad
	 * @param y
	 *            Y location of the pad
	 * 
	 * @return String containing the name of the key (Examples: B4, C#3, ...)
	 */
	static String getKey(int x, int y) {
		String key = "";
		int octave = 0;

		key = getKeyName(x, y);
		// Just split both to make it easier to understand what's going on
		octave = getKeyOctave(x, y);

		return (key + octave);
	}

	/**
	 * Returns the full key name, including octave number, located by the given
	 * MIDI value
	 * 
	 * @param value
	 *            MIDI position of the key
	 * 
	 * @return String containing the name of the key (Examples: B4, C#3, ...)
	 */
	public static String getKey(int value) {
		return (getKeyName(value) + getKeyOctave(value));
	}

	/**
	 * Returns the octave value of a key located by the given MIDI position
	 * value
	 * 
	 * @param value
	 *            MIDI position of the key
	 * 
	 * @return Octave number of the key analysed
	 */
	static int getKeyOctave(int value) {
		return value / 12;
	}

	/**
	 * Returns the octave value of a key located by the given X and Y
	 * coordinates
	 * 
	 * @param x
	 *            X location of the pad
	 * @param y
	 *            Y location of the pad
	 * 
	 * @return Octave number of the key analysed
	 */
	public static int getKeyOctave(int x, int y) {
		int octave = 0;

		// Checking octave
		switch (x) {
		case 1:
		case 2:
			octave = 6;
			if (x == 1 && y == 0)
				octave--;
			else if (x == 2 && y == 7)
				octave++;
			break;
		case 3:
		case 4:
			octave = 5;
			if (x == 3 && y == 0)
				octave--;
			else if (x == 4 && y == 7)
				octave++;
			break;
		case 5:
		case 6:
			octave = 4;
			if (x == 5 && y == 0)
				octave--;
			else if (x == 6 && y == 7)
				octave++;
			break;
		case 7:
		case 8:
			octave = 3;
			if (x == 7 && y == 0)
				octave--;
			else if (x == 8 && y == 7)
				octave++;
			break;
		}

		return (octave += octaveShift);
	}

	/**
	 * Returns the note name (without octave number) located by the given MIDI
	 * position value
	 * 
	 * @param value
	 *            MIDI position of the key
	 * 
	 * @return Note name of the key analysed
	 */
	static String getKeyName(int value) {
		String key = "";

		switch (value % 12) {
		case 0:
			key = "C";
			break;
		case 1:
			key = "C#";
			break;
		case 2:
			key = "D";
			break;
		case 3:
			key = "D#";
			break;
		case 4:
			key = "E";
			break;
		case 5:
			key = "F";
			break;
		case 6:
			key = "F#";
			break;
		case 7:
			key = "G";
			break;
		case 8:
			key = "G#";
			break;
		case 9:
			key = "A";
			break;
		case 10:
			key = "A#";
			break;
		case 11:
			key = "B";
			break;
		}

		return key;
	}

	/**
	 * Returns the note name (without octave number) located by the given X and
	 * Y coordinates
	 * 
	 * @param x
	 *            X location of the pad
	 * @param y
	 *            Y location of the pad
	 * 
	 * @return Note name of the key analysed
	 */
	public static String getKeyName(int x, int y) {
		String key = "";

		// Checking the actual note. This is more tedious
		switch (x) {
		case 1:
		case 3:
		case 5:
		case 7:
			switch (y) {
			case 0:
				key = "B";
				break;
			case 1:
				key = "C#";
				break;
			case 2:
				key = "D#";
				break;
			case 3:
				key = "E";
				break;
			case 4:
				key = "F#";
				break;
			case 5:
				key = "G#";
				break;
			case 6:
				key = "A#";
				break;
			case 7:
				key = "B";
				break;
			}
			break;

		case 2:
		case 4:
		case 6:
		case 8:
			switch (y) {
			case 0:
				key = "C";
				break;
			case 1:
				key = "D";
				break;
			case 2:
				key = "E";
				break;
			case 3:
				key = "F";
				break;
			case 4:
				key = "G";
				break;
			case 5:
				key = "A";
				break;
			case 6:
				key = "B";
				break;
			case 7:
				key = "C";
				break;
			}
			break;
		}
		return key;
	}

	/**
	 * Turns on all the extra control lights on the Launchpad S
	 */
	private void lightControls() {
		Display.on(CONTROLS.SUSTAIN.positionX, CONTROLS.SUSTAIN.positionY,
				CONTROLS.SUSTAIN.color, CONTROLS.SUSTAIN.intensity);
		Display.on(CONTROLS.PLAY.positionX, CONTROLS.PLAY.positionY,
				CONTROLS.PLAY.color, CONTROLS.PLAY.intensity);
		Display.on(CONTROLS.QUIT.positionX, CONTROLS.QUIT.positionY,
				CONTROLS.QUIT.color, CONTROLS.QUIT.intensity);
		Display.on(CONTROLS.OCTAVEUP.positionX, CONTROLS.OCTAVEUP.positionY,
				CONTROLS.OCTAVEUP.color, CONTROLS.OCTAVEUP.intensity);
		Display.on(CONTROLS.OCTAVEDOWN.positionX,
				CONTROLS.OCTAVEDOWN.positionY, CONTROLS.OCTAVEDOWN.color,
				CONTROLS.OCTAVEDOWN.intensity);
		Display.on(CONTROLS.VELOCITYUP.positionX,
				CONTROLS.VELOCITYUP.positionY, CONTROLS.VELOCITYUP.color,
				CONTROLS.VELOCITYUP.intensity);
		Display.on(CONTROLS.VELOCITYDOWN.positionX,
				CONTROLS.VELOCITYDOWN.positionY, CONTROLS.VELOCITYDOWN.color,
				CONTROLS.VELOCITYDOWN.intensity);
		Display.on(CONTROLS.TEMPOUP.positionX, CONTROLS.TEMPOUP.positionY,
				CONTROLS.TEMPOUP.color, CONTROLS.TEMPOUP.intensity);
		Display.on(CONTROLS.TEMPODOWN.positionX, CONTROLS.TEMPODOWN.positionY,
				CONTROLS.TEMPODOWN.color, CONTROLS.TEMPODOWN.intensity);
	}

	/**
	 * Turns on the lights for the Piano keys
	 */
	private void lightKeys() {
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 4; i++) {
				Display.onThenChange(2 + (2 * i), j, "yellow", whiteKeysColor,
						2, whiteKeysIntensity, 300);
				if (j != 0 && j != 4 && j != 7)
					Display.onThenChange(1 + (2 * i), 7 - j, "yellow",
							blackKeysColor, 2, blackKeysIntensity, 300);
				else
					Display.on(1 + (2 * i), 7 - j, "red", 2, 300);
				delay(10);
			}
		}
	}

	/**
	 * Returns after a slight delay
	 * 
	 * @param dur
	 *            duration of the delay
	 */
	private static void delay(int dur) {
		try {
			Thread.sleep(dur);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Emulates a Piano's sustain pedal: keeps all notes active until either the
	 * sustain is released, or until a temporary Sustain cut is pressed
	 * 
	 * @param pressed
	 *            True if the sustain is initiated, false if it is to be stopped
	 * @param cutSustain
	 *            False if the Sustain button was pressed, True if the temporary
	 *            Cut Sustain button was pressed
	 */
	private synchronized void sustainButtonPressed(boolean pressed,
			boolean cutSustain) {
		if (cutSustain) {
			System.out.println("Cutting Sustain");
			Main.setLastReceivedInput("Cut Sustain");
			PianoGUI.setSustain("Cut");
			PianoKey.setSustain(false, true);
		}
		if (pressed) {
			if (!longSustained) {
				System.out.println("Pressing Sustain");
				Main.setLastReceivedInput("Sustain");
				PianoGUI.setSustain("On");
			} else {
				System.out.println("Long Sustain");
				Main.setLastReceivedInput("Long Sustain");
				PianoGUI.setSustain("Long");
			}
			PianoKey.setSustain(true, false);
		} else {
			if (DEBUG)
				System.out.println("Releasing Sustain");
			if (cutSustain) {
				Main.setLastReceivedInput("Cut Sustain");
				PianoGUI.setSustain("Cut");
			} else {
				Main.setLastReceivedInput("Release Sustain");
				PianoGUI.setSustain("Off");
			}
			PianoKey.setSustain(false, false);
		}
	}

	/**
	 * Unused method
	 */
	@Override
	public void resetDemo() {
	}

	/**
	 * Processes MIDI inputs from the Launchpad S and calls the necessary
	 * methods
	 * 
	 * @param arg0
	 *            Observable
	 * @param arg1
	 *            Object
	 * @see java.util.Observer#update(Observable, Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (isRunning) {
			int x = Input.getPositionX();
			int y = Input.getPositionY();
			if (Input.getStatus()) {
				if (x != CONTROLS.QUIT.positionX
						&& y != CONTROLS.QUIT.positionY && quittingQuery) {
					quittingQuery = false;
					notQuitting();
				}

				if (x > 0 && y < 8) {
					new PianoKey(x, y, true);
					String lastIn = Piano.getKeyName(x, y)
							+ Piano.getKeyOctave(x, y);
					Main.setLastReceivedInput(lastIn);
					PianoGUI.setLastKey(lastIn);
				} else if (x == CONTROLS.SUSTAIN.positionX
						&& y == CONTROLS.SUSTAIN.positionY) {
					if (!longSustained) {
						if ((System.currentTimeMillis() - lastSustainPressed) < doubletapTime) {
							longSustained = true;
							sustainButtonPressed(true, false);
						} else {
							sustainButtonPressed(true, false);
						}
					} else {
						longSustained = false;
						sustainButtonPressed(false, false);
					}
					lastSustainPressed = System.currentTimeMillis();
				} else if (x == CONTROLS.CUTSUSTAIN.positionX
						&& y == CONTROLS.CUTSUSTAIN.positionY) {
					Display.on(CONTROLS.CUTSUSTAIN.positionX,
							CONTROLS.CUTSUSTAIN.positionY, "yellow", 3);
					sustainButtonPressed(false, true);
				} else if (x == CONTROLS.PLAY.positionX
						&& y == CONTROLS.SUSTAIN.positionY) {
					playMetronome();
				} else if (x == CONTROLS.QUIT.positionX
						&& y == CONTROLS.QUIT.positionY) {
					// Quitting Demo
					if (!quittingQuery) {
						quittingQuery = true;
						Main.setLastReceivedInput("Quit Piano?");
						Display.blink(CONTROLS.QUIT.positionX,
								CONTROLS.QUIT.positionY, CONTROLS.QUIT.color,
								CONTROLS.QUIT.intensity, 400);
						System.out
								.println("Are you sure you want to quit the Piano demo?");
					} else {
						quittingQuery = false;
						Main.setLastReceivedInput("Quitting Piano");
						Display.blinkoff(CONTROLS.QUIT.positionX,
								CONTROLS.QUIT.positionY);
						quitting = true;
						return;
					}
				} else if (x == CONTROLS.OCTAVEUP.positionX
						&& y == CONTROLS.OCTAVEUP.positionY) {
					Main.setLastReceivedInput("Octave Up");
					shiftOctave(true);
				} else if (x == CONTROLS.OCTAVEDOWN.positionX
						&& y == CONTROLS.OCTAVEDOWN.positionY) {
					Main.setLastReceivedInput("Octave Down");
					shiftOctave(false);
				} else if (x == CONTROLS.VELOCITYUP.positionX
						&& y == CONTROLS.VELOCITYUP.positionY) {
					shiftVelocity(true);
				} else if (x == CONTROLS.VELOCITYDOWN.positionX
						&& y == CONTROLS.VELOCITYDOWN.positionY) {
					shiftVelocity(false);
				} else if (x == CONTROLS.TEMPOUP.positionX
						&& y == CONTROLS.TEMPOUP.positionY) {
					shiftTempo(true);
				} else if (x == CONTROLS.TEMPODOWN.positionX
						&& y == CONTROLS.TEMPODOWN.positionY) {
					shiftTempo(false);
				}

			} else {
				if (x == CONTROLS.SUSTAIN.positionX
						&& y == CONTROLS.SUSTAIN.positionY) {
					if (!longSustained)
						sustainButtonPressed(false, false);
				} else if (x == CONTROLS.CUTSUSTAIN.positionX
						&& y == CONTROLS.CUTSUSTAIN.positionY) {
					Display.off(CONTROLS.CUTSUSTAIN.positionX,
							CONTROLS.CUTSUSTAIN.positionY);
					Main.setLastReceivedInput("Release Sustain");
					PianoGUI.setSustain("Off");
					if (longSustained)
						sustainButtonPressed(true, false);
				} else if (x > 0 && y < 8) {
					new PianoKey(x, y, false);
				}
			}
		}
	}

	/**
	 * Shifts the entire pad layout by an octave
	 * 
	 * @param upOrDown
	 *            True if shifting up, false otherwise
	 */
	private void shiftOctave(boolean upOrDown) {
		int pressDuration = 200;
		if (upOrDown) {
			if (octaveShift == 2)
				Display.onThenChange(CONTROLS.OCTAVEUP.positionX,
						CONTROLS.OCTAVEUP.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (octaveShift == 1) {
				Display.onThenChange(CONTROLS.OCTAVEUP.positionX,
						CONTROLS.OCTAVEUP.positionY, "red", "red", 2, 1,
						pressDuration);
				octaveShift++;
			} else {
				if (octaveShift == -1)
					Display.on(CONTROLS.OCTAVEDOWN.positionX,
							CONTROLS.OCTAVEDOWN.positionY,
							CONTROLS.OCTAVEDOWN.color,
							CONTROLS.OCTAVEDOWN.intensity);
				Display.onThenChange(CONTROLS.OCTAVEUP.positionX,
						CONTROLS.OCTAVEUP.positionY, "yellow",
						CONTROLS.OCTAVEUP.color, 2,
						CONTROLS.OCTAVEUP.intensity, pressDuration);
				octaveShift++;
			}
		} else {
			if (octaveShift == -1)
				Display.onThenChange(CONTROLS.OCTAVEDOWN.positionX,
						CONTROLS.OCTAVEDOWN.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (octaveShift == 0) {
				Display.onThenChange(CONTROLS.OCTAVEDOWN.positionX,
						CONTROLS.OCTAVEDOWN.positionY, "red", "red", 2, 1,
						pressDuration);
				octaveShift--;
			} else {
				if (octaveShift == 2)
					Display.on(CONTROLS.OCTAVEUP.positionX,
							CONTROLS.OCTAVEUP.positionY,
							CONTROLS.OCTAVEUP.color,
							CONTROLS.OCTAVEUP.intensity);
				Display.onThenChange(CONTROLS.OCTAVEDOWN.positionX,
						CONTROLS.OCTAVEDOWN.positionY, "yellow",
						CONTROLS.OCTAVEDOWN.color, 2,
						CONTROLS.OCTAVEDOWN.intensity, pressDuration);
				octaveShift--;
			}
		}
		PianoGUI.setOctaveShift(octaveShift);
	}

	/**
	 * Shifts the simulated velocity of key presses. This is because the
	 * Launchpad S is not velocity sensitive, therefore the only way to change
	 * the volume of the key presses is to shift the velocity
	 * 
	 * @param upOrDown
	 *            True if increasing velocity, False otherwise
	 */
	private void shiftVelocity(boolean upOrDown) {
		int pressDuration = 200;
		int shift = 20;
		if (upOrDown) {
			if (velocity == 127)
				Display.onThenChange(CONTROLS.VELOCITYUP.positionX,
						CONTROLS.VELOCITYUP.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (velocity == 120) {
				Display.onThenChange(CONTROLS.VELOCITYUP.positionX,
						CONTROLS.VELOCITYUP.positionY, "red", "red", 2, 1,
						pressDuration);
				velocity += 7;
			} else {
				if (velocity == 0)
					Display.on(CONTROLS.VELOCITYDOWN.positionX,
							CONTROLS.VELOCITYDOWN.positionY,
							CONTROLS.VELOCITYDOWN.color,
							CONTROLS.VELOCITYDOWN.intensity);
				Display.onThenChange(CONTROLS.VELOCITYUP.positionX,
						CONTROLS.VELOCITYUP.positionY, "yellow",
						CONTROLS.VELOCITYUP.color, 2,
						CONTROLS.VELOCITYUP.intensity, pressDuration);
				velocity += shift;
			}
			Main.setLastReceivedInput("Velocity Up:" + velocity);
		} else {
			if (velocity == 0)
				Display.onThenChange(CONTROLS.VELOCITYDOWN.positionX,
						CONTROLS.VELOCITYDOWN.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (velocity == shift) {
				Display.onThenChange(CONTROLS.VELOCITYDOWN.positionX,
						CONTROLS.VELOCITYDOWN.positionY, "red", "red", 2, 1,
						pressDuration);
				velocity -= shift;
			} else {
				if (velocity == 127) {
					Display.on(CONTROLS.VELOCITYUP.positionX,
							CONTROLS.VELOCITYUP.positionY,
							CONTROLS.VELOCITYUP.color,
							CONTROLS.VELOCITYUP.intensity);
					velocity -= 7;
				} else {
					Display.onThenChange(CONTROLS.VELOCITYDOWN.positionX,
							CONTROLS.VELOCITYDOWN.positionY, "yellow",
							CONTROLS.VELOCITYDOWN.color, 2,
							CONTROLS.VELOCITYDOWN.intensity, pressDuration);
					velocity -= shift;
				}
			}
			Main.setLastReceivedInput("Velocity Down:" + velocity);
		}

		PianoGUI.setVelocity(velocity);
	}

	/**
	 * Shifts the tempo used by the Metronome
	 * 
	 * @param upOrDown
	 *            True if increasing the speed, false otherwise
	 */
	private void shiftTempo(boolean upOrDown) {
		int pressDuration = 200;
		int shift = 10;
		if (DEBUG)
			System.out
					.println("Shifting tempo " + ((upOrDown) ? "up" : "down"));
		if (upOrDown) {
			if (tempo == maxTempo)
				Display.onThenChange(CONTROLS.TEMPOUP.positionX,
						CONTROLS.TEMPOUP.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (tempo == maxTempo - shift) {
				Display.onThenChange(CONTROLS.TEMPOUP.positionX,
						CONTROLS.TEMPOUP.positionY, "red", "red", 2, 1,
						pressDuration);
				tempo += shift;
			} else {
				if (tempo == minTempo)
					Display.on(CONTROLS.TEMPODOWN.positionX,
							CONTROLS.TEMPODOWN.positionY,
							CONTROLS.TEMPODOWN.color,
							CONTROLS.TEMPODOWN.intensity);
				Display.onThenChange(CONTROLS.TEMPOUP.positionX,
						CONTROLS.TEMPOUP.positionY, "yellow",
						CONTROLS.TEMPOUP.color, 2, CONTROLS.TEMPOUP.intensity,
						pressDuration);
				tempo += shift;
			}
			Main.setLastReceivedInput("Tempo Up: " + tempo);
		} else {
			if (tempo == minTempo)
				Display.onThenChange(CONTROLS.TEMPODOWN.positionX,
						CONTROLS.TEMPODOWN.positionY, "red", "red", 2, 1,
						pressDuration);
			else if (tempo == shift) {
				Display.onThenChange(CONTROLS.TEMPODOWN.positionX,
						CONTROLS.TEMPODOWN.positionY, "red", "red", 2, 1,
						pressDuration);
				tempo -= shift;
			} else {
				if (tempo == maxTempo) {
					Display.on(CONTROLS.TEMPOUP.positionX,
							CONTROLS.TEMPOUP.positionY, CONTROLS.TEMPOUP.color,
							CONTROLS.TEMPOUP.intensity);
					tempo -= shift;
				} else {
					Display.onThenChange(CONTROLS.TEMPODOWN.positionX,
							CONTROLS.TEMPODOWN.positionY, "yellow",
							CONTROLS.TEMPODOWN.color, 2,
							CONTROLS.TEMPODOWN.intensity, pressDuration);
					tempo -= shift;
				}
			}
			Main.setLastReceivedInput("Tempo Down: " + tempo);
		}
		PianoGUI.setTempo(tempo);
	}

	/**
	 * This is to reset the Quit button to it's normal 'uncalled' state, in case
	 * the user pressed the Quit button the first time and did not confirm
	 */
	private void notQuitting() {
		Display.blinkoff(CONTROLS.QUIT.positionX, CONTROLS.QUIT.positionY);
		delay(100);
		Display.on(CONTROLS.QUIT.positionX, CONTROLS.QUIT.positionY,
				CONTROLS.QUIT.color, CONTROLS.QUIT.intensity);
	}

	/**
	 * Returns true if the Piano demo is running
	 * 
	 * 
	 * @return True if the demo is still running, false otherwise
	 */
	public static boolean isRunning() {
		return isRunning;
	}
}
