package com.csumb.cst363;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Cst363ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cst363ProjectApplication.class, args);
	}
	
	public static boolean validString(String input) {
		if (input.length() == 0) {
			return false;
		}
		
		for (char c : input.toCharArray()) {
			if (!Character.isLetter(c) && !Character.isDigit(c) && !(c == ' ')) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean validInteger(String input) {
		for (char c : input.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		
		return true;
	}
}
