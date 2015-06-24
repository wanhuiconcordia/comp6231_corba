package manufacturer;


import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import manufacturer.ManufacturerServant;
import tools.LoggerClient;


public class ManufacturerServer {
	
	private ORB orb;
	private ManufacturerServant manufacturerServant;

	private String name;
	private Scanner in;
	private LoggerClient loggerClient;
	
	public ManufacturerServer(){
		name = new String();
		in = new Scanner(System.in);
		loggerClient = new LoggerClient();		
	}
   
	
	public static void main(String args[]) {
		
		ManufacturerServer manufacturerServer = new ManufacturerServer();
		if(!manufacturerServer.initializeOrbEnvirement(args)){
			return;
		}

		System.out.println("Manufacturer Server ready and waiting ...");
		manufacturerServer.orb.run();

		System.out.println("HelloServer Exiting ...");
	}
	
	
	boolean initializeOrbEnvirement(String args[]){
		// create and initialize the ORB
		orb = ORB.init(args, null);
		// get reference to rootpoa and activate the POAManager
		POA rootpoa;
		try {
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			System.out.print("Input the name of current manufacturer:");
			name = in.next();

			// create servant and register it with the ORB
			manufacturerServant = new ManufacturerServant(orb,name,loggerClient);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(manufacturerServant);
			Manufacturer manufacturerRef = ManufacturerHelper.narrow(ref);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			//Use NamingContextExt which is part of the Interoperable. Naming Service (INS) specification.
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(objRef);

			
			NameComponent path[] = namingContextRef.to_name(name);
			namingContextRef.rebind(path, manufacturerRef);
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
	}

