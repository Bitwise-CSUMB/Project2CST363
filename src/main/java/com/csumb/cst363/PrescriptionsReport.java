package com.csumb.cst363;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PrescriptionsReport {

	private static final String DBURL = "jdbc:mysql://localhost:3306/cst363";  // database URL
	private static final String USERID = "root";
	private static final String PASSWORD = "< your mysql password >";

	public static void main(String[] args) {

		try (Connection conn = DriverManager.getConnection(DBURL, USERID, PASSWORD);
			Scanner scanner = new Scanner(System.in))
		{
			System.out.println("Starting pharmacy report ...");

			int pharmacyId;
			LocalDate startDate;
			LocalDate endDate;

			System.out.print("Please enter the pharmacy id: ");
			while (true) {
				try {
					pharmacyId = Integer.parseInt(scanner.nextLine(), 10);
					break;
				}
				catch (NumberFormatException ignored) {
					System.out.print("Invalid pharmacy id, please try again: ");
				}
			}

			System.out.print("Please enter the start date as YYYY-MM-DD: ");
			while (true) {
				try {
					startDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
					break;
				}
				catch (DateTimeParseException ignored) {
					System.out.print("Invalid start date, please try again: ");
				}
			}

			System.out.print("Please enter the end date as YYYY-MM-DD: ");
			while (true) {
				try {
					endDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
					break;
				}
				catch (DateTimeParseException ignored) {
					System.out.print("Invalid end date, please try again: ");
				}
			}

			PreparedStatement ps = conn.prepareStatement("""
				select tradeName, genericName, sum(quantity) totalQuantity
					from pharmacy ph, fill f, prescription p, drug d
					where ph.pharmacyId = f.pharmacyId
						and f.rxNum = p.rxNum and f.fillDrugId = d.drugId
						and ph.pharmacyId = ? and fillDate >= ? and fillDate <= ?
					group by fillDrugId
			""");

			ps.setInt(1, pharmacyId);
			ps.setDate(2, Date.valueOf(startDate));
			ps.setDate(3, Date.valueOf(endDate));

			System.out.println("Querying database ...");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String tradeName = rs.getString("tradeName");
				String genericName = rs.getString("genericName");
				int totalQuantity = rs.getInt("totalQuantity");
				System.out.printf("drug: %s, totalQuantity: %d\n", tradeName != null ? tradeName : genericName, totalQuantity);
			}

			System.out.println("End report.");
		}
		catch (SQLException e) {
			System.out.println("Error: SQLException " + e.getMessage());
		}
	}
}
