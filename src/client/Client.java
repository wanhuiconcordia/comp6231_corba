package client;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import tools.*;
import retailer.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;

public class Client
{
	private Retailer retailer;
	private static NamingContextExt ncRef;
	private Scanner in;
	private CustomerImpl currentCustomer;
	private LoggerClient loggerClient;
	private String name;
	private ArrayList<ItemImpl> retailerItemCatalog;

	static boolean initializeOrbEnvirement(String args[]){
		// create and initialize the ORB
		ORB orb = ORB.init(args, null);

		try {
			// get the root naming context
			org.omg.CORBA.Object objRef;
			objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.  
			ncRef = NamingContextExtHelper.narrow(objRef);
			return true;
		} catch (InvalidName e) {
			System.out.println("Failed to resolve NameService!");
			//e.printStackTrace();
			return false;
		}
	}

	public Client(){
		name = "client";
		in = new Scanner(System.in);
		loggerClient = new LoggerClient();
		retailerItemCatalog = new ArrayList<ItemImpl>();
	}
	public boolean addRetailer(){

		String retailerName = in.next();
		try {
			retailer = RetailerHelper.narrow(ncRef.resolve_str(retailerName));
			System.out.println("Obtained a handle on server object: " + retailer);
			return true;
		} catch (NotFound | CannotProceed
				| org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			System.out.println("Failed to resolve retailer:" + retailerName);
			return false;
			//e.printStackTrace();
		}
	}

