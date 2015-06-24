package warehouse;
import java.util.HashMap;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import manufacturer.*;

public class WarehouseServer {
	String name;
	private HashMap<String,Manufacturer> manufactures;
	InventoryManager inventorymanager;
	public WarehouseServer(){
		
	}
	
	public void main(String []args){
		int length=args.length;
		name=args[length-1];
		Scanner in=new Scanner(System.in);
		System.out.println("Enter number of Manufacturers to register");
		int count=in.nextInt();
		for(int i=1;i<=count;i++){
			
			System.out.println("Enter name of Manufacturer"+i);
			String manufacturename=in.next();
			Manufacturer manufact = null;
			manufactures.put(manufacturename,manufact);
		}
		
		try{
			
			ORB orb=ORB.init(args,null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			WarehouseServant warehouseimpl=new WarehouseServant(orb, name, manufactures);
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(warehouseimpl);
			Warehouse wref=WarehouseHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String wname = name;
		    NameComponent path[] = ncRef.to_name( wname );
		    ncRef.rebind(path, wref);
			System.out.println("warehouse server started and waiting....");
			orb.run();
	     
		}catch(Exception e){
			
			System.out.println("Error:"+ e);
			 e.printStackTrace(System.out);
			
		}
		System.out.println("warehouse server exiting....");
	}

}
