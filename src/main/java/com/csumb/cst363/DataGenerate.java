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
	static final String PASSWORD = "Poweruser123";
	
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
			
			String sqlINSERT = "insert into doctor(doctorSSN, doctorFirstName, doctorLastName, specialty, practiceSinceYear) values ( ?, ?, ?, ?, ?)";
			String[] keycols = {"doctorId"};
			ps = conn.prepareStatement(sqlINSERT, keycols);
			
			// insert 10 rows with data
			for (int k=1; k<=10; k++) {
				// generate unique "practice since" year date
				String practice_since = Integer.toString(2000+gen.nextInt(20));

				// generate unique ssn
				int ssn1 = 100 + gen.nextInt(900);
				int ssn2 = 10 + gen.nextInt(90);
				int ssn3 = 1000 + gen.nextInt(9000);
				String ssn = String.format("%03d-%02d-%04d", ssn1, ssn2, ssn3);
				
				
				// TODO Create and implement ArrayLists of F&L names
				ps.setString(1, ssn);
				ps.setString(2,  "FirstName");
				ps.setString(3,  "LastName");;
				ps.setString(4, specialties[k%specialties.length]);
				ps.setString(5, practice_since);
				row_count = ps.executeUpdate();
				System.out.println("row inserted "+row_count);
				
				rs = ps.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
				System.out.println("row inserted for doctor id "+id);
			}
			
			// display all rows
			System.out.println("All doctors");
			
			String sqlSELECT = "select doctorId, doctorLastName, doctorFirstName, specialty, practiceSinceYear, doctorSSN from doctor";
			ps = conn.prepareStatement(sqlSELECT);
			// there are no parameter markers to set
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("doctorId");
				String last_name = rs.getString("doctorLastName");
				String first_name = rs.getString("doctorFirstName");
				String specialty = rs.getString("specialty");
				String practice_since = rs.getString("practiceSinceYear");
				String ssn = rs.getString("doctorSSN");
				System.out.printf("%10d   %-30s  %-20s %4s %11s \n", id, last_name+", "+first_name, specialty, practice_since, ssn);
			}
		} catch (SQLException e) {
			System.out.println("Error: SQLException "+e.getMessage());
		}
	}
}
