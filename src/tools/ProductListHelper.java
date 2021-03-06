package tools;


/**
* tools/ProductListHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Wednesday, June 24, 2015 9:54:14 PM EDT
*/

abstract public class ProductListHelper
{
  private static String  _id = "IDL:tools/ProductList:1.0";

  public static void insert (org.omg.CORBA.Any a, tools.Product[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static tools.Product[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = tools.ProductHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (tools.ProductListHelper.id (), "ProductList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static tools.Product[] read (org.omg.CORBA.portable.InputStream istream)
  {
    tools.Product value[] = null;
    int _len0 = istream.read_long ();
    value = new tools.Product[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = tools.ProductHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, tools.Product[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      tools.ProductHelper.write (ostream, value[_i0]);
  }

}
