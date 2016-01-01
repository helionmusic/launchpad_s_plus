package tech_demo;

import piano.Piano;
import launchpad_s_plus.Display;
import launchpad_s_plus.PlaySound;

/**
 * Implements methods to play a metronome, for use with the Piano Demo.
 * Metronome can be sped up or slowed down, started or stopped.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Metronome extends Thread {
	private boolean DEBUG = false;
	private static int i, j, intense, intense2, duration;
	private static String col, col2;
	private static boolean running = false;
	private int metronomeTick = 0;

	/**
	 * Unused Constructor
	 */
	public Metronome() {
	}

	/**
	 * Constructor for Metronome. Receives parameters to locate the visual
	 * indicator but also plays the necessary sounds
	 * 
	 * @param x
	 *            X location of the visual metronome
	 * @param y
	 *            Y location of the visual metronome
	 * @param color1
	 *            Color of the visual metronome's first beat
	 * @param color2
	 *            Color of the visual metronome's other three beats
	 * @param intensity1
	 *            Intensity of the visual metronome's first beat
	 * @param intensity2
	 *            Intensity of the visual metronome's other three beats
	 * @param dur
	 *            Duration in between every beat
	 */
	public Metronome(int x, int y, String color1, String color2,
			int intensity1, int intensity2, int dur) {
		i = x;
		j = y;
		col = color1;
		col2 = color2;
		intense = intensity1;
		intense2 = intensity2;
		duration = dur;
		running = true;
		this.start();
	}

	/**
	 * Initiates the Thread. Plays the required metronome sound and delays as
	 * needed
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (running) {
			if (DEBUG)
				System.out.println("Tempo: " + Piano.tempo);
			if (metronomeTick == 0) {
				new PlaySound("MetronomeFirst", 127, true);
				Display.on(i, j, col, intense, Piano.getTempoInMS() / 2);
				delay(Piano.getTempoInMS() / 2);
				Display.off(i, j);
				delay(Piano.getTempoInMS() / 2);
				metronomeTick++;
			} else {
				new PlaySound("Metronome", 127, true);
				Display.on(i, j, col2, intense2, duration / 2);
				delay(Piano.getTempoInMS() / 2);
				Display.off(i, j);
				delay(Piano.getTempoInMS() / 2);
				metronomeTick++;
				if (metronomeTick == 4)
					metronomeTick = 0;
			}
		}
	}

	/**
	 * Starts the metronome
	 */
	public void startMetronome() {
		running = true;
	}

	/**
	 * Stops the metronome
	 */
	public void stopMetronome() {
		running = false;
	}

	/**
	 * Delays for a given duration
	 * 
	 * @param time
	 *            Time in MS
	 */
	private void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the tempo delay to the given time value in MS
	 * 
	 * @param tempoInMS
	 *            time in MS
	 */
	public void setTempo(int tempoInMS) {
		duration = tempoInMS;
	}

	/**
	 * Sets the location of the metronome visual indicator on the Launchpad
	 * 
	 * @param positionX
	 *            X position
	 * @param positionY
	 *            Y position
	 */
	public void setLocation(int positionX, int positionY) {
		i = positionX;
		j = positionY;
	}

	/**
	 * Sets the colors of the visual indicator
	 * 
	 * @param string
	 *            Color for the first beat
	 * @param string2
	 *            Color for the other three beats
	 */
	public void setColors(String string, String string2) {
		col = string;
		col2 = string2;
	}

	/**
	 * Sets the intensities of the visual indicator
	 * 
	 * @param intensity1
	 *            Intensity for the first beat
	 * @param intensity2
	 *            Intensity for the other three beats
	 */
	public void setIntensities(int intensity1, int intensity2) {
		intense = intensity1;
		intense2 = intensity2;
	}
}