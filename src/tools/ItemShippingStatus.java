package tools;


/**
* tools/ItemShippingStatus.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public abstract class ItemShippingStatus extends tools.Item
{
  public boolean shippingStatus = false;

  private static String[] _truncatable_ids = {
    tools.ItemShippingStatusHelper.id ()
  };

  public String[] _truncatable_ids() {
    return _truncatable_ids;
  }

  public void _read (org.omg.CORBA.portable.InputStream istream)
  {
    super._read (istream);
    this.shippingStatus = istream.read_boolean ();
  }

  public void _write (org.omg.CORBA.portable.OutputStream ostream)
  {
    super._write (ostream);
    ostream.write_boolean (this.shippingStatus);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return tools.ItemShippingStatusHelper.type ();
  }
} // class ItemShippingStatus
