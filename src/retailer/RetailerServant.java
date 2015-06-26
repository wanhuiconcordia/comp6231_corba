package retailer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import tools.Customer;
import tools.Item;
import tools.ItemImpl;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusImpl;
import tools.LoggerClient;
import tools.SignUpResult;
import warehouse.Warehouse;
import warehouse.WarehouseHelper;

class RetailerServant extends RetailerPOA {
	private ORB orb;
	private CustomerManager customerManager;
	private LoggerClient loggerClient;
	private ArrayList<Warehouse> warehouseList;
	private String name;
	
	public RetailerServant(ORB orb, LoggerClient loggerClient){
		name = "RetailerServant";
		this.orb = orb;
		warehouseList = new ArrayList<Warehouse>();
		this.loggerClient = loggerClient;
		customerManager = new CustomerManager("customers.xml");
	}

	public void connectWarehouses(Scanner in){
		
		try {
			String warehouseName;
			Warehouse warehouse; 
			org.omg.CORBA.Object objRef;
			objRef = orb.resolve_initial_references("NameService");
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(objRef);
			while(true){
				System.out.print("Please input warehouse name to establish connection (q to finish):");
				warehouseName = in.next();
				if(warehouseName.equals("q")){
					break;
				}else{
					try{
						warehouse = WarehouseHelper.narrow(namingContextRef.resolve_str(warehouseName));
						System.out.println("Obtained a handle on server object: " + warehouseName);
						warehouseList.add(warehouse);
					}catch (NotFound e) {
						System.out.println(warehouseName + " cannot be found!");
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (CannotProceed e) {
						System.out.println(warehouseName + " cannot be resolved!");
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
						System.out.println(warehouseName + " cannot be resolved!");
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}

		} catch (InvalidName e1) {
			e1.printStackTrace();
		} 
	}
	
	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public Item[] getCatalog(int customerReferenceNumber) {
		HashMap<String, Item> itemsMap = new HashMap<String, Item>();
		Customer currentCustomer = customerManager.getCustomerByReferenceNumber(customerReferenceNumber);
		if(currentCustomer == null){
			loggerClient.write(name + ": customer reference number can not be found in customer database.");
			return null;
		}
		for(int i = 0; i < warehouseList.size(); i++){
			Item[] itemListFromWarehouse = warehouseList.get(i).getProductsByID("");
			System.out.println("itemListFromWarehouse.length:" + itemListFromWarehouse.length);
			for(Item item: itemListFromWarehouse){
				String key = item.productID;
				Item itemInMap = itemsMap.get(key); 
				if(itemInMap == null){
					itemsMap.put(key, item);// item.clone() changes to item ?
				}else{
					itemInMap.quantity = itemInMap.quantity + item.quantity;
				}
			}
		}
		
		Item[]itemArray = new Item[itemsMap.size()];
		int i = 0;
		for(Item item: itemsMap.values()){
			itemArray[i++] = item;
		}
		return itemArray;
	}
		

	@Override
	public ItemShippingStatus[] submitOrder(int customerReferenceNumber, Item[] itemOrderArray) {
		System.out.println("submitOrder is called");
		ItemShippingStatusImpl []itemShippingStatusArray = new ItemShippingStatusImpl[0];
		Customer currentCustomer = customerManager.getCustomerByReferenceNumber(customerReferenceNumber);
		if(currentCustomer == null){
			System.out.println("test1");
			loggerClient.write(name + ": customer reference number can not be found in customer database.");
			return itemShippingStatusArray;
		}
		
		if(itemOrderArray == null){
			System.out.println("test2");
			loggerClient.write(name + ": null order list.");
			return itemShippingStatusArray;
		}else if(itemOrderArray.length == 0){
			System.out.println("test3");
			loggerClient.write(name + ": empty order list.");
			return itemShippingStatusArray;
		}else{
			System.out.println("test4 itemOrderArray.size:" + itemOrderArray.length);
			HashMap<String, ItemShippingStatus> receivedItemShippingStatusMap = new HashMap<String, ItemShippingStatus>();
			HashMap<String, Item> orderMap = new HashMap<String, Item>();
			for(Item item: itemOrderArray){
				ItemImpl itemImpl = new ItemImpl(item);
				System.out.println(itemImpl.toString());
				if(item.quantity > 0){
					Item itemInOrderMap = orderMap.get(item.productID);
					if(itemInOrderMap == null){
						orderMap.put(item.productID, new ItemImpl(item));
					}else{
						itemInOrderMap.quantity += item.quantity;
					}
				}
			}
			
			for(Warehouse thisWarehouse: warehouseList){
				System.out.println("test5");
				int itemRequestFromWarehouseCount = orderMap.size();
				if(itemRequestFromWarehouseCount > 0)
				{
					System.out.println("test6");
					Item[] itemRequestFromWarehouseArray = new Item[itemRequestFromWarehouseCount];
					int i = 0;
					for(Item orderItem: orderMap.values()){
						itemRequestFromWarehouseArray[i] = orderItem;
					}
					Item [] itemsGotFromCurrentWarehouse = thisWarehouse.shippingGoods(itemRequestFromWarehouseArray);
					if(itemsGotFromCurrentWarehouse == null){
						System.out.println("warehouse return null");
					}else if(itemsGotFromCurrentWarehouse.length == 0){
						System.out.println("warehouse return empty arrry");
					}else{
						System.out.println("warehouse return :" + itemsGotFromCurrentWarehouse.length);
						String log = new String();
						for(Item item: itemsGotFromCurrentWarehouse){
							Item itemInReceivedItemShippingStatusMap = receivedItemShippingStatusMap.get(item.productID);
							if(itemInReceivedItemShippingStatusMap == null){
								receivedItemShippingStatusMap.put(item.productID, new ItemShippingStatusImpl(item, true));
							}else{
								itemInReceivedItemShippingStatusMap.quantity += item.quantity;
							}

							Item itemInOrderMap = orderMap.get(item.productID);
							if(itemInOrderMap == null){
								System.out.println("Warehouse side error. never request this item from warehouse, but the warehouse return this item.");
							}else{
								itemInOrderMap.quantity -= item.quantity;
								if(itemInOrderMap.quantity == 0){
									orderMap.remove(item.productID);
								}
							}
						}
					}
				}else{
					break;
				}
			}
			itemShippingStatusArray = new ItemShippingStatusImpl[receivedItemShippingStatusMap.size() + orderMap.size()];
			int i = 0;
			for(ItemShippingStatus itemInReceivedItemShippingStatusMap: receivedItemShippingStatusMap.values()){
				itemShippingStatusArray[i] = new ItemShippingStatusImpl(itemInReceivedItemShippingStatusMap); 
				i++;
			}
			for(Item itemInOrderMap: orderMap.values()){
				itemShippingStatusArray[i] = new ItemShippingStatusImpl(itemInOrderMap, false); 
				i++;
			}
			
			for(ItemShippingStatusImpl t:itemShippingStatusArray){
				System.out.println(t.toString());
			}
			
			
			return itemShippingStatusArray;
		}
	}

	@Override
	public SignUpResult signUp(String name, String password, String street1,
			String street2, String city, String state, String zip,
			String country) {
		return customerManager.register(name, password, street1, street2, city, state, zip, country);
	}

	@Override
	public Customer signIn(int customerReferenceNumber, String password) {
		return customerManager.find(customerReferenceNumber, password);
	}

	@Override
	public Item[] getProducts(String productID) {
		ArrayList<Item> allItems = new ArrayList<Item>();
		HashMap<String, Item> itemsMap = new HashMap<String, Item>();

		for(int i = 0; i < warehouseList.size(); i++){
			Item[] itemListFromWarehouse = warehouseList.get(i).getProductsByID(productID);
			for(Item item: itemListFromWarehouse){
				String key = item.productID;
				Item itemInMap = itemsMap.get(key); 
				if(itemInMap == null){
					itemsMap.put(key, item);// item.clone() changes to item ?
				}else{
					itemInMap.quantity = itemInMap.quantity + item.quantity;
				}
			}
		}

		for(Item item: itemsMap.values()){
			allItems.add(item);
		}
		return (Item[]) allItems.toArray();
	}
	/**
	 * get a random order for number between 1 to count
	 * @param count
	 * @return int[] which stores the randomized number from 1 to count
	 */
	private int[] getRandOrder(int count){
		int[][] orderContainer = new int[count][2];
		Random randomGenerator = new Random();
		for(int i = 0; i < count; i++){
			orderContainer[i][0] = i;
			orderContainer[i][1] = randomGenerator.nextInt(count * 2);
		}


		for(int i = 0; i < count - 1; i++){
			for(int j = i + 1; j < count; j++){
				if(orderContainer[i][1] > orderContainer[j][1]){
					int tmp = orderContainer[i][1];
					orderContainer[i][1] = orderContainer[j][1];
					orderContainer[j][1] = tmp;

					tmp = orderContainer[i][0];
					orderContainer[i][0] = orderContainer[j][0];
					orderContainer[j][0] = tmp;
				}
			}
		}
		int []order = new int[count];
		for(int i = 0; i < count; i++){
			order[i] = orderContainer[i][0];
		}
		return order;
	}
	
}