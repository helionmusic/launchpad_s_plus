package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tech_demo.ScrollingText;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import launchpad_s_plus.Colors;

/**
 * Implements the GUI for the Scrolling Text demo
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class TextScrollGUI {
	private static boolean firstTimeRunning = true;
	private static boolean ready = false;

	static ScrollingText scr = new ScrollingText();

	private static final JFrame ScrollingTextDialog = new JFrame();
	private static final JPanel ScrollingTextContainer = new JPanel();

	private static final JButton startButton = new JButton("Start");
	private static final JButton stopButton = new JButton("Stop");
	private static final JPanel startStopPanel = new JPanel();
	private static final JLabel textLabel = new JLabel();
	private static final JTextField textField = new JTextField();
	private static final JPanel textPanel = new JPanel();
	private static final JPanel colorPanel = new JPanel();
	private static final JLabel colorLabel = new JLabel();
	private static final JComboBox<String> color = new JComboBox<String>();
	private static final JPanel intensityPanel = new JPanel();
	private static final JLabel intensityLabel = new JLabel();
	private static final JComboBox<Integer> intensity = new JComboBox<Integer>();
	private static final JPanel loopingPanel = new JPanel();
	private static final JLabel loopingText = new JLabel();
	private static final JCheckBox loopingCheck = new JCheckBox();
	private static final JPanel speedPanel = new JPanel();
	private static final JLabel speedLabel = new JLabel();
	private static final JComboBox<Integer> speed = new JComboBox<Integer>();
	private static final JPanel settingsPanel = new JPanel();

	static String defaultMessage = "Launchpad S+";
	static String defaultColor = "green";
	static int defaultIntensity = 3;
	static int defaultSpeed = 4;
	static boolean defaultLoop = true;

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
	TextScrollGUI(boolean start) {
		if (start) {
			initialize();
			if (firstTimeRunning)
				createAndShowGUI();
			setInteractions();
			firstTimeRunning = false;
			ScrollingTextDialog.setVisible(true);
			askSettings();
			Main.delay(500);
			while (!ready) {
				Main.delay(1);
			}
			
			firstTimeRunning = false;

			new Thread() {
				@Override
				public void run() {
					try {
						scr = new ScrollingText();
						scr.begin();
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			}.start();

		} else {
			quit();
		}
	}

	/**
	 * Sets the window position and dimenstions
	 */
	private static void initialize() {
		Main.setClosing(false);
		Main.setCardIcon(Main.textScrollCard, "textScroll", "selected");

		ScrollingText.setText(defaultMessage);
		ScrollingText.setColor(defaultColor);
		ScrollingText.setIntensity(defaultIntensity);
		ScrollingText.setSpeed(defaultSpeed);

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 450;
		windowH = 200;
		windowX = (mainWindowX + mainWindowW + 20);
		windowY = ((mainWindowY + mainWindowH + windowH) / 3);
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private static void createAndShowGUI() {
		ScrollingTextDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		ScrollingTextDialog.setTitle("Scrolling Text Demo");
		ScrollingTextDialog.setResizable(false);

		ScrollingTextDialog.setBounds(windowX, windowY, windowW, windowH);

		if (firstTimeRunning) {
			speed.addItem(1);
			speed.addItem(2);
			speed.addItem(3);
			speed.addItem(4);
			speed.addItem(5);

			color.addItem("green");
			color.addItem("yellow");
			color.addItem("red");
			color.addItem("lime");
			color.addItem("orange");

			intensity.addItem(1);
			intensity.addItem(2);
			intensity.addItem(3);
		}
		speedLabel.setText("Speed");
		color.setSelectedIndex(0);
		colorLabel.setText("Color");
		speed.setSelectedIndex(defaultSpeed);
		intensity.setSelectedIndex(defaultIntensity - 1);
		intensityLabel.setText("Intensity");
		loopingCheck.setSelected(true);
		loopingText.setText("Looping");
		
		textLabel.setText("Message");
		textField.setPreferredSize(new Dimension(200, 25));
		textField.setDocument(new JTextFieldLimit(31));

		speedPanel.add(speedLabel);
		speedPanel.add(speed);
		colorPanel.add(colorLabel);
		colorPanel.add(color);
		intensityPanel.add(intensityLabel);
		intensityPanel.add(intensity);
		loopingPanel.add(loopingText);
		loopingPanel.add(loopingCheck);

		settingsPanel.add(speedPanel);
		settingsPanel.add(colorPanel);
		settingsPanel.add(intensityPanel);
		settingsPanel.add(loopingPanel);
		settingsPanel.setVisible(true);

		ScrollingText.setText(defaultMessage);
		ScrollingText.setColor(defaultColor);
		ScrollingText.setSpeed(defaultSpeed);
		ScrollingText.setIntensity(defaultIntensity);
		ScrollingText.setLoop(defaultLoop);

		if (firstTimeRunning)
			setText(defaultMessage);
		
		textPanel.add(textLabel);
		textPanel.add(textField);
		textPanel.setVisible(true);

		startStopPanel.add(startButton);
		startStopPanel.add(stopButton);
		startStopPanel.setVisible(true);

		ScrollingTextContainer.setBounds(0, 0, windowW, windowH);
		ScrollingTextContainer.setLayout(new BoxLayout(ScrollingTextContainer,
				BoxLayout.Y_AXIS));

		ScrollingTextContainer.add(textPanel);
		ScrollingTextContainer.add(startStopPanel);
		ScrollingTextContainer.add(settingsPanel);
		ScrollingTextDialog.getContentPane().add(ScrollingTextContainer);
	}

	/**
	 * Defines and implements all the interactions for the Scrolling Text GUI
	 */
	private static void setInteractions() {
		ScrollingTextDialog
				.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						quitScrollingText();
					}
				});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScrollingText.forceQuit();
				defaultMessage = textField.getText();
				ScrollingText.setText(textField.getText());
				resetScrolling();
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScrollingText.forceQuit();
			}
		});

		speed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				defaultSpeed = speed.getSelectedIndex() + 1;
				ScrollingText.setSpeed(speed.getSelectedIndex() + 1);
			}
		});

		color.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				defaultColor = Colors.stringFromValue(color.getSelectedIndex());
				ScrollingText.setColor(Colors.stringFromValue(color
						.getSelectedIndex()));
			}
		});

		intensity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				defaultIntensity = intensity.getSelectedIndex() + 1;
				ScrollingText.setIntensity(intensity.getSelectedIndex() + 1);
			}
		});

		loopingCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				defaultLoop = loopingCheck.isSelected();
				ScrollingText.setLoop(loopingCheck.isSelected());
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScrollingText.forceQuit();
				defaultMessage = textField.getText();
				ScrollingText.setText(textField.getText());
				resetScrolling();
			}
		});
	}

	/**
	 * Stops text that might be already scrolling and displays new text
	 */
	private static void resetScrolling() {
		ScrollingText.forceQuit();
		delay(10);
		new Thread() {
			@Override
			public void run() {
				try {
					new ScrollingText();
					ScrollingText.restartScroll();
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
				}
			}
		}.start();
	}

	/**
	 * Delays for a certain duration
	 * 
	 * @param duration
	 *            Time in MS to delay for
	 */
	private static void delay(int duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the new text to scroll on the Launchpad
	 * 
	 * @param newText
	 *            New text to display
	 */
	private static void setText(String newText) {
		textField.setText(newText);
		ScrollingText.setText(newText);
	}

	/**
	 * Gets the text in the Message field
	 * 
	 * 
	 * @return Text from the message field
	 */
	public static String getText() {
		return textField.getText();
	}

	/**
	 * Gets the color selected on the GUI
	 * 
	 * 
	 * @return Color selected on the GUI
	 */
	public static String getColor() {
		return Colors.stringFromValue(color.getSelectedIndex());
	}

	/**
	 * Gets the speed selected on the GUI
	 * 
	 * 
	 * @return Speed selected on the GUI
	 */
	public static int getSpeed() {
		return speed.getSelectedIndex() + 1;
	}

	/**
	 * Gets the intensity selected on the GUI
	 * 
	 * 
	 * @return Intensity selected on the GUI
	 */
	public static int getIntensity() {
		return intensity.getSelectedIndex() + 1;
	}

	/**
	 * Returns whether the Looping checkbox is selected on the GUI
	 * 
	 * 
	 * @return True if it is checked, False otherwise
	 */
	public static boolean getLooping() {
		return loopingCheck.isSelected();
	}

	/**
	 * Asks for user input before initiating the sub program
	 */
	private static void askSettings() {
		ready = true;
	}

	/**
	 * Method called to close the GUI from the ScrollingText class
	 */
	public static void quit() {
		System.out.println("Quitting\n");
		Main.setClosing(true);
		Main.textScrollRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		ScrollingTextDialog.dispose();
		Main.setCardIcon(Main.textScrollCard, "textScroll", "default");
		scr = null;
		Main.textScrollRunning = false;
	}

	/**
	 * Method called to close the GUI and demo. <br>
	 * Should not be called from within ScrollingText class.
	 * 
	 * @return 1
	 */
	public static int quitScrollingText() {
		System.out.println("Force Quitting\n");
		ScrollingText.forceQuit();
		scr = null;
		Main.setClosing(true);
		Main.textScrollRunning = false;
		Main.nothingRunning();
		Main.resetCards();
		ScrollingTextDialog.dispose();
		return 1;
	}
}
