package database;

import gui.StatisticsGUI;
import java.io.File;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

/**
 * Databases superclass
 * 
 * @author Helion
 * @version $Revision: 1.0 $
 */
public class Database {
	private static boolean DEBUG = false;
	private static final String filename = "lpDatabase.accdb";
	private static String fileDir = getFileLocation();
	private static String filelocation = fileDir + "\\" + filename;

	/**
	 * For use when runnable jar is exported. Returns the path of the database
	 * file.
	 * 
	 * @return Database file path
	 */
	private static String getFileLocation() {
		String location = "";

		try {
			location = new File("").getAbsolutePath();
		} catch (Exception e) {
			location = "C:\\Launchpad S+\\";
			e.printStackTrace();
		}

		if (DEBUG)
			System.out.println("\nFile Directory location: " + location);
		return location;
	}

	/**
	 * Generates a new database file. Used if the file doesn't exist
	 */
	private static void createNewDatabase() {
		// TODO if database file is not found, this method is called to create a
		// new one

	}

	/**
	 * Returns a statistic from the statistics database based on the type of
	 * stat
	 * 
	 * @param type
	 *            Type of statistic to retrieve
	 * 
	 * @return Statistic
	 */
	public synchronized static double getStat(String type) {
		double stat = -1;
		Connection con = null;
		Statement st = null;
		ResultSet rs;
		String sql = "";

		File f = new File(fileDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(filelocation);
		if (!f.exists()) {
			createNewDatabase();
			return 0;
		}

		try {
			System.out.println("Loading file at: " + filelocation);
			con = DriverManager.getConnection("jdbc:ucanaccess://"
					+ filelocation);
			st = con.createStatement();
			sql = "Select Stat from Statistics Where Type='" + type + "'";

			rs = st.executeQuery(sql);
			rs.next();
			stat = rs.getDouble("Stat");
			st.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Retrieving from database failed!");
			e.printStackTrace();
			try {
				st.close();
				con.close();
			} catch (SQLException e1) {
				System.out.println("Error closing database!");
				e1.printStackTrace();
			}
		}
		return stat;
	}

	/**
	 * Saves a statistic to the database
	 * 
	 * @param type
	 *            Type of statistic to save
	 * @param value
	 *            Value to save
	 */
	public synchronized static void saveStat(String type, double value) {
		Connection con = null;
		Statement st = null;
		// ResultSet rs;
		String sql = "";

		File f = new File(fileDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(filelocation);
		if (!f.exists()) {
			createNewDatabase();
			return;
		}

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://"
					+ filelocation);
			st = con.createStatement();
			sql = "UPDATE Statistics SET Stat = " + value + " WHERE Type = '"
					+ type + "'";
			if (DEBUG)
				System.out.println(sql);
			if (sql != null) {
				st.executeUpdate(sql);
			}
			st.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Updating database failed!");
			e.printStackTrace();
			try {
				st.close();
				con.close();
			} catch (SQLException e1) {
				System.out.println("Error closing database!");
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Saves all statistics to database
	 */
	public static void saveStats() {
		saveStat("Pressed", (double) StatisticsGUI.getTotalPadsPressed());
		saveStat("Lit", (double) StatisticsGUI.getTotalPadsLit());
		saveStat("Sent", StatisticsGUI.getTotalDataSent());
		saveStat("Received", StatisticsGUI.getTotalDataReceived());
		saveStat("Games", (double) StatisticsGUI.getTotalGamesPlayed());
		saveStat("Demos", (double) StatisticsGUI.getTotalDemosRan());
		saveStat("Animations",
				(double) StatisticsGUI.getTotalAnimationsCalled());
	}

	/**
	 * Returns the highest 10 scores from the database
	 * 
	 * @param gameID
	 *            Game ID
	 * @return Top 10 scores
	 */
	public synchronized static ArrayList<String[]> getHighScores(int gameID) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		ArrayList<String[]> results = new ArrayList<String[]>();

		File f = new File(fileDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(filelocation);
		if (!f.exists()) {
			createNewDatabase();

			ArrayList<String[]> temp = new ArrayList<String[]>();
			String[] tempEntry = new String[5];
			tempEntry[0] = "null";
			tempEntry[1] = "null";
			tempEntry[2] = "null";
			tempEntry[3] = "null";
			tempEntry[4] = "null";
			temp.add(tempEntry);
			return temp;
		}

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://"
					+ filelocation);
			st = con.createStatement();
			sql = "SELECT * FROM Scores WHERE GameID=" + gameID
					+ " ORDER BY Score DESC";
			rs = st.executeQuery(sql);

			int i = 0;
			while (rs.next() && i < 10) {
				String[] entry = new String[5];
				entry[0] = Integer.toString(rs.getInt("GameID"));
				entry[1] = Integer.toString(rs.getInt("Score"));
				entry[2] = rs.getString("Player1");
				entry[3] = rs.getString("Player2");
				entry[4] = rs.getString("Date_Played");

				results.add(entry);
				i++;
			}

		} catch (SQLException e1) {
			System.out.println("Error retrieving from database!");
			e1.printStackTrace();
		}
		try {
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error closing database!");
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Saves a score to the database
	 * 
	 * @param player1
	 *            Name of player 1
	 * @param player2
	 *            Name of player 2 - If any
	 * @param score
	 *            Score of the player
	 * @param gameID
	 *            Game ID
	 */
	public synchronized static void saveScore(String player1, String player2,
			int score, int gameID) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();

		Connection con = null;
		Statement st = null;
		String sql = "";

		File f = new File(fileDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(filelocation);
		if (!f.exists()) {
			createNewDatabase();
			return;
		}

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://"
					+ filelocation);
			st = con.createStatement();
			sql = "INSERT INTO Scores (Record_Number, Date_Played, Player1, Player2, Score, GameID) VALUES (0,'"
					+ dateFormat.format(date)
					+ "','"
					+ player1
					+ "','"
					+ player2 + "'," + score + "," + gameID + ")";
			if (DEBUG)
				System.out.println(sql);
			st.executeUpdate(sql);
		} catch (SQLException e1) {
			System.out.println("Error inserting to database!");
			e1.printStackTrace();
		}

		try {
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error closing database!");
			e.printStackTrace();
		}
	}
}
