package retailer;

/**
* retailer/RetailerHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public final class RetailerHolder implements org.omg.CORBA.portable.Streamable
{
  public retailer.Retailer value = null;

  public RetailerHolder ()
  {
  }

  public RetailerHolder (retailer.Retailer initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = retailer.RetailerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    retailer.RetailerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return retailer.RetailerHelper.type ();
  }

}
