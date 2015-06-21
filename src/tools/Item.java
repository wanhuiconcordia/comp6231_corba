package tools;


/**
* tools/Item.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public abstract class Item extends tools.Product
{
  public int quantity = (int)0;

  private static String[] _truncatable_ids = {
    tools.ItemHelper.id ()
  };

  public String[] _truncatable_ids() {
    return _truncatable_ids;
  }

  public void _read (org.omg.CORBA.portable.InputStream istream)
  {
    super._read (istream);
    this.quantity = istream.read_long ();
  }

  public void _write (org.omg.CORBA.portable.OutputStream ostream)
  {
    super._write (ostream);
    ostream.write_long (this.quantity);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return tools.ItemHelper.type ();
  }
} // class Item
