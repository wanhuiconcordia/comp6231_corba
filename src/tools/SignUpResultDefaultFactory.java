package tools;


/**
* tools/SignUpResultDefaultFactory.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from supplyChain.idl
* Thursday, June 18, 2015 6:22:17 o'clock PM EDT
*/

public class SignUpResultDefaultFactory implements org.omg.CORBA.portable.ValueFactory {

  public java.io.Serializable read_value (org.omg.CORBA_2_3.portable.InputStream is)
  {
    return is.read_value(new SignUpResultImpl ());
  }
}
