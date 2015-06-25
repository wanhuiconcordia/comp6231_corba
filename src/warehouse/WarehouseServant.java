package warehouse;

//import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import manufacturer.Manufacturer;
import manufacturer.ManufacturerHelper;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import tools.Item;
import tools.ItemImpl;
import tools.LoggerClient;
import tools.Product;

public class WarehouseServant extends WarehousePOA {

	private ORB orb;
	private InventoryManager inventoryManager;
	private String name;
	int minimumquantity=100;
	private HashMap<String,Manufacturer> manufactures;
	private ArrayList<String> retailerNameList;
	private LoggerClient loggerClient;
	Scanner in ;
	public WarehouseServant(ORB orb2,String name){
		this.orb=orb2;
		this.name=name;
		loggerClient=new LoggerClient();
		manufactures = new HashMap<String, Manufacturer>();
		retailerNameList=new ArrayList<String>();
		if(connect(name)){
			
			inventoryManager=new InventoryManager(name);
			for(Manufacturer manufact: manufactures.values()){
				
				Product[] productList = manufact.getProductList();
				
				if(productList == null){
					System.out.println("manufacturer return null");
				}else{
					for(Product product: productList){
						//System.out.println(" items "+product.manufacturerName);
						String key = product.manufacturerName + product.productType;
						Item inventoryItem = inventoryManager.inventoryItemMap.get(key);
						//ItemImpl itemnew=(ItemImpl) inventoryItem;
						//System.out.println(itemnew.toString());
						if(inventoryItem == null){
							System.out.println("added item");
							inventoryManager.inventoryItemMap.put(key, new ItemImpl(product.manufacturerName,product.productType,product.unitPrice, 0));
						}
					}
				}
				
				System.out.println("productList length:" + productList.length);
			}
			
			
			replenish();
		}else{
			inventoryManager=new InventoryManager(name);
		}

	}
	public void replenish(){
		
		for(Item item: inventoryManager.inventoryItemMap.values()){
		System.out.println("entere replenish");
			if(item.quantity < minimumquantity){
				System.out.println("products of :"+item.manufacturerName);
				Manufacturer manufacturer = manufactures.get(item.manufacturerName);
				
				if(manufacturer == null){
					System.out.println("failed to get manufacturers");
					loggerClient.write(name + ": Failed to get manufactorer from manufactures!");
				}else{
					Item orderItem = new ItemImpl(item.manufacturerName,item.productType,item.unitPrice,item.quantity);

					int oneTimeOrderCount = 40;
					orderItem.quantity=oneTimeOrderCount;
					String orderNum = manufacturer.processPurchaseOrder(orderItem);
					System.out.println("returned order item");
					if(orderNum == null){
						System.out.println("returned order item");
						loggerClient.write(name + ": manufacturer.processPurchaseOrder return null!");
					}else{
						if(manufacturer.receivePayment(orderNum, orderItem.unitPrice * orderItem.quantity)){
							item.quantity=item.quantity + oneTimeOrderCount;
							
							System.out.println("item added to inventory");
							inventoryManager.saveItems();
							
						}else{
							loggerClient.write(name + ": manufacturer.receivePayment return null!");
						}
					}

				}
			}

		}
	}
	public boolean connect(String name){
		String manufacturename;
		boolean connected=false;
		Manufacturer manufacturerinterf;
		org.omg.CORBA.Object objRef;
		in= new Scanner(System.in);
		try{
			objRef =  orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			while(true){			
				System.out.println("enter the name of the manufacturer to connect else enter exit");

				manufacturename=in.next();
				if(manufacturename.equals("exit"))
				{
					break;
				}
				else{
					try{
						manufacturerinterf	= ManufacturerHelper.narrow(ncRef.resolve_str(manufacturename));
						manufactures.put(manufacturename,manufacturerinterf);
						connected=true;

					}catch (Exception e){

						System.out.println("cannot connect to the manufacture try again");
//						 e.printStackTrace(System.out);

					}
				}
			}
		}catch(Exception e){

			System.out.println("connot connect to the server");

		}
		return connected;
	}

	//@Override

	private static  Item[] add(Item[] returnitem, Item inventoryItem) {
		// TODO Auto-generated method stub
		returnitem=Arrays.copyOf(returnitem,returnitem.length+1);
		returnitem[returnitem.length-1]=inventoryItem;
		return returnitem;
	}
	//@Override
	public Item[] getProductsByType(String productType) {
		// TODO Auto-generated method stub

		Item[] returnitem=new Item[0];
		for(Item inventoryitems:inventoryManager.inventoryItemMap.values()){

			if(inventoryitems.productType.equals(productType)){
				returnitem=add(returnitem,inventoryitems);
			}
		}
		System.out.println("return itemlist size"+returnitem.length);
		return returnitem;
	}

