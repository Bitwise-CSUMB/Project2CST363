package com.csumb.cst363;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import org.springframework.ui.Model;

public final class InputVerifier {

	private static Pattern nonLetterPattern = Pattern.compile("[^A-Za-z]");
	private static Pattern nonWordPattern = Pattern.compile("[^A-Za-z ]");
	private static Pattern nonAlphanumericWordPattern = Pattern.compile("[^A-Za-z0-9 ]");
	private static Pattern ssnGeneralPattern = Pattern.compile("^[0-9]{9}$");
	private static Pattern ssnPattern = Pattern.compile("^[1-8][0-9]{2}(?!00)[0-9]{2}(?!0000)[0-9]{4}$");
	private static Pattern zipPattern = Pattern.compile("^[0-9]{5}(?:[0-9]{4})?$");
	private static Pattern yearPattern = Pattern.compile("^[0-9]{4}$");
	private static Pattern idPattern = Pattern.compile("^[0-9]{1,9}$");

	private static void verifyRequiredField(String toCheck, String fieldName, Model model)
		throws InputVerificationException
	{
		if (toCheck == null || toCheck.isBlank()) {
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is required.");
			throw new InputVerificationException();
		}
	}

	private static void verifyFieldLength(String toCheck, int maxLength, String fieldName, Model model)
		throws InputVerificationException
	{
		if (toCheck.length() > maxLength) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is longer than the max length of "
				+ maxLength + ".");

			throw new InputVerificationException();
		}
	}

	public static String verifySSNField(String toCheck, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();

		if (!ssnGeneralPattern.matcher(toCheck).matches()) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is not a valid SSN."
				+ " Please use the format DDDDDDDDD.");

			throw new InputVerificationException();
		}

		if (!ssnPattern.matcher(toCheck).matches()) {
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is not a valid SSN.");
			throw new InputVerificationException();
		}

		return toCheck;
	}

	public static String verifyLetterField(String toCheck, int maxLength, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();
		verifyFieldLength(toCheck, maxLength, fieldName, model);

		if (nonLetterPattern.matcher(toCheck).find()) {
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" only accepts letters A-Z.");
			throw new InputVerificationException();
		}

		return toCheck;
	}

	public static String verifyWordField(String toCheck, int maxLength, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();
		toCheck.replaceAll(" {2,}", " ");
		verifyFieldLength(toCheck, maxLength, fieldName, model);

		if (nonWordPattern.matcher(toCheck).find()) {
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" only accepts letters A-Z, and spaces.");
			throw new InputVerificationException();
		}

		return toCheck;
	}

	public static String verifyAlphanumericWordField(String toCheck, int maxLength, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();
		toCheck.replaceAll(" {2,}", " ");
		verifyFieldLength(toCheck, maxLength, fieldName, model);

		if (nonAlphanumericWordPattern.matcher(toCheck).find()) {
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" only accepts letters A-Z, 0-9, and spaces.");
			throw new InputVerificationException();
		}

		return toCheck;
	}

	private static LocalDate parseLocalISODate(String dateStr) {
		try {
			return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
		}
		catch (DateTimeParseException ignored) {
			return null;
		}
	}

	public static LocalDate verifyDateField(String toCheck, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();

		LocalDate date = parseLocalISODate(toCheck);
		if (date == null) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is not a valid date."
				+ " Please use the format YYYY-MM-DD.");

			throw new InputVerificationException();
		}

		int year = date.getYear();
		if (year < 1900 || year > 2022) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" has invalid year."
				+ " Please input values between [1900-2022].");

			throw new InputVerificationException();
		}

		int month = date.getMonthValue();
		if (month < 1 || month > 12) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" has invalid month."
				+ " Please input values between [01-12].");

			throw new InputVerificationException();
		}

		int day = date.getDayOfMonth();
		if (day < 1 || day > 31) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" has invalid day."
				+ " Please input values between [01-31].");

			throw new InputVerificationException();
		}

		return date;
	}

	public static String verifyZipField(String toCheck, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();

		if (!zipPattern.matcher(toCheck).matches()) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is not a valid zipcode."
				+ " Please use the format DDDDD or DDDDDDDDD.");

			throw new InputVerificationException();
		}

		return toCheck;
	}

	public static int verifyYearField(String toCheck, String fieldName, Model model)
		throws InputVerificationException
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();

		if (!yearPattern.matcher(toCheck).matches()) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" is not a valid year."
				+ " Please use the format YYYY.");

			throw new InputVerificationException();
		}

		int year = Integer.parseInt(toCheck, 10);
		if (year < 1900 || year > 2022) {

			model.addAttribute("message", "Error: Field \"" + fieldName + "\" has invalid year."
				+ " Please input values between [1900-2022].");

			throw new InputVerificationException();
		}

		return year;
	}
	
	public static int verifyIdField(String toCheck, String fieldName, Model model) 
		throws InputVerificationException 
	{
		verifyRequiredField(toCheck, fieldName, model);
		toCheck = toCheck.trim();
		
		if (!idPattern.matcher(toCheck).matches()) {
			
			model.addAttribute("message", "Error: Field \"" + fieldName + "\" has an invalid id number."
				+ " Please input a id number with a max length of 9 digits and with no foreign values.");
			
			throw new InputVerificationException();
		}

		return Integer.parseInt(toCheck);
	}

	public static class InputVerificationException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private InputVerifier(){}
}
