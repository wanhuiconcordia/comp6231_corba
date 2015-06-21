package manufacturer;


/**
* manufacturer/ManufacturerOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public interface ManufacturerOperations 
{
  String processPurchaseOrder (tools.Item item);
  tools.Product getProductInfo (String aProdName);
  boolean receivePayment (String orderNum, float totalPrice);
  tools.Product[] getProductList ();
} // interface ManufacturerOperations
