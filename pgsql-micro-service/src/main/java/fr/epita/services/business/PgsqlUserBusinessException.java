package fr.epita.services.business;

public class PgsqlUserBusinessException  extends PgsqlBusinessException{

	private static final long serialVersionUID = 1L;

	public PgsqlUserBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PgsqlUserBusinessException(String message) {
		super(message);
	}

}