	/**
	 * Sign up
	 * @return sign up result
	 */
	public boolean customerSignUp(){
		if(currentCustomer != null){
			customerSignOut();
		}
		System.out.println("Input customer name:");
		String name = in.next();
		System.out.println("Input customer password:");
		String password = in.next();
		System.out.println("Input customer street1:");
		String street1 = in.next();
		System.out.println("Input customer street2:");
		String street2 = in.next();
		System.out.println("Input customer city:");
		String city = in.next();
		System.out.println("Input customer state:");
		String state = in.next();
		System.out.println("Input customer zip code:");
		String zip = in.next();
		System.out.println("Input customer country:");
		String country = in.next();
		try {
			loggerClient.write(this.name + ": Tries to sign up with:" + name + ", " + password
					+ ", " + street1
					+ ", " + street2
					+ ", " + city
					+ ", " + state
					+ ", " + zip
					+ ", " + country);
			SignUpResult signUpResult  = retailer.signUp(name, password, street1, street2, city, state, zip, country);
			if(signUpResult == null){
				loggerClient.write(this.name + ": The retailer returned null. Failed to signup.");
				System.out.println("The retailer returned null. Failed to signup.");
				return false;
			}else{
				if(signUpResult.result){
					currentCustomer = new CustomerImpl(signUpResult.customerReferenceNumber, name, password, street1, street2, city, state, zip, country);
					System.out.println("Your creferenceNumber is:" + signUpResult.customerReferenceNumber);
					loggerClient.write(this.name + ": Sign successfully. The retailer returned:" + signUpResult.message);
				}else{
					System.out.println("Failed to sign up. The retailer returned:" + signUpResult.message);
					loggerClient.write(this.name + ": Failed to sign up. The retailer returned:" + signUpResult.message);
				}
				return signUpResult.result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sign in
	 * @return sign in result
	 */
	public boolean customerSignIn(){
		if(currentCustomer != null){
			customerSignOut();
		}
		try{
			System.out.println("Input customer ReferenceNumber:");
			int customerReferenceNumber = Integer.parseInt(in.next());
			System.out.println("Input customer password:");
			String password = in.next();

			loggerClient.write(name + ": Tries to sign in with reference number:" + customerReferenceNumber + " and password:" + password);
			currentCustomer = (CustomerImpl) retailer.signIn(customerReferenceNumber, password);
			if(currentCustomer == null){
				loggerClient.write(name + ": The retailer returned null. Failed to sign in. Please try again.");
				System.out.println("The retailer returned null. Failed to sign in. Please try again.");
				return false;
			}else{
				System.out.println("Signed in properly. Your person informations are:" + currentCustomer.toString());
				loggerClient.write(name + ": Signed in properly. The customer info:" + currentCustomer.toString());
				return true;
			}
		} catch (NumberFormatException e){
			System.out.println("ReferenceNumber should contains digits only. Please try again.");
			return false;
		}
	}

	/**
	 * Sing out
	 */
	public void customerSignOut(){
		if(currentCustomer == null)
			return;
		loggerClient.write(name + ": Current customer:" + currentCustomer.name + " signed out.");
		currentCustomer = null;
	}

	/**
	 * get retailer's item catalog
	 * @return available item list
	 */
	public void getCatalog(){
		if(currentCustomer == null){
			System.out.println("This operation is only allowed for registed user. Please sign in or sign up.");
		}else{
			Item [] itemArray = retailer.getCatalog(currentCustomer.customerReferenceNumber);
			if(itemArray == null){
				System.out.println("Retailer return null");
				retailerItemCatalog.clear();
			}else if(itemArray.length == 0){
				System.out.println("Retailer return null");
				retailerItemCatalog.clear();
			}else{
				System.out.println("Retailer item catalog:");
				for(Item item: itemArray){
					ItemImpl thisItemImpl = new ItemImpl(item);
					retailerItemCatalog.add(thisItemImpl);
					System.out.println(thisItemImpl.toString());
				}
			}
		}
	}

	/**
	 * make order
	 * @return the items shipping status 
	 */
	public void makeOrder(){
		if(currentCustomer == null){
			System.out.println("This operation is only allowed for registed user. Please sign in or sign up.");
		}else{
			String inputString = new String();
			ArrayList<ItemImpl> itemOrderList = new ArrayList<ItemImpl>();
			for(ItemImpl itemImpl: retailerItemCatalog){
				System.out.print("Input quantity for :" + itemImpl.toString() + " (q for finishing order list):");
				inputString = in.next();
				if(inputString.equals("q")){
					break;
				}else{
					try{
						int quantiy = Integer.parseInt(inputString);
						ItemImpl tmpItem = itemImpl.clone();
						tmpItem.quantity = quantiy;
						itemOrderList.add(tmpItem);
					}catch(NumberFormatException e){
						System.out.println("Please input a number or 'q'.");
					}
				}
			}
			if(itemOrderList.size() > 0){
				ItemImpl [] itemImplArray = new ItemImpl[itemOrderList.size()];
				System.out.println("Try to order:");
				loggerClient.write(name + ": Tries to order:");
				String log = new String();

				int index = 0;
				for(ItemImpl itemImpl: itemOrderList){
					itemImplArray[index] = itemImpl;
					System.out.println(itemImpl.toString());
					log = log + itemImpl.toString() + "</br>";
					index++;
				}
				if(log.length() > 0){
					loggerClient.write(log);
				}

				ItemShippingStatusImpl []itemShippingStatusImplArray;
				itemShippingStatusImplArray = (ItemShippingStatusImpl[]) retailer.submitOrder(currentCustomer.customerReferenceNumber, itemImplArray);
				if(itemShippingStatusImplArray == null){
					System.out.println("submitOrder return null.");
				}else if(itemShippingStatusImplArray.length == 0){
					System.out.println("submitOrder return an empty array.");
				}else{
					for(ItemShippingStatusImpl itemShippingStatusImpl : itemShippingStatusImplArray){
						System.out.println(itemShippingStatusImpl.toString());
					}
				}
			}
		}
	}


	public static void main(String args[])
	{
		if(!initializeOrbEnvirement(args)){
			return;
		}

		Client client = new Client();
		if(!client.addRetailer()){
			//TODO CLOSE ORB
			return;
		}
		String operation;
		do{
			System.out.println("Type [1] to sign up.");
			System.out.println("Type [2] to sign in.");
			System.out.println("Type [3] to sign out.");
			System.out.println("Type [4] to get product catalog.");
			System.out.println("Type [5] to make an order.");
			System.out.println("Type [6] to quit.");
			operation = client.in.next();
			if(operation.compareTo("1") == 0){
				client.customerSignUp();
			}else if(operation.compareTo("2") == 0){
				client.customerSignIn();
			}else if(operation.compareTo("3") == 0){
				client.customerSignOut();
			}else if(operation.compareTo("4") == 0){
				client.getCatalog();
			}else if(operation.compareTo("5") == 0){
				client.makeOrder();
			}else if(operation.compareTo("6") == 0){
				break;
			}else{
				System.out.println("Wrong input. Try again.");
			}
		}while(true);

		client.in.close();
	}
}