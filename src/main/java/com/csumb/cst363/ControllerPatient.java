package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

		// TODO Complete database logic to verify and process new patient

		// remove this fake data.
		p.setPatientId(300198);
		model.addAttribute("message", "Registration successful.");
		model.addAttribute("patient", p);

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
		p.setPrimaryName("Dr. Watson");
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
		p.setPrimaryName("Dr. Watson");
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
