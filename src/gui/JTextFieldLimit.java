package gui;

import javax.swing.text.*;

/**
 * Used to limit the maximum amount of characters in a JTextField
 * 
 * @author Helion
 * 
 */
public class JTextFieldLimit extends PlainDocument {
	private static final long serialVersionUID = 1L;
	private int limit;
	private boolean toUppercase = false;

	/**
	 * Constructor
	 * 
	 * @param limit character limit
	 */
	JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	/**
	 * Constructor
	 * 
	 * @param limit character limit
	 * @param upper upper limit
	 */
	JTextFieldLimit(int limit, boolean upper) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit) {
			if (toUppercase)
				str = str.toUpperCase();
			super.insertString(offset, str, attr);
		}
	}
}