package gui;

import database.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Implements the GUI popup for statistics
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class StatisticsGUI {
	private static boolean DEBUG = false;
	static boolean windowActive = false;

	private static int allTimePressed;
	private static int allTimeLit;
	private static double allTimeSent;
	private static double allTimeReceived;
	private static int allTimePlayed;
	private static int allTimeDemos;
	private static int allTimeAnimations;
	private static int currentPressed;
	private static int currentLit;
	private static double currentSent;
	private static double currentReceived;
	private static int currentPlayed;
	private static int currentDemos;
	private static int currentAnimations;

	private static JFrame frame;
	private static final JPanel panel = new JPanel();
	private static final JPanel currentSessionPanel = new JPanel();
	private static final JPanel currentSessionTitlePanel = new JPanel();
	private static final JPanel currentSessionDetailsPanel = new JPanel();
	private static final JPanel panel_1 = new JPanel();
	private static final JPanel allTimePanel = new JPanel();
	private static final JPanel allTimeTitlePanel = new JPanel();
	private static final JPanel allTimeDetailsPanel = new JPanel();
	private static final JPanel panel_2 = new JPanel();

	private static final JLabel lblCurrentSession = new JLabel("");
	private static final JLabel lblPadsLit = new JLabel("");
	private static final JLabel lblPadsPressed = new JLabel("");
	private static final JLabel lblDataSent = new JLabel("");
	private static final JLabel lblDataReceived = new JLabel("");
	private static final JLabel lblGamesPlayed = new JLabel("");
	private static final JLabel lblTechDemosRan = new JLabel("");
	private static final JLabel lblAnimationsCalled = new JLabel("");

	private static final JLabel lblAllTime = new JLabel("");
	private static final JLabel lbltotalPadsLit = new JLabel("");
	private static final JLabel lbltotalPadsPressed = new JLabel("");
	private static final JLabel lblTotalDataSent = new JLabel("");
	private static final JLabel lblTotalDataReceived = new JLabel("");
	private static final JLabel lblTotalGamesPlayed = new JLabel("");
	private static final JLabel lblTotalTechDemosRan = new JLabel("");
	private static final JLabel lblTotalAnimationsCalled = new JLabel("");

	private static final JSeparator separator = new JSeparator();
	private static final JSeparator separator_1 = new JSeparator();
	private static final JSeparator separator_2 = new JSeparator();

	private static int mainWindowX;
	private static int mainWindowY;
	private static int mainWindowW;
	private static int mainWindowH;

	private static int windowX;
	private static int windowY;
	private static int windowW;
	private static int windowH;

	/**
	 * Constructor for the GUI
	 * 
	 * @param start
	 *            true if the GUI should be displayed, false otherwise
	 */
	public StatisticsGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();

			updateStats();
			setInteractions();
			windowActive = true;
			frame.setVisible(true);
		} else {
			quit();
		}
	}

	/**
	 * Focuses the statistics window
	 */
	public static void focus() {
		frame.setVisible(true);
	}

	/**
	 * Sets the window position and dimensions
	 */
	private void initialize() {
		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 600;
		windowH = 300;
		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY + mainWindowH - windowH);
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private void createAndShowGUI() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(windowX, windowY, windowW, windowH);
		frame.setTitle("Launchpad S+ Statistics");
		frame.setDefaultCloseOperation(quit());

		panel.setPreferredSize(new Dimension(296, 10));
		panel.setMinimumSize(new Dimension(280, 10));
		panel.setBounds(new Rectangle(0, 0, 280, 0));
		panel.setLayout(new BorderLayout(0, 0));

		currentSessionPanel.setBounds(new Rectangle(0, 0, 285, 0));
		currentSessionPanel.setLayout(new BorderLayout(0, 0));

		currentSessionTitlePanel.setLayout(new BorderLayout(0, 0));

		lblCurrentSession.setText("Current Session");
		lblCurrentSession.setPreferredSize(new Dimension(76, 25));
		lblCurrentSession.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentSession.setAlignmentX(Component.CENTER_ALIGNMENT);

		currentSessionDetailsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(0dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(65dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"), }));

		panel_1.setPreferredSize(new Dimension(297, 10));
		panel_1.setMinimumSize(new Dimension(280, 280));
		panel_1.setBounds(new Rectangle(0, 0, 280, 0));
		panel_1.setLayout(new BorderLayout(0, 0));

		allTimePanel.setBounds(new Rectangle(2, 0, 285, 0));
		allTimePanel.setLayout(new BorderLayout(0, 0));

		allTimeTitlePanel.setLayout(new BorderLayout(0, 0));

		lblAllTime.setText("All Time");
		lblAllTime.setPreferredSize(new Dimension(36, 25));
		lblAllTime.setHorizontalAlignment(SwingConstants.CENTER);

		allTimeDetailsPanel.setLayout(new GridLayout(1, 0, 0, 0));

		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(0dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(0dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(30dlu;default)"), }));

		separator_2.setOrientation(SwingConstants.VERTICAL);

		panel.add(currentSessionPanel, BorderLayout.CENTER);
		currentSessionPanel.add(currentSessionTitlePanel, BorderLayout.NORTH);
		currentSessionTitlePanel.add(lblCurrentSession, BorderLayout.CENTER);
		currentSessionTitlePanel.add(separator, BorderLayout.SOUTH);

		currentSessionPanel
				.add(currentSessionDetailsPanel, BorderLayout.CENTER);
		currentSessionDetailsPanel.add(lblPadsLit, "4, 2");
		currentSessionDetailsPanel.add(lblPadsPressed, "6, 2");
		currentSessionDetailsPanel.add(lblDataSent, "4, 4");
		currentSessionDetailsPanel.add(lblDataReceived, "6, 4");
		currentSessionDetailsPanel.add(lblGamesPlayed, "4, 6");
		currentSessionDetailsPanel.add(lblTechDemosRan, "6, 6");
		currentSessionDetailsPanel.add(lblAnimationsCalled, "4, 8");

		panel_1.add(allTimePanel, BorderLayout.CENTER);
		allTimePanel.add(allTimeTitlePanel, BorderLayout.NORTH);
		allTimeTitlePanel.add(lblAllTime);
		allTimeTitlePanel.add(separator_1, BorderLayout.SOUTH);
		allTimeDetailsPanel.add(panel_2);
		allTimePanel.add(allTimeDetailsPanel, BorderLayout.CENTER);

		panel_2.add(lbltotalPadsLit, "4, 2");
		panel_2.add(lbltotalPadsPressed, "6, 2");
		panel_2.add(lblTotalDataSent, "4, 4");
		panel_2.add(lblTotalDataReceived, "6, 4");
		panel_2.add(lblTotalGamesPlayed, "4, 6");
		panel_2.add(lblTotalTechDemosRan, "6, 6");
		panel_2.add(lblTotalAnimationsCalled, "4, 8");

		frame.getContentPane().add(panel, BorderLayout.WEST);
		frame.getContentPane().add(panel_1, BorderLayout.EAST);
		frame.getContentPane().add(separator_2, BorderLayout.CENTER);
	}

	/**
	 * Defines and implements all the interactions for the Statistics GUI
	 */
	private void setInteractions() {

	}

	/**
	 * Updates the metrics for all the statistics
	 */
	private void updateStats() {
		updateCurrent();
		updateAllTime();
	}

	/**
	 * Updates the metrics for the current session's statistics
	 */
	private void updateCurrent() {
		updateCurrentPadsLit();
		updateCurrentPadsPressed();
		updateCurrentDataSent();
		updateCurrentDataReceived();
		updateCurrentGamesPlayed();
		updateCurrentTechDemosRan();
		updateCurrentAnimationsCalled();
	}

	/**
	 * Updates the metrics for the all time statistics
	 */
	private void updateAllTime() {
		updateTotalPadsLit();
		updateTotalPadsPressed();
		updateTotalDataSent();
		updateTotalDataReceived();
		updateTotalGamesPlayed();
		updateTotalTechDemosRan();
		updateTotalAnimationsCalled();
	}

	/**
	 * Gets the all time statistics from the database and saves them for use
	 */
	public static void getStats() {
		System.out.print("Loading stats...");
		allTimePressed = (int) Database.getStat("Pressed");
		allTimeLit = (int) Database.getStat("Lit");
		allTimeSent = Database.getStat("Sent");
		allTimeReceived = Database.getStat("Received");
		allTimePlayed = (int) Database.getStat("Games");
		allTimeDemos = (int) Database.getStat("Demos");
		allTimeAnimations = (int) Database.getStat("Animations");
		System.out.print(" Complete\n");
	}

	/**
	 * Saves the all time statistics to the database
	 */
	public static void saveStats() {
		System.out.print("Saving Stats...");
		Database.saveStats();
		System.out.print(" Complete\n");
	}

	/********************** Updating Current Stats ***************************/

	/**
	 * Updates the current sessions' Pads Pressed statistic
	 */
	private void updateCurrentPadsPressed() {
		setCurrentPadsPressed(currentPressed);
	}

	/**
	 * Updates the current sessions' Pads Lit statistic
	 */
	private void updateCurrentPadsLit() {
		setCurrentPadsLit(currentLit);
	}

	/**
	 * Updates the current sessions' Sent Data statistic
	 */
	private void updateCurrentDataSent() {
		setCurrentDataSent(currentSent);
	}

	/**
	 * Updates the current sessions' Received Data statistic
	 */
	private void updateCurrentDataReceived() {
		setCurrentDataReceived(currentReceived);
	}

	/**
	 * Updates the current sessions' Games Played statistic
	 */
	private void updateCurrentGamesPlayed() {
		setCurrentGamesPlayed(currentPlayed);
	}

	/**
	 * Updates the current sessions' Tech Demos Ran statistic
	 */
	private void updateCurrentTechDemosRan() {
		setCurrentTechDemosRan(currentDemos);
	}

	/**
	 * Updates the current sessions' Animations Called statistic
	 */
	private void updateCurrentAnimationsCalled() {
		setCurrentAnimationsCalled(currentAnimations);
	}

	/************************ Updating Total Stats ***************************/

	/**
	 * Updates the all time Pads Pressed statistic
	 */
	private void updateTotalPadsPressed() {
		setTotalPadsPressed(allTimePressed + currentPressed);
	}

	/**
	 * Updates the all time Pads Lit statistic
	 */
	private void updateTotalPadsLit() {
		setTotalPadsLit(allTimeLit + currentLit);
	}

	/**
	 * Updates the all time Sent Data statistic
	 */
	private void updateTotalDataSent() {
		setTotalDataSent(allTimeSent + currentSent);
	}

	/**
	 * Updates the all time Received Data statistic
	 */
	private void updateTotalDataReceived() {
		setTotalDataReceived(allTimeReceived + currentReceived);
	}

	/**
	 * Updates the all time Games Played statistic
	 */
	private void updateTotalGamesPlayed() {
		setTotalGamesPlayed(allTimePlayed + currentPlayed);
	}

	/**
	 * Updates the all time Tech Demos Ran statistic
	 */
	private void updateTotalTechDemosRan() {
		setTotalTechDemosRan(allTimeDemos + currentDemos);
	}

	/**
	 * Updates the all time Animations Called statistic
	 */
	private void updateTotalAnimationsCalled() {
		setTotalAnimationsCalled(allTimeAnimations + currentAnimations);
	}

	/********************** Setting Current Stats ****************************/

	/**
	 * Sets current Pads Pressed to the given value and updates the all time
	 * Pads Pressed value
	 * 
	 * @param pressed
	 *            Number of pads pressed this session
	 */
	public static void setCurrentPadsPressed(int pressed) {
		currentPressed = pressed;
		lblPadsPressed.setText("Pads pressed: " + pressed);
		setTotalPadsPressed(allTimePressed + pressed);
	}

	/**
	 * Sets current Pads Lit to the given value and updates the all time Pads
	 * Lit value
	 * 
	 * @param lit
	 *            Number of pads lit this session
	 */
	public static void setCurrentPadsLit(int lit) {
		currentLit = lit;
		lblPadsLit.setText("Pads lit: " + lit);
		setTotalPadsLit(allTimeLit + lit);
	}

	/**
	 * Sets current Sent Data to the given value and updates the all time Sent
	 * Data value
	 * 
	 * @param data
	 *            Amount of data sent this session in Bytes
	 */
	public static void setCurrentDataSent(double data) {
		currentSent = data;
		lblDataSent.setText("Data sent: " + convertData(data));
		setTotalDataSent(allTimeSent + data);
	}

	/**
	 * Sets current Received Data to the given value and updates the all time
	 * Received Data value
	 * 
	 * @param data
	 *            Amount of data received this session in Bytes
	 */
	public static void setCurrentDataReceived(double data) {
		currentReceived = data;
		lblDataReceived.setText("Data received: " + convertData(data));
		setTotalDataReceived(allTimeReceived + data);
	}

	/**
	 * Sets current Games Played to the given value and updates the all time
	 * Games Played value
	 * 
	 * @param played
	 *            Number of Games played this session
	 */
	public static void setCurrentGamesPlayed(int played) {
		currentPlayed = played;
		lblGamesPlayed.setText("Games played: " + played);
		setTotalGamesPlayed(allTimePlayed + played);
	}

	/**
	 * Sets current Tech Demos Ran to the given value and updates the all time
	 * Tech Demos Ran value
	 * 
	 * @param played
	 *            Number of Tech Demos ran this sesion
	 */
	public static void setCurrentTechDemosRan(int played) {
		currentDemos = played;
		lblTechDemosRan.setText("Tech Demos ran: " + played);
		setTotalTechDemosRan(allTimeDemos + played);
	}

	/**
	 * Sets current Animations Called to the given value and updates the all
	 * time Animations Ran value
	 * 
	 * @param played
	 *            Number of Animations called this session
	 */
	public static void setCurrentAnimationsCalled(int played) {
		currentAnimations = played;
		lblAnimationsCalled.setText("Animations called: " + played);
		setTotalAnimationsCalled(allTimeAnimations + played);
	}

	/*********************** Setting Total Stats *****************************/

	/**
	 * Sets the all time Pads Pressed value
	 * 
	 * @param pressed
	 *            Number of pads pressed
	 */
	public static void setTotalPadsPressed(int pressed) {
		lbltotalPadsPressed.setText("Pads pressed: " + pressed);
	}

	/**
	 * Sets the all time Pads Lit value
	 * 
	 * @param lit
	 *            Number of pads lit
	 */
	public static void setTotalPadsLit(int lit) {
		lbltotalPadsLit.setText("Pads lit: " + lit);
	}

	/**
	 * Sets the all time Sent Data value
	 * 
	 * @param data
	 *            Amount of data sent in Bytes
	 */
	public static void setTotalDataSent(double data) {
		lblTotalDataSent.setText("Data sent: " + convertData(data));
	}

	/**
	 * Sets the all time Received Data value
	 * 
	 * @param data
	 *            Amount of data received in Bytes
	 */
	public static void setTotalDataReceived(double data) {
		lblTotalDataReceived.setText("Data received: " + convertData(data));
	}

	/**
	 * Sets the all time Games Played value
	 * 
	 * @param played
	 *            Number of games played
	 */
	public static void setTotalGamesPlayed(int played) {
		lblTotalGamesPlayed.setText("Games played: " + played);
	}

	/**
	 * Sets the all time Tech Demos Ran value
	 * 
	 * @param played
	 *            Number of tech demos ran
	 */
	public static void setTotalTechDemosRan(int played) {
		lblTotalTechDemosRan.setText("Tech Demos ran: " + played);
	}

	/**
	 * Sets the all time Animations Called value
	 * 
	 * @param played
	 *            Number of animations called
	 */
	public static void setTotalAnimationsCalled(int played) {
		lblTotalAnimationsCalled.setText("Animations called: " + played);
	}

	/************************* Getting total stats ***************************/

	/**
	 * Returns the total number of pads pressed
	 * 
	 * @return All time Pads Pressed
	 */
	public static int getTotalPadsPressed() {
		int total = (allTimePressed + currentPressed);
		if (DEBUG)
			System.out.println("Total pads pressed = " + total);
		return total;
	}

	/**
	 * Returns the total number of pads lit
	 * 
	 * @return All time Pads Lit
	 */
	public static int getTotalPadsLit() {
		int total = (allTimeLit + currentLit);
		if (DEBUG)
			System.out.println("Total pads lit = " + total);
		return total;
	}

	/**
	 * Returns the total amount of data sent
	 * 
	 * @return All time Data Sent
	 */
	public static double getTotalDataSent() {
		double total = (allTimeSent + currentSent);
		if (DEBUG)
			System.out.println("Total data sent = " + total);
		return total;
	}

	/**
	 * Returns the total amount of data received
	 * 
	 * @return All time Data Received
	 */
	public static double getTotalDataReceived() {
		double total = (allTimeReceived + currentReceived);
		if (DEBUG)
			System.out.println("Total data received = " + total);
		return total;
	}

	/**
	 * Returns the total amount of games played
	 * 
	 * @return All time Games Played
	 */
	public static int getTotalGamesPlayed() {
		int total = (allTimePlayed + currentPlayed);
		if (DEBUG)
			System.out.println("Total games played = " + total);
		return total;
	}

	/**
	 * Returns the total amount of demos ran
	 * 
	 * @return All time Demos Ran
	 */
	public static int getTotalDemosRan() {
		int total = (allTimeDemos + currentDemos);
		if (DEBUG)
			System.out.println("Total demos ran = " + total);
		return total;
	}

	/**
	 * Returns the total amount of animations called
	 * 
	 * @return All time Animations Called
	 */
	public static int getTotalAnimationsCalled() {
		int total = (allTimeAnimations + currentAnimations);
		if (DEBUG)
			System.out.println("Total animations called = " + total);
		return total;
	}

	/*************************************************************************/

	/**
	 * Converts the given value from Bytes to either B, KB, MB, GB or TB based
	 * on the value
	 * 
	 * @param data
	 *            Value in Bytes
	 * @return String containing data converted to adequate form, with data size
	 */
	private static String convertData(double data) {
		String converted = "";

		double B = data;
		double KB = data / 1024.0;
		double MB = (data / 1024.0) / 1024.0;
		double GB = ((data / 1024.0) / 1024.0) / 1024.0;
		double TB = (((data / 1024.0) / 1024.0) / 1024.0) / 1024.0;

		DecimalFormat format = new DecimalFormat("0.00");

		if (TB > 1)
			converted = format.format(TB) + " TB";
		else if (GB > 1)
			converted = format.format(GB) + " GB";
		else if (MB > 1)
			converted = format.format(MB) + " MB";
		else if (KB > 1)
			converted = format.format(KB) + " KB";
		else
			converted = format.format(B) + " B";

		return converted;
	}

	/**
	 * Closes the Statistics window
	 * 
	 * @return 1
	 */
	public int quit() {
		windowActive = false;
		frame.setVisible(false);
		return 1;
	}
}
