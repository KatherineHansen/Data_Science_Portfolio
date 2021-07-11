import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class Transactions_Via_JDBC {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {
		String dbUser =   args[0];
		String passWord = args[1];
		//System.out.println(dbUser + ", " + passWord);
	
		Connection conn = null;
		Statement stmt = null;
		Statement stmtOrder = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		PreparedStatement psOrderDetail = null;
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
				//stop auto commits
				conn.setAutoCommit(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			System.out.println("Connection is valid: " + conn.isValid(2) + '\n');
			
			
			// get max orderNumber
			String sqlOrderNumber = "SELECT max(orderNumber) " +
					"FROM classicmodels.orders ";
			
			// go through twice
			for (int j=1; j<=2; j++) {
				// get customerNumbers
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
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
				String customer = llcustNum.get(ThreadLocalRandom.current().nextInt(0, llcustNum.size() + 1));

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
				LinkedList<String> llCodes = new LinkedList<String>();
				for (int i=0; i <= rand1_4; i++) {
					llCodes.add(llproductCodes.get(ThreadLocalRandom.current().nextInt(0, llproductCodes.size() + 1)));
				}

	
				rs3 = stmt.executeQuery(sqlOrderNumber);
				rs3.next();
				int largestOrderNum = rs3.getInt(1);
				//increase by 1 for new order
				int orderNumber = largestOrderNum+1;
				
				System.out.println("Largest orderNumber pre new order: " + largestOrderNum);
				System.out.println("New orderNumber to be added: " + orderNumber);

				//Insert new order into orders
				String addOrder = "INSERT INTO classicmodels.orders (orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber) " +
						"VALUES (" + orderNumber + ", CURDATE(), CURDATE()+ INTERVAL 7 DAY, NULL, \'In Process\', NULL, " + customer + ")";
				stmtOrder = conn.createStatement();
				stmtOrder.executeUpdate(addOrder);
				
				//Insert each product into orderdetails
				String addOrderDetail = "INSERT INTO classicmodels.orderdetails (orderNumber, productCode, orderLineNumber, quantityOrdered, priceEach) VALUES (?, ?, ?, ?, ?)";
				psOrderDetail = conn.prepareStatement(addOrderDetail);
				int orderLine = 1;
				for (String product : llCodes) {
					psOrderDetail.setInt(1, orderNumber);
					psOrderDetail.setString(2, product);
					psOrderDetail.setInt(3, orderLine);
					psOrderDetail.setInt(4, orderLine);
					psOrderDetail.setBigDecimal(5, BigDecimal.valueOf(orderLine));

					psOrderDetail.executeUpdate();
					orderLine++;
				}

				//For rollback test
				if (j == 2) {
					conn.rollback();
					System.out.println("Comitted: false");
					//break loop;
				}
				//For full commit test
				if (j == 1) {
					conn.commit();
					System.out.println("Committed: true");
					//commit = false;
				}  


			
			
			//Verify max order number.
			rs4 = stmt.executeQuery(sqlOrderNumber);
			rs4.next();
			largestOrderNum = rs4.getInt(1);
			System.out.println("\nLargest orderNumber post commit/rollback: " + largestOrderNum + "\n\n");
			}


			
		}//try
		
		
		
		catch (SQLException se) {
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();

		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmtOrder != null) stmtOrder.close();
				if (rs != null) rs.close();
				if (rs2 != null) rs2.close();
				if (rs3 != null) rs3.close();
				if (rs4 != null) rs4.close();
				if (psOrderDetail != null) psOrderDetail.close();
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { }
			
		}//finally
	}//main

}
