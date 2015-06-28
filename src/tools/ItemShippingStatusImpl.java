package tools;

/**
 * @author comp6231.team5
 * The implementation of ItemShippingStatus
 */
public class ItemShippingStatusImpl extends ItemShippingStatus {
	public ItemShippingStatusImpl(){

	}
	
	/**
	 * constructor
	 * @param item
	 * @param shippingStatus
	 */
	public ItemShippingStatusImpl(Item item, boolean shippingStatus) {
		this.productID = item.productID;
		this.manufacturerName = item.manufacturerName;
		this.productType = item.productType;
		this.unitPrice = item.unitPrice;
		this.quantity = item.quantity;
		this.shippingStatus = shippingStatus;
	}
	
	/**
	 * Constructor
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 * @param quantity
	 * @param shippingStatus
	 */
	public ItemShippingStatusImpl(String manufacturerName,
			String productType,
			float unitPrice,
			int quantity,
			boolean shippingStatus
			){
		this.productID = String.valueOf((manufacturerName + productType).hashCode());
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.shippingStatus = shippingStatus;
	}

	/**
	 * Constructor
	 * @param productID
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 * @param quantity
	 * @param shippingStatus
	 */
	public ItemShippingStatusImpl(String productID,
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

	/**
	 * Constructor
	 * @param itemShippingStatus
	 */
	public ItemShippingStatusImpl(ItemShippingStatus itemShippingStatus){
		this.productID = itemShippingStatus.productID;
		this.manufacturerName = itemShippingStatus.manufacturerName;
		this.productType = itemShippingStatus.productType;
		this.unitPrice = itemShippingStatus.unitPrice;
		this.quantity = itemShippingStatus.quantity;
		this.shippingStatus = itemShippingStatus.shippingStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + unitPrice
				+ ", " + quantity
				+ ", " + (shippingStatus ? "shipped" : "unshipped");
	}
}
