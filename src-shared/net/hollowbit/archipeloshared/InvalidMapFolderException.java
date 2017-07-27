package net.hollowbit.archipeloshared;

import java.io.IOException;

public class InvalidMapFolderException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5041635259786840187L;
	
	public InvalidMapFolderException() {
		super("The map folder has invalid entries.");
	}
	
	public InvalidMapFolderException(String message) {
		super(message);
	}
	
}
