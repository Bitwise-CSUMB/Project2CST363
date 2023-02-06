package com.csumb.cst363;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.csumb.cst363.InputVerifier.InputVerificationException;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatient {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String newPatient(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new Patient());
		return "patient_register";
	}

	/*
	 * Process new patient registration
	 */
	@PostMapping("/patient/new")
	public String newPatient(Patient p, Model model) {

		try (Connection connection = getConnection()) {

			PreparedStatement ps;
			ResultSet rs;

			String patientSSN = InputVerifier.verifySSNField(p.getPatientSSN(), "Your SSN", model);
			String patientFirstName = InputVerifier.verifyWordField(p.getPatientFirstName(), 45, "Your First Name", model);
			String patientLastName = InputVerifier.verifyWordField(p.getPatientLastName(), 45, "Your Last Name", model);
			LocalDate patientBirthdate = InputVerifier.verifyDateField(p.getPatientBirthdate(), "Birth Date", model);
			String patientStreet = InputVerifier.verifyAlphanumericWordField(p.getPatientStreet(), 45, "Street", model);
			String patientCity = InputVerifier.verifyWordField(p.getPatientCity(), 45, "City", model);
			String patientState = InputVerifier.verifyWordField(p.getPatientState(), 45, "State", model);
			String patientZip = InputVerifier.verifyZipField(p.getPatientZip(), "Zipcode", model);
			String patientPrimaryFirstName = InputVerifier.verifyWordField(p.getPrimaryFirstName(), 45, "Primary Physician First Name", model);
			String patientPrimaryLastName = InputVerifier.verifyWordField(p.getPrimaryLastName(), 45, "Primary Physician Last Name", model);

			int primaryDoctorId;

			ps = connection.prepareStatement("""
				select patientSSN
					from patient
					where patientSSN = ?
			""");

			ps.setString(1, patientSSN);
			ps.executeQuery();

			rs = ps.getResultSet();
			if (rs.next()) {
				model.addAttribute("message", "Error: SSN already registered.");
				throw new InputVerificationException();
			}

			ps = connection.prepareStatement("""
				select doctorId, specialty
					from doctor
					where doctorFirstName = ? and doctorLastName = ?
			""");

			ps.setString(1, patientPrimaryFirstName);
			ps.setString(2, patientPrimaryLastName);
			ps.executeQuery();

			rs = ps.getResultSet();
			if (rs.next()) {

				primaryDoctorId = rs.getInt("doctorId");
				String specialty = rs.getString("specialty");

				switch (specialty) {

					case "Family Medicine", "Internal Medicine" -> {}

					case "Pediatrics" -> {

						long age = ChronoUnit.YEARS.between(patientBirthdate, LocalDate.now());
						if (age >= 18) {

							model.addAttribute("message", "Error: Dr. "
								+ patientPrimaryFirstName + " " + patientPrimaryLastName
								+ " is a pediatrician and cannot be the primary care physician of an adult.");

							throw new InputVerificationException();
						}
					}
					default -> {

						model.addAttribute("message", "Error: Dr. "
							+ patientPrimaryFirstName + " " + patientPrimaryLastName
							+ " cannot be a primary care physician based on their specialty.");

						throw new InputVerificationException();
					}
				}
			}
			else {

				model.addAttribute("message", "Error: Could not find a doctor with the name \""
					+ patientPrimaryFirstName + " " + patientPrimaryLastName + "\".");

				throw new InputVerificationException();
			}

			ps = connection.prepareStatement("""
				insert into patient (
					primaryDoctorId, patientSSN, patientFirstName, patientLastName, patientBirthdate,
					patientState, patientZip, patientCity, patientStreet
				) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
			""", Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, primaryDoctorId);
			ps.setString(2, patientSSN);
			ps.setString(3, patientFirstName);
			ps.setString(4, patientLastName);
			ps.setDate(5, Date.valueOf(patientBirthdate));
			ps.setString(6, patientState);
			ps.setString(7, patientZip);
			ps.setString(8, patientCity);
			ps.setString(9, patientStreet);
			ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			rs.next();
			p.setPatientId(rs.getInt(1));

			model.addAttribute("message", "Registration successful.");
			model.addAttribute("patient", p);
		}
		catch (SQLException e) {
			model.addAttribute("message", "Error: Internal database error.");
			e.printStackTrace();
			return "patient_register";
		}
		catch (InputVerificationException ignored) {
			return "patient_register";
		}

		return "patient_show";
	}

	/*
	 * Request blank form to search for patient by and and id
	 */
	@GetMapping("/patient/edit")
	public String getPatientForm(Model model) {
		return "patient_get";
	}

	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String getPatientForm(@RequestParam("patientId") String patientId, @RequestParam("patientLastName") String patientLastName,
			Model model) {
			
		if (!Cst363ProjectApplication.validString(patientLastName) || 
				!Cst363ProjectApplication.validInteger(patientId) ||
				patientId.length() == 0 || patientLastName.length() == 0 ||
				patientId.length() > 8) {
			model.addAttribute("message", "Invalid search term(s)");
			return "patient_get";
		}
		
		int patientIdInt = Integer.parseInt(patientId);
		
		Patient p = new Patient();
		p.setPatientId(patientIdInt);
		p.setPatientLastName(patientLastName);
		
		try (Connection con = getConnection();) {
			PreparedStatement ps = con.prepareStatement("select primaryDoctorId, patientSSN, patientFirstName, "
					+ "patientBirthdate, patientStreet, patientState, "
					+ "patientZip, patientCity, doctorLastName from patient, doctor where "
					+ "patientId=? and patientLastName=? and primaryDoctorId = doctorId");
			ps.setInt(1,  patientIdInt);
			ps.setString(2, patientLastName);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setPrimaryDoctorId(rs.getInt(1));
				p.setPatientSSN(rs.getString(2));
				p.setPatientFirstName(rs.getString(3));
				p.setPatientBirthdate(rs.getString(4));
				p.setPatientStreet(rs.getString(5));
				p.setPatientState(rs.getString(6));
				p.setPatientZip(rs.getString(7));
				p.setPatientCity(rs.getString(8));
				
				model.addAttribute("doctorLastName", rs.getString(9));
				model.addAttribute("patient", p);
				return "patient_show";
			} else {
				model.addAttribute("message", "Patient not found.");
				return "patient_get";
			}
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", p);
			return "patient_show";
		}
	}

	/*
	 * Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{patientId}")
	public String updatePatient(@PathVariable String patientId, Model model) {
		Patient p = new Patient();
		
		if (!Cst363ProjectApplication.validInteger(patientId) ||
				patientId.length() == 0 || patientId.length() > 8) {
			model.addAttribute("message", "Invalid patient id");
			return "patient_get";
		}
		int patientIdInt = Integer.parseInt(patientId);
		p.setPatientId(patientIdInt);
	
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("select primaryDoctorId, patientFirstName, patientLastName,"
					+ "patientBirthdate, patientStreet, patientState, "
					+ "patientZip, patientCity from patient where patientId=?");
			ps.setInt(1,  patientIdInt);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setPrimaryDoctorId(Integer.parseInt(rs.getString(1)));
				p.setPatientFirstName(rs.getString(2));
				p.setPatientLastName(rs.getString(3));
				p.setPatientBirthdate(rs.getString(4));
				p.setPatientStreet(rs.getString(5));
				p.setPatientState(rs.getString(6));
				p.setPatientZip(rs.getString(7));
				p.setPatientCity(rs.getString(8));
				model.addAttribute("patient", p);
				return "patient_edit";
			} else {
				model.addAttribute("message", "Patient not found.");
				model.addAttribute("patient", p);
				con.close();
				ps.close();
				rs.close();
				return "patient_get";
			}

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error. "+e.getMessage());
			model.addAttribute("patient", p);
			return "patient_get";

		}
	}


	/*
	 * Process changes to patient profile.
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient p, Model model) {
		ResultSet rs;
		
		if (!Cst363ProjectApplication.validString(p.getPatientStreet()) || 
				!Cst363ProjectApplication.validString(p.getPatientState()) ||
				!Cst363ProjectApplication.validString(p.getPatientCity()) ||
				!(p.getPatientZip().length() != 5 || p.getPatientZip().length() != 9) ) {
			model.addAttribute("message", "Invalid input(s)");
			return "patient_edit";
		}
		
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("update patient set primaryDoctorId=?, "
					+ "patientFirstName=?, patientLastName=?, patientBirthdate=?, patientStreet=?, "
					+ "patientState=?, patientZip=?, patientCity=? where patientId=?");
			
			ps.setInt(1, p.getPrimaryDoctorId());
			ps.setString(2, p.getPatientFirstName());
			ps.setString(3, p.getPatientLastName());
			ps.setString(4, p.getPatientBirthdate());
			ps.setString(5, p.getPatientStreet());
			ps.setString(6, p.getPatientState());
			ps.setString(7, p.getPatientZip());
			ps.setString(8, p.getPatientCity());
			ps.setInt(9, p.getPatientId());

			int rc = ps.executeUpdate();
			
			con.close();
			ps.close();
			
			try (Connection con2 = getConnection();) {
				PreparedStatement ps2 = con2.prepareStatement("select doctorLastName from doctor where doctorId=?");
				ps2.setInt(1, p.getPrimaryDoctorId());
				rs = ps2.executeQuery();
				
				rs.next();
				model.addAttribute("doctorLastName", rs.getString(1));
			} catch (SQLException e) {
				model.addAttribute("doctorLastName", "N/A");
			}
			
			if (!Cst363ProjectApplication.validString(p.getPatientStreet()) || !Cst363ProjectApplication.validString(p.getPatientState()) ||
					!Cst363ProjectApplication.validString(p.getPatientZip()) || !Cst363ProjectApplication.validString(p.getPatientCity())) {
				model.addAttribute("message", "Invalid input(s)");
				return "patient_edit";
			}
			
			if (rc==1) {
				model.addAttribute("message", "Update successful");
				model.addAttribute("patient", p);
				return "patient_show";

			}else {
				model.addAttribute("message", "Error. Update was not successful");
				model.addAttribute("patient", p);
				return "patient_edit";
			}

		} catch (SQLException e) {
			if (e.getMessage().startsWith("Cannot add or update")) {
				model.addAttribute("message", "Invalid Doctor Id");
			} else {
				model.addAttribute("message", "SQL Error. "+e.getMessage());
			}
			model.addAttribute("patient", p);
			return "patient_edit";
		}
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */
	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}
}
