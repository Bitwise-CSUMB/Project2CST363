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
	public String getPatientForm(@RequestParam("patientId") int patientId, @RequestParam("patientLastName") String last_name,
			Model model) {

		/*
		 * TODO code to search for patient by id and name retrieve patient data and primary
		 * doctor
		 */

		// return fake data for now.
		Patient p = new Patient();
		p.setPatientId(patientId);
		p.setPatientLastName(last_name);
		p.setPatientBirthdate("2001-01-01");
		p.setPatientStreet("123 Main");
		p.setPatientCity("SunCity");
		p.setPatientState("CA");
		p.setPatientZip("99999");
		p.setPrimaryDoctorId(11111);
		//p.setPrimaryName("Dr. Watson");
		p.setSpecialty("Family Medicine");
		p.setPracticeSinceYear("1992");

		model.addAttribute("patient", p);
		return "patient_show";
	}

	/*
	 * Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{patientId}")
	public String updatePatient(@PathVariable int patientId, Model model) {

		// TODO Complete database logic search for patient by id.

		// return fake data.
		Patient p = new Patient();
		p.setPatientId(patientId);
		p.setPatientFirstName("Alex");
		p.setPatientLastName("Patient");
		p.setPatientBirthdate("2001-01-01");
		p.setPatientStreet("123 Main");
		p.setPatientCity("SunCity");
		p.setPatientState("CA");
		p.setPatientZip("99999");
		p.setPrimaryDoctorId(11111);
		//p.setPrimaryName("Dr. Watson");
		p.setSpecialty("Family Medicine");
		p.setPracticeSinceYear("1992");

		model.addAttribute("patient", p);
		return "patient_edit";
	}


	/*
	 * Process changes to patient profile.
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient p, Model model) {

		// TODO

		model.addAttribute("patient", p);
		return "patient_show";
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */
	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}
}
