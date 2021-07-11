import java.sql.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;


public class DBCreateTable {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";
	
	public static void main(String[] args) throws FileNotFoundException {
		String dbUser =   args[0];
		String passWord = args[1]; 
		//System.out.println(dbUser + ", " + passWord);
		
		Connection conn = null;
		ResultSet rsDB = null;
		Statement stmtDrop = null;
		Statement stmtDB = null;
		Statement stmtAuthor = null;
		PreparedStatement psTitle = null;
		Statement stmtBooks = null;
		ResultSet rs = null;
		
		Scanner sc = null;
		
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2) + "\n");
			
			//check if DB books already exists
			rsDB = conn.getMetaData().getCatalogs();
			while (rsDB.next()) {
			  String databaseName = rsDB.getString(1);
			  //drop if books exists
			  if(databaseName.equals("books")) {
			  stmtDrop = conn.createStatement();			    
			      String sqlDrop = "DROP DATABASE books";
			      stmtDrop.executeUpdate(sqlDrop);
			  }
			}
			
			stmtDB = conn.createStatement();
        	//creates BOOKS DB
			String createDBSql = "CREATE DATABASE BOOKS";
			stmtDB.executeUpdate(createDBSql);

			//create table author and books
			String createAuthorsTableSql = "CREATE TABLE BOOKS.AUTHORS " + 
                    					  "(id INTEGER NOT NULL AUTO_INCREMENT, " + 
                    					  " author VARCHAR(255) UNIQUE, " +
                    					  " PRIMARY KEY ( id ))";

			String createBooksTableSql =  "CREATE TABLE BOOKS.BOOKS " + 
										  "(id INTEGER NOT NULL AUTO_INCREMENT, " + 
										  " title VARCHAR(255), " +
										  " authorID INTEGER not NULL, " +
										  " PRIMARY KEY ( id ))";

			stmtDB.executeUpdate(createAuthorsTableSql);
			stmtDB.executeUpdate(createBooksTableSql);

			
			//read in txt file for tables info
			File file = new File("CreateTable.txt"); //check path when turning in //C:\\Users\\ki6241pu\\Desktop\\CS 385\\CreateTable.txt
		    sc = new Scanner(file); 
			  
		    //store authors and titles in lists
		    LinkedList<String> llAuthors = new LinkedList<String>();
		    //counter to match authors to books	
		    int counter =0;
		    //set up ps I used for title table
		    String addTitleSql = "INSERT INTO books.books (title, authorID) VALUES (?, ?)";
		    psTitle = conn.prepareStatement(addTitleSql);
		    
		    //put lines into list
		    while (sc.hasNextLine()) {
		    	llAuthors.add(sc.nextLine());
		    	//System.out.println(llAuthors);
		    }
		    //putting in DB
		    for (String line : llAuthors) {
		    	String lineStr = line;
		    	String regex = "<Author>(.+?)";
		    	Pattern pattern = Pattern.compile(regex);
		    	Matcher matcherAuthor = pattern.matcher(lineStr);
		    	
		    	String regexTitle = "<Title>(.+?)</Title>";
		    	Pattern patternTitle = Pattern.compile(regexTitle);
		    	Matcher matcherTitle = patternTitle.matcher(lineStr);
		    	//if author line, put in table authors
		    	if (matcherAuthor.matches()) {
		    		String tag_value = matcherAuthor.group(1);
		    		//System.out.println(tag_value); //printing only group 1
		    		counter += 1;
		    		//System.out.println(counter);
		    		stmtAuthor = conn.createStatement();
		    		stmtAuthor.executeUpdate("INSERT INTO books.authors (author) VALUES (\'" + tag_value + "\')");
		    	} 
		    	//if title line, put in table books
		    	if (matcherTitle.matches()) {
		    		String title = matcherTitle.group(1);
		    		//System.out.println(title);
		    		//uses ps
		    		psTitle.setString(1, title);
					psTitle.setInt(2, counter);
					psTitle.executeUpdate();
		    	}
		    }//for
		        
			//now print out contents
		    stmtBooks = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    String sqlBooks = "SELECT A.author, B.title " +
		    				  "FROM books.authors AS A " +
		    				  "JOIN books.books AS B ON A.id = B.authorID";
		    
		    rs = stmtBooks.executeQuery(sqlBooks);
		    while (rs.next()) {
				String rsTitle = rs.getString(2);
				String rsAuthor = rs.getString(1);
				
				System.out.println("Author: " + rsAuthor + " | Title: " + rsTitle);
		    }
			
		    
		    
		    
		} catch (SQLException se) {
			// Handle errors for JDBC
			// See https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " +    se.getErrorCode());
			se.printStackTrace();
		
		} finally {
			try {
				if(rsDB != null) rsDB.close();
				if(stmtDrop != null) stmtDrop.close();
				if(stmtDB != null) stmtDB.close();
				if(stmtAuthor != null) stmtAuthor.close();
				if(psTitle != null) psTitle.close();
				if(stmtBooks != null) stmtBooks.close();
				if(rs != null) rs.close();
				
				if(sc != null) sc.close();
				
				if (conn != null) {
					conn.close(); 
					System.out.println("\nConnection is closed:" + conn.isClosed());
				}
			} catch (SQLException se2) { } 
		}
	}
}

