package warehouse;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 * @author comp6231.team5
 * The bridge between retailers and manufacturers
 * Provide services for retailers
 * Ask for items and get item information from manufacturers
 */
public class WarehouseServer {
	private String name;
	private WarehouseServant warehouseServant;
	
	/**
	 * Constructor
	 */
	public WarehouseServer(){
		Scanner in=new Scanner(System.in);
		System.out.println("Enter name of the warehouse");
		name=in.next();
	}
	
	/**
	 * @param args
	 * initialize orb;
	 * resolve name service
	 * provide interface for user to input manufacturers' names
	 * @return
	 */
	
	public void bind(String [] args){
		try{

			ORB orb=ORB.init(args,null);

			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			warehouseServant=new WarehouseServant(orb, name);
			System.out.println("after create warehouse");
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(warehouseServant);
			Warehouse wref=WarehouseHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String wname = name;
			NameComponent path[] = ncRef.to_name( wname );
			ncRef.rebind(path, wref);
			System.out.println("warehouse server started and waiting....");
			orb.run();

		}catch(Exception e){

			System.out.println("connot bond warehouseservent to warehouseserver");
			exit(1);
			// e.printStackTrace(System.out);

		}
	}

	private void exit(int i) {
		// TODO Auto-generated method stub

	}
	public static void main(String []args){


		WarehouseServer warehouse=new  WarehouseServer();

		warehouse.bind(args);

		System.out.println("warehouse server exiting....");
	}

}
