package launchpad_s_plus;

import tech_demo.Animation;

/**
 * This simply calls an animation. Was used for testing.
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */

public class testLaunchpadOutput extends Thread {

	testLaunchpadOutput() {
		new Animation("wave");
	}
}
