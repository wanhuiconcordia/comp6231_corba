package tools;

public class CustomerImpl extends Customer {
	public CustomerImpl(int customerReferenceNumber,
			String name,
			String password,
			String street1,
			String street2,
			String city,
			String state,
			String zip,
			String country){
		this.customerReferenceNumber = customerReferenceNumber;
		this.name = name;
		this.password = password;
		this.street1 = street1;
		this.street2 = street2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	public CustomerImpl(){
		
	}
	
	public CustomerImpl(Customer customer){
		this.customerReferenceNumber = customer.customerReferenceNumber;
		this.name = customer.name;
		this.password = customer.password;
		this.street1 = customer.street1;
		this.street2 = customer.street2;
		this.city = customer.city;
		this.state = customer.state;
		this.zip = customer.zip;
		this.country = customer.country;
	}
	
	public String toString(){
		return customerReferenceNumber
				+ ", " + name
				+ ", " + password
				+ ", " + street1
				+ ", " + street2
				+ ", " + city
				+ ", " + state
				+ ", " + zip
				+ ", " + country;
	}
}
