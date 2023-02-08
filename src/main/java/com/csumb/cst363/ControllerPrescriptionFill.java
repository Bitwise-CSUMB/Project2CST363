package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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

			int rxNum = InputVerifier.verifyIdField(p.getRxNum(), "Rx", model);
			String patientLastName = InputVerifier.verifyWordField(p.getPatientLastName(), 45, "Patient Last Name", model);
			String pharmacyName = InputVerifier.verifyWordField(p.getPharmacyName(), 45, "Pharmacy Name", model);
			String pharmacyZip = InputVerifier.verifyZipField(p.getPharmacyZip(), "Pharmacy Zip", model);
			String pharmacyCity = InputVerifier.verifyWordField(p.getPharmacyCity(), 45, "Pharmacy City", model);
			String pharmacyStreet = InputVerifier.verifyAlphanumericWordField(p.getPharmacyStreet(), 45, "Pharmacy Street", model);

			// Verify prescription rx and patient last name

			int doctorId;
			int patientId;
			int drugId;
			int quantity;

			ps = con.prepareStatement("""
				select doctorId, pre.patientId, drugId, quantity
				from prescription pre, patient pat
				where pre.patientId = pat.patientId
					and rxNum = ?
					and patientLastName = ?
			""");
			ps.setInt(1, rxNum);
			ps.setString(2, patientLastName);
			ps.executeQuery();

			rs = ps.getResultSet();
			if (rs.next()) {
				doctorId = rs.getInt(1);
				patientId = rs.getInt(2);
				drugId = rs.getInt(3);
				quantity = rs.getInt(4);
			}
			else {
				model.addAttribute("message", "Error: Rx corresponding to patient not found.");
				throw new InputVerificationException();
			}

			// Verify pharmacy

			int pharmacyId;
			String pharmacyPhone;

			ps = con.prepareStatement("""
				select pharmacyId, pharmacyPhone
				from pharmacy
				where pharmacyName = ?
					and pharmacyZip = ?
					and pharmacyCity = ?
					and pharmacyStreet = ?
			""");
			ps.setString(1, pharmacyName);
			ps.setString(2, pharmacyZip);
			ps.setString(3, pharmacyCity);
			ps.setString(4, pharmacyStreet);
			ps.executeQuery();

			rs = ps.getResultSet();
			if (rs.next()) {
				pharmacyId = rs.getInt(1);
				pharmacyPhone = rs.getString(2);
			}
			else {
				model.addAttribute("message", "Error: Pharmacy not found.");
				throw new InputVerificationException();
			}

			// Verify the pharmacy has a drug that can fill the prescription

			String tradeName;
			String genericName;

			ps = con.prepareStatement("""
				select tradeName, genericName
				from drug
				where drugId = ?
			""");
			ps.setInt(1, drugId);
			ps.executeQuery();

			rs = ps.getResultSet();
			rs.next();
			tradeName = rs.getString(1);
			genericName = rs.getString(2);

			int fillDrugId = drugId;
			Integer genericCompanyId = null;
			int price = -1;

			PreparedStatement pharmacyDrugPS = con.prepareStatement("""
				select price
				from pharmacyDrug
				where pharmacyId = ? and drugId = ?
			""");

			if (tradeName == null) {

				// Generic drug

				ps = con.prepareStatement("""
					select d.drugId, companyId
					from drug d left join companyMakesDrug makes on d.drugId = makes.drugId
					where genericName = ?
				""");
				ps.setString(1, genericName);
				ps.executeQuery();

				rs = ps.getResultSet();
				while (rs.next()) {

					fillDrugId = rs.getInt(1);
					genericCompanyId = rs.getInt(2);
					pharmacyDrugPS.setInt(1, pharmacyId);
					pharmacyDrugPS.setInt(2, fillDrugId);
					pharmacyDrugPS.executeQuery();

					ResultSet pharmacyDrugRS = pharmacyDrugPS.getResultSet();
					if (pharmacyDrugRS.next()) {
						price = pharmacyDrugRS.getInt(1);
						break;
					}
				}
			}
			else {

				// Trade drug

				pharmacyDrugPS.setInt(1, pharmacyId);
				pharmacyDrugPS.setInt(2, drugId);
				pharmacyDrugPS.executeQuery();

				ResultSet pharmacyDrugRS = pharmacyDrugPS.getResultSet();
				if (pharmacyDrugRS.next()) {
					price = pharmacyDrugRS.getInt(1);
				}
			}

			if (price == -1) {
				model.addAttribute("message", "Error: Pharmacy does not carry the required drug.");
				throw new InputVerificationException();
			}

			// Get current date
			java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			// Add fill row

			ps = con.prepareStatement("""
				insert into fill (fillId, rxNum, pharmacyId, fillDrugId, fillGenericCompanyId, fillDate)
				values (?, ?, ?, ?, ?, ?)
			""");
			ps.setInt(1, 0); // Auto-generated
			ps.setInt(2, rxNum);
			ps.setInt(3, pharmacyId);
			ps.setInt(4, fillDrugId);
			if (genericCompanyId != null) {
				ps.setInt(5, genericCompanyId);
			}
			else {
				ps.setNull(5, Types.INTEGER);
			}
			ps.setDate(6, sqlDate);
			ps.executeUpdate();

			// Add doctor info to prescription entity

			ps = con.prepareStatement("""
				select doctorSSN, doctorFirstName, doctorLastName
				from doctor where doctorId = ?
			""");

			ps.setInt(1, doctorId);
			ps.executeQuery();

			rs = ps.getResultSet();
			rs.next();
			p.setDoctorSSN(rs.getString(1));
			p.setDoctorFirstName(rs.getString(2));
			p.setDoctorLastName(rs.getString(3));

			// Add patient info to prescription entity

			ps = con.prepareStatement("""
				select patientSSN, patientFirstName, patientLastName
				from patient where patientId = ?
			""");
			ps.setInt(1, patientId);
			ps.executeQuery();

			rs = ps.getResultSet();
			rs.next();
			p.setPatientSSN(rs.getString(1));
			p.setPatientFirstName(rs.getString(2));
			p.setPatientLastName(rs.getString(3));

			// Add drug info to prescription entity
			p.setDrugName(tradeName != null ? tradeName : genericName);
			p.setQuantity(String.valueOf(quantity));
			p.setCost(String.valueOf(price * quantity));

			// Add pharmacy info to prescription entity
			p.setPharmacyPhone(pharmacyPhone);
			p.setPharmacyName(pharmacyName);
			p.setPharmacyZip(pharmacyZip);
			p.setPharmacyCity(pharmacyCity);
			p.setPharmacyStreet(pharmacyStreet);

			// Add fill date to prescription entity
			p.setFillDate(sqlDate.toString());
		}
		catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("message", "SQL Error: " + e.getMessage());
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		catch (InputVerificationException ignored) {
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
