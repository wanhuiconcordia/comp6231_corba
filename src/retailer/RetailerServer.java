package retailer;
import java.util.ArrayList;
import java.util.Scanner;

import tools.*;
import warehouse.Warehouse;
import warehouse.WarehouseHelper;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;


class RetailerImpl extends RetailerPOA {
	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val; 
	}

	// implement shutdown() method
	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public Item[] getCatalog(int customerReferenceNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemShippingStatus[] submitOrder(int customerReferenceNumber,
			Item[] itemList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SignUpResult signUp(String name, String password, String street1,
			String street2, String city, String state, String zip,
			String country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer signIn(int customerReferenceNumber, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item[] getProducts(String productID) {
		// TODO Auto-generated method stub
		return null;
	}
}


public class RetailerServer {

	private ORB orb;
	private NamingContextExt namingContextRef;
	private Retailer retailerRef;

	private String name;
	private Scanner in;
	private LoggerClient loggerClient;
	private ArrayList<Warehouse> warehouseList;

	public RetailerServer(){
		name = new String();
		in = new Scanner(System.in);
		loggerClient = new LoggerClient();
		warehouseList = new ArrayList<Warehouse> ();
	}

	boolean initializeOrbEnvirement(String args[]){
		// create and initialize the ORB
		orb = ORB.init(args, null);
		// get reference to rootpoa and activate the POAManager
		POA rootpoa;
		try {
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			RetailerImpl retailerImpl = new RetailerImpl();
			retailerImpl.setORB(orb); 

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(retailerImpl);
			retailerRef = RetailerHelper.narrow(ref);
			return true;

		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdapterInactive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServantNotActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPolicy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;
	}

	public boolean registerRetailer(){

		try {
			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			//Use NamingContextExt which is part of the Interoperable. Naming Service (INS) specification.
			namingContextRef = NamingContextExtHelper.narrow(objRef);

			System.out.print("Input the name of current retailer:");
			name = in.next();
			NameComponent path[] = namingContextRef.to_name(name);
			namingContextRef.rebind(path, retailerRef);
			System.out.println(name + " is registered.");
			return true;
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void connectWarehouses(){
		String warehouseName;
		Warehouse warehouse; 
		while(true){
			System.out.print("Please input warehouse name to establish connection (q to finish):");
			warehouseName = in.next();
			if(warehouseName.equals("q")){
				break;
			}else{
				try {
					warehouse = WarehouseHelper.narrow(namingContextRef.resolve_str(warehouseName));
					System.out.println("Obtained a handle on server object: " + warehouseName);
					warehouseList.add(warehouse);
				} catch (NotFound | CannotProceed
						| org.omg.CosNaming.NamingContextPackage.InvalidName e) {
					System.out.println("Failed to resolve warehouse:" + warehouseName);
					//e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) {
		RetailerServer retailerServer = new RetailerServer();
		if(!retailerServer.initializeOrbEnvirement(args)){
			return;
		}

		if(!retailerServer.registerRetailer()){
			return;
		}

		retailerServer.connectWarehouses();

		System.out.println("RetailerServer ready and waiting ...");
		retailerServer.orb.run();

		System.out.println("HelloServer Exiting ...");
	}
}
