package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Implements the GUI for Errors
 * 
 * @author Helion
 * 
 * @version $Revision: 1.0 $
 */
public class Error {
	private static final JFrame errorFrame = new JFrame();
	private static final JPanel errorContainer = new JPanel();
	private static final JLabel errorText = new JLabel();
	private static final JLabel errorText2 = new JLabel();
	private static final JButton errorButton = new JButton();
	private static String errorType;

	private static int mainWindowX;
	private static int mainWindowY;
	private static int mainWindowW;
	private static int mainWindowH;

	private static int windowX;
	private static int windowY;
	private static int windowW;
	private static int windowH;

	/**
	 * Constructor for Error.
	 * 
	 * @param type
	 *            Error type
	 */
	public Error(String type) {
		errorType = type;
		initialize();
		createAndShowGUI();
		setInteractions();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainWindowX = Main.getWindowPositionX();
		mainWindowY = Main.getWindowPositionY();
		mainWindowW = Main.getWindowDimensionsW();
		mainWindowH = Main.getWindowDimensionsH();

		windowW = 350;
		windowH = 110;
		windowX = ((mainWindowX + mainWindowW) / 2) - (windowW / 2) + 50;
		windowY = ((mainWindowY + mainWindowH) / 2) - (windowH / 2) + 100;
	}

	/**
	 * Creates the GUI and places GUI elements
	 */
	private void createAndShowGUI() {
		switch (errorType) {
		case "test":
			errorText.setText("Test Error.");
			break;
		case "noConnection":
			errorText.setText("No Launchpad S Connection Found.");
			errorText2
					.setText("Please reconnect the Launchpad and restart the program.");
			break;
		case "cannotConnect":
			errorText
					.setText("Cannot connect to Launchpad S... Already is use.");
			errorText2
					.setText("Close any other programs using the Launchpad and restart.");
			break;
		default:
			errorText.setText("An unknown error has occurred...");
			break;
		}

		errorButton.setText("OK");

		errorContainer.setLayout(new FlowLayout());
		errorContainer.add(errorText);
		errorContainer.add(errorText2);
		errorContainer.add(errorButton);

		errorFrame.add(errorContainer);

		errorFrame.setVisible(true);
		errorFrame.setTitle("Error");
		errorFrame.setResizable(false);
		errorFrame.setBounds(windowX, windowY, windowW, windowH);
	}

	/**
	 * Defines and implements all the interactions for the Reaction Game GUI
	 */
	private void setInteractions() {
		errorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				okPressed();
			}
		});

		errorFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				okPressed();
			}
		});
	}

	/**
	 * Method called when the OK Button is pressed
	 */
	private void okPressed() {
		errorFrame.dispose();
	}
}
