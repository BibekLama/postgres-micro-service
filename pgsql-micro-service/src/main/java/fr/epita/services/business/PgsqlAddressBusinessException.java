package fr.epita.services.business;

public class PgsqlAddressBusinessException extends PgsqlBusinessException{

	private static final long serialVersionUID = 1L;

	public PgsqlAddressBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PgsqlAddressBusinessException(String message) {
		super(message);
	}

}