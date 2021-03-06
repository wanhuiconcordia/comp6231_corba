package retailer;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import tools.Customer;
import tools.CustomerImpl;
import tools.SignUpResult;
import tools.SignUpResultImpl;
import tools.XmlFileController;

public class CustomerManager {

	private ArrayList<CustomerImpl> customers;

	private String fileName;

	public CustomerManager(String fileName){
		customers = new ArrayList<CustomerImpl>();
		this.fileName = fileName;
		loadCustomers();
	}

	public void saveCustomers()
	{
		XmlFileController xmlFileControler = new XmlFileController(fileName);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("customers");
		for(CustomerImpl customerImpl: customers){
			Element ml = customerImpl.toXmlElement();
			root.add(ml);
		}
		try {
			xmlFileControler.Write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadCustomers()
	{
		XmlFileController xmlfile = new XmlFileController(fileName);
		Element root = xmlfile.Read();
		if(root != null){
			List<Element> nodes = root.elements("customer");
			for(Element subElem: nodes){
				int customerReferenceNumber = Integer.parseInt(subElem.element("customerReferenceNumber").getText());
				String name = subElem.element("name").getText();
				String password = subElem.element("password").getText(); 
				String street1 = subElem.element("street1").getText();
				String street2 = subElem.element("street2").getText();
				String city = subElem.element("city").getText();
				String state = subElem.element("state").getText();
				String zip = subElem.element("zip").getText();
				String country = subElem.element("country").getText();
				customers.add(new CustomerImpl(customerReferenceNumber, name, password, street1, street2, city, state, zip, country));
			}
		}
	}

	public synchronized SignUpResult register(String name, String password, String street1, String street2, String city, String state, String zip, String country){
		for(CustomerImpl customerImpl: customers){
			if(customerImpl.name.equals(name) && customerImpl.password.equals(password)){
				return new SignUpResultImpl(false, -1, "Failed to sign up! (User name exists, try another name)");
			}
		}
		int customerReferenceNumber = 1000 + customers.size();
		customers.add(new CustomerImpl(customerReferenceNumber, name, password, street1, street2, city, state, zip, country));
		saveCustomers();
		return new SignUpResultImpl(true, customerReferenceNumber , "Sign up successfully.");
	}

	public synchronized Customer find(int customerReferenceNumber, String password){
		for(CustomerImpl customerImpl: customers){
			if(customerImpl.customerReferenceNumber == customerReferenceNumber
					&& customerImpl.password.equals(password)){
				return customerImpl;
			}
		}
		return null;
	}

	public synchronized boolean find(int customerReferenceNumber){
		for(CustomerImpl customerImpl: customers){
			if(customerImpl.customerReferenceNumber == customerReferenceNumber){
				return true;
			}
		}
		return false;
	}

	public synchronized Customer getCustomerByReferenceNumber(int customerReferenceNumber){
		for(CustomerImpl customerImpl: customers){
			if(customerImpl.customerReferenceNumber == customerReferenceNumber){
				return customerImpl;
			}
		}
		return null;
	}
}
