package warehouse;

//import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	
	public WarehouseServant(ORB orb2,String name,  HashMap<String,Manufacturer> manufacturerlist){
	
		this.orb=orb2;
		this.name=name;
		this.manufactures=new   HashMap<String,Manufacturer>(manufacturerlist);
		inventoryManager=new InventoryManager(name);
		for(Manufacturer manufact: manufactures.values()){
			Product[] productList = manufact.getProductList();
			if(productList != null){
				for(Product product: productList){
					String key = product.manufacturerName + product.productType;
					Item inventoryItem = inventoryManager.inventoryItemMap.get(key);
					if(inventoryItem == null){
						inventoryManager.inventoryItemMap.put(key, new ItemImpl(product.manufacturerName,product.productType,product.unitPrice, 0));
					}
				}
			}
		}
		replenish();
		
	}
	public void replenish(){
		for(Item item: inventoryManager.inventoryItemMap.values()){
			if(item.quantity < minimumquantity){
				Manufacturer manufacturer = manufactures.get(item.manufacturerName);
				
				if(manufacturer == null){
					loggerClient.write(name + ": Failed to get manufactorer from manufactures!");
				}else{
					Item orderItem = new ItemImpl(item);
					
					int oneTimeOrderCount = 40;
					orderItem.quantity=oneTimeOrderCount;
					String orderNum = manufacturer.processPurchaseOrder(orderItem);
					if(orderNum == null){
						loggerClient.write(name + ": manufacturer.processPurchaseOrder return null!");
					}else{
						if(manufacturer.receivePayment(orderNum, orderItem.unitPrice * orderItem.quantity)){
							item.quantity=item.quantity + oneTimeOrderCount;
							inventoryManager.saveItems();
						}else{
							loggerClient.write(name + ": manufacturer.receivePayment return null!");
						}
					}

				}
			}

		}
	}
	public void connect(Manufacturer manufactureimpl,String name){
		try{
			
			org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			manufactureimpl	= ManufacturerHelper.narrow(ncRef.resolve_str(name));
			
		}catch (Exception e){
			
			System.out.println("Error:"+ e);
			 e.printStackTrace(System.out);
			
		}
		
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
		
		Item[] returnitem=null;
		for(Item inventoryitems:inventoryManager.inventoryItemMap.values()){
			
			if(inventoryitems.productType.equals(productType)){
				returnitem=add(returnitem,inventoryitems);
			}
		}
		return returnitem;
	}

	//@Override
	public Item[] getProductsByRegisteredManufacturers(String manufacturerName) {
		// TODO Auto-generated method stub
		Item[] returnitem=null;
		
			if(manufactures.containsKey(manufacturerName)){
			
				for(Item inventoryitems:inventoryManager.inventoryItemMap.values()){
			
					if(inventoryitems.manufacturerName.equals(manufacturerName)){
						returnitem=add(returnitem,inventoryitems);
					}
				}
			
		}
		return returnitem;
	}

	public synchronized Item[] shippingGoods(Item[] itemlist) {
		Item availableItems[]=null;
		for(Item item: itemlist){
			String key = item.manufacturerName+ item.productType;
			Item inventoryItem = inventoryManager.inventoryItemMap.get(key);
			if(inventoryItem != null){
				if(inventoryItem.quantity < item.quantity){
					availableItems=add(availableItems,new ItemImpl(inventoryItem));
					inventoryItem.quantity=0;
				}else{
					availableItems=add(availableItems,new ItemImpl(item));
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
	public Item[] getProducts(int productID, String manufacturerName) {
		// TODO Auto-generated method stub
		
		Item[] returnitem=null;
		if((productID!=0)){
			
			Item inventoryItem = inventoryManager.inventoryItemMap.get(productID);
			if(inventoryItem != null){
				
				returnitem=add(returnitem,inventoryItem);
				
			}
			
		}
		else{
			
			for(Item i:inventoryManager.inventoryItemMap.values()){
				
				returnitem=add(returnitem,i);
			}
			
		}
		return returnitem;
	}

	//@Override
	public boolean registerRetailer(String retailerName) {
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
		orb.shutdown(false);
	}
	@Override
	
	public Item[] getProductsByID(int productID) {
		// TODO Auto-generated method stub
		Item[] returnitem=null;
		if((productID)!=0){
			
			Item inventoryItem = inventoryManager.inventoryItemMap.get(productID);
			if(inventoryItem != null){
				
				returnitem=add(returnitem,inventoryItem);
				
			}
			
		}
		else{
			
			for(Item i:inventoryManager.inventoryItemMap.values()){
				
				returnitem=add(returnitem,i);
			}
			
		}
		return returnitem;
	}

	
}
