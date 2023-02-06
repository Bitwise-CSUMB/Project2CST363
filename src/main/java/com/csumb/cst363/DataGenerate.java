package com.csumb.cst363;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * @author jeremiah
 * This class clears the 'fill', 'prescription', 'patient'
 * and 'doctor' rows, and generates random data for
 * 100 patients, 10 doctors and 100 prescriptions
 */

public class DataGenerate {
	
	static final String DBURL = "jdbc:mysql://localhost:3306/cst363";  // database URL
	static final String USERID = "root";
	static final String PASSWORD = "<Poweruser123>";
	
	static final String[] specialties= {"Internal Medicine", "Family Medicine", "Pediatrics", "Orthpedics", "Dermatology",
			"Cardiology", "Gynecology", "Gastroenterology", "Psychiatry", "Oncology"};
	
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
			
			// generate doctor data and insert into table.  We want to generated column "id" value to be returned
			// as a generated key
			
			String sqlINSERT = "insert into doctor(doctorSSN, doctorFirstName, doctorLastName, specialty, praticeSinceYear) values ( ?, ?, ?, ?, ?)";
			String[] keycols = {"doctorId"};
			ps = conn.prepareStatement(sqlINSERT, keycols);
			
			// insert 10 rows with data
			for (int k=1; k<=10; k++) {
				String practice_since = Integer.toString(2000+gen.nextInt(20));
				// TODO ssn generated is not guaranteed to be unique.  This should be fixed.
				String ssn = Integer.toString(123450000+gen.nextInt(10000));
				ps.setString(1,  "Doctor Number"+k);
				ps.setString(2, "Dr.");
				ps.setString(3, specialties[k%specialties.length]);
				ps.setString(4, practice_since);
				ps.setString(5, ssn);
				row_count = ps.executeUpdate();
				System.out.println("row inserted "+row_count);
				
				rs = ps.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
				System.out.println("row inserted for doctor id "+id);
			}
		} catch (SQLException e) {
			System.out.println("Error: SQLException "+e.getMessage());
		}
	}
}
