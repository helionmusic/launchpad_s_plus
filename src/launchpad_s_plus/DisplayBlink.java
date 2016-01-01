package launchpad_s_plus;

import javax.sound.midi.InvalidMidiDataException;

/**
 * Class used by Display.java to start blinking lights. Pretty much the same
 * functions. Except they are significantly simpler to use if called from the
 * Display class. <br>
 * <br>
 * This should never be called directly from any games or animations. Only use
 * Display.java
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class DisplayBlink extends Thread {
	private static boolean[][] blinking = new boolean[9][9];
	private int i, j, intense, intense2, intense3, duration;
	private String col, col2, col3;

	/**
	 * Unused Constructor
	 */
	DisplayBlink() {
		System.out.println("Please input parameters.");
	}

	/**
	 * Constructor for DisplayBlink
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param color1
	 *            String value of the first color to blink with
	 * @param color2
	 *            String value of the second color to blink with
	 * @param intensity
	 *            First intensity value to blink with (3 max)
	 * @param intensity2
	 *            Second intensity value to blink with (3 max)
	 * @param period
	 *            Time delay for blinking
	 */
	public DisplayBlink(int positionI, int positionJ, String color1,
			String color2, int intensity, int intensity2, int period) {
		i = positionI;
		j = positionJ;
		col = color1;
		col2 = color2;
		col3 = "none";
		intense = intensity;
		intense2 = intensity2;
		duration = period;
		if (intensity != 0)
			blinking[i][j] = true;
		else
			blinking[i][j] = false;
		this.start();
	}

	/**
	 * Starts the thread which constitutes a blinking pad. Thread ends when the
	 * blinking[i][j] is set to false
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (blinking[i][j]) {
			try {
				if (col2 == "none")
					blink(i, j, col, intense, duration);
				else if (col3 == "none")
					blink(i, j, col, col2, intense, intense2, duration);
				else
					blink(i, j, col, col2, col3, intense, intense2, intense3,
							duration);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				for (int i=0; i<duration; i++){
					Thread.sleep(1);
					if (!blinking[i][j])
						break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set a certain pad to blink or to stop
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param status
	 *            True as long as it blinks - False to stop blinking
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void setBlink(int positionI, int positionJ, boolean status)
			throws InvalidMidiDataException, InterruptedException {
		blinking[positionI][positionJ] = status;
	}

	/**
	 * For all the blinking colors having the same INTENSITY value: <br>
	 * <br>
	 * Starts blinking (on and off) a pad based on the given input parameters.
	 * If the intensity is 0, stop blinking the pad
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param col
	 *            String value of the color to blink with
	 * @param intensity
	 *            Intensity value to blink with (3 max)
	 * @param delay
	 *            Time delay for blinking
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void blink(int positionI, int positionJ, String col,
			int intensity, int delay) throws InvalidMidiDataException,
			InterruptedException {
		while (blinking[positionI][positionJ]) {
			Display.on(positionI, positionJ, col, intensity);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
			Display.off(positionI, positionJ);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
		}
	}

	/**
	 * For all the blinking colors having the same INTENSITY value: <br>
	 * <br>
	 * Starts blinking (two colors) a pad based on the given input parameters.
	 * If the intensity is 0, stop blinking the pad
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param color
	 *            String value of the first color to blink with
	 * @param intensity
	 *            Intensity value to blink with (3 max)
	 * @param period
	 *            Time delay for blinking
	 */
	public DisplayBlink(int positionI, int positionJ, String color,
			int intensity, int period) {
		i = positionI;
		j = positionJ;
		col = color;
		col2 = "none";
		col3 = "none";
		intense = intensity;
		intense2 = 0;
		duration = period;
		if (intensity != 0)
			blinking[i][j] = true;
		else
			blinking[i][j] = false;
		this.start();
	}

	/**
	 * For all the blinking colors having the same INTENSITY value: <br>
	 * <br>
	 * Starts blinking (three colors) a pad based on the given input parameters.
	 * If the intensity is 0, stop blinking the pad
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param color1
	 *            String value of the first color to blink with
	 * @param color2
	 *            String value of the second color to blink with
	 * @param color3
	 *            String value of the third color to blink with
	 * @param intensity
	 *            First intensity value to blink with (3 max)
	 * @param intensity2
	 *            Second intensity value to blink with (3 max)
	 * @param intensity3
	 *            Third intensity value to blink with (3 max)
	 * @param period
	 *            Time delay for blinking
	 */
	public DisplayBlink(int positionI, int positionJ, String color1,
			String color2, String color3, int intensity, int intensity2,
			int intensity3, int period) {
		i = positionI;
		j = positionJ;
		col = color1;
		col2 = color2;
		col3 = color3;
		intense = intensity;
		intense2 = intensity2;
		intense3 = intensity3;
		duration = period;
		if (intensity != 0)
			blinking[i][j] = true;
		else
			blinking[i][j] = false;
		this.start();
	}

	/**
	 * /** For all the blinking colors having different INTENSITY values: <br>
	 * <br>
	 * Starts blinking (two colors) a pad based on the given input parameters.
	 * If the intensity is 0, stop blinking the pad
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param col
	 *            String value of the first color to blink with
	 * @param col2
	 *            String value of the second color to blink with
	 * @param intensity
	 *            First intensity value to blink with (3 max)
	 * @param intensity2
	 *            Second intensity value to blink with (3 max)
	 * @param delay
	 *            Time delay for blinking
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void blink(int positionI, int positionJ, String col,
			String col2, int intensity, int intensity2, int delay)
			throws InvalidMidiDataException, InterruptedException {
		while (blinking[positionI][positionJ]) {
			Display.on(positionI, positionJ, col, intensity);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
			Display.on(positionI, positionJ, col2, intensity2);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
		}
	}

	/**
	 * For all the blinking colors having different INTENSITY values: <br>
	 * <br>
	 * Starts blinking (three colors) a pad based on the given input parameters.
	 * If the intensity is 0, stop blinking the pad
	 * 
	 * @param positionI
	 *            X location of the pad
	 * @param positionJ
	 *            Y location of the pad
	 * @param col
	 *            String value of the first color to blink with
	 * @param col2
	 *            String value of the second color to blink with
	 * @param col3
	 *            String value of the third color to blink with
	 * @param intensity
	 *            First intensity value to blink with (3 max)
	 * @param intensity2
	 *            Second intensity value to blink with (3 max)
	 * @param intensity3
	 *            Third intensity value to blink with (3 max)
	 * @param delay
	 *            Time delay for blinking
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public static void blink(int positionI, int positionJ, String col,
			String col2, String col3, int intensity, int intensity2,
			int intensity3, int delay) throws InvalidMidiDataException,
			InterruptedException {
		while (blinking[positionI][positionJ]) {
			Display.on(positionI, positionJ, col, intensity);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
			Display.on(positionI, positionJ, col2, intensity2);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
			Display.on(positionI, positionJ, col3, intensity3);
			for (int i=0; i<delay; i++){
				Thread.sleep(1);
				if (!blinking[positionI][positionJ])
					break;
			}
		}
	}
}