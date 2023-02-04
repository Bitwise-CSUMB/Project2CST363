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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * Controller class for doctor registration and profile update.
 */
@Controller
public class ControllerDoctor {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * Request for new doctor registration form.
	 */
	@GetMapping("/doctor/register")
	public String newDoctor(Model model) {
		// return blank form for new patient registration
		model.addAttribute("doctor", new Doctor());
		return "doctor_register";
	}

	/*
	 * Process doctor registration.
	 */
	@PostMapping("/doctor/register")
	public String createDoctor(Doctor doctor, Model model) {

		try (Connection con = getConnection();) {
			PreparedStatement ps = con.prepareStatement("insert into doctor(doctorLastName, doctorFirstName, specialty, practiceSinceYear,  doctorSSN ) values(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, doctor.getDoctorLastName());
			ps.setString(2, doctor.getDoctorFirstName());
			ps.setString(3, doctor.getSpecialty());
			ps.setString(4, doctor.getPracticeSinceYear());
			ps.setString(5, doctor.getDoctorSSN());

			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) doctor.setDoctorId((int)rs.getLong(1));

			// display message and patient information
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("doctor", doctor);
			return "doctor_show";

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("doctor", doctor);
			return "doctor_register";
		}
	}

	/*
	 * Request blank form for doctor search.
	 */
	@GetMapping("/doctor/get")
	public String getDoctor(Model model) {
		// return form to enter doctor id and name
		model.addAttribute("doctor", new Doctor());
		return "doctor_get";
	}

	/*
	 * Search for doctor by id and name.
	 */
	@PostMapping("/doctor/get")
	public String getDoctor(Doctor doctor, Model model) {

		try (Connection con = getConnection();) {
			// for DEBUG
			System.out.println("start getDoctor "+doctor);
			PreparedStatement ps = con.prepareStatement("select doctorLastName, doctorFirstName, specialty, practiceSinceYear from doctor where doctorId=? and doctorLastName=?");
			ps.setInt(1, doctor.getDoctorId());
			ps.setString(2, doctor.getDoctorLastName());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				doctor.setDoctorLastName(rs.getString(1));
				doctor.setDoctorFirstName(rs.getString(2));
				doctor.setPracticeSinceYear(rs.getString(4));
				doctor.setSpecialty(rs.getString(3));
				model.addAttribute("doctor", doctor);
				// for DEBUG
				System.out.println("end getDoctor "+doctor);
				return "doctor_show";

			} else {
				model.addAttribute("message", "Doctor not found.");
				return "doctor_get";
			}

		} catch (SQLException e) {
			System.out.println("SQL error in getDoctor "+e.getMessage());
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("doctor", doctor);
			return "doctor_get";
		}
	}

	/*
	 * search for doctor by id.
	 */
	@GetMapping("/doctor/edit/{id}")
	public String getDoctor(@PathVariable int id, Model model) {
		Doctor doctor = new Doctor();
		doctor.setDoctorId(id);
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("select doctorLastName, doctorFirstName, specialty, practiceSinceYear from doctor where doctorId=?");
			ps.setInt(1,  id);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				doctor.setDoctorLastName(rs.getString(1));
				doctor.setDoctorFirstName(rs.getString(2));
				doctor.setPracticeSinceYear(rs.getString(4));
				doctor.setSpecialty(rs.getString(3));
				model.addAttribute("doctor", doctor);
				return "doctor_edit";
			} else {
				model.addAttribute("message", "Doctor not found.");
				model.addAttribute("doctor", doctor);
				return "doctor_get";
			}

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("doctor", doctor);
			return "doctor_get";

		}
	}

	/*
	 * process profile update for doctor.  Change specialty or year of practice.
	 */
	@PostMapping("/doctor/edit")
	public String updateDoctor(Doctor doctor, Model model) {
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("update doctor set specialty=?, practiceSinceYear=? where doctorId=?");
			ps.setString(1,  doctor.getSpecialty());
			ps.setString(2, doctor.getPracticeSinceYear());
			ps.setInt(3,  doctor.getDoctorId());

			int rc = ps.executeUpdate();
			if (rc==1) {
				model.addAttribute("message", "Update successful");
				model.addAttribute("doctor", doctor);
				return "doctor_show";

			}else {
				model.addAttribute("message", "Error. Update was not successful");
				model.addAttribute("doctor", doctor);
				return "doctor_edit";
			}

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("doctor", doctor);
			return "doctor_edit";
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
