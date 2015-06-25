package warehouse;

import java.util.Scanner;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import tools.Item;
import tools.ItemImpl;
import tools.LoggerClient;

public class WarehouseClient {

	private Warehouse warehouse;
	private NamingContextExt namingContextRef;
	private Scanner in;
	private LoggerClient loggerClient;
	private String name;

	boolean initializeOrbEnvirement(String args[]){
		// create and initialize the ORB
		ORB orb = ORB.init(args, null);

		try {
			// get the root naming context
			org.omg.CORBA.Object objRef;
			objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.  
			namingContextRef = NamingContextExtHelper.narrow(objRef);
			return true;
		} catch (InvalidName e) {
			System.out.println("Failed to resolve NameService!");
			//e.printStackTrace();
			return false;
		}
	}

	public WarehouseClient() {
		name = "warehouseClient";
		in = new Scanner(System.in);
		loggerClient = new LoggerClient();
	}

	public boolean connectWarehouse(){

		System.out.print("Please input warehouse name to establish connection:");
		String warehouseName = in.nextLine();

		try {
			warehouse = WarehouseHelper.narrow(namingContextRef.resolve_str(warehouseName));
			System.out.println("Obtained a handle on server object: " + warehouseName);
			return true;
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println("Failed to resolve retailer:" + warehouseName);
		return false;
	}

	private void testGetProductsById(){
		System.out.println("Please input product ID:");
		try{
			int productID = in.nextInt();
			in.nextLine();
			Item[] itemArray = warehouse.getProductsByID(productID);
//			
		//	System.out.println("There are " + itemArray.length + " items");
			System.out.println("There are " + itemArray.length + " items");
			for(Item item: itemArray){
				ItemImpl tmpItemImpl = new ItemImpl(item);
				System.out.println(tmpItemImpl.toString());
			}
		}catch(Exception e){
			System.out.println("Input a integer for product id.");
		}
	}		

	private void testGetProductsByType(){
		System.out.println("Please input product type:");
		String productType = in.nextLine();
		System.out.println("productType:" + productType);
		Item[] itemArray = warehouse.getProductsByType(productType);
		System.out.println("There are " + itemArray.length + " items");
		for(Item item: itemArray){
			ItemImpl tmpItemImpl = new ItemImpl(item);
			System.out.println(tmpItemImpl.toString());
		}
	}

	private void testGetProductsByManufactureId(){
		System.out.println("Please input manufacture ID:");
		String manufactureID = in.nextLine();
		Item[] itemArray = warehouse.getProductsByRegisteredManufacturers(manufactureID);
		System.out.println("There are " + itemArray.length + " items");
		for(Item item: itemArray){
			ItemImpl tmpItemImpl = new ItemImpl(item);
			System.out.println(tmpItemImpl.toString());
		}
	}

	private void testGetProductsPerManufacturer(){
		try{
			System.out.println("Please input manufacturer ID:");
			String manufacturerID = in.nextLine();
			System.out.println("Please input product ID:");
			int productID = in.nextInt();
			in.nextLine();
			Item[] itemArray = warehouse.getProducts(productID, manufacturerID);
			System.out.println("There are " + itemArray.length + " items");
			for(Item item: itemArray){
				ItemImpl tmpItemImpl = new ItemImpl(item);
				System.out.println(tmpItemImpl.toString());
			}
		}catch(Exception e){
			System.out.println("Input a integer for product id.");
		}
	}

	private void testRegisteringRetailer(){
		System.out.println("Will register:" + name);
		if(warehouse.registerRetailer(name)){
			System.out.println("Registering is done Successfully.");
		}else{
			System.out.println("Failed to do registration.");
		}
	}

	private void testUnregisteringRetailer(){
		System.out.println("Will unregister:" + name);
		if(warehouse.unregisterRegailer(name)){
			System.out.println("UnRegistering is done Successfully.");
		}else{
			System.out.println("Failed to do unRegistration.");
		}
	}

	private void testShippingGoods(){
		Item []itemArray1 = warehouse.getProductsByID(-1);


		Item[] itemArray = warehouse.shippingGoods(itemArray1);
		System.out.println("There are " + itemArray.length + " items");
		for(Item item: itemArray){
			ItemImpl tmpItemImpl = new ItemImpl(item);
			System.out.println(tmpItemImpl.toString());
		}
	}

	private void testShuttingDownWarehouse(){
		warehouse.shutdown();
		System.out.println("warehouse is shutdown propperly.");
	}

	public static void main(String[] args) {
		WarehouseClient warehouseClient = new WarehouseClient();
		if(!warehouseClient.initializeOrbEnvirement(args)){
			return;
		}
		if(!warehouseClient.connectWarehouse()){
			return;
		}

		String cmd = new String(); 
		while(true){
			System.out.println("Please select the option:");
			System.out.println("\t1 for getting product by id.");
			System.out.println("\t2 for getting product by product type.");
			System.out.println("\t3 for getting product by manufacture id.");
			System.out.println("\t4 for getting product by product type per manufacturer id.");
			System.out.println("\t5 for registering retailer.");
			System.out.println("\t6 for unregistering retailer.");
			System.out.println("\t7 for shipping goods.");
			System.out.println("\t8 for shutting down warehouse.");

			System.out.println("\tq for QUIT.");

			cmd = warehouseClient.in.nextLine();
			if(cmd.equals("1")){
				warehouseClient.testGetProductsById();
			}else if(cmd.equals("2")){
				warehouseClient.testGetProductsByType();
			}else if(cmd.equals("3")){
				warehouseClient.testGetProductsByManufactureId();
			}else if(cmd.equals("4")){
				warehouseClient.testGetProductsPerManufacturer();
			}else if(cmd.equals("5")){
				warehouseClient.testRegisteringRetailer();
			}else if(cmd.equals("6")){
				warehouseClient.testUnregisteringRetailer();
			}else if(cmd.equals("7")){
				warehouseClient.testShippingGoods();
			}else if(cmd.equals("8")){
				warehouseClient.testShuttingDownWarehouse();
				break;
			}else if(cmd.equals("q")){
				break;
			}else{
				System.out.println("Wrong input. Please try again.");
			}
		}
	}

}


