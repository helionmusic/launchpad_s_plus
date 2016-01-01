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

import tech_demo.Animation;

/**
 * Implements the GUI for Animation
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class AnimationGUI {
	private static boolean ready = false;
	private static boolean firstTimeRunning = true;

	private static final JFrame AnimationDialog = new JFrame();
	private static final JPanel AnimationWindow = new JPanel();
	private static final JLabel currentAnimText = new JLabel();
	private static final JPanel currentAnim = new JPanel();
	private static final JLabel lastPressed = new JLabel();
	private static final JPanel lastPressedText = new JPanel();
	private static final JButton nextButton = new JButton("Next");
	private static final JButton prevButton = new JButton("Previous");
	private static final JPanel currentAnimationParticlesPanel = new JPanel();
	private static final JLabel currentAnimationParticlesDescription = new JLabel("Particles: ");
	private static final JButton moreButton = new JButton("More");
	private static final JButton lessButton = new JButton("Less");
	private static final JPanel buttonsPanel = new JPanel();
	private static final JPanel buttonsPanel2 = new JPanel();
	private static final JPanel infoPanel = new JPanel();

	private static int mainWindowX;
	private static int mainWindowY;
	private static int mainWindowW;
	private static int mainWindowH;

	private static int windowX;
	private static int windowY;
	private static int windowW;
	private static int windowH;

	private static String lastChoice = "random lines";
	private static int currentParticles;

	/**
	 * Constructor for the GUI
	 * 
	 * @param start
	 *            true if the GUI should be displayed, false otherwise
	 */
	public AnimationGUI(boolean start) {
		if (start) {
			initialize();
			createAndShowGUI();
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
						runAnimation();
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
	 * Initializes and runs the animations
	 */
	private void runAnimation() {
		new Animation(lastChoice);
		Animation.setRunning(true);
		while (Animation.isRunning()) {
			new Animation(Animation.getChoice());
			try {
				Thread.sleep((Animation.getCurrent() == 3) ? 2000 : 300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Animation.setRunning(false);
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
		Main.setCardIcon(Main.animationCard, "animation", "selected");

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 250;
		windowH = 200;
		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY + windowH + mainWindowH) / 2;
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private void createAndShowGUI() {
		AnimationDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		AnimationDialog.setTitle("Animation Demo");
		AnimationDialog.setResizable(false);
		AnimationDialog.setLayout(new BorderLayout());
		AnimationDialog.setBounds(windowX, windowY, windowW, windowH);

		setLastInput("None");
		setAnimationName(Animation.getChoice());
		setSimultaneousNumber(Animation.getSimultaneousNumber());

		addPanels();
		setInteractions();
		checkParticlesAllowed();

		AnimationDialog.getContentPane().add(AnimationWindow);
		AnimationDialog.setVisible(true);
	}

	/**
	 * Updates the last pressed text
	 * 
	 * @param value
	 *            Last Pressed input
	 */
	public static void setLastInput(String value) {
		lastPressed.setText("Last MIDI Input: " + value);
	}

	/**
	 * Links all the GUI panels
	 */
	private void addPanels() {
		currentAnim.add(currentAnimText);
		lastPressed.add(lastPressedText);

		buttonsPanel.add(prevButton);
		buttonsPanel.add(nextButton);
		
		currentAnimationParticlesPanel.add(currentAnimationParticlesDescription);
		
		buttonsPanel2.add(lessButton);
		buttonsPanel2.add(moreButton);
		
		infoPanel.add(lastPressed);
		
		AnimationWindow.setLayout(new BoxLayout(AnimationWindow, BoxLayout.Y_AXIS));
		AnimationWindow.add(infoPanel);
		AnimationWindow.add(currentAnim);
		AnimationWindow.add(buttonsPanel);
		AnimationWindow.add(currentAnimationParticlesPanel);
		AnimationWindow.add(buttonsPanel2);
	}

	/**
	 * Updates the currently playing animation name
	 * 
	 * @param choice
	 *            Currently playing animation
	 */
	public static void setAnimationName(String choice) {
		lastChoice = choice;
		currentAnimText.setText("Current Animation: " + lastChoice);
	}
	
	public static void setSimultaneousNumber(int number){
		currentParticles = number;
		currentAnimationParticlesDescription.setText("Particles: " + currentParticles);
	}
	
	private void checkParticlesAllowed(){
		switch (lastChoice){
		case "fireworks":
		case "random lines":
			moreButton.setEnabled(true);
			lessButton.setEnabled(true);
			break;
		default:
			moreButton.setEnabled(false);
			lessButton.setEnabled(false);
			break;
		}
	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private void setInteractions() {
		AnimationDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quit();
			}
		});

		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Animation.chosen = (Animation.chosen + (Animation
						.choicesAvailable() - 1))
						% Animation.choicesAvailable();
				setAnimationName(Animation.getChoice());
				checkParticlesAllowed();
			}
		});

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Animation.chosen = ((Animation.chosen + +1) % Animation
						.choicesAvailable());
				setAnimationName(Animation.getChoice());
				checkParticlesAllowed();
			}
		});
		
		moreButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				Animation.incrementSimultaneous();
				setSimultaneousNumber(Animation.getSimultaneousNumber());
			}
		});
		
		lessButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				Animation.decrementSimultaneous();
				setSimultaneousNumber(Animation.getSimultaneousNumber());
			}
		});
		
	}

	/**
	 * Method called to close the GUI from the Animation class
	 */
	public static void quit() {
		Animation anim = new Animation(false);
		Animation.setRunning(false);
		try {
			anim.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.setCurrentlyRunning("None");

		Main.setCardIcon(Main.animationCard, "animation", "default");
		Main.animationRunning = false;
		Main.resetCards();
		Main.nothingRunning();
		AnimationDialog.dispose();
	}

	/**
	 * Method called to close the GUI and demo. <br>
	 * Should not be called from within Animation class.
	 * 
	 * @return 1
	 */
	public static int quitAnimation() {
		Main.setCardIcon(Main.animationCard, "animation", "default");
		Main.animationRunning = false;
		Main.resetCards();
		Main.nothingRunning();
		AnimationDialog.dispose();
		return 1;
	}
}
