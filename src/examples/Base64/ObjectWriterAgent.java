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

package examples.Base64;

import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFServiceCommunicator;

import java.util.*;
import java.io.*;

/**
This agent makes the following task:
1. searches in the DF for an ObjectReaderAgent;
2. sends an ACLMessage with a content encoded in Base64 to the 
   ObjectReaderAgent.
@author Fabio Bellifemine - CSELT S.p.A
@version $Date$ $Revision$
*/

public class ObjectWriterAgent extends Agent {


protected void setup() {

  System.out.println(getLocalName()+" agent sends an ACLMessage whose content is a Java object");

  /** Search with the DF for the name of the ObjectReaderAgent **/
  AID reader = new AID();
  DFAgentDescription dfd = new DFAgentDescription();  
  ServiceDescription sd = new ServiceDescription();
  sd.setType("ObjectReaderAgent"); 
  dfd.addServices(sd);
  try {
    while (true) {
      System.out.println(getLocalName()+ " waiting for an ObjectReaderAgent registering with the DF");
      SearchConstraints c = new SearchConstraints();
      List result = DFServiceCommunicator.search(this,dfd,c);
      if (result.size() > 0) {
	dfd = (DFAgentDescription)result.get(0);
	reader = dfd.getName();
	break;
      }
      Thread.sleep(10000);
    }
  } catch (Exception fe) {
    fe.printStackTrace();
    System.err.println(getLocalName()+" search with DF is not succeeded because of " + fe.getMessage());
    doDelete();
    }

   try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

      msg.addReceiver(reader);

      Person p = new Person("JADE", "CSELT", new Date(), 2);
      msg.setContentObject(p);
    
      send(msg);
      System.out.println(getLocalName()+" sent a message to "+reader);
  } catch (IOException e ) {
    e.printStackTrace();}
    doDelete();
  }
}
