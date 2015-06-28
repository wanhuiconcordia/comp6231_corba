package tools;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

/**
 * @author comp6231.team5
 * The implementation of Item
 */
public class ItemImpl extends Item {
	
	protected static final long serialVersionUID = -1927708729616470764L;
	
	/**
	 * Constructor
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 * @param quantity
	 */
	public ItemImpl(String manufacturerName,
			String productType,
			float unitPrice,
			int quantity
			){
		this.productID = String.valueOf((manufacturerName + productType).hashCode());
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
	}
	
	/**
	 * @param productID
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 * @param quantity
	 */
	public ItemImpl(String productID,
			String manufacturerName,
			String productType,
			float unitPrice,
			int quantity
			){
		this.productID = productID;
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
	}
	
	/**
	 * Constructor
	 * @param item
	 */
	public ItemImpl(Item item){
		this.productID = item.productID;
		this.manufacturerName = item.manufacturerName;
		this.productType = item.productType;
		this.unitPrice = item.unitPrice;
		this.quantity = item.quantity;
	}
	/**
	 * @return the clone product from the current item 
	 */
	public Product cloneProduct(){
		return new ProductImpl(productID, manufacturerName, productType, unitPrice);
	}
	
	/**
	 * Constructor 
	 */
	public ItemImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + quantity
				+ ", " + unitPrice;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ItemImpl clone(){
		return new ItemImpl(productID, manufacturerName, productType, unitPrice, quantity);
	}
	
	/**
	 * save the current item to an  Element object
	 * @return the Element object
	 */
	public Element toXmlElement() {
		DefaultElement customerElem = new DefaultElement("item");
		
		Element subElem = customerElem.addElement("productID");
		subElem.setText(productID);
		
		subElem = customerElem.addElement("manufacturerName");
		subElem.setText(manufacturerName);
		
		subElem = customerElem.addElement("productType");
		subElem.setText(productType);
		
		subElem = customerElem.addElement("unitPrice");
		subElem.setText(String.valueOf(unitPrice));
		
		subElem = customerElem.addElement("quantity");
		subElem.setText(String.valueOf(quantity));
		return customerElem;
	}
}
