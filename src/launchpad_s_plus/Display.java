package launchpad_s_plus;

import gui.StatisticsGUI;
import javax.sound.midi.InvalidMidiDataException;

/**
 * This class is used to turn on or off pads on the launchpad <br>
 * Methods available for use include: <br>
 * - a way to turn on a pad permanently (until overwritten), <br>
 * - a way to turn on a pad for a certain amount of time (in ms) <br>
 * - a way to turn on a pad for a certain duration (in ms) and change it to
 * another color permanently afterwards <br>
 * - a way to turn off a pad <br>
 * - a way to start blinking a pad based on timing (in ms) <br>
 * - a way to start blinking a pad with 2 colors <br>
 * - a way to start blinking a pad with 3 colors <br>
 * - a way to stop blinking a pad <br>
 * - a way to turn off anything that might be on or blinking.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Display extends Thread {

	/**
	 * Turn on a pad until overwritten (color as an String passed)
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 * @param col
	 *            String value of the color to use
	 * @param intensity
	 *            Intensity of the color to use
	 */
	public static void on(int positionX, int positionY, String col,
			int intensity) {
		if (positionX < 0 || positionX > 8)
			return;
		if (positionY < 0 || positionY > 8)
			return;

		switch (intensity) {
		case 0:
			off(positionX, positionY);
			break;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.weak[Colors.colorValue(col)], 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.mid[Colors.colorValue(col)], 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.strong[Colors.colorValue(col)], 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.strong[Colors.colorValue(col)], 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Turn on a pad until overwritten (color as an int passed)
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 * @param col
	 *            int value of the color to use
	 * @param intensity
	 *            Intensity of the color to use
	 */
	public static void on(int positionX, int positionY, int col, int intensity) {
		if (positionX < 0 || positionX > 8)
			return;
		if (positionY < 0 || positionY > 8)
			return;

		switch (intensity) {
		case 0:
			off(positionX, positionY);
			break;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, 0, true)
						.setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Turn on a pad for a duration (color as an int passed)
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 * @param col
	 *            int value of the color to use
	 * @param intensity
	 *            Intensity of the color to use
	 * @param duration
	 *            Duration to keep the pad turned on for
	 */
	public static void on(int positionX, int positionY, int col, int intensity,
			int duration) {
		if (positionX < 0 || positionX > 8)
			return;
		if (positionY < 0 || positionY > 8)
			return;

		switch (intensity) {
		case 0:
			off(positionX, positionY);
			break;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, duration,
						false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, duration,
						false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, duration,
						false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY], col, duration,
						false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Turn on a pad for a duration (color as a String passed)
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 * @param col
	 *            String value of the color to use
	 * @param intensity
	 *            Intensity of the color to use
	 * @param duration
	 *            Duration to keep the pad turned on for
	 */
	public static void on(int positionX, int positionY, String col,
			int intensity, int duration) {
		if (positionX < 0 || positionX > 8)
			return;
		if (positionY < 0 || positionY > 8)
			return;

		switch (intensity) {
		case 0:
			off(positionX, positionY);
			break;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.weak[Colors.colorValue(col)], duration, false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.mid[Colors.colorValue(col)], duration, false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.strong[Colors.colorValue(col)], duration, false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout,
						Launchpad.map[positionX][positionY],
						Colors.strong[Colors.colorValue(col)], duration, false).setName("DisplayPad " + positionX + " " + positionY);
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Turn on a pad for a duration and then change to another color instead of
	 * turning it off (color as a String passed)
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 * @param col
	 *            String value of the color to use
	 * @param col2
	 *            String value of the color to change to
	 * @param intensity
	 *            Intensity of the color to use
	 * @param intensity2
	 *            Intensity of the second color to use
	 * @param duration
	 *            Duration to keep the initial pad turned on for
	 */
	public static void onThenChange(int positionX, int positionY, String col,
			String col2, int intensity, int intensity2, int duration) {
		if (positionX < 0 || positionX > 8)
			return;
		if (positionY < 0 || positionY > 8)
			return;

		switch (intensity) {
		case 1:
			switch (intensity2) {
			case 1:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.weak[Colors.colorValue(col)],
							Colors.weak[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.weak[Colors.colorValue(col)],
							Colors.mid[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.weak[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.weak[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		case 2:
			switch (intensity2) {
			case 1:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.mid[Colors.colorValue(col)],
							Colors.weak[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.mid[Colors.colorValue(col)],
							Colors.mid[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.mid[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.mid[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		case 3:
			switch (intensity2) {
			case 1:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.weak[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.mid[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		default:
			switch (intensity2) {
			case 1:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.weak[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.mid[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				try {
					new DisplayPad(Launchpad.lpout,
							Launchpad.map[positionX][positionY],
							Colors.strong[Colors.colorValue(col)],
							Colors.strong[Colors.colorValue(col2)], duration,
							false).setName("DisplayPad " + positionX + " " + positionY);
				} catch (InvalidMidiDataException | InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			break;
		}
	}

	/**
	 * Turn off a single pad
	 * 
	 * @param positionX
	 *            Row of the pad
	 * @param positionY
	 *            Column of the pad
	 */
	public static void off(int positionX, int positionY) {
		try {
			new DisplayPad(Launchpad.lpout,
					Launchpad.map[positionX][positionY], 0, 0, false).setName("DisplayPad " + positionX + " " + positionY);
		} catch (InvalidMidiDataException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop blinking a pad
	 * 
	 * @param x
	 *            Row of the pad
	 * @param y
	 *            Column of the pad
	 */
	public static void blinkoff(int x, int y) {
		if (x > 8 || x < 0)
			return;
		if (y > 8 || y < 0)
			return;
		new DisplayBlink(x, y, "green", 0, 0).setName("DisplayBlink " + x + " " + y);
	}

	/**
	 * Blink a pad with 3 different colors
	 * 
	 * @param x
	 *            Row of the pad
	 * @param y
	 *            Column of the pad
	 * @param color
	 *            String value of the color
	 * @param intensity
	 *            Intensity of the color
	 * @param speed
	 *            Time in ms to keep blinking at
	 */
	public static void blink(int x, int y, String color, int intensity,
			int speed) {
		if (x > 8 || x < 0)
			return;
		if (y > 8 || y < 0)
			return;
		new DisplayBlink(x, y, color, intensity, speed).setName("DisplayBlink " + x + " " + y);
	}

	/**
	 * Blink a pad with 3 different colors
	 * 
	 * @param x
	 *            Row of the pad
	 * @param y
	 *            Column of the pad
	 * @param color
	 *            String value of first color
	 * @param color2
	 *            String value of the second color
	 * @param intensity
	 *            Intensity of the first color
	 * @param intensity2
	 *            Intensity of the second color
	 * @param speed
	 *            Time in ms to keep blinking at
	 */
	public static void blink(int x, int y, String color, String color2,
			int intensity, int intensity2, int speed) {
		if (x > 8 || x < 0)
			return;
		if (y > 8 || y < 0)
			return;
		new DisplayBlink(x, y, color, color2, intensity, intensity2, speed).setName("DisplayBlink " + x + " " + y);
	}

	/**
	 * Blink a pad with 3 different colors
	 * 
	 * @param x
	 *            Row of the pad
	 * @param y
	 *            Column of the pad
	 * @param color
	 *            String value of first color
	 * @param color2
	 *            String value of the second color
	 * @param color3
	 *            String value of the third color
	 * @param intensity
	 *            Intensity of the first color
	 * @param intensity2
	 *            Intensity of the second color
	 * @param intensity3
	 *            Intensity of the third color
	 * @param speed
	 *            Time in ms to keep blinking at
	 */
	public static void blink(int x, int y, String color, String color2,
			String color3, int intensity, int intensity2, int intensity3,
			int speed) {
		if (x > 8 || x < 0)
			return;
		if (y > 8 || y < 0)
			return;
		new DisplayBlink(x, y, color, color2, color3, intensity, intensity2,
				intensity3, speed).setName("DisplayBlink " + x + " " + y);
	}

	/**
	 * Displays text on the Launchpad S based on the given parameters
	 * 
	 * @param text
	 *            Text to display on the Launchpad S
	 * @param color
	 *            Color of the text
	 * @param intensity
	 *            Intensity of the text
	 * @param speed
	 *            Speed at which to scroll the Text on the Launchpad S
	 * @param loop
	 *            True if the text should loop, False otherwise
	 */
	public static void text(String text, int color, int intensity, int speed,
			boolean loop) {
		switch (intensity) {
		case 0:
			return;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout, text, color, speed, loop, false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout, text, color, speed, loop, false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout, text, color, speed, loop, false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout, text, color, speed, loop, false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Displays text on the Launchpad S based on the given parameters
	 * 
	 * @param text
	 *            Text to display on the Launchpad S
	 * @param color
	 *            Color of the text
	 * @param intensity
	 *            Intensity of the text
	 * @param speed
	 *            Speed at which to scroll the Text on the Launchpad S
	 */
	public synchronized static void text(String text, String color,
			int intensity, int speed) {
		switch (intensity) {
		case 0:
			return;
		case 1:
			try {
				new DisplayPad(Launchpad.lpout, text,
						Colors.weak[Colors.colorValue(color)], speed, false,
						false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				new DisplayPad(Launchpad.lpout, text,
						Colors.mid[Colors.colorValue(color)], speed, false,
						false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				new DisplayPad(Launchpad.lpout, text,
						Colors.strong[Colors.colorValue(color)], speed, false,
						false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				new DisplayPad(Launchpad.lpout, text,
						Colors.strong[Colors.colorValue(color)], speed, false,
						false).setName("Display text");
			} catch (InvalidMidiDataException | InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Stops displaying text on the Launchpad S
	 */
	public synchronized static void stopText() {
		try {
			new DisplayPad(Launchpad.lpout, null, 0, 0, false, true).setName("Display stop text");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turn off and stop blinking all pads
	 */
	public static void clearPads() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				off(i, j);
				blinkoff(i, j);
			}
	}

	/**
	 * Updates the statistics with the current session's number of pads lit and
	 * amount data sent
	 */
	public static void updatePadsLit() {
		StatisticsGUI.setCurrentPadsLit(getNumberOfPadsLit());
		StatisticsGUI.setCurrentDataSent(getDataSent());
	}

	/**
	 * Returns the number of pads lit during the current session
	 * 
	 * 
	 * @return Number of pads lit
	 */
	public static int getNumberOfPadsLit() {
		return DisplayPad.getNumberOfPadsLit();
	}

	/**
	 * Returns the amount of bytes sent to the Launchpad S during the current
	 * session
	 * 
	 * 
	 * @return Bytes sent
	 */
	public static int getDataSent() {
		return DisplayPad.getDataSent();
	}
}
