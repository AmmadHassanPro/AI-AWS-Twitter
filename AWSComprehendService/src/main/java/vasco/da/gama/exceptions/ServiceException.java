package vasco.da.gama.exceptions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 7024609414327624720L;
	private String message;
	
	public ServiceException() {}
	
	public ServiceException(String message, String errorCode) {
		setMessage(message);
		setErrorCode(errorCode);
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	private String errorCode;
	
	@Override
	public String toString() {
		return "error code: " + getErrorCode() + " message: " + getMessage();
	}
}
