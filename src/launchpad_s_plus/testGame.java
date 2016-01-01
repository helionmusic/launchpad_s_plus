package launchpad_s_plus;

import java.io.IOException;

/**
 * This was simply used to show all possible colors available on the board. Also
 * was used to test the first versions of pad blinking and multi-threading
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

public class testGame {
	private boolean firstrun = true;

	testGame() {
		test();
	}

	public void test() {
		try {
			turnOnPads();
			while (System.in.available() == 0) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void turnOnPads() {

		if (firstrun)
			new DisplayBlink(0, 7, "green", 3, 300);
		try {
			Display.on(1, 0, "red", 0);
			Display.on(1, 1, "red", 1);
			Display.on(1, 2, "red", 2);
			Display.on(1, 3, "red", 3);
			if (firstrun)
				new DisplayBlink(1, 4, "red", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(1, 5, "red", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(1, 6, "red", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(1, 7, "red", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(1, 8, "red", 3, 500);
			Thread.sleep(100);

			Display.on(2, 0, "green", 0);
			Display.on(2, 1, "green", 1);
			Display.on(2, 2, "green", 2);
			Display.on(2, 3, "green", 3);
			if (firstrun)
				new DisplayBlink(2, 4, "green", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(2, 5, "green", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(2, 6, "green", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(2, 7, "green", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(2, 8, "green", 3, 500);
			Thread.sleep(100);

			Display.on(3, 0, "yellow", 0);
			Display.on(3, 1, "yellow", 1);
			Display.on(3, 2, "yellow", 2);
			Display.on(3, 3, "yellow", 3);
			if (firstrun)
				new DisplayBlink(3, 4, "yellow", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(3, 5, "yellow", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(3, 6, "yellow", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(3, 7, "yellow", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(3, 8, "yellow", 3, 500);
			Thread.sleep(100);

			Display.on(4, 0, "orange", 0);
			Display.on(4, 1, "orange", 1);
			Display.on(4, 2, "orange", 2);
			Display.on(4, 3, "orange", 3);
			if (firstrun)
				new DisplayBlink(4, 4, "orange", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(4, 5, "orange", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(4, 6, "orange", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(4, 7, "orange", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(4, 8, "orange", 3, 500);
			Thread.sleep(100);

			Display.on(5, 0, "lime", 0);
			Display.on(5, 1, "lime", 1);
			Display.on(5, 2, "lime", 2);
			Display.on(5, 3, "lime", 3);
			if (firstrun)
				new DisplayBlink(5, 4, "lime", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(5, 5, "lime", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(5, 6, "lime", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(5, 7, "lime", 3, 500);
			Thread.sleep(100);
			if (firstrun)
				new DisplayBlink(5, 8, "lime", 3, 500);
			Thread.sleep(100);
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
