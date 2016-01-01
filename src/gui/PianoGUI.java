package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import launchpad_s_plus.VirtualMIDI;
import piano.Piano;

/**
 * Implements the GUI for Piano
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class PianoGUI {
	private static boolean DEBUG = false;
	
	private static boolean changingvMIDI = false;
	private static boolean firstTimeRunning = true;
	private static URI vMIDILink;

	private static final JFrame PianoDialog = new JFrame();
	private static final JPanel PianoWindow = new JPanel();
	private static final JLabel tutorial = new JLabel();
	private static final JPanel vMIDIWrapper = new JPanel();
	private static final JLabel vMIDIButton = new JLabel();
	private static final JLabel vMIDIText = new JLabel();
	private static final JPanel vMIDILinkWrapper = new JPanel();
	private static final JLabel vMIDILinkText = new JLabel();
	private static final JLabel velocity = new JLabel();
	private static final JPanel velocityText = new JPanel();
	private static final JLabel tempo = new JLabel();
	private static final JPanel tempoText = new JPanel();
	private static final JLabel octaveShift = new JLabel();
	private static final JPanel octaveText = new JPanel();
	private static final JLabel lastKey = new JLabel();
	private static final JPanel lastInText = new JPanel();
	private static final JLabel sustain = new JLabel();
	private static final JPanel sustainText = new JPanel();

	private static final JPanel infoPanel = new JPanel();
	private static final JPanel rightPanel = new JPanel();
	private static final JPanel leftPanel = new JPanel();

	private static boolean usingVMIDI = false;
	private static boolean allowVMIDI = false;
	private static boolean ready = false;

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
	PianoGUI(boolean start) {
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
						new Piano();
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			}.start();

		} else {
			quitPiano();
		}
	}

	/**
	 * Creates the GUI layout
	 */
	private void createAndShowGUI() {
		PianoDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon.png")));
		PianoDialog.setTitle("Piano Demo");
		PianoDialog.setResizable(false);
		PianoDialog.setLayout(null);

		windowX = (mainWindowX + mainWindowW + 20);
		windowY = (mainWindowY);
		windowW = 600;
		windowH = mainWindowH;
		PianoDialog.setBounds(windowX, windowY, windowW, windowH);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBounds(0, 0, 150, windowH);
		leftPanel.setVisible(true);

		rightPanel.setBounds(150, -5, 450, windowH);
		rightPanel.setVisible(true);

		tutorial.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/AppsGUI/pianoTutorial.png")));

		vMIDIText.setText("Use Virtual MIDI ");
		vMIDIText.setBounds(0, 0, 120, 24);

		try {
			vMIDILink = new URI("http://www.nerds.de/data/setuploopbe1.exe");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		vMIDILinkText.setText("Download LoopBe1 for VMIDI");
		vMIDILinkText.setFont(new Font("Arial", Font.PLAIN, 10));
		vMIDILinkText.setToolTipText(vMIDILink.toString());
		vMIDILinkText.setForeground(Color.BLUE);
		vMIDILinkText.setBounds(0, 40, 120, 24);

		if (firstTimeRunning) {
			vMIDIWrapper.setBounds(0, 0, 200, 24);
		}

		vMIDIButton.setBounds(120, 0, 30, 24);
		changeButton(vMIDIButton, "off", "default");

		infoPanel.setBounds(0, 100, 120, 200);

		velocityText.setBounds(0, 30, 120, 24);
		velocity.setText("Velocity: " + Piano.velocity);

		tempoText.setBounds(0, 55, 120, 24);
		tempo.setText("Tempo: " + Piano.tempo + "bpm");

		octaveText.setBounds(0, 80, 120, 24);
		octaveShift.setText("Octave +0");

		lastInText.setBounds(0, 105, 120, 24);
		lastKey.setText("Last Key: None");

		sustainText.setBounds(0, 130, 120, 24);
		sustain.setText("Sustain: Off");

		PianoWindow.setBounds(0, 0, windowW, windowH);
		PianoWindow.setLayout(null);

		if (!allowVMIDI) {
			vMIDIButton.setEnabled(false);
			vMIDIText.setEnabled(false);
			vMIDILinkText.setEnabled(true);
		} else {
			vMIDILinkText.setEnabled(false);
		}

		if (firstTimeRunning) {
			addPanels();
			setInteractions();
		}

		PianoDialog.getContentPane().add(PianoWindow);
		PianoDialog.setVisible(true);
	}

	/**
	 * Adds the GUI elements to the created layout
	 */
	private void addPanels() {
		vMIDIWrapper.add(vMIDIText, BorderLayout.WEST);
		vMIDIWrapper.add(vMIDIButton, BorderLayout.EAST);

		velocityText.add(velocity, BorderLayout.EAST);
		tempoText.add(tempo, BorderLayout.EAST);
		octaveText.add(octaveShift, BorderLayout.EAST);
		lastInText.add(lastKey, BorderLayout.EAST);
		sustainText.add(sustain, BorderLayout.EAST);

		rightPanel.add(tutorial, BorderLayout.NORTH);

		infoPanel.add(lastInText, BorderLayout.EAST);
		infoPanel.add(octaveText, BorderLayout.EAST);
		infoPanel.add(tempoText, BorderLayout.EAST);
		infoPanel.add(velocityText, BorderLayout.EAST);
		infoPanel.add(sustainText, BorderLayout.EAST);
		leftPanel.add(infoPanel);
		leftPanel.add(vMIDIWrapper);
		if (!allowVMIDI) {
			vMIDIWrapper.add(vMIDILinkText);
			leftPanel.add(vMIDILinkWrapper, BorderLayout.SOUTH);
		}

		PianoWindow.add(leftPanel);
		PianoWindow.add(rightPanel);
	}

	/**
	 * Defines and implements all the interactions for the Game of Life GUI
	 */
	private void setInteractions() {
		PianoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quitPiano();
			}
		});

		PianoDialog.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (DEBUG)
					System.out.println(arg0.getX() + " " + arg0.getY());
			}

		});

		vMIDILinkText.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (vMIDILinkText.isEnabled()) {
					vMIDILinkText.setForeground(Color.GREEN);
				}
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(vMIDILink);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Desktop linking not supported");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (vMIDILinkText.isEnabled()) {
					vMIDILinkText.setForeground(Color.BLUE);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (vMIDILinkText.isEnabled()) {
					vMIDILinkText.setForeground(Color.BLUE);
					vMIDILinkText.setFont(new Font("Arial", Font.BOLD, 10));
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (vMIDILinkText.isEnabled()) {
					vMIDILinkText.setForeground(Color.BLUE);
					vMIDILinkText.setFont(new Font("Arial", Font.PLAIN, 10));
				}
			}

		});

		vMIDIButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (vMIDIButton.isEnabled()) {
					toggleVMIDIButton();
				}
				changingvMIDI = true;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (vMIDIButton.isEnabled()) {
					changingvMIDI = false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (vMIDIButton.isEnabled()) {
					if (usingVMIDI)
						changeButton(vMIDIButton, "on", "hover");
					else
						changeButton(vMIDIButton, "off", "hover");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (vMIDIButton.isEnabled()) {
					if (usingVMIDI)
						changeButton(vMIDIButton, "on", "default");
					else
						changeButton(vMIDIButton, "off", "default");
				}
			}
		});
	}

	/**
	 * Asks for user input before initializing the game.
	 */
	private void askSettings() {
		ready = true;
	}

	/**
	 * Enables the Virtual MIDI button if the user has the necessary software
	 * installed
	 * 
	 * @param status
	 *            True if the user can use Virtual MIDI, False otherwise
	 */
	private static void setVMIDI(boolean status) {
		usingVMIDI = status;
		if (usingVMIDI)
			changeButton(vMIDIButton, "on", "default");
		else
			changeButton(vMIDIButton, "off", "default");

		Piano.setVMIDIUse(usingVMIDI);
	}

	/**
	 * Toggles the status of the Virtual MIDI Button and enables its use or
	 * disables it accordingly
	 */
	private void toggleVMIDIButton() {
		if (!vMIDIButton.isEnabled() || changingvMIDI)
			return;
		usingVMIDI = !usingVMIDI;
		System.out.println((usingVMIDI) ? "Now using VMIDI"
				: "Now not using VMIDI");
		if (usingVMIDI)
			setVMIDI(true);
		else
			setVMIDI(false);
	}

	/**
	 * Sets the window position and dimenstions
	 */
	private void initialize() {

		Main.setCardIcon(Main.pianoCard, "piano", "selected");
		allowVMIDI = VirtualMIDI.found();
		if (!allowVMIDI) {
			vMIDIButton.setEnabled(false);
		}

		setVMIDI(false);

		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();
	}

	/**
	 * Updates the velocity text
	 * 
	 * @param value
	 *            Velocity
	 */
	public static void setVelocity(int value) {
		velocity.setText("Velocity: " + value);
	}

	/**
	 * Updates the Tempo text
	 * 
	 * @param value
	 *            Tempo
	 */
	public static void setTempo(int value) {
		tempo.setText("Tempo: " + value + "bpm");
	}

	/**
	 * Updates the octave shift text
	 * 
	 * @param value
	 *            Octave shift
	 */
	public static void setOctaveShift(int value) {
		if (value >= 0)
			octaveShift.setText("Octave +" + value);
		else
			octaveShift.setText("Octave " + value);
	}

	/**
	 * Updates the last key pressed text
	 * 
	 * @param key
	 *            Last key pressed
	 */
	public static void setLastKey(String key) {
		lastKey.setText("Last Pressed: " + key);
	}

	/**
	 * Updates the sustain status text
	 * 
	 * @param status
	 *            Sustain status
	 */
	public static void setSustain(String status) {
		sustain.setText("Sustain: " + status);
	}

	/**
	 * Updates the Virtual MIDI button
	 * 
	 * @param button
	 *            Button
	 * @param name
	 *            File name of the button images
	 * @param state
	 *            Status of the button to change to
	 */
	private static void changeButton(JLabel button, String name, String state) {
		String file = "/gui_images/Icons/Buttons/" + name + "Button_" + state
				+ ".png";
		button.setIcon(new ImageIcon(Main.class.getResource(file)));
	}

	/**
	 * Method called to close the GUI from the Piano class
	 */
	public static void quit() {
		quitPiano();
		Main.pianoRunning = false;
		Main.resetCards();
		PianoDialog.dispose();
		Main.nothingRunning();
		Main.setCardIcon(Main.pianoCard, "piano", "default");
	}

	/**
	 * Method called to close the GUI and demo. <br>
	 * Should not be called from within Piano class.
	 * 
	 * @return 1
	 */
	public static int quitPiano() {
		Piano.forceQuit();
		Main.pianoRunning = false;
		Main.resetCards();
		PianoDialog.dispose();
		return 1;
	}

}
