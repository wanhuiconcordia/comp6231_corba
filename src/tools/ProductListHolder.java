package tools;


/**
* tools/ProductListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Wednesday, June 24, 2015 9:54:14 PM EDT
*/

public final class ProductListHolder implements org.omg.CORBA.portable.Streamable
{
  public tools.Product value[] = null;

  public ProductListHolder ()
  {
  }

  public ProductListHolder (tools.Product[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = tools.ProductListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    tools.ProductListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return tools.ProductListHelper.type ();
  }

}
