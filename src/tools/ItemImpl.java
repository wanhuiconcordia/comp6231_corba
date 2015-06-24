package tools;

public class ItemImpl extends Item {
	
	public ItemImpl(){
		
	}

	public ItemImpl(String manufacturerName,
			String productType,
			float unitPrice,
			int quantity
			){
		this.productID = (manufacturerName + productType).hashCode();
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
	}
	
	public ItemImpl(int productID,
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
	
	public Product cloneProduct(){
		return new ProductImpl(productID, manufacturerName, productType, unitPrice);
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
}
