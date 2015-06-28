package tools;

/**
 * @author comp6231.team5
 * The implementation of SignUpResult
 */
public class SignUpResultImpl extends SignUpResult {
	public SignUpResultImpl(){

	}

	/**
	 * Constructor
	 * @param result
	 * @param customerReferenceNumber
	 * @param message
	 */
	public SignUpResultImpl(boolean result,
			int customerReferenceNumber,
			String message){
		this.result = result;
		this.customerReferenceNumber = customerReferenceNumber;
		this.message = message;
	}
	
	/**
	 * Constructor
	 * @param signUpResutl
	 */
	public SignUpResultImpl(SignUpResult signUpResutl){
		this.result = signUpResutl.result;
		this.customerReferenceNumber = signUpResutl.customerReferenceNumber;
		this.message = signUpResutl.message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return result
				+ ", " + customerReferenceNumber
				+ ", " + message;
	}
}
