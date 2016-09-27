package net.hollowbit.archipeloshared;

public class StringValidator {
	
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	public static final String ALPHABET_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String SYMBOLS = "!@#$%^&*()-_+=";
	public static final String NUMBERS = "0123456789";
	
	public static final String PASSWORD = ALPHABET + ALPHABET_CAPS + SYMBOLS + NUMBERS;
	public static final String USERNAME = ALPHABET + ALPHABET_CAPS + NUMBERS + "_";
	
	//Checks if the string has only characters from charset
	public static boolean isStringValid (String text, String charSet) {
		for (char c : text.toCharArray()) {
			boolean isCharInSet = false;
			for (char p : charSet.toCharArray()) {
				if (c == p) {
					isCharInSet = true;
					break;
				}
			}
			
			if (!isCharInSet)
				return false;
		}
		return true;
	}
	
}
