/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package test.leap.midp;

import jade.imtp.leap.ConnectionListener;
import jade.core.Agent;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestConnectionListener implements ConnectionListener {
	private Form f;
	
	public TestConnectionListener() {
		f = new Form("Connection events");
    Display.getDisplay(Agent.midlet).setCurrent(f);
	}
		
	/**
	   This method is called whenever a temporary disconnection 
	   is detected.
	 */
	public void handleDisconnection() {
		f.append(new StringItem(null, "Disconnection "+System.currentTimeMillis()));
    Display.getDisplay(Agent.midlet).setCurrent(f);
	}
	
	/**
	   This method is called whenever a the device reconnects
	   after a temporary disconnection.
	 */
	public void handleReconnection() {
		f.append(new StringItem(null, "Reconnection "+System.currentTimeMillis()));
    Display.getDisplay(Agent.midlet).setCurrent(f);
	}

	/**
	   This method is called when the device detects it is no longer
	   possible to reconnect (e.g. because the maximum disconnection 
	   timeout expired) 
	 */
	public void handleReconnectionFailure() {
		f.append(new StringItem(null, "Impossible to reconnect "+System.currentTimeMillis()));
    Display.getDisplay(Agent.midlet).setCurrent(f);
	}
}

