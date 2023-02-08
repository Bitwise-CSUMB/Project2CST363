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
		
		try (Connection con = getConnection()) {
			PreparedStatement ps;
			ResultSet rs;
			
			// * 1.  Validate that Prescription p contains rxid, pharmacy name and pharmacy address
			// *     and uniquely identify a prescription and a pharmacy.
			int rxNum;
			try {
				rxNum = Integer.valueOf(p.getRxNum());
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
			
			ps = con.prepareStatement("select prescription.doctorId, doctorSSN, doctorFirstName, doctorLastName, patientId, "
					+ "pharmacyId, drugId from prescription "
					+ "join fill on fill.rxnum = prescription.rxnum "
					+ "join doctor on prescription.doctorid = doctor.doctorid "
					+ "where prescription.rxnum = ?");
			ps.setInt(1, rxNum);
			ps.executeQuery();
			rs = ps.getResultSet();
			
			int doctorId;
			int patientId;
			int pharmacyId;
			int drugId;
			String doctorSSN;
			String doctorFirstName;
			String doctorLastName;
			
			if (rs.next()) {
				doctorId = rs.getInt("doctorId");
				patientId = rs.getInt("patientId");
				pharmacyId = rs.getInt("pharmacyId");
				drugId = rs.getInt("drugId");
				doctorSSN = rs.getString("doctorSSN");
				doctorFirstName = rs.getString("doctorFirstName");
				doctorLastName = rs.getString("doctorLastName");
			} else {
				model.addAttribute("message", "Error: Rx does not exist.");
				throw new InputVerificationException();
			}
			
			// * 2.  update prescription with pharmacyid, name and address.
			ps = con.prepareStatement("select pharmacyName, pharmacyZip, "
					+ "pharmacyCity, pharmacyStreet, pharmacyPhone from pharmacy "
					+ "where pharmacyId = ?");
			ps.setInt(1, pharmacyId);
			ps.executeQuery();
			rs = ps.getResultSet();
			if (rs.next()) {
				p.setPharmacyId(pharmacyId);
				p.setPharmacyName(rs.getString("pharmacyName"));
				p.setPharmacyZip(rs.getString("pharmacyZip"));
				p.setPharmacyCity(rs.getString("pharmacyCity"));
				p.setPharmacyStreet(rs.getString("pharmacyStreet"));
				p.setPharmacyPhone(rs.getString("pharmacyPhone"));
			} else {
				model.addAttribute("message","Prescription refill unsuccessful.");
				model.addAttribute("prescription",p);
				return "prescription_fill";
			}
					
			// * 3.  update prescription with today's date.
			// Get the current time
			java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			
			p.setFillDate(sqlDate.toString());
				
			// * 4.  Display updated prescription
			// * 5.  or if there is an error show the form with an error message.
			
			// Add patient and doctor info to prescription entity
			ps = con.prepareStatement("select patientSSN, patientFirstName, patientLastName "
					+ " from patient where patientId = ?");
			ps.setInt(1, patientId);
			ps.executeQuery();
			rs = ps.getResultSet();
			if (rs.next()) {
				p.setPatientSSN(rs.getString("patientSSN"));
				p.setPatientFirstName(rs.getString("patientFirstName"));
				p.setPatientLastName(rs.getString("patientLastName"));
				
				p.setDoctorSSN(doctorSSN);
				p.setDoctorFirstName(doctorFirstName);
				p.setDoctorLastName(doctorLastName);
			} else {
				model.addAttribute("message","Prescription refill unsuccessful.");
				model.addAttribute("prescription",p);
				return "prescription_fill";
			}
			
			// Add drug price to prescription entity
			
			ps = con.prepareStatement("select price from pharmacyDrug where drugId = ?");
			ps.setInt(1, drugId);
			ps.executeQuery();
			rs = ps.getResultSet();
			if (rs.next()) {
				p.setCost(String.valueOf(rs.getInt("price")));
			} else {
				model.addAttribute("message","Drug price not present.");
				model.addAttribute("prescription",p);
				return "prescription_fill";
			}
			
			// Add quantity to prescription entity
			ps = con.prepareStatement("select quantity from prescription where rxNum = ?");
			ps.setInt(1, rxNum);
			ps.executeQuery();
			rs = ps.getResultSet();
			if (rs.next()) {
				p.setQuantity(rs.getString("quantity"));
			} else {
				model.addAttribute("message","Drug quantity could not be retrieved.");
				model.addAttribute("prescription",p);
				return "prescription_fill";
			}
			
			// Add CORRECT drug name to prescription entity
			// Checks if there is tradeName first, otherwise uses genericName
			
			ps = con.prepareStatement("select tradeName from drug where drugId = ?");
			ps.setInt(1, drugId);
			ps.executeQuery();
			rs = ps.getResultSet();
			if (rs.next()) {
				p.setDrugName(rs.getString("tradeName"));
			} else {
				ps = con.prepareStatement("select genericName from drug where drugId = ?");
				ps.setInt(1, drugId);
				ps.executeQuery();
				rs = ps.getResultSet();
				if (rs.next()) {
					p.setDrugName(rs.getString("genericName"));
				} else {
					model.addAttribute("message","Drug name not present.");
					model.addAttribute("prescription",p);
					return "prescription_fill";
				}
			}
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("prescription", p);
			return "prescription_fill";
		} catch (InputVerificationException ignored) {
			return "prescription_fill";
		}

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