package tools;

/**
 * @author comp6231.team5
 * The implementation of Product
 */
public class ProductImpl extends Product {
	
	/**
	 * Constructor 
	 */
	public ProductImpl(){
		
	}
		
	/**
	 * Constructor
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 */
	public ProductImpl(String manufacturerName,
			String productType,
			float unitPrice
			){
		this.productID = String.valueOf((manufacturerName + productType).hashCode());
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
	}
	
	/**
	 * Constructor
	 * @param productID
	 * @param manufacturerName
	 * @param productType
	 * @param unitPrice
	 */
	public ProductImpl(String productID,
			String manufacturerName,
			String productType,
			float unitPrice
			){
		this.productID = productID;
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
	}
	
	/**
	 * Constructor
	 * @param product
	 */
	public ProductImpl(Product product){
		this.productID = product.productID;
		this.manufacturerName = product.manufacturerName;
		this.productType = product.productType;
		this.unitPrice = product.unitPrice;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Product clone(){
		return new ProductImpl(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + unitPrice;
	}
}
