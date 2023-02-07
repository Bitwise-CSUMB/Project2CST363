package com.csumb.cst363;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;

public class FDADrugReport {
	
	private static final String DBURL = "jdbc:mysql://localhost:3306/cst363";
	private static final String USERID = "root";
	private static final String PASSWORD = "password";

	public static void main(String[] args) {
		try (Connection con = DriverManager.getConnection(DBURL, USERID, PASSWORD);
			Scanner scan = new Scanner(System.in)) {
			
			System.out.println("Beginning prescribed drugs query..." +
							"\n" + "At any stage type 'cancel' to abort.");
			
			String drugName;
			LocalDate startDate;
			LocalDate endDate;
			
			while (true) {
				try {
					System.out.println("\n" + "Please enter the target drug name.");
				
					String input = scan.nextLine();
					for (char c : input.toCharArray()) {
						
						if (!(Character.isLetter(c) || c == ' ')) {
							throw new IOException();
						}
					}
					
					if (input.equals("cancel")) {
						System.exit(0);
					}
					
					drugName = input.trim();
					break;
					
				} catch (IOException e) {
					System.out.println("Bro your input has something other than letters and spaces." + "\n" + 
									"Try again please..." + "\n");
				}
			}
			
			while (true) {
				try {
					System.out.println("\n" + "Please enter the target search start date. (YYYY-MM-DD)");
					
					String input = scan.nextLine();
					if (input.equals("cancel")) {
						System.exit(0);
					}
					
					startDate = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
					break;
					
				} catch(DateTimeParseException e) {
					System.out.println("Incorrect date format.");
				}
			}
			
			while (true) {
				try {
					System.out.println("\n" + "Please enter the target search end date. (YYYY-MM-DD)");
					
					String input = scan.nextLine();
					if (input.equals("cancel")) {
						System.exit(0);
					}
					
					endDate = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
					break;
					
				} catch(DateTimeParseException e) {
					System.out.println("Incorrect date format.");
				}
			}
			
			PreparedStatement ps;
			ResultSet rs;
			
			ps = con.prepareStatement("select distinct drugId, tradeName from drug where tradeName like ?");
			ps.setString(1, drugName + "%");
			rs = ps.executeQuery();
			
			HashMap<Integer, String> drugMap = new HashMap<Integer, String>();
			while (rs.next()) {
				if (!(drugMap.containsKey(Integer.parseInt(rs.getString(1))))) {
					drugMap.put(Integer.parseInt(rs.getString(1)), rs.getString(2));
				}
			}
			
			ps = con.prepareStatement("select distinct drugId, genericName from drug where genericName like ?");
			ps.setString(1, drugName + "%");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (!(drugMap.containsKey(Integer.parseInt(rs.getString(1))))) {
					drugMap.put(Integer.parseInt(rs.getString(1)), rs.getString(2));
				}
			}
			
			System.out.println("\n" + "Search results for the prescribing doctor and number of drugs using the search term '" + drugName + "' between"
					+ " the dates " + startDate + " and " + endDate + "." + "\n");
			
			for (Integer drugId : drugMap.keySet()) {
				ps = con.prepareStatement("select doctorFirstName, doctorLastName, sum(quantity) from doctor d join prescription p"
						+ " on d.doctorId = p.doctorId where drugId = ? and prescribeDate >= ? and prescribeDate <= ?"
						+ " group by doctorFirstName, doctorLastName");
				
				ps.setInt(1, drugId);
				ps.setDate(2, Date.valueOf(startDate));
				ps.setDate(3, Date.valueOf(endDate));
				
				rs = ps.executeQuery();
				
				while (rs.next()) {
					System.out.println(rs.getString(1) + " " + rs.getString(2) + " prescribed: " + rs.getString(3) + " of the drug '" + drugMap.get(drugId) + "'");
				}
			}
			
			System.out.println("\n" + "No more results.");
						
		} catch (SQLException e) {
			System.out.println("Error: SQLException " + e.getMessage());
		}
	}
}
