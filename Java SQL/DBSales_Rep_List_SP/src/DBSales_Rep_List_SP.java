import java.sql.*;

public class DBSales_Rep_List_SP {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {
		String dbUser =   args[0];
		String passWord = args[1];
		//System.out.println(dbUser + ", " + passWord);
	
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2) + '\n');
	
			//call stored procedure
			String callSQL = "CALL classicmodels.createSalesRepList(?,?,?);";
			cs = conn.prepareCall(callSQL);
			//set parameters (testing with USA)
			cs.setString(1, "USA");
			cs.setString(2, "");
			cs.setString(3, "");
			cs.executeQuery();
			
			String email = cs.getString(2);
			String name = cs.getString(3);
			//name lists of variables
			String[] emails = email.split(";");
			String[] names = name.split(";");
			//print out each item
			for (int i=0; i<=emails.length-1; i++) {
				System.out.println(names[i]+"\t"+emails[i]);
			}

	
		}//try
	
	
	
		catch (SQLException se) {
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();

		} finally {
			try {
				if (cs != null) cs.close();
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { } 
		}
	

	
	}

}
