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
import jade.core.CaseInsensitiveString;
import java.io.*;

/**
 * Class declaration
 * @author Giovanni Caire - TILAB
 * @author Ronnie Taib - Motorola
 * @author Steffen Rusitschka - Siemens
 */
class JICPClient {

	private TransportProtocol protocol;
	private ConnectionFactory connFactory;
	private ConnectionPool pool; 
	
  /**
   * Constructor declaration
   */
  public JICPClient(TransportProtocol tp, ConnectionFactory f, int max) {
  	protocol = tp;
  	connFactory = f;
  	pool = new ConnectionPool(protocol, connFactory, max);
  } 

  /**
   * Send a command to this transport address
   * @param ta the address to send the command to
   * @param dataType the type of data as defined in the JICPPeer
   * @param data the command
   * @return a byte array corresponding to the answer
   * 
   * @throws ICPException
   */
  public byte[] send(TransportAddress ta, byte dataType, byte[] data) throws ICPException {
    ConnectionWrapper cw = null;
  	boolean done = false;
  	
  	while (true) {
	    try {
	      // Acquire a connection wrapper from the pool
	      cw = pool.acquire(ta);
	
	      // Prepare JICP information
	      byte dataInfo = JICPProtocol.DEFAULT_INFO;
				if (cw.isOneShot()) {
					dataInfo |= JICPProtocol.TERMINATED_INFO;
				}
	
	      // Get the actual connection and send the request
	      Connection connection = cw.getConnection();
	      JICPPacket request = new JICPPacket(dataType, dataInfo, ta.getFile(), data);
	      connection.writePacket(request);
	
	      // Read the reply
	      JICPPacket reply = connection.readPacket();
		    if (reply.getType() == JICPProtocol.ERROR_TYPE) {
		      throw new ICPException(new String(reply.getData()));
		    } 
	      if ((reply.getInfo() & JICPProtocol.TERMINATED_INFO) != 0) {
	      	// The server cannot keep the connection open --> set it as one-shot
	      	cw.setOneShot();
	      }
	      pool.release(cw);
		
	    	done = true;
		    return reply.getData();
	    } 
	    catch (EOFException eof) {
	    	if (!cw.isReused()) {
	      	throw new ICPException("EOF reached");
	    	}
	    } 
	    catch (IOException ioe) {
	    	if (!cw.isReused()) {
	      	throw new ICPException("I/O error sending/receiving data to "+ta.getHost()+":"+ta.getPort(), ioe);
	    	}
	    } 
	    catch (ICPException icpe) {
	    	// Re-throw the exception
	      throw icpe;
	    } 
	    catch (Exception e) {
	      throw new ICPException("Problems in communication with "+ta.getHost()+":"+ta.getPort(), e);
	    } 
			finally {    
				if (done) {
					if (cw.isOneShot()) {
			      pool.remove(ta, cw);
					}
		  	}
		  	else {
		  		// Some error occurred --> The connection (if any) is no longer valid
			    if (cw != null) {
			    	pool.remove(ta, cw);
					}
		  	}
			}
  	}
  } 
  
  public void shutdown() {
  	pool.shutdown();
  }
  
  /**
     Called by the JICPPeer ticker at each tick
   */
  public void tick(long currentTime) {
  }
}
