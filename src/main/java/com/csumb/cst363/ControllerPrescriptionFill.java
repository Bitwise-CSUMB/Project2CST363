package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.csumb.cst363.InputVerifier.InputVerificationException;

@Controller
public class ControllerPrescriptionFill {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	/*
	 * Patient requests form to search for prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_fill";
	}


	/*
	 * Process the prescription fill request from a patient.
	 * 1.  Validate that Prescription p contains rxid, pharmacy name and pharmacy address
	 *     and uniquely identify a prescription and a pharmacy.
	 * 2.  update prescription with pharmacyid, name and address.
	 * 3.  update prescription with today's date.
	 * 4.  Display updated prescription
	 * 5.  or if there is an error show the form with an error message.
	 */
	@PostMapping("/prescription/fill")
	public String processFillForm(Prescription p,  Model model) {

		// TODO
		
		try (Connection con = getConnection()) {
			PreparedStatement ps;
			ResultSet rs;
			
			// * 1.  Validate that Prescription p contains rxid, pharmacy name and pharmacy address
			// *     and uniquely identify a prescription and a pharmacy.
			int rxNum;
			try {
				rxNum = p.getRxNum();
				if (rxNum <= 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				model.addAttribute("message", "Error: Rx number invalid.");
				throw new InputVerificationException();
			}
			String patientLastName = InputVerifier.verifyWordField(p.getPatientLastName(), 45, "Patient Last Name", model);
			String pharmacyName = InputVerifier.verifyAlphanumericWordField(p.getPharmacyName(), 45, "Pharmacy Name", model);
			String pharmacyZip = InputVerifier.verifyZipField(p.getPharmacyZip(), "Pharmacy Zip", model);
			String pharmacyCity = InputVerifier.verifyWordField(p.getPharmacyCity(), 45, "Pharmacy City", model);
			String pharmacyStreet = InputVerifier.verifyWordField(p.getPharmacyStreet(), 45, "Pharmacy Street", model);
			
			ps = con.prepareStatement("select prescription.rxNum, doctorId, pharmacyName, pharmacyZip, pharmacyCity, "
					+ "pharmacyStreet, prescription.patientId, fill.pharmacyId, patientFirstName, patientLastName, quantity "
					+ "from prescription"
					+ "join fill on prescription.rxNum = fill.rxNum "
					+ "join pharmacy on fill.pharmacyId = pharmacy.pharmacyId "
					+ "join patient on prescription.patientId = patient.patientId "
					+ "join drug on prescription.drugId = drug.drugId "
					+ "where prescription.rxNum = ?");
			ps.setInt(1, rxNum);
			ps.executeQuery();
			rs = ps.getResultSet();
			
			int doctorId;
			int patientId;
			int pharmacyId;
			int quantity;
			
			if (rs.next()) {
				doctorId = rs.getInt("doctorId");
				patientId = rs.getInt("patientId");
				pharmacyId = rs.getInt("pharmacyId");
				quantity = rs.getInt("quantity");
			} else {
				model.addAttribute("message", "Error: Rx does not exist.");
				throw new InputVerificationException();
			}
			
			// * 2.  update prescription with pharmacyid, name and address.
			
			ps = con.prepareStatement("update prescription "
					+ "join fill on prescription.rxNum = fill.rxNum "
					+ "join pharmacy on fill.pharmacyId = pharmacy.pharmacyId "
					+ "set pharmacy.pharmacyId = ?, pharmacy.pharmacyName = ? "
					+ "pharmacy.pharmacyZip = ?, pharmacy.pharmacyCity = ? "
					+ "pharmacy.pharmacyStreet = ? "
					+ "where prescription.rxNum = ?");
			ps.setInt(1, pharmacyId);
			ps.setString(2, rs.getString("pharmacyName"));
			ps.setString(3, rs.getString("pharmacyZip"));
			ps.setString(4, rs.getString("pharmacyCity"));
			ps.setString(5, rs.getString("pharmacyStreet"));
			ps.setInt(6, rs.getInt("rxNum"));
			
			int update1 = ps.executeUpdate();
			
			// * 3.  update prescription with today's date.
			// Get the current time
			java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			
			ps = con.prepareStatement("update prescription "
					+ "join fill on prescription.rxNum = fill.rxNum "
					+ "set fill.pharmacyId = ?, fill.fillDate = ? "
					+ "where rxNum = ?");
			ps.setInt(1, pharmacyId);
			ps.setDate(2, sqlDate);
			ps.setInt(3, rxNum);
			
			int update2 = ps.executeUpdate();
			
			// * 4.  Display updated prescription
			// * 5.  or if there is an error show the form with an error message.
			
			if (update1 != 1 && update2 != 1) {
				model.addAttribute("message","Prescription refill unsuccessful.");
				model.addAttribute("prescription",p);
				return "prescription_fill";
			}
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("prescription", p);
			return "prescription_fill";
		} catch (InputVerificationException ignored) {
			return "prescription_fill";
		}

//		// temporary code to set fake data for now.
//		p.setPharmacyId(70012345);
//		p.setCost(String.format("%.2f", 12.5));
//		p.setFillDate( new java.util.Date().toString() );

		// display the updated prescription

		model.addAttribute("message", "Prescription has been filled.");
		model.addAttribute("prescription", p);
		return "prescription_show";

	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}