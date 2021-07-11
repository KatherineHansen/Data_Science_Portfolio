import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class DBGetProductsPerCodes_SP {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {
		String dbUser =   args[0];
		String passWord = args[1];
		//System.out.println(dbUser + ", " + passWord);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2) + '\n');
			
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			//get just productCodes
			String sqlProductCodes = "SELECT productCode " +
						 			 "FROM classicmodels.products ";
			rs = stmt.executeQuery(sqlProductCodes);
			
			//list for codes
			LinkedList<String> llproductCodes = new LinkedList<String>();
			
			rs.beforeFirst();
			while (rs.next()) {
				String productCode = rs.getString("productCode");
				llproductCodes.add(productCode);
			}//while
			
			//generate 2-6 random
			int rand2_6 = ThreadLocalRandom.current().nextInt(1, 6);
			//create , delimited codes
			StringBuilder productCodesSmall = new StringBuilder();
			for (int i=0; i <=rand2_6; i++) {
				productCodesSmall.append(llproductCodes.get(ThreadLocalRandom.current().nextInt(0, llproductCodes.size() + 1))+",");
			}
			String codesString = productCodesSmall.toString();
			//System.out.println(codesString);
			//calling stored procedure
			String callSQL = "CALL classicmodels.getProductsPerCodes(?);";
			CallableStatement cs = conn.prepareCall(callSQL);
			cs.setString(1, codesString);
			rs2 = cs.executeQuery();
			
			//Checking by printing rs
			while(rs2.next()) {
				String productCode = rs2.getString("productCode");
				String productName = rs2.getString("productName");
				String productLine = rs2.getString("productLine");
				String productVendor = rs2.getString("productVendor");
				String quantityInStock = rs2.getString("quantityInStock");
				
				System.out.println("CODE: "+productCode + " NAME: "+productName + " LINE: "+productLine + " VENDOR: "+productVendor + " STOCK: "+quantityInStock);
			}
			
		

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
				if (rs2 != null) rs2.close();
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { } // nothing we can do
		} 
	}//main
}
