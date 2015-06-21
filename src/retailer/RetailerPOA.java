package retailer;


/**
* retailer/RetailerPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Sunday, June 21, 2015 10:55:15 AM EDT
*/

public abstract class RetailerPOA extends org.omg.PortableServer.Servant
 implements retailer.RetailerOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("getCatalog", new java.lang.Integer (0));
    _methods.put ("submitOrder", new java.lang.Integer (1));
    _methods.put ("signUp", new java.lang.Integer (2));
    _methods.put ("signIn", new java.lang.Integer (3));
    _methods.put ("getProducts", new java.lang.Integer (4));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // retailer/Retailer/getCatalog
       {
         int customerReferenceNumber = in.read_long ();
         tools.Item $result[] = null;
         $result = this.getCatalog (customerReferenceNumber);
         out = $rh.createReply();
         tools.ItemListHelper.write (out, $result);
         break;
       }

       case 1:  // retailer/Retailer/submitOrder
       {
         int customerReferenceNumber = in.read_long ();
         tools.Item itemList[] = tools.ItemListHelper.read (in);
         tools.ItemShippingStatus $result[] = null;
         $result = this.submitOrder (customerReferenceNumber, itemList);
         out = $rh.createReply();
         tools.ItemShippingStatusListHelper.write (out, $result);
         break;
       }

       case 2:  // retailer/Retailer/signUp
       {
         String name = in.read_string ();
         String password = in.read_string ();
         String street1 = in.read_string ();
         String street2 = in.read_string ();
         String city = in.read_string ();
         String state = in.read_string ();
         String zip = in.read_string ();
         String country = in.read_string ();
         tools.SignUpResult $result = null;
         $result = this.signUp (name, password, street1, street2, city, state, zip, country);
         out = $rh.createReply();
         tools.SignUpResultHelper.write (out, $result);
         break;
       }

       case 3:  // retailer/Retailer/signIn
       {
         int customerReferenceNumber = in.read_long ();
         String password = in.read_string ();
         tools.Customer $result = null;
         $result = this.signIn (customerReferenceNumber, password);
         out = $rh.createReply();
         tools.CustomerHelper.write (out, $result);
         break;
       }

       case 4:  // retailer/Retailer/getProducts
       {
         String productID = in.read_string ();
         tools.Item $result[] = null;
         $result = this.getProducts (productID);
         out = $rh.createReply();
         tools.ItemListHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:retailer/Retailer:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Retailer _this() 
  {
    return RetailerHelper.narrow(
    super._this_object());
  }

  public Retailer _this(org.omg.CORBA.ORB orb) 
  {
    return RetailerHelper.narrow(
    super._this_object(orb));
  }


} // class RetailerPOA
