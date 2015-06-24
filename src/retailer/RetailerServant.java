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
						warehouse = WarehouseHelper.narrow(namingContextRef.resolve_str(warehouseName));
						System.out.println("Obtained a handle on server object: " + warehouseName);
						warehouseList.add(warehouse);
				}
			}

		} catch (InvalidName e1) {
			e1.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public Item[] getCatalog(int customerReferenceNumber) {
		ArrayList<Item> allItems = new ArrayList<Item>();
		HashMap<Integer, Item> itemsMap = new HashMap<Integer, Item>();
		
		Customer currentCustomer = customerManager.getCustomerByReferenceNumber(customerReferenceNumber);
		if(currentCustomer == null){
			loggerClient.write(name + ": customer reference number can not be found in customer database.");
			return null;
		}
		for(int i = 0; i < warehouseList.size(); i++){
			Item[] itemListFromWarehouse = warehouseList.get(i).getProducts(0, "");
			for(Item item: itemListFromWarehouse){
				int key = item.productID;
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
		

	@Override
	public ItemShippingStatus[] submitOrder(int customerReferenceNumber, Item[] itemList) {
		List<Item> orderItemList = Arrays.asList(itemList);
		Customer currentCustomer = customerManager.getCustomerByReferenceNumber(customerReferenceNumber);
		if(currentCustomer == null){
			loggerClient.write(name + ": customer reference number can not be found in customer database.");
			return null;
		}else if(orderItemList == null){
			loggerClient.write(name + ": null order list.");
			return null;
		}else if((orderItemList.size()) == 0){
			loggerClient.write(name + ": empty order list.");
			return null;
		}else{
			HashMap<Integer, ItemShippingStatus> itemShippingStatusMap = new HashMap<Integer, ItemShippingStatus>(); 
			int []randomOrder = getRandOrder(warehouseList.size());
			Item[] itemsGotFromCurrentWarehouse;
			for(int currentWarehouseIndex: randomOrder){
				itemsGotFromCurrentWarehouse = warehouseList.get(currentWarehouseIndex).shippingGoods(itemList);
				
				if(itemsGotFromCurrentWarehouse != null){
					loggerClient.write(name + ": Items got from " + warehouseList.get(currentWarehouseIndex).getName());
					String log = new String();
					for(Item item: itemsGotFromCurrentWarehouse){
						//System.out.println(item.toString());
						log = log + item.toString() + "</br>";
					}
					
					if(log.length() > 0){
						loggerClient.write(log);
					}
					
					for(Item item: itemsGotFromCurrentWarehouse){
						int key = item.productID;
						ItemShippingStatus itemShippingStatus = itemShippingStatusMap.get(key);
						if(itemShippingStatus == null){
							itemShippingStatusMap.put(key, new ItemShippingStatusImpl(item, true));
						}else{
							itemShippingStatus.quantity = itemShippingStatus.quantity + item.quantity;
						}
					}
					

					for (int i = 0; i < orderItemList.size();) {
						Item item = orderItemList.get(i);
						for(Item item_t: itemsGotFromCurrentWarehouse){
							if(item.manufacturerName.equals(item_t.manufacturerName)
								&&(item.productType.equals(item_t.productType))
								&&(item.unitPrice == item_t.unitPrice)){
								
									item.quantity = item.quantity - item_t.quantity;
							}
						}
						if(item.quantity == 0){
							orderItemList.remove(i);
						}else{
							i++;
						}
					}
				}					

				//if no more item in orderItemList break;
				if(orderItemList.isEmpty()){
					break;
				}
			}
			//if there are still some items in orderItemList, put them in to inhandItemShippingStatusList and mark their shippingStatus as false

			ArrayList<ItemShippingStatus> itemShippingStatusList = new ArrayList<ItemShippingStatus>();
			for(ItemShippingStatus itemShippingStatus: itemShippingStatusMap.values()){
				itemShippingStatusList.add(itemShippingStatus);
			}
			
			if(!orderItemList.isEmpty()){
				for(Item item: orderItemList){
					itemShippingStatusList.add(new ItemShippingStatusImpl(item, false));
				}
			}
			
			String log = new String();
			for(ItemShippingStatus itemShippingStatus: itemShippingStatusList){
				log = log + itemShippingStatus.toString() + "</br>";
			}
			if(log.length() > 0){
				loggerClient.write(name + ": item shipping status will be sent to client.");
				loggerClient.write(log);
			}
			return (ItemShippingStatus[]) itemShippingStatusList.toArray();
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
	public Item[] getProducts(int productID) {
		ArrayList<Item> allItems = new ArrayList<Item>();
		HashMap<Integer, Item> itemsMap = new HashMap<Integer, Item>();

		for(int i = 0; i < warehouseList.size(); i++){
			Item[] itemListFromWarehouse = warehouseList.get(i).getProductsByID(productID);
			for(Item item: itemListFromWarehouse){
				int key = item.productID;
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