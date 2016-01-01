package gui;

import launchpad_s_plus.*;
import tech_demo.*;

import javax.sound.midi.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

/**
 * Main program class. Implements main GUI window and runs all the sub-programs
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class Main {
	// For Debugging purposes
	private static boolean DEBUG = true;
	private static boolean simulatedFailedConnection = false;
	private static boolean simulatedAvailableConnection = true;
	private static boolean disableCardsTEST = false;

	// Statuses
	public static boolean programRunning = false;
	private static boolean closing = false;
	private static boolean lpConnected = false;
	private static boolean setupInitiationError = false;
	private static boolean somethingRunning = false;
	public static boolean gameOfLifeRunning = false;
	public static boolean snakeRunning = false;
	public static boolean pianoRunning = false;
	public static boolean checkersRunning = false;
	public static boolean lightsOutRunning = false;
	public static boolean simonRunning = false;
	public static boolean reactionGameRunning = false;
	public static boolean reactiveDemoRunning = false;
	public static boolean animationRunning = false;
	public static boolean textScrollRunning = false;
	private static String lastMidiInReceived = "None";
	private static String currentlyRunning = "None";
	private static int previousColor;

	// Variables
	private static int hoverVolume = 20;

	// Main window frame
	private static JFrame frmLaunchpadS;
	static JLabel mainCard = new JLabel("");
	static JLabel currentRunningPanel = new JLabel();
	static JLabel lastMidiInPanel = new JLabel();
	static JPanel panel_2 = new JPanel();

	// Menu Items
	static JMenuBar menuBar = new JMenuBar();
	static final JMenu mnFile = new JMenu("File");
	static final JMenuItem mntmReconnect = new JMenuItem("Reconnect");
	static final JMenuItem mntmExit = new JMenuItem("Exit");
	static final JMenu mnStats = new JMenu("?");
	static final JMenuItem mntmStatistics = new JMenuItem("Statistics");

	// Announcing all GUI windows
	static CheckersGUI checkersWindow;
	static GameOfLifeGUI gameOfLifeWindow;
	static LightsOutGUI lightsOutWindow;
	static ReactionGameGUI reactionGameWindow;
	static SnakeGUI snakeWindow;
	static PianoGUI pianoWindow;
	static TextScrollGUI textScrollWindow;
	static ReactiveDemoGUI reactiveDemoWindow;
	static AnimationGUI animationWindow;

	// Announcing all the cards
	final static JLabel gameOfLifeCard = new JLabel("");
	final static JLabel pianoCard = new JLabel("");
	final static JLabel snakeCard = new JLabel("");
	final static JLabel checkersCard = new JLabel("");
	final static JLabel reactionGameCard = new JLabel("");
	final static JLabel reactiveDemoCard = new JLabel("");
	final static JLabel lightsOutCard = new JLabel("");
	final static JLabel simonCard = new JLabel("");
	final static JLabel animationCard = new JLabel("");
	final static JLabel textScrollCard = new JLabel("");

	// Dimensions
	final int dimensionX = 650;
	final int dimensionY = 415;
	final int maxSecondaryWindowW = 600;
	final static int cardX = 100;
	final static int cardY = 180;

	/**
	 * Launches the application.
	 * 
	 * @param args
	 *            Application arguments. Unused for now
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					Main window = new Main();
					window.frmLaunchpadS.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		try {
			Program.initiation();
		} catch (MidiUnavailableException | InterruptedException
				| InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			setupInitiationError = true;
		}
		initialize();
		disableIncomplete();
		programRunning = true;
	}

	// TODO Finish the stuff in here
	private void disableIncomplete() {
		// checkersCard.setEnabled(false);
	}

	/**
	 * Runs an small introduction to confirm the program is ready to run. <br>
	 * Scrolls a short text on the Launchpad S and plays an intro sound.
	 */
	private void runIntro() {
		new PlaySound("/Samples/App/intro", 40, true).setName("Intro Sound Thread");

		Thread Intro = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("\nRunning Intro");
					Display.text("S+", Colors.randomColor(), 3, 3);
					Input.clearPressed();
					while (!Input.getLastKeyPressed().equals("CC 0")) {
						delay(10);
					}
					Input.setLastKeyPressed("None");
					delay(10);
					Display.stopText();
					System.out.println("\n\n");
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
				}
			}
		};
		Intro.setName("Intro Thread");
		Intro.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		lpConnected = SetupProcess.lpdetected;
		if (DEBUG)
			lpConnected = !simulatedFailedConnection;

		if (lpConnected) {
			try {
				Launchpad.disableSelfListen();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		frmLaunchpadS = new JFrame();
		frmLaunchpadS.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/Images/icon2.png")));
		if (lpConnected) {
			frmLaunchpadS.setTitle("Launchpad S+");

			if (setupInitiationError) {
				Thread InitiationError = new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (Exception ex) {
							Thread.currentThread().interrupt();
						}
						frmLaunchpadS
								.setTitle("Launchpad S+ - Unable to connect");
						disableAllCards();
						displayError("test");
					}
				};
				InitiationError.setName("Initiation Error Thread");
				InitiationError.start();
			}
		} else {
			frmLaunchpadS.setTitle("Launchpad S+ - No Launchpad connected");
			disableAllCards();
			Thread noLaunchpadConnectedThread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
					displayConnectionError();
				}
			};
			noLaunchpadConnectedThread.setName("No Launchpad Connection Thread");
			noLaunchpadConnectedThread.start();
		}
		frmLaunchpadS.setResizable(false);

		if (lpConnected)
			runIntro();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();

		int xWindow = (int) (screenWidth / 2) - (dimensionX / 2)
				- (maxSecondaryWindowW / 2) - 10;
		int yWindow = (int) (screenHeight / 2) - (dimensionY / 2);

		frmLaunchpadS.setBounds(xWindow, yWindow, dimensionX, dimensionY);

		frmLaunchpadS.getContentPane().add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 100, 94, 90, 110, 0 };
		gbl_panel_2.rowHeights = new int[] { 5, 185, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		// Announcing all card interactions
		setInteractions();

		// Initializing all the cards' main image, location and constraints
		initializeCards();

		JPanel panel = new JPanel();
		panel.setBorder(null);
		frmLaunchpadS.getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
				.decode("110px:grow"), }, new RowSpec[] {
				RowSpec.decode("110px"), FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(40dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(40dlu;default)"), }));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "1, 1, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
				.decode("110px"), }, new RowSpec[] { RowSpec.decode("110px"), }));

		panel_1.add(mainCard, "1, 1, center, center");

		if (checkLaunchpadConnection())
			setMainCardIcon("red");
		else
			setMainCardIcon("empty");
		mainCard.setAlignmentY(Component.TOP_ALIGNMENT);

		JPanel panel_3 = new JPanel();
		panel.add(panel_3, "1, 3, fill, fill");
		panel_3.setLayout(new GridLayout(2, 0, 0, 0));

		JLabel lblCurrentlyRunning = new JLabel("Currently Running:");
		lblCurrentlyRunning.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(lblCurrentlyRunning);

		currentRunningPanel.setText("None");
		currentRunningPanel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(currentRunningPanel);

		JPanel panel_4 = new JPanel();
		panel.add(panel_4, "1, 5, fill, fill");
		panel_4.setLayout(new GridLayout(2, 0, 0, 0));

		JLabel label_5 = new JLabel("Last MIDI In:");
		label_5.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(label_5);

		lastMidiInPanel.setText("None");
		lastMidiInPanel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lastMidiInPanel);

		frmLaunchpadS.setJMenuBar(menuBar);

		menuBar.add(mnFile);

		mntmReconnect.setSelected(true);
		mnFile.add(mntmReconnect);
		mnFile.add(mntmExit);
		mnStats.add(mntmStatistics);

		menuBar.add(mnStats);
	}

	/**
	 * Initializes the contents of the application cards
	 */
	private void initializeCards() {
		setCardIcon(checkersCard, "checkers", "default");
		GridBagConstraints gbc_checkersCard = new GridBagConstraints();
		gbc_checkersCard.anchor = GridBagConstraints.NORTH;
		gbc_checkersCard.insets = new Insets(0, 0, 5, 5);
		gbc_checkersCard.gridx = 0;
		gbc_checkersCard.gridy = 0;
		panel_2.add(checkersCard, gbc_checkersCard);

		setCardIcon(gameOfLifeCard, "gameOfLife", "default");
		GridBagConstraints gbc_gameOfLifeCard = new GridBagConstraints();
		gbc_gameOfLifeCard.anchor = GridBagConstraints.NORTH;
		gbc_gameOfLifeCard.insets = new Insets(0, 0, 5, 5);
		gbc_gameOfLifeCard.gridx = 1;
		gbc_gameOfLifeCard.gridy = 0;
		panel_2.add(gameOfLifeCard, gbc_gameOfLifeCard);

		setCardIcon(lightsOutCard, "lightsOut", "default");
		GridBagConstraints gbc_lightsOutCard = new GridBagConstraints();
		gbc_lightsOutCard.insets = new Insets(0, 0, 5, 5);
		gbc_lightsOutCard.gridx = 2;
		gbc_lightsOutCard.gridy = 0;
		panel_2.add(lightsOutCard, gbc_lightsOutCard);

		setCardIcon(reactionGameCard, "reactionGame", "default");
		GridBagConstraints gbc_reactionGameCard = new GridBagConstraints();
		gbc_reactionGameCard.anchor = GridBagConstraints.NORTH;
		gbc_reactionGameCard.insets = new Insets(0, 0, 5, 5);
		gbc_reactionGameCard.gridx = 3;
		gbc_reactionGameCard.gridy = 0;
		panel_2.add(reactionGameCard, gbc_reactionGameCard);

		setCardIcon(simonCard, "simon", "default");
		GridBagConstraints gbc_simonCard = new GridBagConstraints();
		gbc_simonCard.anchor = GridBagConstraints.NORTH;
		gbc_simonCard.insets = new Insets(0, 0, 5, 5);
		gbc_simonCard.gridx = 4;
		gbc_simonCard.gridy = 0;
		panel_2.add(simonCard, gbc_simonCard);

		setCardIcon(snakeCard, "snake", "default");
		GridBagConstraints gbc_snakeCard = new GridBagConstraints();
		gbc_snakeCard.anchor = GridBagConstraints.NORTH;
		gbc_snakeCard.insets = new Insets(0, 0, 5, 5);
		gbc_snakeCard.gridx = 0;
		gbc_snakeCard.gridy = 1;
		panel_2.add(snakeCard, gbc_snakeCard);

		setCardIcon(pianoCard, "piano", "default");
		GridBagConstraints gbc_pianoCard = new GridBagConstraints();
		gbc_pianoCard.anchor = GridBagConstraints.NORTH;
		gbc_pianoCard.insets = new Insets(0, 0, 5, 5);
		gbc_pianoCard.gridx = 1;
		gbc_pianoCard.gridy = 1;
		panel_2.add(pianoCard, gbc_pianoCard);

		setCardIcon(textScrollCard, "textScroll", "default");
		GridBagConstraints gbc_textScrollCard = new GridBagConstraints();
		gbc_textScrollCard.anchor = GridBagConstraints.NORTH;
		gbc_textScrollCard.insets = new Insets(0, 0, 5, 5);
		gbc_textScrollCard.gridx = 2;
		gbc_textScrollCard.gridy = 1;
		panel_2.add(textScrollCard, gbc_textScrollCard);

		setCardIcon(reactiveDemoCard, "reactiveDemo", "default");
		GridBagConstraints gbc_reactiveDemoCard = new GridBagConstraints();
		gbc_reactiveDemoCard.anchor = GridBagConstraints.NORTH;
		gbc_reactiveDemoCard.insets = new Insets(0, 0, 5, 5);
		gbc_reactiveDemoCard.gridx = 3;
		gbc_reactiveDemoCard.gridy = 1;
		panel_2.add(reactiveDemoCard, gbc_reactiveDemoCard);

		setCardIcon(animationCard, "animation", "default");
		GridBagConstraints gbc_animationCard = new GridBagConstraints();
		gbc_animationCard.anchor = GridBagConstraints.NORTH;
		gbc_animationCard.insets = new Insets(0, 0, 5, 5);
		gbc_animationCard.gridx = 4;
		gbc_animationCard.gridy = 1;
		panel_2.add(animationCard, gbc_animationCard);
	}

	/**
	 * Defines and implements the basic interactions for the Main GUI
	 */
	private void setInteractions() {
		frmLaunchpadS.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					quitProgram();
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					delay(300);
					System.exit(0);
				}
			}
		});

		mntmReconnect.setEnabled(false);
		mntmReconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (mntmReconnect.isEnabled())
					reconnect();
			}
		});

		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				quitProgram();
			}
		});

		mntmStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				displayStatistics();
			}
		});

		setCardInteractions();
	}

	/**
	 * Defines and implements the card interactions for the Main GUI
	 */
	private void setCardInteractions() {
		mainCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");
				randomizeMainCardColor();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
				randomizeMainCardColor();
			}
		});

		gameOfLifeCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!gameOfLifeRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(gameOfLifeCard, "gameOfLife", "hovered");
				} else
					setCardIcon(gameOfLifeCard, "gameOfLife", "quit");
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!gameOfLifeRunning) {
					setCardIcon(gameOfLifeCard, "gameOfLife", "default");
				} else
					setCardIcon(gameOfLifeCard, "gameOfLife", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Game Of Life");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(gameOfLifeCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !gameOfLifeCard.isEnabled()) {
					setCardIcon(gameOfLifeCard, "gameOfLife", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!gameOfLifeRunning) {
					if (!somethingRunning) {
						runGameOfLife(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runGameOfLife(true);
						setClosing(false);
					}
				} else {
					runGameOfLife(false);
				}
			}
		});

		checkersCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!checkersRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(checkersCard, "checkers", "hovered");
				} else {
					setCardIcon(checkersCard, "checkers", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!checkersRunning)
					setCardIcon(checkersCard, "checkers", "default");
				else
					setCardIcon(checkersCard, "checkers", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Checkers");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(checkersCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !checkersCard.isEnabled()) {
					setCardIcon(checkersCard, "checkers", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!checkersRunning) {
					if (!somethingRunning) {
						runCheckers(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runCheckers(true);
						setClosing(false);
					}
				} else {
					runCheckers(false);
				}
			}
		});

		pianoCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!pianoRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(pianoCard, "piano", "hovered");
				} else {
					setCardIcon(pianoCard, "piano", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!pianoRunning)
					setCardIcon(pianoCard, "piano", "default");
				else
					setCardIcon(pianoCard, "piano", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Piano Demo");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(pianoCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !pianoCard.isEnabled()) {
					setCardIcon(pianoCard, "piano", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (mouseIsInBounds(pianoCard, arg0)) {
					if (!pianoRunning) {
						if (!somethingRunning) {
							runPiano(true);
							setClosing(false);
						} else {
							quitRunning();
							delay(100);
							runPiano(true);
							setClosing(false);
						}
					} else {
						runPiano(false);
					}
				}
			}
		});

		reactiveDemoCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!reactiveDemoRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(reactiveDemoCard, "reactiveDemo", "hovered");
				} else {
					setCardIcon(reactiveDemoCard, "reactiveDemo", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!reactiveDemoRunning) {
					setCardIcon(reactiveDemoCard, "reactiveDemo", "default");
				} else
					setCardIcon(reactiveDemoCard, "reactiveDemo", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Reactive Demo");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(reactiveDemoCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !reactiveDemoCard.isEnabled()) {
					setCardIcon(reactiveDemoCard, "reactiveDemo", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!reactiveDemoRunning) {
					if (!somethingRunning) {
						runReactiveDemo(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runReactiveDemo(true);
						setClosing(false);
					}
				} else {
					runReactiveDemo(false);
				}
			}
		});

		snakeCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!snakeRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(snakeCard, "snake", "hovered");
				} else {
					setCardIcon(snakeCard, "snake", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!snakeRunning)
					setCardIcon(snakeCard, "snake", "default");
				else
					setCardIcon(snakeCard, "snake", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Snake");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(snakeCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !snakeCard.isEnabled()) {
					setCardIcon(snakeCard, "snake", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!snakeRunning) {
					if (!somethingRunning) {
						runSnake(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runSnake(true);
						setClosing(false);
					}
				} else {
					runSnake(false);
				}
			}
		});

		lightsOutCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!lightsOutRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(lightsOutCard, "lightsOut", "hovered");
				} else {
					setCardIcon(lightsOutCard, "lightsOut", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!lightsOutRunning)
					setCardIcon(lightsOutCard, "lightsOut", "default");
				else
					setCardIcon(lightsOutCard, "lightsOut", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Lights Out");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(lightsOutCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !lightsOutCard.isEnabled()) {
					setCardIcon(lightsOutCard, "lightsOut", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!lightsOutRunning) {
					if (!somethingRunning) {
						runLightsOut(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runLightsOut(true);
						setClosing(false);
					}
				} else {
					runLightsOut(false);
				}
			}
		});

		reactionGameCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!reactionGameRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(reactionGameCard, "reactionGame", "hovered");
				} else {
					setCardIcon(reactionGameCard, "reactionGame", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!reactionGameRunning)
					setCardIcon(reactionGameCard, "reactionGame", "default");
				else
					setCardIcon(reactionGameCard, "reactionGame", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Reaction Game");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(reactionGameCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !reactionGameCard.isEnabled()) {
					setCardIcon(reactionGameCard, "reactionGame", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!reactionGameRunning) {
					if (!somethingRunning) {
						runReactionGame(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runReactionGame(true);
						setClosing(false);
					}
				} else {
					runReactionGame(false);
				}
			}
		});

		simonCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!simonRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(simonCard, "simon", "hovered");
				} else {
					setCardIcon(simonCard, "simon", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!simonRunning)
					setCardIcon(simonCard, "simon", "default");
				else
					setCardIcon(simonCard, "simon", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Simon");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(simonCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !simonCard.isEnabled()) {
					setCardIcon(simonCard, "simon", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!simonRunning) {
					if (!somethingRunning) {
						runSimon(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runSimon(true);
						setClosing(false);
					}
				} else {
					runSimon(false);
				}
			}
		});

		animationCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!animationRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(animationCard, "animation", "hovered");
				} else {
					setCardIcon(animationCard, "animation", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!animationRunning)
					setCardIcon(animationCard, "animation", "default");
				else
					setCardIcon(animationCard, "animation", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Animations");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(animationCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !animationCard.isEnabled()) {
					setCardIcon(animationCard, "animation", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!animationRunning) {
					if (!somethingRunning) {
						runAnimation(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runAnimation(true);
						setClosing(false);
					}
				} else {
					runAnimation(false);
				}
			}
		});

		textScrollCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (!textScrollRunning) {
					new PlaySound("/Samples/App/hover", hoverVolume, true).setName("Hover Sound Thread");;
					setCardIcon(textScrollCard, "textScroll", "hovered");
				} else {
					setCardIcon(textScrollCard, "textScroll", "quit");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (!textScrollRunning)
					setCardIcon(textScrollCard, "textScroll", "default");
				else
					setCardIcon(textScrollCard, "textScroll", "selected");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection))
					return;
				if (!somethingRunning) {
					setCurrentlyRunning("Text Scroll Demo");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!mouseIsInBounds(textScrollCard, arg0)
						|| !SwingUtilities.isLeftMouseButton(arg0)
						|| (!lpConnected && !simulatedAvailableConnection)
						|| !textScrollCard.isEnabled()) {
					setCardIcon(textScrollCard, "textScroll", "default");
					setCurrentlyRunning("None");
					return;
				}
				if (!textScrollRunning) {
					if (!somethingRunning) {
						runTextScroll(true);
						setClosing(false);
					} else {
						quitRunning();
						delay(100);
						runTextScroll(true);
						setClosing(false);
					}
				} else {
					runTextScroll(false);
				}
			}
		});
	}

	/**
	 * Checks whether the mouse in within the bounds of the given card
	 * 
	 * @param card
	 *            Reference card to check
	 * @param arg0
	 *            Mouse event. Contains mouse position information
	 * 
	 * @return True if the mouse is within the card's bounds, False otherwise
	 */
	protected boolean mouseIsInBounds(JLabel card, MouseEvent arg0) {
		return ((arg0.getX() > 0) && (arg0.getY() > 0)
				&& (arg0.getX() < card.getWidth()) && (arg0.getY() < card
				.getHeight()));
	}

	/**
	 * Quits the entire program
	 */
	protected void quitProgram() {
		quitRunning();
		Program.closeProgram();
		System.exit(0);
	}

	/**
	 * Quits any sub program that might be running
	 */
	protected void quitRunning() {
		if (somethingRunning) {
			if (gameOfLifeRunning)
				runGameOfLife(false);
			else if (snakeRunning)
				runSnake(false);
			else if (pianoRunning)
				runPiano(false);
			else if (checkersRunning)
				runCheckers(false);
			else if (lightsOutRunning)
				runLightsOut(false);
			else if (simonRunning)
				runSimon(false);
			else if (reactionGameRunning)
				runReactionGame(false);
			else if (reactiveDemoRunning)
				runReactiveDemo(false);
			else if (animationRunning)
				runAnimation(false);
			else
				runTextScroll(false);
		}
	}

	/**
	 * Quits any sub program that might be running and quits the program
	 * 
	 * 
	 * @return 1
	 */
	protected int quitAll() {
		Thread quittingAll = new Thread() {
			@Override
			public void run() {
				try {
					Display.clearPads();
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
				}
			}
		};
		quittingAll.setName("Quitting All Thread");
		quittingAll.start();

		quitRunning();

		setCurrentlyRunning("none");
		System.exit(0);
		return 1;
	}

	/**
	 * Sets the currently running sub program. Use "none" if there is no more
	 * sub program running.
	 * 
	 * @param string
	 *            Sub program name
	 */
	public static void setCurrentlyRunning(String string) {
		if (string == "None")
			somethingRunning = false;
		currentlyRunning = string;
		repaintCurrent();
		delay(50);
	}

	/**
	 * Updates the currently running sub program
	 */
	protected synchronized static void repaintCurrent() {
		currentRunningPanel.setText(currentlyRunning);
	}

	/**
	 * Updates the last received MIDI in value
	 */
	protected synchronized static void repaintLastMidi() {
		lastMidiInPanel.setText(lastMidiInReceived);
	}

	/**
	 * Sets the last received MIDI input
	 * 
	 * @param string
	 *            MIDI note name
	 */
	public static void setLastReceivedInput(String string) {
		lastMidiInReceived = string;
		repaintLastMidi();
	}

	/**
	 * Delays for a certain amount of time in MS
	 * 
	 * @param time
	 *            Delay duration in MS
	 */
	static void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a new Game Of Life GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Game Of Life is not already running
	 */
	private void runGameOfLife(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!gameOfLifeCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Game Of Life");
			somethingRunning = true;
			gameOfLifeRunning = true;
			setCardIcon(gameOfLifeCard, "gameOfLife", "selected");
			Thread GameOfLifeGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new GameOfLifeGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			GameOfLifeGUIThread.setName("Game of Life GUI Thread");
			GameOfLifeGUIThread.start();
		} else {
			GameOfLifeGUI.quitGameOfLife();
			gameOfLifeRunning = false;
			setCardIcon(gameOfLifeCard, "gameOfLife", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Snake GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Snake is not already running
	 */
	private void runSnake(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!snakeCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Snake");
			somethingRunning = true;
			snakeRunning = true;
			setCardIcon(snakeCard, "snake", "selected");
			Thread SnakeGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new SnakeGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			SnakeGUIThread.setName("Snake GUI Thread");
			SnakeGUIThread.start();
		} else {
			SnakeGUI.quitSnake();
			snakeRunning = false;
			setCardIcon(snakeCard, "snake", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Lights Out GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Lights Outis not already running
	 */
	private void runLightsOut(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!lightsOutCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Lights Out");
			somethingRunning = true;
			lightsOutRunning = true;
			setCardIcon(lightsOutCard, "lightsOut", "selected");
			Thread LightsOutThread = new Thread() {
				@Override
				public void run() {
					try {
						new LightsOutGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			LightsOutThread.setName("Lights Out GUI Thread");
			LightsOutThread.start();
		} else {
			closing = true;
			LightsOutGUI.quitLightsOut();
			lightsOutRunning = false;
			setCardIcon(lightsOutCard, "lightsOut", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Simon GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Simon is not already running
	 */
	private void runSimon(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!simonCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Simon");
			somethingRunning = true;
			simonRunning = true;
			setCardIcon(simonCard, "simon", "selected");
			Thread SimonGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new SimonGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			SimonGUIThread.setName("Simon GUI Thread");
			SimonGUIThread.start();
		} else {
			SimonGUI.quitSimon();
			simonRunning = false;
			setCardIcon(simonCard, "simon", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Scrolling Text GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Scrolling Text is not already running
	 */
	private void runTextScroll(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!textScrollCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("TextScroll");
			somethingRunning = true;
			textScrollRunning = true;
			setCardIcon(textScrollCard, "textScroll", "selected");
			Thread ScrollingTextGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new TextScrollGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			ScrollingTextGUIThread.setName("Scrolling Text GUI Thread");
			ScrollingTextGUIThread.start();
		} else {
			TextScrollGUI.quitScrollingText();
			textScrollRunning = false;
			setCardIcon(textScrollCard, "textScroll", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Checkers GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Checkers is not already running
	 */
	private void runCheckers(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!checkersCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Checkers");
			somethingRunning = true;
			checkersRunning = true;
			setCardIcon(checkersCard, "checkers", "selected");
			Thread CheckersGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new CheckersGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			CheckersGUIThread.setName("Checkers GUI Thread");
			CheckersGUIThread.start();
		} else {
			CheckersGUI.quitCheckers();
			checkersRunning = false;
			setCardIcon(checkersCard, "checkers", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Reaction Game GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Reaction Game is not already running
	 */
	private void runReactionGame(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!reactionGameCard.isEnabled())
			displayError();

		if (start) {
			Thread ReactionGameGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new ReactionGameGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			ReactionGameGUIThread.setName("Reaction Game GUI Thread");
			ReactionGameGUIThread.start();
		} else {
			reactionGameRunning = false;
			ReactionGameGUI.quitReactionGame();
			setCurrentlyRunning("None");
		}
		// while (ReactiveDemo.isRunning()) {
		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * Starts a new Reactive Demo GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Reactive Demo is not already running
	 */
	private void runReactiveDemo(boolean start) {
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!reactiveDemoCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Reactive Demo");
			somethingRunning = true;
			reactiveDemoRunning = true;
			setCardIcon(reactiveDemoCard, "reactiveDemo", "selected");
			Thread ReactiveDemoGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new ReactiveDemoGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			};
			ReactiveDemoGUIThread.setName("Reactive Demo GUI Thread");
			ReactiveDemoGUIThread.start();
		} else {
			reactiveDemoRunning = false;
			ReactiveDemoGUI.quit();
			setCardIcon(reactiveDemoCard, "reactiveDemo", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Piano Demo GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Piano Demo is not already running
	 */
	private void runPiano(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!pianoCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Piano Demo");
			somethingRunning = true;
			pianoRunning = true;
			setCardIcon(pianoCard, "piano", "selected");
			Thread PianoDemoGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new PianoGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			PianoDemoGUIThread.setName("Piano Demo GUI Thread");
			PianoDemoGUIThread.start();
		} else {
			PianoGUI.quitPiano();
			pianoRunning = false;
			setCardIcon(pianoCard, "piano", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts a new Animation GUI or quits an already running one
	 * 
	 * @param start
	 *            True if Animation is not already running
	 */
	private void runAnimation(boolean start) {
		if (!start)
			connectivityCheck(true);
		if (!lpConnected && !simulatedAvailableConnection)
			return;

		if (!animationCard.isEnabled())
			displayError();

		if (start) {
			setCurrentlyRunning("Animation Demo");
			somethingRunning = true;
			animationRunning = true;
			setCardIcon(animationCard, "animation", "selected");
			Thread AnimationGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new AnimationGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}

				}
			};
			AnimationGUIThread.setName("Animation Demo GUI Thread");
			AnimationGUIThread.start();
		} else {
			AnimationGUI.quit();
			animationRunning = false;
			setCardIcon(animationCard, "animation", "default");
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Starts or stops Idle animations
	 * 
	 * @param start
	 *            True if idle animations should be started, False otherwise
	 */
	@SuppressWarnings("unused")
	private void runIdle(boolean start) {
		if (!lpConnected)
			return;

		if (start) {
			setCurrentlyRunning("Idle");
			somethingRunning = true;
			Thread IdleAnimationThread = new Thread() {
				@Override
				public void run() {
					try {
						new IdleAnimations();
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			};
			IdleAnimationThread.setName("Idea Animation Thread");
			IdleAnimationThread.start();
		} else {
			setCurrentlyRunning("None");
		}
	}

	/**
	 * Resets all the cards to their default picture
	 */
	static void resetCards() {
		setCardIcon(checkersCard, "checkers", "default");
		setCardIcon(gameOfLifeCard, "gameOfLife", "default");
		setCardIcon(lightsOutCard, "lightsOut", "default");
		setCardIcon(reactionGameCard, "reactionGame", "default");
		setCardIcon(simonCard, "simon", "default");
		setCardIcon(snakeCard, "snake", "default");
		setCardIcon(pianoCard, "piano", "default");
		setCardIcon(textScrollCard, "textScroll", "default");
		setCardIcon(reactiveDemoCard, "reactiveDemo", "default");
		setCardIcon(animationCard, "animation", "default");
	}

	/**
	 * Sets the currently running sub program status to "none"
	 */
	public static void nothingRunning() {
		somethingRunning = false;
		setCurrentlyRunning("None");
	}

	/**
	 * If no Launchpad S connection is found, will call methods to disable all
	 * cards and display an error
	 */
	private void connectionNotFound() {
		displayConnectionError();
		setMainCardIcon("empty");
		if (!DEBUG || disableCardsTEST)
			disableAllCards();
	}

	/**
	 * Disables all the cards. To be used if there is no connection
	 */
	private void disableAllCards() {
		checkersCard.setEnabled(false);
		gameOfLifeCard.setEnabled(false);
		lightsOutCard.setEnabled(false);
		reactionGameCard.setEnabled(false);
		simonCard.setEnabled(false);
		snakeCard.setEnabled(false);
		pianoCard.setEnabled(false);
		textScrollCard.setEnabled(false);
		reactiveDemoCard.setEnabled(false);
		animationCard.setEnabled(false);
	}

	/**
	 * Displays an error stating there is no Launchpad S connection
	 */
	private void displayConnectionError() {
		displayError("noConnection");
	}

	/**
	 * Displays an error
	 */
	private void displayError() {
		Thread UnknownErrorGUIThread = new Thread() {
			@Override
			public void run() {
				try {
					new Error("unknown");
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
				}
			}
		};
		UnknownErrorGUIThread.setName("Unknown Error GUI Thread");
		UnknownErrorGUIThread.start();
	}

	/**
	 * Displays an error
	 * 
	 * @param type error type/message
	 */
	private void displayError(final String type) {
		Thread CustomErrorGUIThread = new Thread() {
			@Override
			public void run() {
				try {
					new Error(type);
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
				}
			}
		};
		CustomErrorGUIThread.setName("Custom Error GUI Thread");
		CustomErrorGUIThread.start();
	}

	/**
	 * Randomizes the main logo on the top left to a random color. <br>
	 * Method is called when the mouse is hovered over it
	 */
	private void randomizeMainCardColor() {
		if (!lpConnected) {
			return;
		}
		int color = Calculate.randInt(0, 2);
		while (color == previousColor)
			color = Calculate.randInt(0, 2);

		previousColor = color;
		switch (color) {
		case 0:
			setMainCardIcon("red");
			break;
		case 1:
			setMainCardIcon("green");
			break;
		case 2:
			setMainCardIcon("yellow");
			break;
		default:
			setMainCardIcon("yellow");
			break;
		}
	}

	/**
	 * Checks whether the Launchpad S is connected or not
	 * 
	 * 
	 * @return True if there is a connection, False otherwise
	 */
	private boolean checkLaunchpadConnection() {
		try {
			lpConnected = launchpad_s_plus.Launchpad.found();
			if (DEBUG && simulatedFailedConnection)
				lpConnected = false;
			return lpConnected;
		} catch (Exception e) {
			e.printStackTrace();
			lpConnected = false;
			return lpConnected;
		}
	}

	/**
	 * Changes the main logo's color
	 * 
	 * @param color
	 *            Color to change main logo to
	 */
	private void setMainCardIcon(String color) {
		mainCard.setIcon(new ImageIcon(Main.class
				.getResource("/gui_images/Icons/logo_" + color + ".png")));
	}

	/**
	 * Changes the image of the given card based on its state. <br>
	 * States can be any of the following: <br>
	 * - default: The sub program is not running and the mouse is not above the
	 * card <br>
	 * - hovered: The sub program is not running and the mouse is hovering above
	 * the card <br>
	 * - selected: The sub program is running and the mouse is not hovering
	 * above the card <br>
	 * - quit: The sub program is running and the mouse is hovering above the
	 * card
	 * 
	 * @param card
	 *            JLabel card to modify the image of
	 * @param cardName
	 *            Name if the card's file, not including the state
	 * @param state
	 *            State of the card to be changed to
	 */
	public static void setCardIcon(JLabel card, String cardName, String state) {
		if (cardName == "test")
			card.setIcon(new ImageIcon(Main.class.getResource("/Images/"
					+ cardName + "Card_" + state + ".png")));
		else
			card.setIcon(new ImageIcon(Main.class
					.getResource("/gui_images/Cards/" + cardName + "Card_"
							+ state + ".png")));
	}

	/**
	 * Runs a connectivity check to detect whether the launchpad has
	 * disconnected in between programs or not
	 * 
	 * @param check
	 *            True to check
	 */
	private void connectivityCheck(boolean check) {
		if (!checkLaunchpadConnection() && check) {
			setCurrentlyRunning("None");
			connectionNotFound();
			return;
		}
	}

	/**
	 * Reconnects to the Launchpad S if it had been disconencted. <br>
	 * //TODO Currently not working
	 */
	private void reconnect() {
		try {
			SetupProcess.resetMIDIList();

			new SetupProcess();
		} catch (MidiUnavailableException | InterruptedException
				| InvalidMidiDataException e) {
			e.printStackTrace();
		}
		if (checkLaunchpadConnection())
			setMainCardIcon("green");
		else
			setMainCardIcon("empty");
	}

	/**
	 * Displays a new window containing statistics
	 */
	private void displayStatistics() {
		if (!StatisticsGUI.windowActive) {
			Thread StatisticsGUIThread = new Thread() {
				@Override
				public void run() {
					try {
						new StatisticsGUI(true);
					} catch (Exception ex) {
						Thread.currentThread().interrupt();
					}
				}
			};
			StatisticsGUIThread.setName("Statics GUI Thread");
			StatisticsGUIThread.start();
		} else {
			StatisticsGUI.focus();
		}
	}

	/**
	 * Sets a flag that is used to determine if there is a sub program currently
	 * closing. <br>
	 * This is used to make sure that Input.waitForInput() is exited in case a
	 * sub program is trying to exit while also waiting for input.
	 * 
	 * @param status
	 *            Status of the flag
	 */
	public static void setClosing(boolean status) {
		closing = status;
	}

	/**
	 * Returns true if there is a sub program currently quitting
	 * 
	 * 
	 * @return True if there is, False otherwise
	 */
	public static boolean isClosing() {
		return closing;
	}

	/**
	 * Returns the main GUI window's X position
	 * 
	 * 
	 * @return X position of the main GUI window
	 */
	public static int getWindowPositionX() {
		return frmLaunchpadS.getX();
	}

	/**
	 * Returns the main GUI window's Y position
	 * 
	 * 
	 * @return Y position of the main GUI window
	 */
	public static int getWindowPositionY() {
		return frmLaunchpadS.getY();
	}

	/**
	 * Returns the main GUI window's width
	 * 
	 * 
	 * @return Width of the main GUI window
	 */
	public static int getWindowDimensionsW() {
		return frmLaunchpadS.getWidth();
	}

	/**
	 * Returns the main GUI window's height
	 * 
	 * 
	 * @return height of the main GUI window
	 */
	public static int getWindowDimensionsH() {
		return frmLaunchpadS.getHeight();
	}
}
