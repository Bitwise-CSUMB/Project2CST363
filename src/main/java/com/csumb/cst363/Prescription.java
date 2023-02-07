package com.csumb.cst363;

/*
 * This class is used to transfer data to/from prescription template pages.
 */
public class Prescription {

	///////////
	// START // Following fields are set when doctor creates a prescription.
	///////////

	// table: prescription
	private String rxNum;  // primary key generated by database.
	private String quantity;

	// table: patient
	private String patientSSN;
	private String patientFirstName;
	private String patientLastName;

	// table: doctor
	private String doctorSSN;
	private String doctorFirstName;
	private String doctorLastName;

	// table: drug (tradeName or genericName)
	private String drugName;

	/////////
	// END //
	/////////

	///////////
	// START // Following fields will be null or blank until prescription has been filled.
	///////////

	// table: pharmacy
	private Integer pharmacyId;
	private String pharmacyName;
	private String pharmacyPhone;

	// table: fill
	private String fillDate;

	// no table
	private String pharmacyAddress;
	private String cost;

	/////////
	// END //
	/////////

	public String getRxNum() {
		return rxNum;
	}

	public void setRxNum(String rxNum) {
		this.rxNum = rxNum;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPatientSSN() {
		return patientSSN;
	}

	public void setPatientSSN(String patientSSN) {
		this.patientSSN = patientSSN;
	}

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getDoctorSSN() {
		return doctorSSN;
	}

	public void setDoctorSSN(String doctorSSN) {
		this.doctorSSN = doctorSSN;
	}

	public String getDoctorFirstName() {
		return doctorFirstName;
	}

	public void setDoctorFirstName(String doctorFirstName) {
		this.doctorFirstName = doctorFirstName;
	}

	public String getDoctorLastName() {
		return doctorLastName;
	}

	public void setDoctorLastName(String doctorLastName) {
		this.doctorLastName = doctorLastName;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public Integer getPharmacyId() {
		return pharmacyId;
	}

	public void setPharmacyId(Integer pharmacyId) {
		this.pharmacyId = pharmacyId;
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	public String getPharmacyPhone() {
		return pharmacyPhone;
	}

	public void setPharmacyPhone(String pharmacyPhone) {
		this.pharmacyPhone = pharmacyPhone;
	}

	public String getFillDate() {
		return fillDate;
	}

	public void setFillDate(String fillDate) {
		this.fillDate = fillDate;
	}

	public String getPharmacyAddress() {
		return pharmacyAddress;
	}

	public void setPharmacyAddress(String pharmacyAddress) {
		this.pharmacyAddress = pharmacyAddress;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Prescription ["
			+ "rxNum=" + rxNum
			+ ", quantity=" + quantity
			+ ", patientSSN=" + patientSSN
			+ ", patientFirstName=" + patientFirstName
			+ ", patientLastName=" + patientLastName
			+ ", doctorSSN=" + doctorSSN
			+ ", doctorFirstName=" + doctorFirstName
			+ ", doctorLastName=" + doctorLastName
			+ ", drugName=" + drugName
			+ ", pharmacyId=" + pharmacyId
			+ ", pharmacyName=" + pharmacyName
			+ ", pharmacyPhone=" + pharmacyPhone
			+ ", fillDate=" + fillDate
			+ ", pharmacyAddress=" + pharmacyAddress
			+ ", cost=" + cost
			+ "]";
	}
}
