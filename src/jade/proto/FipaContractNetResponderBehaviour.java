package jade.proto;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

import java.io.*;

/**
* Behaviour class for <code>fipa-contract-net</code>
* <em>Responder</em> role.  This abstract behaviour implements the
* <code>fipa-contract-net</code> interaction protocol from the point
* of view of a responder to a call for proposal (<code>cfp</code>)
* message.  In order to use correctly this behaviour, the programmer
* should do the following:

* <ul> <li> Implement a class that extends
* <code>FipaContractNetResponderBehaviour</code>.  This class must
* implement 3 methods that are called by
* <code>FipaContractNetResponderBehaviour</code>:

* <ul>

* <li> <code>public ACLMessage evaluateCfp(ACLMessage cfp)</code> to
* evaluate the <code>cfp</code> message received and to return an
* <code>ACLMessage</code> to be sent in response
* (<code>propose</code>, <code>refuse</code> or
* <code>not-understood</code>).  If <code>null</code> is returned,
* then the <code>cfp</code> is ignored and the behaviour is reset and
* start again waiting for <code>cfp</code> messages.

* <li> <code>public ACLMessage AcceptedProposal(ACLMessage msg)</code>
* to evaluate the received <code>accept-proposal</code> message and to
* return an <code>ACLMessage</code> to be sent back (<code>inform
* Done</code> or <code>failure</code>).

* <li> <code>public void RejectedProposal(ACLMessage msg)</code> to
* evaluate the received <code>reject-proposal</code>. After this
* method, the protocol is reset and it restarts again.

* <li> Optionally, the programmer might override the
* <code>reset()</code> method that is called to reset the behaviour
* and wait for a new <code>cfp</code> message. In this case, remind to
* call also <code>super.reset()</code> from within overridden
* <code>reset()</code> version!

* </ul>

* <li> create a new instance of this class and add it to the agent
* calling <code>Agent.addBehaviour()</code>)

* </ul>
* @see jade.proto.FipaContractNetInitiatorBehaviour
* @author Fabio Bellifemine - CSELT
* @version $Date$ $Revision$
*/
public abstract class FipaContractNetResponderBehaviour extends SimpleBehaviour {
    
  /** It is the pointer to the Agent class.
   * A common usage of this variable is to cast it to the actual type of
   * Agent class and then use the methods of the extended class. 
   * For instance 
   * <code>appointments = (AppointmentAgent)myAgent.getAppointments() </code>
   */
 public Agent myAgent;
 MessageTemplate mt=MessageTemplate.MatchType("cfp");
 MessageTemplate template;
 int   state=0; // state of the protocol
 long timeout;
 ACLMessage cfpMsg, acceptMsg, informMsg, proposeMsg;
  /**
   * this variable should be set to true when the behaviour should
   * terminate
 */
 public boolean finished = false;
  /** 
   * default timeout in milliseconds to wait for proposals.  This
   * timeout is overriden by the reply-by parameter of the
   * <code>cfp</code> message, if set.
   */
  public static final long DEFAULTTIMEOUT = 90000; 
  ACLMessage wakeMsg;
  Waker waker;
 
  /**
   * This method is called to restart the protocol and wait again for
   * a new call for proposal message.  The method can be overriden by
   * subclasses, but <code>super.reset()</code> should always be
   * called.
   */
 public void reset() {
    mt=MessageTemplate.MatchType("cfp");
    template = null;
    state = 0;
    cfpMsg = null;
    acceptMsg = null;
    informMsg = null;
    proposeMsg = null;
    finished = false;
 }
 

