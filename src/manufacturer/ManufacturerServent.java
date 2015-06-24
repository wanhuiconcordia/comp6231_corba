package manufacturer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.omg.CORBA.ORB;

import tools.Item;
import tools.ItemImpl;
import tools.LoggerClient;
import tools.Product;
import tools.ProductImpl;
import tools.PurchaseOrderManager;
import tools.XmlFileController;

public class ManufacturerServent extends ManufacturerPOA{

	private static final long serialVersionUID = 1L;
	private String name;
	private HashMap<String, ItemImpl> purchaseOrderMap;
	private int orderNum;
	private LoggerClient loggerClient;
	private PurchaseOrderManager purchaseOrderManager;
	private ORB Orb;
	private ItemImpl impl;
	
	
	public ManufacturerServent(ORB orb, String name,LoggerClient loggerClient) {
		this.Orb = orb;
		this.name = name;
		this.loggerClient = loggerClient;
		purchaseOrderMap = new HashMap<String, ItemImpl>();
		orderNum = 1000;
		purchaseOrderManager = new PurchaseOrderManager(name);
		setProduct();
		System.out.println("ManufacturerImplementation constructed:" + name);
	}

	public String processPurchaseOrder(Item item) {
		
		impl = new ItemImpl(item);
		if(!impl.getManufacturerName().equals(name)){
			System.out.println(name + ": Manufacturer name is not equal to current manufacturer name:" + impl.getManufacturerName());
			loggerClient.write(name + ": Manufacturer name is not equal to current manufacturer name:" + impl.getManufacturerName());
			return null;
		}		
		ItemImpl availableItem = purchaseOrderManager.itemsMap.get(impl.getProductType());
		if(availableItem == null){
			loggerClient.write(name + ": " + impl.getProductType() + " is not supported!");
			return null;
		}else{
			if(impl.getUnitPrice() < availableItem.getUnitPrice()){
				loggerClient.write(name + ": The order price (" + impl.getUnitPrice() + ") is lower than defined price(" + availableItem.getUnitPrice() + ")");
				return null;
			}else{
				if(impl.getQuantity() >= availableItem.getQuantity()){
					int oneTimeQuantity = 100;
					if(produce(impl.getProductType(), oneTimeQuantity)){
						availableItem.setQuantity(availableItem.getQuantity() + oneTimeQuantity);
						
						purchaseOrderManager.saveItems();
						
						loggerClient.write(name + ": Produced " + oneTimeQuantity + " " + impl.getProductType());
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
			return new ProductImpl(avaiableItem.getManufacturerName(),avaiableItem.getProductType(), avaiableItem.getUnitPrice());
		}
	}

	
	@Override
	public boolean receivePayment(String orderNum, float totalPrice) {
		ItemImpl waitingForPayItem = purchaseOrderMap.get(orderNum);
		if(waitingForPayItem == null){
			loggerClient.write(name + ": " + orderNum + " does not exist in purchaseOrderMap of current manufacturer!");
			return false;
		}else{
			if(waitingForPayItem.getQuantity() * waitingForPayItem.getUnitPrice() == totalPrice){
				ItemImpl inhandItem = purchaseOrderManager.itemsMap.get(waitingForPayItem.getProductType());
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
	 */
	private void setProduct(){
		XmlFileController xmlfile = new XmlFileController("settings/product_info.xml");
		Element root = xmlfile.Read();
		if(root != null){
			List<Element> nodes = root.elements("product");
			boolean newProductAdded = false;
			for(Element subElem: nodes){
				String manufacturerName = subElem.element("manufacturerName").getText();
				if(manufacturerName.equals(name)){
					String productType = subElem.element("productType").getText();
					float unitPrice = Float.parseFloat(subElem.element("unitPrice").getText());
					if(purchaseOrderManager.itemsMap.get(productType) == null){
						purchaseOrderManager.itemsMap.put(productType, new ItemImpl(manufacturerName, productType, unitPrice, 0));
						newProductAdded = true;
					}
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
