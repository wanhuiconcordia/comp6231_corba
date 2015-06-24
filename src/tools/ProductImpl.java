package tools;

public class ProductImpl extends Product {
	public ProductImpl(){
		
	}
	
	public ProductImpl(String manufacturerName,
			String productType,
			float unitPrice
			){
		this.productID = (manufacturerName + productType).hashCode();
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
	}
	
	public ProductImpl(int productID,
			String manufacturerName,
			String productType,
			float unitPrice
			){
		this.productID = productID;
		this.manufacturerName = manufacturerName;
		this.productType = productType;
		this.unitPrice = unitPrice;
	}
	
	public ProductImpl(Product product){
		this.productID = product.productID;
		this.manufacturerName = product.manufacturerName;
		this.productType = product.productType;
		this.unitPrice = product.unitPrice;
	}
	
	/**
	 * determine if the current product is the same as the other one
	 * @param otherProduct
	 * @return true if the same, false if not
	 */
	public boolean isSame(Product otherProduct){
		return (manufacturerName == otherProduct.manufacturerName)
				&& (productType == otherProduct.productType)
				&& (unitPrice == otherProduct.unitPrice);
	}
	
	/**
	 * @return manufacturer name
	 */
	public String getManufacturerName(){
		return manufacturerName;
	}
	
	/**
	 * @return product type
	 */
	public String getProductType(){
		return productType;
	}
	
	/**
	 * @return
	 */
	public float getUnitPrice(){
		return unitPrice;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Product clone(){
		return new ProductImpl(this);
	}
	
	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + unitPrice;
	}
}
