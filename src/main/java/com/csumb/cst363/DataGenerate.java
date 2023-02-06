package com.csumb.cst363;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class DataGenerate {
	
	static final String DBURL = "jdbc:mysql://localhost:3306/cst363";  // database URL
	static final String USERID = "root";
	static final String PASSWORD = "<Poweruser123>";
	
	public static void main(String args[]) {
		
		Random gen = new Random();
		
		// connect to mysql server
		
		try (Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);) {
			
			PreparedStatement ps;
			ResultSet rs;
			int id;
			int row_count;
			
			// delete all fill rows
			ps = conn.prepareStatement("delete from fill");
			row_count = ps.executeUpdate();
			System.out.println("rows deleted "+row_count);
			
			// delete all prescription rows
			ps = conn.prepareStatement("delete from prescription");
			row_count = ps.executeUpdate();
			System.out.println("rows deleted "+row_count);
			
			// delete all patient rows
			ps = conn.prepareStatement("delete from patient");
			row_count = ps.executeUpdate();
			System.out.println("rows deleted "+row_count);
			
			// delete all doctor rows
			ps = conn.prepareStatement("delete from doctor");
			row_count = ps.executeUpdate();
			System.out.println("rows deleted "+row_count);
						
		} catch (SQLException e) {
			System.out.println("Error: SQLException "+e.getMessage());
		}
	}
}
