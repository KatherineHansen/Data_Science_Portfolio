import java.sql.*;
import java.util.LinkedList;
import java.util.ListIterator;


public class DB_Employee_Location_SP {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {
		String dbUser =   args[0];	// The name of the MySQL account to use.
		String passWord = args[1]; // The password for the MySQL account.
		//System.out.println(dbUser + ", " + passWord);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet spRs = null;
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2) + '\n');
			
			//static query info
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT E.firstName, E.lastName, O.city, O.state, O.country, O.territory, E.employeeNumber " +
						 "FROM classicmodels.employees AS E " +
						 "JOIN classicmodels.offices AS O ON E.officeCode = O.officeCode ";
			rs = stmt.executeQuery(sql);
			//list for employeeNumbers for later
			LinkedList<Integer> llemployeeNumber = new LinkedList<Integer>();
			System.out.println("STATIC");
			rs.beforeFirst();
			while (rs.next()) {
				int id = rs.getInt("employeeNumber");
				String first = rs.getString("firstName");
				String last = rs.getString("lastName");
				String city = rs.getString("city");
				String state = rs.getString("state");
				String country = rs.getString("country");
				String territory = rs.getString("territory");
				
				if (state != null)
			    {			//has state
					System.out.println("ID: "  + id + " | Name: " + first + " " + last + " | Location: " + city + ", " + state + ", " + country);
			    } else {	//no state, so use territory
			    	System.out.println("ID: "  + id + " | Name: " + first + " " + last + " | Location: " + city + ", " + territory + ", " + country);
			    }
				//build up id list
				llemployeeNumber.add(id);
			}//while
			
			System.out.println("\nSTORED PROCEDURE");
			//takes employee number and gets that row's info
			String callSQL = "CALL classicmodels.EmployeeLocation(?)";
			CallableStatement cs = conn.prepareCall(callSQL);
			//iterator to run through list
			ListIterator<Integer> listIterator = llemployeeNumber.listIterator();
			int input = 0;
			while (listIterator.hasNext()) {
				input = listIterator.next();
				cs.setInt(1, input);
				spRs = cs.executeQuery();
				spRs.next();
				String spfirst = spRs.getString("firstName");
				String splast = spRs.getString("lastName");
				String spcity = spRs.getString("city");
				String spstate = spRs.getString("state");
				String spcountry = spRs.getString("country");
				String spterritory = spRs.getString("territory");
					
				if (spstate != null)
			    {
					System.out.println("ID: " + input + " | Name: " + spfirst + " " + splast + " | Location: " + spcity + ", " + spstate + ", " + spcountry);
			    } else {
			    	System.out.println("ID: " + input + " | Name: " + spfirst + " " + splast + " | Location: " + spcity + ", " + spterritory + ", " + spcountry);
			    }
			}//while	
		}//try
		
		
		
		catch (SQLException se) {
			// Handle errors for JDBC
			// See https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();
			
		} finally {
			// We are done.  Clean-up the environment.
			// finally block used to close resources.
			try {
				if (stmt != null) stmt.close();
				if (rs != null) rs.close();
				if (spRs != null) spRs.close();
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { } // nothing we can do
		} 
		
	}//main
}//class
