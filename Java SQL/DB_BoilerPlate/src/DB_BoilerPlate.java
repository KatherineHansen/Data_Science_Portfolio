import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_BoilerPlate {
	// static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {

		// The name of the MySQL account to use.
		String dbUser = args[0];
		// The password for the MySQL account.
		String passWord = args[1];
		// System.out.println(dbUser + ", " + passWord);

		Connection conn = null; 
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Open a connection to the MySql Database Manager on this same system.
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);

			// Define and Execute a simple query.
			// The query will return a result set object.
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	

			String sql = "SELECT employeeNumber, lastName, firstName, email FROM classicmodels.employees";
			System.out.println("\nCreated statement and executing query for \n   " + sql);
			rs = stmt.executeQuery(sql);

			// Extract data from result set itself going forward and using column names.
			// We will be using the actual names of the columns to do this.
			System.out.println("\nResultSet from query:");
			while (rs.next()) {
				// Retrieve by column name
				int id = rs.getInt("employeeNumber");
				String email = rs.getString("email");
				String first = rs.getString("firstName");
				String last = rs.getString("lastName");

				// Display values
				System.out.println("ID: " + id + ",\tFirst: " + first + ",\tLast: " + last + ",\tEmail: " + email);
			} // End of ... while
			System.out.println("ResultSet cursor is past the end: " + rs.isAfterLast());
			
			// Do it again, but this time using ResultSet column numbers.
			System.out.println("\nReprocess the ResultSet, but this time using column numbers.");
			rs.beforeFirst();
			while (rs.next()) {
				// Retrieve by column name
				int id = rs.getInt(1);
				String email = rs.getString(2);
				String first = rs.getString(3);
				String last = rs.getString(4);

				// Display values
				System.out.println("ID: " + id + ",\tFirst: " + first + ",\tLast: " + last + ",\tEmail: " + email);
			} // End of ... while
			System.out.println("ResultSet cursor is past the end: " + rs.isAfterLast());

		} catch (SQLException se) {
			// Handle errors for JDBC
			// See https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
			se.printStackTrace();

		} finally {
			// We are done. Clean-up the environment.
			// finally block used to close resources.
			try {
				if (rs != null)		rs.close();

				if (stmt != null)	stmt.close();
				if (conn != null) {
					conn.close();
					System.out.println("Connection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) {
			} // nothing we can do

		} // End of ... finally.
	}
}
