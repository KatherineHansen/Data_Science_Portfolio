import java.util.Scanner;
import java.sql.*;

public class PreparedStatements {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";
	
	public static void main(String[] args) {
		String dbUser =   args[0];	// The name of the MySQL account to use.
		String passWord = args[1]; // The password for the MySQL account.
		//System.out.println(dbUser + ", " + passWord);
		
		//things
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rsEmployees = null;
		Statement stmt = null;
		
		//body
		wholeThing:
		try {
			// Open a connection to the MySQL Database Manager on this same system.
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2));
			System.out.println();
			
			// keyboard input
			Scanner keyboard = new Scanner(System.in);
			// first input -customer ID number
			int custID;
			//begin
			System.out.println("Welcome to Classic Models." +
					"\nTo continue, please enter your customer ID number (or -1 to end)");
		
			//whole deal
			boolean weDone = false;
			do {	
				loop:
					do {
						// not number input
						while (!keyboard.hasNextInt()) {
							System.out.println("I am sorry.  I did not recognize that response.");
							System.out.println("To continue, please enter your customer ID number (or -1 to end)");
							//try again
							keyboard.next();
						} 
						custID = keyboard.nextInt(); 
						if (custID == -1) {	
							//exit whole try block; jump to end of program
							break wholeThing;
						}
						//prepared statement for single customer ID number input; gives 1 row
						String sqlCust = 
								"SELECT C.customerNumber, C.customerName, C.contactLastName, C.contactFirstName, " +
								"E.lastName, E.firstName, E.email AS EMAIL, E.extension AS PHONE, C.salesRepEmployeeNumber " + 
								"FROM classicmodels.customers AS C " + 
								"LEFT OUTER JOIN classicmodels.employees AS E ON C.salesRepEmployeeNumber = E.employeeNumber " + 
								"WHERE customerNumber = ?";
						PreparedStatement psCustomer = conn.prepareStatement(sqlCust);
						psCustomer.setInt(1, custID);
						rs = psCustomer.executeQuery();
				
				
				
						//customer ID number is a number but not known in database
						if (!rs.next()){
							System.out.println("I am sorry, but we do not know you by that ID.");
							System.out.println("To continue, please enter your customer ID number (or -1 to end).");
							do {
								//does same thing as this does in the beginning? Might be extra for no reason...
								while (!keyboard.hasNextInt()) {
									System.out.println("I am sorry.  I did not recognize that response.");
									System.out.println("To continue, please enter your customer ID number (or -1 to end)");
									keyboard.next();
								} 
							} while (custID <= 0);    
						}
						//do the prepared statement with accepted customer ID number
						psCustomer = conn.prepareStatement(sqlCust);
						psCustomer.setInt(1, custID);
						rs = psCustomer.executeQuery();
				
				
						//get relevant info from sql query
						loopSR:
						while (rs.next()) {
							String customerName = rs.getString("customerName");
					
							String srFirst = rs.getString("firstName");
							String srLast = rs.getString("lastName");
							String srEmail = rs.getString("EMAIL");
					
							System.out.println("Thank you, we know you as " + customerName);
					
							// customer exists, but we don't have a sales rep for them
							String checkforSR = rs.getString("salesRepEmployeeNumber");
							while (rs.wasNull()) {
								System.out.println("But unfortunately we do not have a sales rep assigned for you.");
								System.out.println("You may try another ID input, or enter -1 to end.");
								// has failed having a sales rep and so will try all loop again
								break loopSR;
							}
							//gets out of this large do block
							break loop;
						}
				
					} while (!rs.next());
			
			
				//getting prepared statement info again, this time outside the block +more
				String srFirst = rs.getString(6);
				String srLast = rs.getString(5);
				String srEmail = rs.getString(7);
				String srPhone = rs.getString(8);
			
				String cFirst = rs.getString("contactFirstName");
				String cLast = rs.getString("contactLastName");
				String customerName = rs.getString("customerName");
			
				System.out.println("Can you now enter all or the first part of either the last name or email address of yours sales representative?");
				//gets sales rep input; all upper so we can compare later
				String srInfo = keyboard.next().toUpperCase();
				//repeats second round of input
				do {
					//prep for list of all employee info in case customer types wrong salesrep info
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					String sqlEList = "SELECT lastName, firstName, email " +
									  "FROM classicmodels.employees";
					rsEmployees = stmt.executeQuery(sqlEList);
					rsEmployees.beforeFirst();
			
					//check for if employee info exists at all even if not customer's salesrep
					boolean isExisting = false;
					loop2:
					while (rsEmployees.next()) {
						String srFirstwrong = rsEmployees.getString("firstName");
						String srLastwrong = rsEmployees.getString("lastName");
						String srEmailwrong = rsEmployees.getString("EMAIL");
				
						isExisting = false;
						if (srLastwrong.toUpperCase().startsWith(srInfo) || srEmailwrong.toUpperCase().startsWith(srInfo)) {
							isExisting = true;
							//leaves this while; now knows if employee exists
							break loop2;
						}
					}
					//stop program during second part
					if (srInfo.equals("-1")) {	
						//exit whole try block; jump to end of program
						break wholeThing;
					}
			
					//check for at least 4 length input
				
					if (srInfo.length() < 4) {
						System.out.println("I am sorry, but can you be more specific?");
						System.out.println("You may try again or enter '-1' to quit.");
					} else //input at least 4; now check if input is correct salesrep info
					if (srLast.toUpperCase().startsWith(srInfo) || srEmail.toUpperCase().startsWith(srInfo)) {
						System.out.println("Welcome " + cFirst + " " + cLast + " of " + customerName + ". How may we be of service?");
						System.out.println(srFirst + " " + srLast + "’s extension is "+ srPhone + ", or would you like for me to have him contact you?");
						System.out.println("\nYou've made it! You may now try again by entering a new customer ID or enter '-1' to quit.");
						//to start again
						break;
					} else //was not correct; was found to exist earlier 
					if (isExisting) {
						System.out.println("I am sorry. We do have a sales representative by that name, but not as your sales rep.");
						System.out.println("We will, though, have someone contact you to follow up.");
						System.out.println("You may try again or enter '-1' to quit.");
					} else { //was not correct nor exists at all
						System.out.println("I am very sorry, but we do not have a sales representative by that name.");
						System.out.println("We will, though, have someone contact you to follow up.");
						System.out.println("You may try again or enter '-1' to quit.");
					}
				
					srInfo = keyboard.next().toUpperCase(); 
					if (srInfo.equals("-1")) {	
						//exit whole try block; jump to end of program
						break wholeThing;
					}
				} while(!srInfo.equals("-1"));//end do loop3
			
			} while (!weDone); // so this should never be true. The only way to stop is '-1'.
			break wholeThing;
		
		} // END TRY BLOCK	
		
		
		catch (SQLException se) {
			// Handle errors for JDBC
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();
			
		} finally {
			// close resources.
			try {
				if (rs   != null) rs.close();
				if (stmt != null) stmt.close();
				if (rsEmployees != null) rsEmployees.close();
				
				if (conn != null) {
					conn.close(); 
					System.out.println();
					System.out.println("Connection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { }
		}
	}
}
