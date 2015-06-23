package warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import manufacturer.Manufacturer;
import manufacturer.ManufacturerHelper;
import manufacturer.ManufacturerPOA;
import manufacturer.ManufacturerServent;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import retailer.Retailer;
import tools.Item;

public class WareHouseServent extends WarehousePOA {
	
	private ORB orb;
	private InventoryManager inventoryManager;
	private String name;
	private HashMap<String,Manufacturer> manufactures;
	private ArrayList<String> retailers;
	
	public WareHouseServent(ORB orb2,String name,  HashMap<String,Manufacturer> manufactures){
	
		this.orb=orb2;
		this.name=name;
		this.manufactures=new   HashMap<String,Manufacturer>(manufactures);
		inventoryManager=new InventoryManager(name);
		
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
	public Item[] getProductsByID(String productID) {
		// TODO Auto-generated method stub
		Item[] returnitem=null;
		if(!(productID.isEmpty())){
			
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

	//@Override
	public Item[] getProducts(String productID, String manufacturerName) {
		// TODO Auto-generated method stub
		
		Item[] returnitem=null;
		if(!(productID.isEmpty())){
			
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
			
			retailers.add(retailerName);
			return true;
		
		}
	}

	//@Override
	public boolean unregisterRegailer(String regailerName) {
		// TODO Auto-generated method stub
		if(regailerName.isEmpty()){
			
			return false;
		
		}else{
			if(retailers.contains(regailerName)){

				retailers.remove(regailerName);
				return true;
				
			}else{
				
				return false;
			}

			
		}
	
	}
	@Override
	public Item[] shippingGoods(Item[] itemList) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
