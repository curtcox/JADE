package FIPA;


/**
* FIPA/_MTSStub.java
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from fipa.idl
* venerd� 7 dicembre 2001 16.57.12 CET
*/

public class _MTSStub extends org.omg.CORBA.portable.ObjectImpl implements FIPA.MTS
{

  public void message (FIPA.FipaMessage aFipaMessage)
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("message", false);
       FIPA.FipaMessageHelper.write ($out, aFipaMessage);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       message (aFipaMessage);
    } finally {
        _releaseReply ($in);
    }
  } // message

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FIPA/MTS:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _MTSStub
