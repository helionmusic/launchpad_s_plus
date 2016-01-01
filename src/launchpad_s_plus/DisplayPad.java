package launchpad_s_plus;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.sound.midi.*;

/**
 * Class used by Display.java to turn on or off a pad. Pretty much the same
 * functions. Except they are significantly simpler to use if called from the
 * Display class. <br>
 * <br>
 * This should never be called directly from any games or animations. Only use
 * Display.java
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class DisplayPad extends Thread {
	static Receiver LPout;
	int Pad, Color, Color2, Duration, Ticks, textColor, textSpeed;
	String textToSend = "";
	boolean isCC, onOnly, isText, looping, stopping;
	boolean DEBUG = false;
	boolean DEBUG2 = false;

	private static int padsLit = 0;
	private static int padsShut = 0;
	private static int dataSent = 0;
	private static int sentText = 0;

	/**
	 * Unused Constructor
	 */
	public DisplayPad() {
		System.out.println("Please inititate displayPad with parameters.");
	}

	/**
	 * Constructor used to turn on a pad with 1 color
	 * 
	 * @param lpout
	 *            MIDI Receiver port (Launchpad S output port)
	 * @param pad
	 *            MIDI value locating the pad to display on
	 * @param color
	 *            MIDI value of the velocity to use (Used for coloring the pad
	 *            on the Launchpad S)
	 * @param duration
	 *            Duration in MS to turn on the pad for if it is to be
	 *            temporarily on
	 * @param Only
	 *            True if the pad should be lit permanently, false if it should
	 *            be lit for the given duration
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public DisplayPad(Receiver lpout, int pad, int color, int duration,
			boolean Only) throws InvalidMidiDataException, InterruptedException {
		LPout = lpout;
		Pad = pad;
		Color = color;
		Color2 = -1;
		Duration = duration;
		onOnly = Only;
		isText = false;
		looping = false;
		stopping = false;
		if (Pad > 127 || Pad < 0)
			isCC = true;
		else
			isCC = false;

		if (DEBUG)
			System.out.println("Turning on pad " + pad + " for " + duration
					+ " ms.");
		this.start();
	}

	/**
	 * Constructor used to turn on a pad with 2 colors
	 * 
	 * @param lpout
	 *            MIDI Receiver port (Launchpad S output port)
	 * @param pad
	 *            MIDI value locating the pad to display on
	 * @param color1
	 *            MIDI value of the velocity to use for the first color (Used
	 *            for coloring the pad on the Launchpad S)
	 * @param color2
	 *            MIDI value of the velocity to use for the second color (Used
	 *            for coloring the pad on the Launchpad S)
	 * @param duration
	 *            Duration in MS to turn on the pad for with the first color
	 * @param Only
	 *            False if the second color should be permanent, true if it
	 *            should be temporary
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public DisplayPad(Receiver lpout, int pad, int color1, int color2,
			int duration, boolean Only) throws InvalidMidiDataException,
			InterruptedException {
		LPout = lpout;
		Pad = pad;
		Color = color1;
		Color2 = color2;
		Duration = duration;
		onOnly = Only;
		isText = false;
		looping = false;
		stopping = false;
		if (Pad > 127 || Pad < 0)
			isCC = true;
		else
			isCC = false;

		if (DEBUG)
			System.out.println("Turning on pad " + pad + " for " + duration
					+ " ms.");
		this.start();
	}

	/**
	 * Constructor used to display text on the Launchpad S
	 * 
	 * @param lpout
	 *            MIDI Receiver port (Launchpad S output port)
	 * @param text
	 *            Text to be displayed on the Launchpad S
	 * @param color
	 *            MIDI value of the velocity to use for the text (Used for color
	 *            on the Launchpad S)
	 * @param speed
	 *            Speed at which to scroll the text on the Launchpad. Range: 0
	 *            to 5, 5 being the fastest
	 * @param loop
	 *            True if the text should be looped, False otherwise
	 * @param stop
	 *            True if the text scrolling should be stopped
	 * 
	 * @throws InvalidMidiDataException
	 *             Invalid Midi Data Exception
	 * @throws InterruptedException
	 *             Thread Interrupted Exception
	 */
	public DisplayPad(Receiver lpout, String text, int color, int speed,
			boolean loop, boolean stop) throws InvalidMidiDataException,
			InterruptedException {
		LPout = lpout;
		textToSend = text;
		textColor = color;
		textSpeed = speed;
		looping = loop;
		stopping = stop;
		isText = true;
		isCC = false;
		onOnly = false;
		this.start();
	}

	/**
	 * Checks what type of Display output is called and processes it as a new
	 * thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (isText) {
			if (!stopping) {
				sendText(textToSend, textColor, textSpeed);
			} else
				sendStopText();
			return;
		}
		if (!onOnly || Duration != 0) {
			if (isCC) {
				if (Color2 == -1)
					turnOnOffCC();
				else
					turnOnChangeCC();
			} else {
				if (Color2 == -1)
					turnOnOff();
				else
					turnOnChange();
			}
		} else {
			if (isCC)
				turnOnCC();
			else
				turnOn();
		}
	}

	/**
	 * Turns on a pad with a certain color and then changes it to another one
	 */
	private void turnOnChange() {
		ShortMessage midimessage = new ShortMessage();

		// Setting MIDI Message to contain a Note ON signal, pad location and
		// color

		try {
			midimessage.setMessage(ShortMessage.NOTE_ON, 0, Pad, Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();

		// Delaying for a given duration
		try {
			Thread.sleep(Duration);
			if (DEBUG)
				System.out.println("Waiting " + Duration + " on pad " + Pad);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Setting MIDI Message to contain a Note OFF signal and pad location
		try {
			midimessage.setMessage(ShortMessage.NOTE_ON, 0, Pad, Color2);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();
	}

	/**
	 * Turns on a Control Change pad with a certain color and then changes it to
	 * another on
	 */
	private void turnOnChangeCC() {
		ShortMessage midimessage = new ShortMessage();
		if (Pad > 135 || Pad < 0)
			return;
		try {
			midimessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, Pad - 24,
					Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();

		// Delaying for a given duration
		try {
			Thread.sleep(Duration);
			if (DEBUG)
				System.out.println("Waiting " + Duration + " on pad " + Pad);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Setting MIDI Message to contain a Note OFF signal and pad location
		try {
			midimessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, Pad - 24,
					Color2);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsShut();
	}

	/**
	 * Turns on a pad for a duration and then turns it off
	 */
	public void turnOnOff() {
		ShortMessage midimessage = new ShortMessage();

		// Setting MIDI Message to contain a Note ON signal, pad location and
		// color

		try {
			midimessage.setMessage(ShortMessage.NOTE_ON, 0, Pad, Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();

		// Delaying for a given duration
		try {
			Thread.sleep(Duration);
			if (DEBUG)
				System.out.println("Waiting " + Duration + " on pad " + Pad);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Setting MIDI Message to contain a Note OFF signal and pad location
		try {
			midimessage.setMessage(ShortMessage.NOTE_OFF, 0, Pad, 0);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsShut();
	}

	/**
	 * Turns on a Control Change pad for a duration and then turns it off
	 */
	public void turnOnOffCC() {
		ShortMessage midimessage = new ShortMessage();
		if (Pad > 135 || Pad < 0)
			return;
		try {
			midimessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, Pad - 24,
					Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();

		// Delaying for a given duration
		try {
			Thread.sleep(Duration);
			if (DEBUG)
				System.out.println("Waiting " + Duration + " on pad " + Pad);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Setting MIDI Message to contain a Note OFF signal and pad location
		try {
			midimessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, Pad - 24, 0);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsShut();
	}

	/**
	 * Turns on a pad permanently
	 */
	public void turnOn() {
		ShortMessage midimessage = new ShortMessage();

		// Setting MIDI Message to contain a Note ON signal, pad location and
		// color
		try {
			midimessage.setMessage(ShortMessage.NOTE_ON, 0, Pad, Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();
	}

	/**
	 * Turns on a Control Change pad permanently
	 */
	public void turnOnCC() {
		ShortMessage midimessage = new ShortMessage();
		if (Pad > 135 || Pad < 0)
			return;
		try {
			midimessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, Pad - 24,
					Color);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		// Sending MIDI Message
		try {
			LPout.send(midimessage, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrementPadsLit();
	}

	/**
	 * Displays text on the Launchpad S based on the given parameters.
	 * 
	 * @param text
	 *            Text to display
	 * @param color
	 *            Color of the text
	 * @param scrollSpeed
	 *            Speed of the text
	 */
	public void sendText(String text, int color, int scrollSpeed) {
		/*
		 * Based on the Launchpad S Programmer's Reference Manual documentation
		 * provided by Novation.
		 */

		// Character limit of 31 in the Launchpad S buffer
		if (text.length() > 31)
			text = text.substring(0, 31);
		
		boolean loop = false;
		byte speed;
		if (scrollSpeed < 0)
			speed = 0x0;
		else if (scrollSpeed < 6)
			speed = (byte) (scrollSpeed + 1);
		else
			speed = 0x7;

		if (loop) {
			color += 64; // set bit 4 to set looping
			System.out.println("Looping: " + color);
		}

		byte[] header = new byte[] { (byte) 0xF0, 0x00, 0x20, 0x29, 0x09,
				(byte) color, speed };
		byte[] message = null;
		try {
			message = text.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] terminator = new byte[] { (byte) 0xF7 };
		byte[] fullMessage = new byte[message.length + header.length
				+ terminator.length];

		if (DEBUG2) {
			System.out.println("Sysex header: " + Arrays.toString(header));
			System.out.println("Sysex message: " + Arrays.toString(message));
			System.out.println("Sysex terminator: "
					+ Arrays.toString(terminator));
		}

		// Adding all the elements to the full sysex message to send
		System.arraycopy(header, 0, fullMessage, 0, header.length);
		System.arraycopy(message, 0, fullMessage, header.length, message.length);
		System.arraycopy(terminator, 0, fullMessage, header.length
				+ message.length, terminator.length);

		SysexMessage textMessage;
		try {
			textMessage = new SysexMessage(fullMessage, fullMessage.length);

			if (DEBUG2)
				System.out.println("Sending Sysex message: "
						+ Arrays.toString(fullMessage));

			Launchpad.lpout.send(textMessage, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		sentText += fullMessage.length * 8;
		Display.updatePadsLit();
	}
	
	/**
	 * Stops any text from scrolling on the Launchpad S
	 */
	private void sendStopText() {
		/*
		 * Based on the Launchpad S Programmer's Reference Manual documentation
		 * provided by Novation.
		 */

		byte[] message = new byte[] { (byte) 0xF0, 0x00, 0x20, 0x29, 0x09,
				0x00, (byte) 0xF7 };
		SysexMessage textMessage;
		try {
			textMessage = new SysexMessage(message, message.length);

			if (DEBUG)
				System.out.println("Sending Sysex stop message.");

			Launchpad.lpout.send(textMessage, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		sentText += message.length * 8;
		Display.updatePadsLit();
	}

	/**
	 * Increments the current session's Pads Shut statistic
	 */
	private synchronized static void incrementPadsShut() {
		padsShut++;
	}

	/**
	 * Increments the current session's Pad Lit statistic
	 */
	private synchronized static void incrementPadsLit() {
		padsLit++;
		Display.updatePadsLit();
	}

	/**
	 * Returns the current session's Pad Lit statistic
	 * 
	 * @return Number of Pads Lit
	 */
	public static int getNumberOfPadsLit() {
		return padsLit;
	}

	/**
	 * Returns the current session's amount of Data Sent in Bytes
	 * 
	 * @return Amount of Data Sent in Bytes
	 */
	public static int getDataSent() {
		dataSent = ((padsLit + padsShut) * 8) + sentText;
		return dataSent;
	}
}
