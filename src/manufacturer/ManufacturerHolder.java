package manufacturer;

/**
* manufacturer/ManufacturerHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public final class ManufacturerHolder implements org.omg.CORBA.portable.Streamable
{
  public manufacturer.Manufacturer value = null;

  public ManufacturerHolder ()
  {
  }

  public ManufacturerHolder (manufacturer.Manufacturer initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = manufacturer.ManufacturerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    manufacturer.ManufacturerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return manufacturer.ManufacturerHelper.type ();
  }

}
