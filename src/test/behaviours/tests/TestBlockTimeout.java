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

package test.behaviours.tests;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import test.common.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestBlockTimeout extends Test {
	public static final String TEST_NAME = "Stress-test-for-timeout-blocking-mechanism";
	
	private static final String SENDER_CLASS = "test.behaviours.SeqSender";
	private static final String RECEIVER_CLASS = "test.behaviours.SeqReceiver";

	private static final int DEFAULT_N_AGENTS = 4;
	private static final int DEFAULT_N_MESSAGES = 100;
	private static final long DEFAULT_SHORTEST_PERIOD = 5; 
	private int nAgents = DEFAULT_N_AGENTS;
	private int nMessages = DEFAULT_N_MESSAGES;
	private long shortestPeriod = DEFAULT_SHORTEST_PERIOD;
  private ACLMessage startMsg = new ACLMessage(ACLMessage.INFORM);
	private RemoteController rc;
	
  public String getName() {
  	return TEST_NAME;
  }
  
  public String getDescription() {
  	StringBuffer sb = new StringBuffer("Tests the behaviour block/restart mechanism when a timeout is specified by stressing it\n");
  	sb.append("It should be noticed that passing this test does not ensure the mechanism works correctly\n");
  	sb.append("This test will take a while");
  	return sb.toString();
  }
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException { 
  	try {
  		final DataStore store = ds;
  		final String key = resultKey;
  		
  		// Get arguments
	  	Object[] args = getGroupArguments();
    	if (args != null && args.length > 0) {
    		// Number of senders/receivers
      	nAgents = Integer.parseInt((String) args[0]);
      	if (args.length > 1) {
      		// Number of messages
      		nMessages = Integer.parseInt((String) args[1]);
      		if (args.length > 2) {
      			// Shortest period
	      		shortestPeriod = Long.parseLong((String) args[2]);
      		}
      	}
    	} 

    	// Launch a peripheral container
	    rc = TestUtility.launchJadeInstance("Container", null, "-container -port 8888", new String[] {});
    	
	    // Launch senders and receivers
  	  for (int i = 0; i < nAgents; ++i) {
  	  	String senderName = new String("s"+i);
  	  	String receiverName = new String("r"+i);
    		// Launch the sender on the main
    		String[] agentArgs = new String[] {receiverName, String.valueOf(nMessages), String.valueOf(shortestPeriod*(i+1))}; 
    		TestUtility.createAgent(a, senderName, SENDER_CLASS, agentArgs, Agent.getAMS(), AgentManager.MAIN_CONTAINER_NAME); 
				// Launch the receiver on container-1 (it exists for sure)
    		agentArgs = new String[] {a.getLocalName()};
    		TestUtility.createAgent(a, receiverName, RECEIVER_CLASS, agentArgs, Agent.getAMS(), "Container-1");
    		// Prepare the message to start the senders
    		startMsg.addReceiver(new AID(senderName, AID.ISLOCALNAME));
  	  }
    
    	// Create the behaviour to return: A Parallel behaviour with
  	  // a watchdog WakerBehaviour and a behaviour that terminates 
  	  // when all receivers have completed. If the watchdog behaviour 
  	  // terminates first the test is failed.
  	  
    	Behaviour b1 = new SimpleBehaviour(a) {
    		private int cnt = 0;
    		public void action() {
    			ACLMessage msg = myAgent.receive();
    			if (msg != null) {
    				cnt++;
    			}
    			block();
    		}
    	
    		public boolean done() {
    	 		return (cnt >= nAgents);
    		}
    	
    		public int onEnd() {
					store.put(key, new Integer(Test.TEST_PASSED));
    			return 0;
    		}
    	};
    	
    	long timeout = 2*(shortestPeriod >= 10 ? shortestPeriod : 10)*nAgents*nMessages;
    	Behaviour b2 = new TickerBehaviour(a, timeout) {
    		public void onTick() {
					store.put(key, new Integer(Test.TEST_FAILED));
					stop();
    		}
    	};
    	
    	ParallelBehaviour b = new ParallelBehaviour(a, ParallelBehaviour.WHEN_ANY) {
    		public void onStart() {
    			myAgent.send(startMsg);
    		}
    	};
    	b.addSubBehaviour(b1);
    	b.addSubBehaviour(b2);
  		
  		return b;
  	}
  	catch (TestException te) {
  		throw te;
  	}
  	catch (Exception e) {
  		throw new TestException("Error loading test", e);
  	}
  }
					
  public void clean(Agent a) {
  	try {
  		// Kill senders and receivers
  	  for (int i = 0; i < nAgents; ++i) {
  	  	String senderName = new String("s"+i);
  	  	String receiverName = new String("r"+i);
    		TestUtility.killTarget(a, new AID(senderName, AID.ISLOCALNAME)); 
    		TestUtility.killTarget(a, new AID(receiverName, AID.ISLOCALNAME)); 
  	  }
  	  
  	  // Kill the peripheral container
  	  rc.kill();
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  }
  	
}

