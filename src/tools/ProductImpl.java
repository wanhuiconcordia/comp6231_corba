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
	
	public String toString(){
		return productID
				+ ", " + manufacturerName
				+ ", " + productType
				+ ", " + unitPrice;
	}
}
