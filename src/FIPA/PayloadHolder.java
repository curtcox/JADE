package FIPA;


/**
* FIPA/PayloadHolder.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from fipa.idl
* marted� 14 agosto 2001 12.58.13 GMT+02:00
*/

public final class PayloadHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public PayloadHolder ()
  {
  }

  public PayloadHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FIPA.PayloadHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FIPA.PayloadHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FIPA.PayloadHelper.type ();
  }

}
