package tools;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class ItemImpl extends Item {
	
	protected static final long serialVersionUID = -1927708729616470764L;
	public int quantity;

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
	
	public ItemImpl() {
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + quantity
				+ ", " + unitPrice;
	}
	
	public ItemImpl clone(){
		return new ItemImpl(productID, manufacturerName, productType, unitPrice, quantity);
	}
	

	/**
	 * @return quantity of the item
	 */
	public int getQuantity(){
		return quantity;
	}
	
	/**
	 * @param q
	 */
	public void setQuantity(int q){
		quantity = q;
	}
	
	
	/**
	 * determine whether the current item is the same as the other item
	 * @param otherItem
	 * @return ture if the same, false if not
	 */
	public boolean isSameProductAs(Item otherItem){
		return (this.manufacturerName.equals(otherItem.manufacturerName))
				&& (this.productType.equals(otherItem.productType))
				&& (this.unitPrice == otherItem.unitPrice);
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
