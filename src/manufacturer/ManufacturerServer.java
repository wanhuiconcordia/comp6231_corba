package manufacturer;


import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import tools.LoggerClient;


public class ManufacturerServer {
	
	private ORB orb;
	private static LoggerClient loggerClient;

	public static void main(String[] args) {
		
		 try{
		      // create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
		      ORB orb = ORB.init(args, null);      
		      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		      rootpoa.the_POAManager().activate();
		 
		      loggerClient = new LoggerClient();
		      // create servant and register it with the ORB
		      ManufacturerServent servent1 = new ManufacturerServent(orb, "Manufacturer1",loggerClient);
		 
		      // get object reference from the servant
		      org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servent1);
		      Manufacturer href = ManufacturerHelper.narrow(ref);
		 
		      org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
		      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		 
		      NameComponent path[] = ncRef.to_name( "Manufacturer1" );
		      ncRef.rebind(path, href);
		 
		      System.out.println("Addition Server ready and waiting ...");
		 
		      for (;;){
		    	  // wait for invocations from clients
			  orb.run();
		      }
		    } 
		 
		      catch (Exception e) {
		        System.err.println("ERROR: " + e);
		        e.printStackTrace(System.out);
		      }
		 
		      System.out.println("ManufacturerServer Exiting ...");
		 
		  }

	}

