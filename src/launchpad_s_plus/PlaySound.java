package launchpad_s_plus;

import javax.sound.sampled.*;

/**
 * Implements a way to play sound samples located in the resources folder. If
 * the sound file is not found, or of invalid format, a default short Metronome
 * sound will play instead
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class PlaySound extends Thread {
	private static boolean DEBUG = false;
	private static boolean DEBUG2 = false;
	private static String defaultSound = "/Samples/Metronome/Metronome.wav";
	boolean play;
	String fileName;
	String fileURL;
	int velocity;

	/**
	 * Constructor. Takes in the name of the file, volume (from 0 to 127) and
	 * checks whether the sound should be played or stopped
	 * 
	 * @param name
	 *            File name
	 * @param vol
	 *            Velocity to play the sound at. Range: 0 to 127, 127 being
	 *            loudest
	 * @param playOrStop
	 *            True if the file should be played, false otherwise
	 */
	public PlaySound(String name, int vol, boolean playOrStop) {
		fileName = name;
		play = playOrStop;
		velocity = vol;
		this.start();
	}

	/**
	 * Initiates the thread that plays the sound
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		fileURL = getFileURL(this.fileName);
		if (play)
			playSound(fileURL, velocity);
		else
			stopSound(fileURL);
	}

	/**
	 * Returns the full path of the given file name
	 * 
	 * @param name
	 *            Name of the file to play
	 * 
	 * @return Full path of the file to play
	 */
	private String getFileURL(String name) {
		String temp = "";
		if (name.contains("Metronome"))
			temp = "/Samples/Metronome/";
		else if (name.contains("piano"))
			temp = "/Samples/Piano/";
		else if (name.contains("simon"))
			temp = "/Samples/Simon/";
		else if (name.contains("reaction"))
			temp = "/Samples/Reaction/";

		temp = temp + (name + ".wav");
		return temp;
	}

	/**
	 * Converts from MIDI-based velocity (0 to 127) to Gain value
	 * 
	 * @param velocity
	 *            MIDI-based velocity (0 to 127)
	 * 
	 * @return Gain converted value
	 */
	public float getGain(int velocity) {
		float amplitude = (float) velocity / 127;
		double gain;
		gain = 20 * Math.log10(amplitude / 1);

		return (float) gain;
	}

	/**
	 * Plays the sound located at the filepath given with the given velocity
	 * level
	 * 
	 * @param file
	 *            Full path of the sound to play
	 * @param velocity
	 *            MIDI-Based velocity level (0 to 127. 127 being loudest)
	 */
	public void playSound(String file, int velocity) {
		Clip sound;
		if (DEBUG)
			System.out.println("\nReading sound file: " + file);

		if (velocity == 0) {
			stopSound(file);
			return;
		}
		try {
			sound = AudioSystem.getClip();
			try {
				sound.open(AudioSystem.getAudioInputStream(getClass()
						.getResource(file)));
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out
						.println("Sound file not found... Using default sound instead.");
				sound.open(AudioSystem.getAudioInputStream(getClass()
						.getResource(defaultSound)));
				if (DEBUG)
					System.out.println(sound.getFormat() + "\n");
			}

			FloatControl volume = (FloatControl) sound
					.getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(getGain(velocity));
			if (DEBUG2) {
				System.out.println("Velocity: " + velocity);
				System.out.println("Gain: " + getGain(velocity));
			}

			sound.start();

			sound.addLineListener(new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						event.getLine().close();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops playing the sound located by the filepath
	 * 
	 * @param file
	 *            Full path of the sound file to stop playing
	 */
	public void stopSound(String file) {
		Clip sound;
		try {
			sound = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("Closing sound file: " + file);

		try {
			// TODO fix this to actually close an already running sound
			sound = AudioSystem.getClip();
			sound.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
