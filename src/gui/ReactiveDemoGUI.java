package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tech_demo.ReactiveDemo;

/**
 * Implements the GUI for Reactive Demo
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class ReactiveDemoGUI {
	private static boolean ready = false;

	private static final JFrame ReactiveDialog = new JFrame();
	private static final JPanel ReactiveWindow = new JPanel();
	private static final JLabel currentAnimText = new JLabel();
	private static final JPanel currentAnim = new JPanel();
	private static final JLabel lastPressed = new JLabel();
	private static final JPanel lastPressedText = new JPanel();
	private static final JButton nextButton = new JButton("Next");
	private static final JButton prevButton = new JButton("Previous");
	private static final JPanel buttonsPanel = new JPanel();

	private static final JPanel infoPanel = new JPanel();

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
	public ReactiveDemoGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();
			askSettings();
			Main.delay(500);
			while (!ready) {
				Main.delay(1);
			}

			new Thread() {
				@Override
				public void run() {
					try {
						ReactiveDemo lineShooter = new ReactiveDemo();
						lineShooter.begin();
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
	 * Asks for user input before initializing the game.
	 */
	private void askSettings() {
		ready = true;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Main.setCardIcon(Main.reactiveDemoCard, "reactiveDemo", "selected");

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 250;
		windowH = 130;
		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY + windowH + mainWindowH) / 2;
	}

	/**
	 * Creates the GUI layout
	 */
	private void createAndShowGUI() {
		ReactiveDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		ReactiveDialog.setTitle("Reactive Demo");
		ReactiveDialog.setResizable(false);
		ReactiveDialog.setLayout(new BorderLayout());
		ReactiveDialog.setBounds(windowX, windowY, windowW, windowH);

		setLastInput("None");
		setAnimationName(ReactiveDemo.getReaction());

		addPanels();
		setInteractions();

		ReactiveDialog.getContentPane().add(ReactiveWindow);
		ReactiveDialog.setVisible(true);
	}

	/**
	 * Updates the last received Input
	 * 
	 * @param value
	 *            Last received input
	 */
	public static void setLastInput(String value) {
		lastPressed.setText("Last MIDI Input: " + value);
	}

	/**
	 * Adds the GUI elements to the created layout
	 */
	private void addPanels() {
		currentAnim.add(currentAnimText);
		lastPressed.add(lastPressedText);

		buttonsPanel.add(prevButton);
		buttonsPanel.add(nextButton);

		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(currentAnim);
		ReactiveWindow.add(lastPressed);
		ReactiveWindow.add(infoPanel);
		ReactiveWindow.add(buttonsPanel);
	}

	/**
	 * Changes the current animation on the GUI
	 * 
	 * @param anim
	 *            Animation
	 */
	public static void setAnimationName(String anim) {
		currentAnimText.setText("Current Animation: " + anim);
	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private void setInteractions() {
		ReactiveDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quit();
			}
		});

		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ReactiveDemo.setAnimation((ReactiveDemo.type + 1)
						% ReactiveDemo.getChoices());
				setAnimationName(ReactiveDemo.getReaction());
			}
		});

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ReactiveDemo.setAnimation((ReactiveDemo.type + 1)
						% ReactiveDemo.getChoices());
				setAnimationName(ReactiveDemo.getReaction());
			}
		});
	}

	/**
	 * Method called to close the GUI from the ReactiveDemo class
	 */
	public static void quit() {
		ReactiveDemo.quit();
		Main.setCardIcon(Main.reactiveDemoCard, "reactiveDemo", "default");
		Main.reactiveDemoRunning = false;
		Main.resetCards();
		Main.nothingRunning();
		ReactiveDialog.dispose();
	}

	/**
	 * Method called to close the GUI and demo. <br>
	 * Should not be called from within ReactiveDemo class
	 * 
	 * @return 1
	 */
	public static int quitReactive() {
		Main.setCardIcon(Main.reactiveDemoCard, "reactiveDemo", "default");
		Main.reactiveDemoRunning = false;
		Main.resetCards();
		Main.nothingRunning();
		ReactiveDialog.dispose();
		return 1;
	}
}