	//@Override
	public Item[] getProductsByRegisteredManufacturers(String manufacturerName) {
		// TODO Auto-generated method stub
		Item[] returnitem=new Item[0];

		if(manufactures.containsKey(manufacturerName)){

			for(Item inventoryitems:inventoryManager.inventoryItemMap.values()){

				if(inventoryitems.manufacturerName.equals(manufacturerName)){
					returnitem=add(returnitem,inventoryitems);
				}
			}

		}
		System.out.println("return itemlist size"+returnitem.length);
		return returnitem;
	}

	public synchronized Item[] shippingGoods(Item[] itemlist) {
		Item availableItems[]=new Item[0];
		for(Item item: itemlist){
			System.out.println("item sent is:"+item.productType +" with quantity :"+item.quantity);
			String key = item.manufacturerName+ item.productType;
			
			Item inventoryItem = inventoryManager.inventoryItemMap.get(key);
			System.out.println("item sent is:"+inventoryItem.productType +" with quantity :"+inventoryItem.quantity);
			if(inventoryItem != null){
				if(inventoryItem.quantity < item.quantity){
					availableItems=add(availableItems,new ItemImpl(inventoryItem.manufacturerName,inventoryItem.productType,inventoryItem.unitPrice,inventoryItem.quantity));
					inventoryItem.quantity=0;
				}else{
					availableItems=add(availableItems,new ItemImpl(item.manufacturerName,item.productType,item.unitPrice,item.quantity));
					inventoryItem.quantity=(inventoryItem.quantity - item.quantity);
				}
				inventoryManager.saveItems();
			}
		}
		replenish();

		String log = new String();
		for(Item item: availableItems){
			log = log + "Manufacturer name:" + item.manufacturerName
					+ ", Product type:" + item.productType 
					+ ", Unit price:" + item.unitPrice+ ",	quantity:" + item.quantity + "</br>";
		}
		if(log.length() > 0){
			loggerClient.write(name + ": available items:");
			loggerClient.write(log);
		}

		return availableItems;

	}
	//@Override
	public Item[] getProducts(String productID, String manufacturerName) {
		// TODO Auto-generated method stub

		Item[] returnitem=new Item[0];
		
		if(productID == null){
			for(Item i:inventoryManager.inventoryItemMap.values()){

				returnitem=add(returnitem,i);
			}
		}else if(productID.isEmpty()){
			for(Item i:inventoryManager.inventoryItemMap.values()){

				returnitem=add(returnitem,i);
			}
		}else{
			for(String inventorykey:inventoryManager.inventoryItemMap.keySet()){
				int hashofkey=inventorykey.hashCode();
				System.out.println("hashcode"+hashofkey+"key"+ inventorykey);
				if(productID.equals(String.valueOf(hashofkey))){
					Item inventoryItem = inventoryManager.inventoryItemMap.get(inventorykey);
					if(inventoryItem != null){
						returnitem=add(returnitem,inventoryItem);
					}
				}
			}
		}

		System.out.println("return itemlist size"+returnitem.length);
		return returnitem;
	}

	//@Override
	public boolean registerRetailer(String retailerName) {
		//System.out.println("enter to register the clinet");
		// TODO Auto-generated method stub
		if(retailerName.isEmpty()){

			return false;

		}
		else{
			if(!retailerNameList.contains(retailerName)){
				retailerNameList.add(retailerName);
			}
			return true;
		}
	}


	//@Override
	public boolean unregisterRegailer(String regailerName) {
		// TODO Auto-generated method stub
		if(regailerName.isEmpty()){
			return false;
		}else{
			if(retailerNameList.contains(regailerName)){
				retailerNameList.remove(regailerName);
				return true;
			}else{

				return false;
			}
		}
	}


	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		try{
			orb.shutdown(false);
		}catch(Exception e){
			System.out.println(name + " is shut down propperly.");
		}
	}
	@Override

	public Item[] getProductsByID(String productID) {
		// TODO Auto-generated method stub
		System.out.println("getProductsByID is called:" + productID);
		Item[] returnitem=new Item[0];
		
		if(productID == null){
			for(Item i:inventoryManager.inventoryItemMap.values()){

				returnitem=add(returnitem,i);
			}
		}else if(productID.isEmpty()){
			for(Item i:inventoryManager.inventoryItemMap.values()){

				returnitem=add(returnitem,i);
			}
		}else{
			for(String inventorykey:inventoryManager.inventoryItemMap.keySet()){
				int hashofkey=inventorykey.hashCode();
				System.out.println("hashcode"+hashofkey+"key"+ inventorykey);
				if(productID.equals(String.valueOf(hashofkey))){
					Item inventoryItem = inventoryManager.inventoryItemMap.get(inventorykey);
					
					if(inventoryItem != null){

						returnitem=add(returnitem,inventoryItem);

					}
				}
			}
		}
		System.out.println("return itemlist size"+returnitem.length);
		return returnitem;
	}
}
