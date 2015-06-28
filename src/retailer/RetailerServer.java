package retailer;
import java.util.Scanner;

import tools.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * @author comp6231.team5
 * Provide services for client
 * Ask for items and get item information from warehouses
 */
public class RetailerServer {

	private ORB orb;
	private RetailerServant retailerServant;

	private String name;
	private Scanner in;
	private LoggerClient loggerClient;

	/**
	 * Constructor
	 */
	public RetailerServer(){
		name = new String();
		in = new Scanner(System.in);
		loggerClient = new LoggerClient();
	}

	/**
	 * @param args
	 * initialize orb;
	 * resolve name service
	 * provide interface for user to input retailer's name
	 * @return
	 */
	boolean initializeOrbEnvirement(String args[]){
		// create and initialize the ORB
		orb = ORB.init(args, null);
		// get reference to rootpoa and activate the POAManager
		POA rootpoa;
		try {
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			retailerServant = new RetailerServant(orb,loggerClient);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(retailerServant);
			Retailer retailerRef = RetailerHelper.narrow(ref);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			//Use NamingContextExt which is part of the Interoperable. Naming Service (INS) specification.
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(objRef);

			System.out.print("Input the name of current retailer:");
			name = in.next();
			NameComponent path[] = namingContextRef.to_name(name);
			namingContextRef.rebind(path, retailerRef);
			System.out.println(name + " is registered.");
			
			
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


	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[]) {
		RetailerServer retailerServer = new RetailerServer();
		if(!retailerServer.initializeOrbEnvirement(args)){
			return;
		}

		retailerServer.retailerServant.connectWarehouses(retailerServer.in);

		System.out.println("RetailerServer ready and waiting ...");
		retailerServer.orb.run();

		System.out.println("HelloServer Exiting ...");
	}
}
