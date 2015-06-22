package tools;

public class SignUpResultImpl extends SignUpResult {
	public SignUpResultImpl(){

	}

	public SignUpResultImpl(boolean result,
			int customerReferenceNumber,
			String message){
		this.result = result;
		this.customerReferenceNumber = customerReferenceNumber;
		this.message = message;
	}
	
	public SignUpResultImpl(SignUpResult signUpResutl){
		this.result = signUpResutl.result;
		this.customerReferenceNumber = signUpResutl.customerReferenceNumber;
		this.message = signUpResutl.message;
	}
	
	public String toString(){
		return result
				+ ", " + customerReferenceNumber
				+ ", " + message;
	}
}
