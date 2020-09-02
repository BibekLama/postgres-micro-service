package fr.epita.services.business;

public class PgsqlBusinessException extends Exception{

	private static final long serialVersionUID = 1L;

	public PgsqlBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PgsqlBusinessException(String message) {
		super(message);
	}

}
