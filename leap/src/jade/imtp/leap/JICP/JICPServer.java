/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/**
 * ***************************************************************
 * The LEAP libraries, when combined with certain JADE platform components,
 * provide a run-time environment for enabling FIPA agents to execute on
 * lightweight devices running Java. LEAP and JADE teams have jointly
 * designed the API for ease of integration and hence to take advantage
 * of these dual developments and extensions so that users only see
 * one development platform and a
 * single homogeneous set of APIs. Enabling deployment to a wide range of
 * devices whilst still having access to the full development
 * environment and functionalities that JADE provides.
 * Copyright (C) 2001 Telecom Italia LAB S.p.A.
 * Copyright (C) 2001 Motorola.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.imtp.leap.JICP;

//#MIDP_EXCLUDE_FILE

import jade.mtp.TransportAddress;
import jade.imtp.leap.*;
import jade.util.leap.Properties;

import java.io.*;
import java.net.*;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * Class declaration
 * @author Giovanni Caire - TILAB
 * @author Ronnie Taib - Motorola
 * @author Nicolas Lhuillier - Motorola
 * @author Steffen Rusitschka - Siemens
 */
class JICPServer extends Thread {
  protected ServerSocket server;
  protected ICP.Listener cmdListener;
  protected boolean      listen = true;
  private int            mediatorCnt = 1;
  private Hashtable      mediators = new Hashtable();

  private static int     verbosity = 2;

  /**
   * Constructor declaration
   * @param port
   * @param cmdHandler
   */
  public JICPServer(int port, ICP.Listener l, boolean changePortIfBusy) throws ICP.ICPException {
    cmdListener = l;

    try {
      server = new ServerSocket(port);
    } 
    catch (IOException ioe) {
    	if (changePortIfBusy) {
    		// The specified port is busy. Let the system find a free one
    		try {
      		server = new ServerSocket(0);
    		}
    		catch (IOException ioe2) {
      		throw new ICP.ICPException("Problems initializing server socket. No free port found");
				}
    	}
    	else {
	      throw new ICP.ICPException("I/O error opening server socket on port "+port);
    	}
    } 
    
    setDaemon(true);
    setName("Main");
  }

  int getLocalPort() {
  	return server.getLocalPort();
  }
  
  /**
   * Method declaration
   * @see
   */
  public void shutdown() {
    listen = false;

    try {
      // Force the listening thread (this) to exit from the accept()
      // Calling this.interrupt(); should be the right way, but it seems
      // not to work...so do that by closing the server socket.
      server.close();

      // Wait for the listening thread to complete
      this.join();
    } 
    catch (IOException ioe) {
      ioe.printStackTrace();
    } 
    catch (InterruptedException ie) {
      ie.printStackTrace();
    } 
  } 

  /**
   * Method declaration
   * @see
   */
  public void run() {
    while (listen) {
      try {
        Socket s = server.accept();          // accept connection
        new ConnectionHandler(s).start();    // start a handler and go back to listening
      } 
      catch (InterruptedIOException e) {
        // These can be generated by socket timeout (just ignore
        // the exception) or by a call to the shutdown()
        // method (the listen flag has been set to false and the
        // server will exit).
      } 
      catch (Exception e) {
        // If the listen flag is false then this exception has
        // been forced by the shutdown() method --> do nothing.
        // Otherwise some error occurred
        if (listen) {
          log("Problems accepting a new connection", 1);
          e.printStackTrace();

          // Stop listening
          listen = false;
        } 
      } 
    } 

    // release socket
    try {
      server.close();
    } 
    catch (IOException io) {
      log("I/O error closing the server socket", 1);
      io.printStackTrace();
    } 

    server = null;

    // Close all mediators
    Enumeration e = mediators.elements();
    while (e.hasMoreElements()) {
      Mediator m = (Mediator) e.nextElement();
      m.shutdown(Mediator.KILLED);
    } 
    mediators.clear();
  } 

  /**
   * Called by a Mediator to notify that it is no longer active
   */
  void deregisterMediator(String id) {
    mediators.remove(id);
  } 

  /**
   * Class declaration
   * @author LEAP
   */
  class ConnectionHandler extends Thread {
    protected Socket socket;

    /**
     * Constructor declaration
     * @param s
     */
    public ConnectionHandler(Socket s) {
      socket = s;
    }

