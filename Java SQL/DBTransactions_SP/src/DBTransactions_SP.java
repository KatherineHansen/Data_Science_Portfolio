import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class DBTransactions_SP {
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
		CallableStatement cs = null;
		ResultSet rs4 = null;
	
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
				//stop auto commits
				conn.setAutoCommit(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			System.out.println("Connection is valid: " + conn.isValid(2) + '\n');
			
			
			//pre stored procedure check
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			String sqlOrderNumber = "SELECT max(orderNumber) " +
					"FROM classicmodels.orders ";
			rs4 = stmt.executeQuery(sqlOrderNumber);
			rs4.next();
			int largestOrderNum = rs4.getInt(1);
			System.out.println("\nLargest orderNumber pre SP: " + largestOrderNum + "\n\n");
			
			
			//2 rounds
			for (int j=1; j<=2; j++) {
				String sqlCustNum = "SELECT customerNumber " +
						"FROM classicmodels.customers ";
				rs = stmt.executeQuery(sqlCustNum);

				//list for customerNumbers
				LinkedList<String> llcustNum = new LinkedList<String>();
				rs.beforeFirst();
				while (rs.next()) {
					String custNum = rs.getString("customerNumber");
					llcustNum.add(custNum);
				}

				//generate 1 random customer
				int customer = Integer.parseInt(llcustNum.get(ThreadLocalRandom.current().nextInt(0, llcustNum.size() + 1)));
	
				//get productCodes
				String sqlProductCodes = "SELECT productCode " +
						"FROM classicmodels.products ";
				rs2 = stmt.executeQuery(sqlProductCodes);

				//list for codes
				LinkedList<String> llproductCodes = new LinkedList<String>();
				rs2.beforeFirst();
				while (rs2.next()) {
					String productCodes = rs2.getString("productCode");
					llproductCodes.add(productCodes);
				}//while

				//generate 1-4 random codes
				int rand1_4 = ThreadLocalRandom.current().nextInt(0, 4);
				StringBuilder productCodesSmall = new StringBuilder();
				for (int i=0; i <= rand1_4; i++) {
					productCodesSmall.append(llproductCodes.get(ThreadLocalRandom.current().nextInt(0, llproductCodes.size() + 1))+",");
				}
				String codesString = productCodesSmall.toString();
				
				//call SP addorder
				String callSQL = "CALL classicmodels.AddOrder(?,?,?,?,?,?);";
				cs = conn.prepareCall(callSQL);
				//commit or not
				String commit;
				if (j == 1) {
					commit = "commit";
				} else {
					commit = "rollback";
				}
				
				cs.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				cs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				cs.setInt(3, customer);
				cs.setString(4, codesString);
				cs.setString(5, commit);
				cs.setNull(6, java.sql.Types.INTEGER);
				cs.executeQuery();
				
				int orderNum = cs.getInt(6);
				System.out.println("Max orderNumber given at the end of SP "+j+": "+orderNum);
		
			}//for loop
				

		}//try



		catch (SQLException se) {
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();

		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) rs.close();
				if (rs2 != null) rs2.close();
				if (rs4 != null) rs4.close();
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { }

		}//finally
	}//main

}
