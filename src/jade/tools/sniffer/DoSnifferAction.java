/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in compliance with the FIPA specifications.
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


package jade.tools.sniffer;

import jade.gui.AgentTree;
import java.util.Vector;

   /**
   Javadoc documentation for the file
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   <Br>
   <a href="mailto:a_soracchi@libero.it"> Andrea Soracchi(e-mail) </a>
   @version $Date$ $Revision$
   */

   /**
  * For sniff the Agent in the tree.
  * @see jade.tools.sniffer.DoNotSniffAction
  * @see jade.tools.sniffer.ShowOnlyAction
  */

public class DoSnifferAction extends AgentAction{

private MainPanel mainPanel;
private Agent agent;
private Sniffer mySniffer;
private Vector sniffedAgents=new Vector();

 public DoSnifferAction(ActionProcessor actPro,MainPanel mainPanel,Sniffer mySniffer ) {
   super("DoSnifferActionIcon","Do sniff this agent(s)",actPro);
   this.mainPanel=mainPanel;
   this.mySniffer=mySniffer;
 }

  public void doAction(AgentTree.AgentNode node){
   String realName;

     //Check if the agent is in the canvas

     realName=checkString(node.getName());
     agent=new Agent(realName);

     if (!mainPanel.panelcan.canvAgent.isPresent(realName))
       mainPanel.panelcan.canvAgent.addAgent(agent);   // add Agent in the Canvas

     mainPanel.panelcan.canvAgent.rAgfromNoSniffVector(new Agent(realName));

     sniffedAgents.addElement(agent);
  }

  public void sendAgentVector() {
   mySniffer.sniffMsg(sniffedAgents,Sniffer.SNIFF_ON);   // Sniff the Agents
   sniffedAgents.removeAllElements();
  }

  private String checkString(String nameAgent) {
   int index;

   index=nameAgent.indexOf("@");
   if (index != -1) return nameAgent.substring(0,index);
   else {
     System.out.println("The agent's name is not correct");
     System.exit(-1);
     return null;
   }
  }

} // End of class DoSnifferAction
