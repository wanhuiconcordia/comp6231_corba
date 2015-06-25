package manufacturer;


/**
* manufacturer/ManufacturerHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Wednesday, June 24, 2015 9:54:14 PM EDT
*/

abstract public class ManufacturerHelper
{
  private static String  _id = "IDL:manufacturer/Manufacturer:1.0";

  public static void insert (org.omg.CORBA.Any a, manufacturer.Manufacturer that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static manufacturer.Manufacturer extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (manufacturer.ManufacturerHelper.id (), "Manufacturer");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static manufacturer.Manufacturer read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ManufacturerStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, manufacturer.Manufacturer value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static manufacturer.Manufacturer narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof manufacturer.Manufacturer)
      return (manufacturer.Manufacturer)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      manufacturer._ManufacturerStub stub = new manufacturer._ManufacturerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static manufacturer.Manufacturer unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof manufacturer.Manufacturer)
      return (manufacturer.Manufacturer)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      manufacturer._ManufacturerStub stub = new manufacturer._ManufacturerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
