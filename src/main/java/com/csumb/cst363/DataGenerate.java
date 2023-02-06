package com.csumb.cst363;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
	static final String[] firstNames= {"Emma", "Liam", "Olivia", "Noah", "Ava", "Isabella", "Sophia", "Jackson", "Mia",
			"Aiden", "Charlotte", "Harper", "Elijah", "Amelia", "Lucas", "Evelyn", "Mason", "Abigail", "William", "Scarlett"};
	static final String[] lastNames= {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Garcia", "Rodriguez", "Martinez",
			"Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson"};
	static final String[] states= {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
			"Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine",
			"Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
			"New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma",
			"Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
			"Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
	static final String[] cities= {"Aurora", "Bristol", "Chandler", "Dover", "Everett", "Franklin", "Glendale", "Harrison",
			"Irvine", "Jefferson", "Kenton", "Lancaster", "Mesa", "Newport", "Oakland", "Portsmouth", "Quincy", "Raleigh",
			"Savannah", "Tacoma", "Union", "Ventura", "Waltham", "Xenia", "Yuma", "Zion"};
	static final String[] streets= {"Arcane St.", "Blessed Rd.", "Celestial Ave.", "Dream Blvd.", "Elysian St.", "Fantasy Rd.",
			"Gates St.", "Haven Ave.", "Infinite Way", "Jewel Rd.", "Kismet St.", "Lunar Ave.", "Majestic St.", "Nirvana Rd.",
			"Oracle Way", "Pleasant St.", "Quiet Ave.", "Radiant St.", "Sunset Blvd."};
	
	public static void main(String args[]) {
		
		// TODO Need to run new_schema.sql and drug.sql first before running this class
		
		Random gen = new Random();
		
		// connect to mysql server
		
		try (Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);) {
			
			PreparedStatement ps;
			ResultSet rs;
			int id;
			int row_count;
					
			String[] tables = new String[] {"fill","prescription","patient","doctor"};
			for (String table : tables) {
				ps = conn.prepareStatement("delete from " + table);
				row_count = ps.executeUpdate();
				System.out.println(table + " rows deleted "+row_count);
			}

			// generate doctor data and insert into table.  We want to generated column
			//  "id" value to be returned as a generated key
			
			String sqlINSERTdr = "insert into doctor(doctorSSN, doctorFirstName, doctorLastName, specialty, practiceSinceYear) values ( ?, ?, ?, ?, ?)";
			String[] keycolsdr = {"doctorId"};
			ps = conn.prepareStatement(sqlINSERTdr, keycolsdr);
			
			// insert 10 rows with data
			for (int k=1; k<=10; k++) {
				// generate unique "practice since" year date
				String practice_since = Integer.toString(2000+gen.nextInt(20));

				// generate random ssn
				int ssn1 = 100 + gen.nextInt(900);
				int ssn2 = 10 + gen.nextInt(90);
				int ssn3 = 1000 + gen.nextInt(9000);
				String ssn = String.format("%03d-%02d-%04d", ssn1, ssn2, ssn3);
				
				ps.setString(1, ssn);
				ps.setString(2, firstNames[k%firstNames.length]);
				ps.setString(3, lastNames[k%lastNames.length]);
				ps.setString(4, specialties[k%specialties.length]);
				ps.setString(5, practice_since);
				row_count = ps.executeUpdate();
				System.out.println("row inserted "+row_count);
				
				rs = ps.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
				System.out.println("row inserted for doctor id "+id);
			}
			
			// generate patient data and insert into table.  We want to generated column
			//  "id" value to be returned as a generated key
			
			String sqlINSERTpatient = "insert into patient(primaryDoctorId, patientSSN, patientFirstName, patientLastName, patientBirthdate, patientState, patientZip, patientCity, patientStreet) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			String[] keycolspatient = {"patientId"};
			ps = conn.prepareStatement(sqlINSERTpatient, keycolspatient);
			
			// insert 100 rows with data
			for (int k=1; k<=100; k++) {
				// generate patient birth date in format yyyy-mm-dd
				Calendar start = Calendar.getInstance();
				start.set(1939, Calendar.SEPTEMBER, 1);
				Calendar end = Calendar.getInstance();
				end.set(2022, Calendar.DECEMBER, 31);
				long startMillis = start.getTimeInMillis();
				long endMillis = end.getTimeInMillis();
				long randomMillis = startMillis + (long) (Math.random() * (endMillis - startMillis));
				Calendar randomDate = Calendar.getInstance();
				randomDate.setTimeInMillis(randomMillis);
				String birthDate = String.format("%04d-%02d-%02d", randomDate.get(Calendar.YEAR), randomDate.get(Calendar.MONTH) + 1, randomDate.get(Calendar.DAY_OF_MONTH));

				// generate random ssn
				int ssn1 = 100 + gen.nextInt(900);
				int ssn2 = 10 + gen.nextInt(90);
				int ssn3 = 1000 + gen.nextInt(9000);
				String ssn = String.format("%03d-%02d-%04d", ssn1, ssn2, ssn3);
				
				// generate random doctorId (1 - 10)
				int doctorId = 1 + gen.nextInt(10);
				String doctorIdString = Integer.toString(doctorId);
				
				// generate random zip (5 digit)
				int zip = 10000 + gen.nextInt(89999);
				String zipString = Integer.toString(zip);
				
				ps.setString(1, doctorIdString);
				ps.setString(2, ssn);
				ps.setString(3, firstNames[k%firstNames.length]);
				ps.setString(4, lastNames[k%lastNames.length]);
				ps.setString(5, birthDate);
				ps.setString(6, states[k%states.length]);
				ps.setString(7, zipString);
				ps.setString(8, cities[k%cities.length]);
				ps.setString(9, streets[k%streets.length]);

				row_count = ps.executeUpdate();
				System.out.println("row inserted "+row_count);
				
				rs = ps.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
				System.out.println("row inserted for patient id "+id);
			}
			
			// generate prescription data and insert into table.  We want to generated column
			//  "id" value to be returned as a generated key
			
			String sqlINSERTprescription = "insert into prescription(doctorId, patientId, drugId, prescribeDate, quantity) values ( ?, ?, ?, ?, ?)";
			String[] keycolsprescription = {"rxNum"};
			ps = conn.prepareStatement(sqlINSERTprescription, keycolsprescription);
			
			// insert 100 rows with data
			for (int k=1; k<=100; k++) {
				// generate prescribeDate date in format yyyy-mm-dd
				Calendar start = Calendar.getInstance();
				start.set(2020, Calendar.JANUARY, 1);
				Calendar end = Calendar.getInstance();
				end.set(2022, Calendar.DECEMBER, 31);
				long startMillis = start.getTimeInMillis();
				long endMillis = end.getTimeInMillis();
				long randomMillis = startMillis + (long) (Math.random() * (endMillis - startMillis));
				Calendar randomDate = Calendar.getInstance();
				randomDate.setTimeInMillis(randomMillis);
				String prescribeDate = String.format("%04d-%02d-%02d", randomDate.get(Calendar.YEAR), randomDate.get(Calendar.MONTH) + 1, randomDate.get(Calendar.DAY_OF_MONTH));

				// generate random doctorId (1 - 10)
				int doctorId = 1 + gen.nextInt(10);
				String doctorIdString = Integer.toString(doctorId);
				
				// generate random patientId (1 - 100)
				int patientId = 1 + gen.nextInt(100);
				String patientIdString = Integer.toString(patientId);
				
				// generate random drugId (1 - 5)
				int drugId = 1 + gen.nextInt(5);
				String drugIdString = Integer.toString(drugId);
				
				// generate random quantity (1 - 10)
				int quantity = 1 + gen.nextInt(10);
				String quantityString = Integer.toString(quantity);
								
				ps.setString(1, doctorIdString);
				ps.setString(2, patientIdString);
				ps.setString(3, drugIdString);
				ps.setString(4, prescribeDate);
				ps.setString(5, quantityString);

				row_count = ps.executeUpdate();
				System.out.println("row inserted "+row_count);
				
				rs = ps.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
				System.out.println("row inserted for prescription id "+id);
			}
			
			// display all doctor rows
			System.out.println("All doctor");
			
			String sqlSELECTdoctor = "select doctorId, doctorSSN, doctorFirstName, doctorLastName, specialty, practiceSinceYear from doctor";
			ps = conn.prepareStatement(sqlSELECTdoctor);
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
			
			// display all patient rows
			System.out.println("All patient");
			
			String sqlSELECTpatient = "select patientId, primaryDoctorId, patientSSN, patientFirstName, patientLastName, patientBirthdate, patientState, patientZip, patientCity, patientStreet from patient";
			ps = conn.prepareStatement(sqlSELECTpatient);
			// there are no parameter markers to set
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("patientId");
				String primary_doc_id = rs.getString("primaryDoctorId");
				String ssn = rs.getString("patientSSN");
				String first_name = rs.getString("patientFirstName");
				String last_name = rs.getString("patientLastName");
				String birthdate = rs.getString("patientBirthdate");
				String state = rs.getString("patientState");
				String zip = rs.getString("patientZip");
				String city = rs.getString("patientCity");
				String street = rs.getString("patientStreet");
				System.out.printf("%10d, %3s, %12s, %12s, %12s, %12s, %15s, %12s, %18s, %8s\n",
						  id, primary_doc_id, ssn, first_name, last_name, birthdate, street, city, state, zip);
			}
			
			// display all doctor rows
			System.out.println("All prescription");
			
			String sqlSELECT = "select rxNum, doctorId, patientId, drugId, prescribeDate, quantity from prescription";
			ps = conn.prepareStatement(sqlSELECT);
			// there are no parameter markers to set
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("rxNum");
				String doctor_id = rs.getString("doctorId");
				String patient_id = rs.getString("patientId");
				String drug_id = rs.getString("drugId");
				String prescribe_date = rs.getString("prescribeDate");
				String quantity = rs.getString("quantity");
				System.out.printf("%10d, %3s, %3s, %3s, %12s, %3s\n",
						  id, doctor_id, patient_id, drug_id, prescribe_date, quantity);
			}
		} catch (SQLException e) {
			System.out.println("Error: SQLException "+e.getMessage());
		}
	}
}
