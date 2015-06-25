package manufacturer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.dom4j.Element;
import org.omg.CORBA.ORB;

import tools.Item;
import tools.ItemImpl;
import tools.LoggerClient;
import tools.Product;
import tools.ProductImpl;
import tools.PurchaseOrderManager;
import tools.XmlFileController;

public class ManufacturerServant extends ManufacturerPOA{

	private static final long serialVersionUID = 1L;
	private String name;
	private HashMap<String, ItemImpl> purchaseOrderMap;
	private int orderNum;
	private LoggerClient loggerClient;
	private PurchaseOrderManager purchaseOrderManager;
	private ORB Orb;
	private ItemImpl impl;
	Random random ; 


	public ManufacturerServant(ORB orb, String name,LoggerClient loggerClient) {
		this.Orb = orb;
		this.name = name;
		this.loggerClient = loggerClient;
		purchaseOrderMap = new HashMap<String, ItemImpl>();
		orderNum = 1000;
		purchaseOrderManager = new PurchaseOrderManager(name);
		random = new Random();
		setProduct(random);
		System.out.println("ManufacturerImplementation constructed:" + name);
	}

	public String processPurchaseOrder(Item item) {

		impl = new ItemImpl(item);
		if(!impl.manufacturerName.equals(name)){
			System.out.println(name + ": Manufacturer name is not equal to current manufacturer name:" + impl.manufacturerName);
			loggerClient.write(name + ": Manufacturer name is not equal to current manufacturer name:" + impl.manufacturerName);
			return null;
		}		
		ItemImpl availableItem = purchaseOrderManager.itemsMap.get(impl.productType);
		if(availableItem == null){
			loggerClient.write(name + ": " + impl.productType + " is not supported!");
			return null;
		}else{
			if(impl.unitPrice < availableItem.unitPrice){
				loggerClient.write(name + ": The order price (" + impl.unitPrice + ") is lower than defined price(" + availableItem.unitPrice + ")");
				return null;
			}else{
				if(impl.getQuantity() >= availableItem.getQuantity()){
					int oneTimeQuantity = 100;
					if(produce(impl.productType, oneTimeQuantity)){
						availableItem.setQuantity(availableItem.getQuantity() + oneTimeQuantity);

						purchaseOrderManager.saveItems();

						loggerClient.write(name + ": Produced " + oneTimeQuantity + " " + impl.productType);
					}else{
						loggerClient.write(name + ": Failed to produce:" + oneTimeQuantity);
						return null;
					}
				}

				if(impl.getQuantity() >= availableItem.getQuantity()){
					return null;
				}else{
					String orderNumString = new Integer(orderNum++).toString();
					purchaseOrderMap.put(orderNumString, impl);
					loggerClient.write(name + ": Send order number (" + orderNumString + ") to warehouse.");
					return orderNumString;
				}
			}
		}
	}

	/**
	 * Simulate real produce.
	 * @param productName
	 * @param quantity
	 * @return
	 */
	private boolean produce(String productName, int quantity){
		return true;
	}


	@Override
	public Product getProductInfo(String productType) {
		ItemImpl avaiableItem = purchaseOrderManager.itemsMap.get(productType);
		if(avaiableItem == null){
			loggerClient.write(name + ": " + productType + " does not exist in this manufacturer!");
			return null;
		}else{
			return new ProductImpl(avaiableItem.manufacturerName,avaiableItem.productType, avaiableItem.unitPrice);
		}
	}


	@Override
	public boolean receivePayment(String orderNum, float totalPrice) {
		ItemImpl waitingForPayItem = purchaseOrderMap.get(orderNum);
		if(waitingForPayItem == null){
			loggerClient.write(name + ": " + orderNum + " does not exist in purchaseOrderMap of current manufacturer!");
			return false;
		}else{
			if(waitingForPayItem.getQuantity() * waitingForPayItem.unitPrice == totalPrice){
				ItemImpl inhandItem = purchaseOrderManager.itemsMap.get(waitingForPayItem.productType);
				inhandItem.setQuantity(inhandItem.getQuantity() - waitingForPayItem.getQuantity());
				purchaseOrderManager.saveItems();
				loggerClient.write(name + ": received pament. OrderNum:" + orderNum + ", totalPrice:" + totalPrice);
				purchaseOrderMap.remove(orderNum);
				return true;
			}else{
				loggerClient.write(name + ": the total price does not match for this order number: " + orderNum);
				return false;
			}
		}
	}

	/**
	 * @return current manufacturer's name
	 */
	public String getName(){
		return name;
	}


	@Override
	public Product[] getProductList() {
		ArrayList<Product> productList = new ArrayList<Product>();
		for(ItemImpl item: purchaseOrderManager.itemsMap.values()){
			productList.add(item.cloneProduct());
		}

		Product[] proArr = productList.toArray(new Product[productList.size()]);
		return proArr;
	}

	/**
	 * Read product information from configure(xml) file and put them into a items map 
	 * @param randm 
	 */
	private void setProduct(Random randm){
		XmlFileController productInfofile = new XmlFileController(name + ".xml");
		Element root = productInfofile.Read();
		if(root == null){
			XmlFileController xmlfile = new XmlFileController("settings/product_info.xml");
			Element root2 = xmlfile.Read();
			
			if(root2 == null){
				System.out.println("Failed to read settings/product_info.xml");
			}else{
				List<Element> nodes = root2.elements("product");
				for(Element subElem: nodes){
					String manufacturerName = name;
					String productType = subElem.element("productType").getText();
					float unitPrice = randm.nextInt(300 - 200 + 1) + 200;
					int quantity = randm.nextInt(150 - 10 + 1) + 10;
					ItemImpl itemImpl = new ItemImpl(manufacturerName, productType, unitPrice, quantity);
					System.out.println(itemImpl.toString() + " is added from : product_info.xml");
					if(purchaseOrderManager.itemsMap.get(productType) == null){
						purchaseOrderManager.itemsMap.put(productType, itemImpl);
					}
				}
				purchaseOrderManager.saveItems();
			}
		}else{
			List<Element> nodes = root.elements("product");
			boolean newProductAdded = false;
			for(Element subElem: nodes){
				String manufacturerName = name;
				String productType = subElem.element("productType").getText();
				float unitPrice = randm.nextInt(300 - 200 + 1) + 200;
				if(purchaseOrderManager.itemsMap.get(productType) == null){
					purchaseOrderManager.itemsMap.put(productType, new ItemImpl(manufacturerName, productType, unitPrice, randm.nextInt(150 - 10 + 1) + 10));
					newProductAdded = true;
				}
			}
			if(newProductAdded){
				purchaseOrderManager.saveItems();
			}
		}

	}

	@Override
	public void shutdown() {
		Orb.shutdown(false);

	}

}
