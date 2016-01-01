package tech_demo;

import gui.StatisticsGUI;

/**
 * Tech Demos superclass
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("unused")
public abstract class TechDemo extends Thread {
	public static int demosRan = 0;
	public String name;
	private int refreshRate = 60;
	protected String demoName;
	public static boolean instanceRunning = false;
	public static String runningDemoName = "none";

	/**
	 * Sets the currently running demo name to the given parameter
	 * 
	 * @param value
	 *            Name of the currently running demo
	 */
	public static void setRunningDemoName(String value) {
		runningDemoName = value;
		if (value.equals("none"))
			instanceRunning = false;
		else
			instanceRunning = true;
	}

	/**
	 * Sets a flag used to determine whether a tech demo is running or not
	 * 
	 * @param status
	 *            Flag status
	 */
	protected static synchronized void setRunning(boolean status) {
		instanceRunning = status;
	}

	/**
	 * Displays the total number of tech demos called
	 */
	public static void displayNumberOfTechDemosRan() {
		System.out.println("Total Tech Demos ran: " + demosRan);
		return;
	}

	/**
	 * Returns the number of tech demos ran during the last session
	 * 
	 * @return Tech Demos ran
	 */
	public static int getNumberOfTechDemosRan() {
		return demosRan;
	}

	/**
	 * Increments the amount of Demos ran
	 */
	public static void incrementDemosRan() {
		demosRan++;
		StatisticsGUI.setCurrentTechDemosRan(demosRan);
	}

	/**
	 * Used to reset the demo
	 */
	public abstract void resetDemo();
}