    /**
     * Thread entry point
     */
    public void run() {
      DataOutputStream out = null;
      DataInputStream  inp = null;
      boolean          closeConnection = true;

      try {
        // Get input and output stream from the socket
        inp = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        // Read the input
        JICPPacket request = JICPPacket.readFrom(inp);

        // Reply packet
        JICPPacket reply = null;

        byte       type = request.getDataType();
        switch (type) {
        case JICPProtocol.COMMAND_TYPE:
          // Get the right recipient and let it process the command.
          // Send back the response
          String recipientID = request.getRecipientID();
          if (recipientID == null) {
          	// The recipient is my ICP.Listener (the local CommandDispatcher)
            byte[] rsp = cmdListener.handleCommand(request.getData());
            reply = new JICPPacket(JICPProtocol.RESPONSE_TYPE, JICPProtocol.COMPRESSED_INFO, rsp);
          } 
          else {
            // The recipient is one of the mediators
            JICPMediator m = (JICPMediator) mediators.get(recipientID);
            if (m != null) {
              reply = m.handleJICPPacket(request);
            } 
            else {
              reply = new JICPPacket("Unknown recipient "+recipientID, null);
            } 
          } 
          break;

        case JICPProtocol.GET_ADDRESS_TYPE:
          // Respond sending back the caller address
          String address = socket.getInetAddress().getHostAddress();
          log("Received a GET_ADDRESS request from "+address, 1);
          reply = new JICPPacket(JICPProtocol.GET_ADDRESS_TYPE, JICPProtocol.UNCOMPRESSED_INFO, address.getBytes());
          break;

        case JICPProtocol.CREATE_MEDIATOR_TYPE:
          // Starts a new Mediator and sends back its ID
          address = socket.getInetAddress().getHostAddress();
          String   id = String.valueOf(mediatorCnt++);
          log("Received a CREATE_MEDIATOR request from "+address+". New Mediator ID is "+id+".", 1);
          String s = new String(request.getData());
          Properties p = parseProperties(s);
          JICPMediator m = startMediator(id, p);
        	m.setConnection(socket);
          mediators.put(id, m);
          reply = new JICPPacket(JICPProtocol.RESPONSE_TYPE, JICPProtocol.UNCOMPRESSED_INFO, id.getBytes());
        	reply.writeTo(out);
        	closeConnection = false;
        	return;

        case JICPProtocol.CONNECT_MEDIATOR_TYPE:
          // A mediated container is (re)connecting to its mediator
          address = socket.getInetAddress().getHostAddress();
          recipientID = request.getRecipientID();
          log("Received a CONNECT_MEDIATOR request from "+address+". Mediator ID is "+recipientID, 1);
          m = (JICPMediator) mediators.get(recipientID);
          if (m != null) {
          	reply = new JICPPacket(JICPProtocol.RESPONSE_TYPE, JICPProtocol.UNCOMPRESSED_INFO, null);
          	// Directly send back the response and don't close the socket,
          	// but pass it to the proper mediator
          	reply.writeTo(out);
          	m.setConnection(socket);
          	closeConnection = false;
          	return;
          }
          else {
          	reply = new JICPPacket(JICPProtocol.ERROR_TYPE, JICPProtocol.UNCOMPRESSED_INFO, null);
          }	
          break;

        default:
          // Send back an error response
          log("Uncorrect JICP data type: "+request.getDataType(), 1);
          reply = new JICPPacket("Uncorrect JICP data type: "+request.getDataType(), null);
        }

        // Send the actual response data
        reply.writeTo(out);
      } 
      catch (Exception e) {
        log("Problems in communication with client", 1);
        e.printStackTrace();

        // If there was a connection try to send the exception over it.
        if (out != null) {
          try {
            new JICPPacket("Unexpected error", e).writeTo(out);
          } 
          catch (Exception e2) {    /* ignore it */
          	log("PANIC "+e2, 0);
          } 
        } 
      } 
      finally {
        try {
          if (closeConnection) {
            // Close streams and socket
            if (inp != null) {
              inp.close();
            } 

            if (out != null) {
              out.close();
            } 

            if (socket != null) {
              socket.close();
            } 
          } 
        } 
        catch (IOException io) {
          log("I/O error while closing the socket", 1);
          io.printStackTrace();
        } 
      } 
    } 

  }

  private Properties parseProperties(String s) throws ICP.ICPException {
  	StringTokenizer st = new StringTokenizer(s, "=;");
  	Properties p = new Properties();
  	while (st.hasMoreTokens()) {
  		String key = st.nextToken();
  		if (!st.hasMoreTokens()) {
  			throw new ICP.ICPException("Wrong initialization properties format.");
  		}
  		p.setProperty(key, st.nextToken());
  	}
  	return p;
  }
  
  private JICPMediator startMediator(String id, Properties p) throws Exception {
		String className = p.getProperty(JICPProtocol.MEDIATOR_CLASS_KEY);
		if (className != null) {
  		JICPMediator m = (JICPMediator) Class.forName(className).newInstance();
  		m.init(this, id, p);
  		return m;
		}
		else {
			throw new ICP.ICPException("No JICPMediator class specified.");
		}
  }
  
  /**
   */
  static void log(String s) {
    log(s, 2);
  } 

  /**
   */
  static void log(String s, int level) {
    if (verbosity >= level) {
      String name = Thread.currentThread().getName();
      System.out.println("JICPServer("+name+"): "+s);
    } 
  } 

}