  /**
   * Constructor of the class.
   * @param a is the pointer to the Agent class
   */ 
  FipaContractNetResponderBehaviour(Agent a) {
    myAgent = a;
  }
 
  
  /**
   * This method checks whether this behaviour has finished its task.
   * @return <code>true</code> if the behaviour must be terminated,
   * <code>false</code> otherwise.
   */
  public boolean done() {
    return finished;
  }
 
 
  /**
   * Actual body of the behaviour. It cannot be overriden.
   */
  final public void action() {
    switch (state) {
    case 0: {
      cfpMsg = myAgent.receive(mt);
      if (cfpMsg == null) {
	block();
	return;
      }
      System.err.println("FipaContractNetResponderBehaviour: receive");
      cfpMsg.dump();
      state = 1;
      break;
    }
    case 1: {
      state = 2;
      proposeMsg = evaluateCfp(cfpMsg);
      if (proposeMsg == null) {
	reset();
      }
      break;
    }
    case 2: {
      state = 3;
      proposeMsg.setSource(myAgent.getName());
      proposeMsg.setReplyTo(cfpMsg.getReplyWith());
      proposeMsg.setProtocol("FIPA-Contract-Net");
      proposeMsg.setConversationId(cfpMsg.getConversationId());
      if (proposeMsg.getReplyWith() == null)
	proposeMsg.setReplyWith("ContractNetResponder"+(new Date()).getTime());
      template = MessageTemplate.MatchReplyTo(proposeMsg.getReplyWith());
      myAgent.send(proposeMsg);
      System.err.println("FipaContractNetResponderBehaviour: send");
      proposeMsg.dump();
      if (! proposeMsg.getType().equalsIgnoreCase("propose"))
	reset();
      else {
	timeout = proposeMsg.getReplyByDate().getTime()-(new Date()).getTime();
	if (timeout <= 1000) timeout = DEFAULTTIMEOUT; // at least 1 second
	//start a thread with the timeout
	wakeMsg = new ACLMessage("inform");
	wakeMsg.setReplyTo(proposeMsg.getReplyWith());
	wakeMsg.setConversationId("WAKEUP"+proposeMsg.getReplyWith());
	wakeMsg.setContent("(timeout " + timeout + ")");
	waker = new Waker(myAgent,wakeMsg,timeout);
	waker.start();
      }
      break;
    }   
    case 3: { // in this state I can receive only accept-proposal or reject-proposal
      acceptMsg = myAgent.receive(template);
      if (acceptMsg == null) {
	block();
	return;
      } else if (acceptMsg.getType().equalsIgnoreCase("accept-proposal")) {
	System.err.println("FipaContractNetResponderBehaviour: receive");
	waker.stop();
	acceptMsg.dump();
	state = 4;
	informMsg = AcceptedProposal(acceptMsg);
      } else if (acceptMsg.getType().equalsIgnoreCase("reject-proposal")) {
	waker.stop();
	System.err.println("FipaContractNetResponderBehaviour: receive");
	acceptMsg.dump();
	RejectedProposal(acceptMsg);
	reset();
      } else if (acceptMsg.getConversationId() == null) {
	myAgent.postMessage(acceptMsg); // the message is not for me
      } else if (acceptMsg.getConversationId().equalsIgnoreCase(wakeMsg.getConversationId())) { 
	// wake-up message
	reset();
      } else 
	myAgent.postMessage(acceptMsg); // the message is not for me
      break;
    }
    case 4: { // send the last message
      informMsg.setSource(myAgent.getName());
      informMsg.setDest(acceptMsg.getSource());
      informMsg.setReplyTo(acceptMsg.getReplyWith());
      informMsg.setProtocol("FIPA-Contract-Net");
      informMsg.setConversationId(acceptMsg.getConversationId());
      myAgent.send(informMsg);
      System.err.println("FipaContractNetResponderBehaviour: send");
      informMsg.dump();
      reset();
      break;
    }
    } // end of switch
  }


  /**
   * This method is called when the <code>accept-proposal</code>
   * message is received. 
   * @param msg contains the received <code>accept-proposal</code> message.
   * @return the <code>ACLMessage</code> to be sent as a response at
   * the next state of the protocol.
   */
  public abstract ACLMessage AcceptedProposal(ACLMessage msg);

  /**
   * This method is called when the <code>reject-proposal</code>
   * message is received.  The method is also called when the timeout
   * is elapsed.  The protocol uses a default timeout, that is the
   * class variable DEFAULTTIMEOUT; the <code>:reply-by</code> slot of
   * the <code>propose</code> message overrides the default timeout.
   * After the execution of this method the protocol is reset.
   * @param msg contains the received reject-proposal message.
   */
public abstract void RejectedProposal(ACLMessage msg);


  /**
   * This method is called when the <code>cfp</code>
   * message is received. 
   * @param msg contains the received cfp message.
   * @return the <code>ACLMessage</code> to be sent as a response at
   * the next state of the protocol. If null, or different from
   * <code>propose</code>, then the protocol is reset.
   */
public abstract ACLMessage evaluateCfp(ACLMessage cfp);
}














