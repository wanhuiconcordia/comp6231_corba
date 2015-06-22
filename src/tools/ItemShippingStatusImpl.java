package tools;

public class ItemShippingStatusImpl extends ItemShippingStatus {
	public ItemShippingStatusImpl(){

	}
	
	public ItemShippingStatusImpl(String manufacturerName,
			String productType,
			float unitPrice,
			int quantity,
			boolean shippingStatus
			){
		this.productID = (manufacturerName + productType).hashCode();
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.shippingStatus = shippingStatus;
	}

	public ItemShippingStatusImpl(int productID,
			String manufacturerName,
			String productType,
			float unitPrice,
			int quantity,
			boolean shippingStatus
			){
		this.productID = productID;
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.shippingStatus = shippingStatus;
	}

	public ItemShippingStatusImpl(ItemShippingStatus itemShippingStatus){
		this.productID = itemShippingStatus.productID;
		this.manufacturerName = itemShippingStatus.manufacturerName;
		this.productType = itemShippingStatus.productType;
		this.unitPrice = itemShippingStatus.unitPrice;
		this.quantity = itemShippingStatus.quantity;
		this.shippingStatus = itemShippingStatus.shippingStatus;
	}

	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + unitPrice
				+ ", " + quantity
				+ ", " + shippingStatus;
	}
}
