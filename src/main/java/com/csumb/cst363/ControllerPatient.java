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
