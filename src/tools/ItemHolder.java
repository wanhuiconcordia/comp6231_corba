package tools;

/**
* tools/ItemHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 11:04:37 o'clock PM EDT
*/

public final class ItemHolder implements org.omg.CORBA.portable.Streamable
{
  public tools.Item value = null;

  public ItemHolder ()
  {
  }

  public ItemHolder (tools.Item initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = tools.ItemHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    tools.ItemHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return tools.ItemHelper.type ();
  }

}
