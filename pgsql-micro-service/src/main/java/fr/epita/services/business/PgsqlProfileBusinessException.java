package fr.epita.services.business;

public class PgsqlProfileBusinessException extends PgsqlBusinessException{

	private static final long serialVersionUID = 1L;

	public PgsqlProfileBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PgsqlProfileBusinessException(String message) {
		super(message);
	}

}
