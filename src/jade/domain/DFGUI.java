/*
  $Log$
  Revision 1.4  1999/03/14 17:45:36  rimassa
  Decoupled event handler thread from DF agent thread using an event
  queue, avoiding deadlock on agent registration through GUI.

  Revision 1.3  1999/02/14 23:19:22  rimassa
  Changed getName() calls to getLocalName() where appropriate.

  Revision 1.2  1999/02/04 13:25:02  rimassa
  Removed some debugging code.

  Revision 1.1  1999/02/03 15:36:55  rimassa
  A class working as a GUI for DF agents.

*/

package jade.domain;

import java.awt.*;
import java.util.*;
import java.io.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jade.core.Agent;
import jade.domain.*; 
import jade.lang.acl.*;

public class DFGUI extends Frame {

  df agent;

    // Constants used by subFrames
    final static int REGISTER=0;  
    final static int VIEW=1;
    final static int MODIFY=2;
    final static int DEREGISTER=3;


public DFGUI() {
    // This code is automatically generated by Visual Cafe when you add
    // components to the visual environment. It instantiates and initializes
    // the components. To modify the code, only use code syntax that matches
    // what Visual Cafe can generate, or Visual Cafe may be unable to back
    // parse your Java file into its visual environment.
	
    //{{INIT_CONTROLS
		setLayout(null);
		setVisible(false);
		setSize(405,305);
		textFieldDFname = new java.awt.TextField();
		textFieldDFname.setVisible(false);
		textFieldDFname.setBounds(12,60,348,24);
		add(textFieldDFname);
		labelDFname = new java.awt.Label("Insert the agent name of the DF you want to register with");
		labelDFname.setVisible(false);
		labelDFname.setBounds(12,12,348,40);
		add(labelDFname);
		buttonDFname = new java.awt.Button();
		buttonDFname.setLabel("OK");
		buttonDFname.setVisible(false);
		buttonDFname.setBounds(144,96,60,40);
		buttonDFname.setBackground(new Color(12632256));
		add(buttonDFname);
		canvas1 = new java.awt.Canvas();
		canvas1.setBounds(0,0,20,40);
		add(canvas1);
		textFieldErrMsg = new java.awt.TextField();
		textFieldErrMsg.setEditable(false);
		textFieldErrMsg.setVisible(false);
		textFieldErrMsg.setBounds(12,204,348,60);
		textFieldErrMsg.setFont(new Font("Dialog", Font.ITALIC, 10));
		textFieldErrMsg.setBackground(new Color(-71));
		add(textFieldErrMsg);
		setTitle("DF");
		//}}
    
    //{{INIT_MENUS
		mainMenuBar = new java.awt.MenuBar();
		menu1 = new java.awt.Menu("Commands");
		miView = new java.awt.MenuItem("View Registered Agent Descriptions");
		menu1.add(miView);
		miRegister = new java.awt.MenuItem("Register New Agent Description");
		menu1.add(miRegister);
		miModify = new java.awt.MenuItem("Modify a registered Agent Description");
		menu1.add(miModify);
		miDeregister = new java.awt.MenuItem("Deregister an Agent Description");
		menu1.add(miDeregister);
		miRegisterDF = new java.awt.MenuItem("Register this DF with another DF");
		menu1.add(miRegisterDF);
		menu1.addSeparator();
		miExit = new java.awt.MenuItem("Shutdown the DF Agent");
		menu1.add(miExit);
		mainMenuBar.add(menu1);
		menu3 = new java.awt.Menu("Help");
		mainMenuBar.setHelpMenu(menu3);
		miAbout = new java.awt.MenuItem("About..");
		menu3.add(miAbout);
		mainMenuBar.add(menu3);
		setMenuBar(mainMenuBar);
		//$$ mainMenuBar.move(4,277);
		//}}
		
    //{{REGISTER_LISTENERS
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);
    SymAction lSymAction = new SymAction();
    miView.addActionListener(lSymAction);
    miRegister.addActionListener(lSymAction);
    miModify.addActionListener(lSymAction);
    miDeregister.addActionListener(lSymAction);
    miRegisterDF.addActionListener(lSymAction);
    miAbout.addActionListener(lSymAction);
    miExit.addActionListener(lSymAction);

		SymMouse aSymMouse = new SymMouse();
		buttonDFname.addMouseListener(aSymMouse);
    //}}
  }
	
  public DFGUI(String title) {
    this();
    setTitle(title);
  }

  public DFGUI(df a) {
    this("DF " + a.getLocalName());
    agent = a;
  }
	
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
  public void setVisible(boolean b) {
    if(b) {
      setLocation(50, 50);
    }	
    super.setVisible(b);
  }

  static public void main(String args[]) {
    (new DFGUI()).setVisible(true);
  }

  public void addNotify() {
    // Record the size of the window prior to calling parents addNotify.
    Dimension d = getSize();

    super.addNotify();

    if (fComponentsAdjusted)
      return;

    // Adjust components according to the insets
    setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
    Component components[] = getComponents();
    for (int i = 0; i < components.length; i++) {
      Point p = components[i].getLocation();
      p.translate(getInsets().left, getInsets().top);
      components[i].setLocation(p);
    }
    fComponentsAdjusted = true;
  }

  // Used for addNotify check.
  boolean fComponentsAdjusted = false;


  //{{DECLARE_MENUS
	java.awt.MenuBar mainMenuBar;
	java.awt.Menu menu1;
	java.awt.MenuItem miView;
	java.awt.MenuItem miRegister;
	java.awt.MenuItem miModify;
	java.awt.MenuItem miDeregister;
	java.awt.MenuItem miRegisterDF;
	java.awt.MenuItem miExit;
	java.awt.Menu menu3;
	java.awt.MenuItem miAbout;
	//}}



  class SymWindow extends java.awt.event.WindowAdapter {
    public void windowClosing(java.awt.event.WindowEvent event) {
      Object object = event.getSource();
      if (object == DFGUI.this)
	DFGUI_WindowClosing(event);
    }
  }

  void DFGUI_WindowClosing(java.awt.event.WindowEvent event) {
    miExit_Action(null);
  }
	
  class SymAction implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent event)
      {
        		    textFieldErrMsg.setVisible(false);  
	Object object = event.getSource();
	if (object == miView)
	  miView_Action(event);
	else if (object == miAbout)
	  miAbout_Action(event);
	else if (object == miRegister)
	  miRegister_Action(event);
	else if (object == miModify)
	  miModify_Action(event);
	else if (object == miDeregister)
	  miDeregister_Action(event);
	else if (object == miRegisterDF)
	  miRegisterDF_Action(event);
	else if (object == miExit)
	  miExit_Action(event);
      }
  }

  void miAbout_Action(java.awt.event.ActionEvent event) {
    //{{CONNECTION
    // Action from About Create and show as modal
    (new AboutDialog(this, true)).setVisible(true);
    //}}
  } 

  void miExit_Action(java.awt.event.ActionEvent event) {
    // FIXME: add a window to ask are you sure the default DF is mandatory!?
    dispose();
    agent.doDelete();

  }
	
  void miView_Action(java.awt.event.ActionEvent event) {
    if (agent.getDFAgentDescriptors().hasMoreElements())
      (new agentDescriptionFrame("VIEW REGISTERED AGENT DESCRIPTIONS", agent.getDFAgentDescriptors(), VIEW ,agent)).setVisible(true); 
    else showErrorMsg("No data have yet been registered with the DF. Nothing to view");
  }

 void showErrorMsg(String s) {
    textFieldErrMsg.setText(s);
    textFieldErrMsg.setVisible(true);
 }

 void miModify_Action(java.awt.event.ActionEvent event) {
   showErrorMsg("NOT YET IMPLEMENTED");
   /*
    if (agent.getDFAgentDescriptors().hasMoreElements())
      (new agentDescriptionFrame("MODIFY REGISTERED AGENT DESCRIPTIONS", agent.getDFAgentDescriptors(), MODIFY ,agent)).setVisible(true); 
    else System.err.println("No data have yet been registered with the DF. Nothing to modify");
    */
  }

 void miDeregister_Action(java.awt.event.ActionEvent event) {
    if (agent.getDFAgentDescriptors().hasMoreElements())
      (new agentDescriptionFrame("DEREGISTER REGISTERED AGENT DESCRIPTIONS", agent.getDFAgentDescriptors(), DEREGISTER ,agent)).setVisible(true); 
    else showErrorMsg("No data have yet been registered with the DF. Nothing to deregister");
  }

 void miRegister_Action(java.awt.event.ActionEvent event) {
    (new agentDescriptionFrame("REGISTER NEW AGENT DESCRIPTION", null, REGISTER,agent)).setVisible(true); 
  }

 void miRegisterDF_Action(java.awt.event.ActionEvent event) {
    labelDFname.setVisible(true);
    textFieldDFname.setVisible(true);
    buttonDFname.setVisible(true);
    textFieldDFname.requestFocus();
  }


	//{{DECLARE_CONTROLS
	java.awt.TextField textFieldDFname;
	java.awt.Label labelDFname;
	java.awt.Button buttonDFname;
	java.awt.Canvas canvas1;
	java.awt.TextField textFieldErrMsg;
	//}}



	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
		    textFieldErrMsg.setVisible(false);
			Object object = event.getSource();
			if (object == buttonDFname)
				buttonDFname_MouseClicked(event);
		}
	}

	void buttonDFname_MouseClicked(java.awt.event.MouseEvent event)
	{
		// to do: code goes here.
		// Registration of this DF with another DF
		AgentManagementOntology.DFAgentDescriptor dfd = new AgentManagementOntology.DFAgentDescriptor();
		dfd.setName(agent.getName());
		dfd.addAddress(agent.getAddress());
		dfd.setType("DF");
		dfd.addInteractionProtocol("fipa-request");
		dfd.setOntology("fipa-agent-management");
		try {
		    dfd.setOwnership(InetAddress.getLocalHost().getHostName());
		} catch(UnknownHostException uhe) {
		}
		dfd.setDFState("active");

        AgentManagementOntology.ServiceDescriptor sd  = new AgentManagementOntology.ServiceDescriptor(); 
	    sd.setName("Federated-DF"); 
	    sd.setType("fipa-df");
	    sd.setOntology("fipa-agent-management");
	    sd.setFixedProps("(list (implemented-by CSELT) (version 0.94))");
	    dfd.addAgentService(sd);
        
	    String parentName = textFieldDFname.getText(); 
	    agent.postRegisterEvent(parentName, dfd);

	//{{CONNECTION
	// Hide the TextField
	textFieldDFname.setVisible(false);
	// Hide the Label
        labelDFname.setVisible(false);
        // Hide the Button
        buttonDFname.setVisible(false);
	//}}
	}
}









class AboutDialog extends Dialog {

  public AboutDialog(Frame parent, boolean modal) {
    super(parent, modal);

    // This code is automatically generated by Visual Cafe when you add
    // components to the visual environment. It instantiates and initializes
    // the components. To modify the code, only use code syntax that matches
    // what Visual Cafe can generate, or Visual Cafe may be unable to back
    // parse your Java file into its visual environment.

    //{{INIT_CONTROLS
    setLayout(null);
    setVisible(false);
    setSize(249,150);
    okButton = new java.awt.Button();
    okButton.setLabel("OK");
    okButton.setBounds(95,85,66,27);
    add(okButton);
    textArea1 = new java.awt.TextArea("",10,10,TextArea.SCROLLBARS_NONE);
    textArea1.setEditable(false);
    textArea1.setText("This is a FIPA97-compliant Directory Facilitator. It provides a yellow-page service to a multi-agent system. This GUI allows to manage this DF agent.");
    textArea1.setBounds(0,20,249,150);
    add(textArea1);
    setTitle("About the DF");
    //}}

    //{{REGISTER_LISTENERS
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);
    SymAction lSymAction = new SymAction();
    okButton.addActionListener(lSymAction);
    //}}

  }

  public AboutDialog(Frame parent, String title, boolean modal) {
    this(parent, modal);
    setTitle(title);
  }


  public void setVisible(boolean b) {
    if (b) {
      Rectangle bounds = getParent().getBounds();
      Rectangle abounds = getBounds();

      setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
		  bounds.y + (bounds.height - abounds.height)/2);
    }

    super.setVisible(b);
  }

  //{{DECLARE_CONTROLS
  java.awt.Button okButton;
  java.awt.TextArea textArea1;
  //}}



  class SymWindow extends java.awt.event.WindowAdapter {
    public void windowClosing(java.awt.event.WindowEvent event) {
      Object object = event.getSource();
      if (object == AboutDialog.this)
	AboutDialog_WindowClosing(event);
    }
  }

  void AboutDialog_WindowClosing(java.awt.event.WindowEvent event) {
    dispose();
  }

  class SymAction implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent event) {
      Object object = event.getSource();
      if (object == okButton)
	okButton_Clicked(event);
    }
  }

  void okButton_Clicked(java.awt.event.ActionEvent event) {
    //{{CONNECTION
    // Clicked from okButton Hide the Dialog
    dispose();
    //}}
  }
}








class agentDescriptionFrame extends Frame
{
	public agentDescriptionFrame()
	{
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		
		//{{INIT_CONTROLS
		setLayout(null);
		setVisible(false);
		setSize(680,550);
		setFont(new Font("Dialog", Font.BOLD, 12));
		label1 = new java.awt.Label("DF AGENT DESCRIPTION");
		label1.setBounds(250,20,290,25);
		label1.setFont(new Font("Dialog", Font.BOLD, 14));
		label1.setBackground(new Color(12632256));
		add(label1);
		label2 = new java.awt.Label("agent-name:");
		label2.setBounds(12,50,100,25);
		label2.setFont(new Font("Dialog", Font.BOLD, 12));
		label2.setBackground(new Color(12632256));
		add(label2);
		label3 = new java.awt.Label("agent-type:");
		label3.setBounds(400,50,80,25);
		label3.setFont(new Font("Dialog", Font.BOLD, 12));
		label3.setBackground(new Color(12632256));
		add(label3);
		label4 = new java.awt.Label("agent-address:");
		label4.setBounds(12,80,100,25);
		label4.setFont(new Font("Dialog", Font.BOLD, 12));
		label4.setBackground(new Color(12632256));
		add(label4);
		label5 = new java.awt.Label("ownership:");
		label5.setBounds(12,120,100,25);
		label5.setFont(new Font("Dialog", Font.BOLD, 12));
		label5.setBackground(new Color(12632256));
		add(label5);
		label6 = new java.awt.Label("df-state:");
		label6.setBounds(400,120,80,25);
		label6.setFont(new Font("Dialog", Font.BOLD, 12));
		label6.setBackground(new Color(12632256));
		add(label6);
		label7 = new java.awt.Label("language:");
		label7.setBounds(12,160,100,25);
		label7.setFont(new Font("Dialog", Font.BOLD, 12));
		label7.setBackground(new Color(12632256));
		add(label7);
		label8 = new java.awt.Label("ontology:");
		label8.setBounds(12,190,100,25);
		label8.setFont(new Font("Dialog", Font.BOLD, 12));
		label8.setBackground(new Color(12632256));
		add(label8);
		label9 = new java.awt.Label("interaction-protocols:");
		label9.setBounds(12,220,130,25);
		label9.setFont(new Font("Dialog", Font.BOLD, 12));
		label9.setBackground(new Color(12632256));
		add(label9);
		label10 = new java.awt.Label("AGENT SERVICES:");
		label10.setBounds(250,245,190,25);
		label10.setFont(new Font("Dialog", Font.BOLD, 14));
		label10.setBackground(new Color(12632256));
		add(label10);
		label11 = new java.awt.Label("Service no. 0 of 0");
		label11.setBounds(12,270,100,25);
		label11.setFont(new Font("Dialog", Font.BOLD, 10));
		label11.setBackground(new Color(12632256));
		add(label11);
		label12 = new java.awt.Label("service-name:");
		label12.setBounds(12,310,100,25);
		label12.setFont(new Font("Dialog", Font.BOLD, 12));
		label12.setBackground(new Color(12632256));
		add(label12);
		label13 = new java.awt.Label("service-type:");
		label13.setBounds(400,310,100,25);
		label13.setFont(new Font("Dialog", Font.BOLD, 12));
		label13.setBackground(new Color(12632256));
		add(label13);
		label14 = new java.awt.Label("service-ontology:");
		label14.setBounds(12,340,100,25);
		label14.setFont(new Font("Dialog", Font.BOLD, 12));
		label14.setBackground(new Color(12632256));
		add(label14);
		label15 = new java.awt.Label("fixed-properties:");
		label15.setBounds(12,380,160,25);
		label15.setFont(new Font("Dialog", Font.BOLD, 12));
		label15.setBackground(new Color(12632256));
		add(label15);
		label16 = new java.awt.Label("negotiable-properties:");
		label16.setBounds(12,410,160,25);
		label16.setFont(new Font("Dialog", Font.BOLD, 12));
		label16.setBackground(new Color(12632256));
		add(label16);
		label17 = new java.awt.Label("communication-properties:");
		label17.setBounds(12,440,160,25);
		label17.setFont(new Font("Dialog", Font.BOLD, 12));
		label17.setBackground(new Color(12632256));
		add(label17);

		button1 = new java.awt.Button();
		button1.setLabel("NextService");
		button1.setBounds(500,270,100,25);
		button1.setFont(new Font("Dialog", Font.BOLD, 10));
		button1.setBackground(new Color(12632256));
		add(button1);
		button2 = new java.awt.Button();
		button2.setLabel("PreviousService");
		button2.setBounds(380,270,100,25);
		button2.setFont(new Font("Dialog", Font.BOLD, 10));
		button2.setBackground(new Color(12632256));
		add(button2);
		button3 = new java.awt.Button();
		button3.setLabel("NextAgent");
		button3.setBounds(140,500,100,25);
		button3.setFont(new Font("Dialog", Font.BOLD, 12));
		button3.setBackground(new Color(12632256));
		add(button3);
		button4 = new java.awt.Button();
		button4.setLabel("OK");
		button4.setBounds(280,500,36,25);
		button4.setFont(new Font("Dialog", Font.BOLD, 12));
		button4.setBackground(new Color(12632256));
		add(button4);
		button5 = new java.awt.Button();
		button5.setLabel("EXIT");
		button5.setBounds(380,500,36,25);
		button5.setFont(new Font("Dialog", Font.BOLD, 12));
		button5.setBackground(new Color(12632256));
		add(button5);
		button6 = new java.awt.Button();
		button6.setLabel("AddNewService");
		button6.setBounds(260,270,100,25);
		button6.setFont(new Font("Dialog", Font.BOLD, 10));
		button6.setBackground(new Color(12632256));
		add(button6);
		button7 = new java.awt.Button();
		button7.setLabel("DelThisService");
		button7.setBounds(140,270,100,25);
		button7.setFont(new Font("Dialog", Font.BOLD, 10));
		button7.setBackground(new Color(12632256));
		add(button7);
		button8 = new java.awt.Button();
		button8.setLabel("PrevAgent");
		button8.setBounds(0,500,100,25);
		button8.setFont(new Font("Dialog", Font.BOLD, 12));
		button8.setBackground(new Color(12632256));
		add(button8);


		textField1 = new java.awt.TextField();
		textField1.setBounds(120,50,260,25);
		add(textField1);
		textField2 = new java.awt.TextField();
		textField2.setBounds(500,50,160,25);
		add(textField2); // agent-type
		textField3 = new java.awt.TextField();
		textField3.setBounds(120,80,260,25);
		add(textField3); // agent-address
		textField4 = new java.awt.TextField();
		textField4.setBounds(120,120,260,25);
		add(textField4);
		textField5 = new java.awt.TextField();
		textField5.setBounds(500,120,160,25);
		add(textField5); // df-state
		textField6 = new java.awt.TextField();
		textField6.setBounds(120,160,260,25);
		add(textField6); // language
		textField7 = new java.awt.TextField();
		textField7.setBounds(120,190,260,25);
		add(textField7); // ontology
		textField14 = new java.awt.TextField();
		textField14.setBounds(150,220,260,25);
		add(textField14); // interaction-protocols
		textField8 = new java.awt.TextField();
		textField8.setBounds(120,310,260,25);
		add(textField8); // service-name
		textField9 = new java.awt.TextField();
		textField9.setBounds(500,310,160,25);
		add(textField9); // service-type
		textField10 = new java.awt.TextField();
		textField10.setBounds(120,340,260,25);
		add(textField10); // service-ontology
		textField11 = new java.awt.TextField();
		textField11.setBounds(180,380,450,25);
		add(textField11); // fixed-properties
		textField12 = new java.awt.TextField();
		textField12.setBounds(180,410,450,25);
		add(textField12);
		textField13 = new java.awt.TextField();
		textField13.setBounds(180,440,450,25);
		add(textField13); // communication-properties

		canvas1 = new java.awt.Canvas();
		canvas1.setBounds(0,0,680,550);
		canvas1.setBackground(new Color(12632256));
		add(canvas1);

		setTitle("Agent Description");
		//}}
		
		//{{INIT_MENUS
		//}}
		
		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymMouse aSymMouse = new SymMouse();
		button1.addMouseListener(aSymMouse);
		button2.addMouseListener(aSymMouse);
		button3.addMouseListener(aSymMouse);
		button4.addMouseListener(aSymMouse);
		button5.addMouseListener(aSymMouse);
		button6.addMouseListener(aSymMouse);
		button7.addMouseListener(aSymMouse);
		button8.addMouseListener(aSymMouse);
		//}}
	}
	
	public agentDescriptionFrame(String title)
	{
		this();
		setTitle(title);
	}

    /**
    * This constructor is used by DFGUI
    * @param title is the title of the Window
    * @param data is the structure with the data of the user
    * @param usage is one of the constants in DFGUI and indicates the usage of
    * @param thisAgent is the agent that executes this frame and it is used
    * to actually send messages
    * this DFGUI (REGISTER, MODIFY, VIEW, DEREGISTER)
    */
	public agentDescriptionFrame(String title, Enumeration data, int useFor, df thisAgent) {
	    this(title);
	    myAgent = thisAgent;
	    usage = useFor;
	    allData = data;
	    switch (usage) {
	    case DFGUI.REGISTER: {
	      currentDFAgentDescriptor = new AgentManagementOntology.DFAgentDescriptor();
	      button3.setVisible(false); // NextAgent
	      button8.setVisible(false); // button PreviousAgent
	      
	      setServiceVisible(false);
	      setAllEditable(true);
	      break;
	    }
	    case DFGUI.MODIFY: { 
	      setAllEditable(true);
	      initializeDFAgentDescriptors(data);	            	   
	      updateTextFields();
	      if (! allDFAgentDescriptors.hasMoreElements())
		button3.setVisible(false); // button NextAgent
	      if (! allServiceDescriptors.hasMoreElements())
		button1.setVisible(false); // button NextService
	      button2.setVisible(false); // button PreviousService
	      button8.setVisible(false); // button PreviousAgent
	      break;
	    }
	    case DFGUI.DEREGISTER: {
	      button6.setVisible(false); // AddNewService
	      button7.setVisible(false); // DelThisService
	      setAllEditable(false);
	      initializeDFAgentDescriptors(data);	            	      
	      updateTextFields();
	      if (! allDFAgentDescriptors.hasMoreElements())
		button3.setVisible(false); // button NextAgent
	      if (! allServiceDescriptors.hasMoreElements())
		button1.setVisible(false); // button NextService
	      button2.setVisible(false); // button PreviousService
	      button8.setVisible(false); // button PreviousAgent
	      break;
	    }
	    case DFGUI.VIEW: {
	      button4.setVisible(false);
	      button6.setVisible(false); // AddNewService
	      button7.setVisible(false); // DelThisService
	      setAllEditable(false);
	      initializeDFAgentDescriptors(data);	            	      
	      updateTextFields();
	      if (! allDFAgentDescriptors.hasMoreElements())
		button3.setVisible(false); // button NextAgent
	      if (! allServiceDescriptors.hasMoreElements())
		button1.setVisible(false); // button NextService
	      button2.setVisible(false); // button PreviousService
	      button8.setVisible(false); // button PreviousAgent
	      break;
	    }
	    }
	}

/**
* This method initializes all the class variables related to the display of
* the DFAgentDescriptors
*/
void initializeDFAgentDescriptors(Enumeration data) {
  allDFAgentDescriptors = data; 
  if (allDFAgentDescriptors.hasMoreElements()) {
    currentDFAgentDescriptor       = (AgentManagementOntology.DFAgentDescriptor)allDFAgentDescriptors.nextElement();
    currentDFAgentDescriptorNumber++;
  }
  else currentDFAgentDescriptor = null;


  allServiceDescriptors          = currentDFAgentDescriptor.getAgentServices();

  if (allServiceDescriptors.hasMoreElements())
      currentServiceDescriptor       = (AgentManagementOntology.ServiceDescriptor)allServiceDescriptors.nextElement();
  else currentServiceDescriptor = null;
  Enumeration e = currentDFAgentDescriptor.getAgentServices();  
  Object o;  
  numberOfServiceDescriptors     = 0; 
  while (e.hasMoreElements()) {
    numberOfServiceDescriptors ++;
    o=e.nextElement();
  }

  currentServiceDescriptorNumber = 0;
}

  void setServiceVisible(boolean tf) {
    textField8.setVisible(tf);
    textField9.setVisible(tf);
    textField10.setVisible(tf);
    textField11.setVisible(tf);
    textField12.setVisible(tf);
    textField13.setVisible(tf);
    label12.setVisible(tf);
    label13.setVisible(tf);
    label14.setVisible(tf);
    label15.setVisible(tf);
    label16.setVisible(tf);
    label17.setVisible(tf);
    button1.setVisible(tf);
    button2.setVisible(tf);
    label11.setVisible(tf);
  }

void setAllEditable(boolean tf) {
  // all textfields and textareas must become noteditable
  textField1.setEditable(tf);
  textField2.setEditable(tf);
  textField3.setEditable(tf);
  textField4.setEditable(tf);
  textField5.setEditable(tf);
  textField6.setEditable(tf);
  textField7.setEditable(tf);
  textField8.setEditable(tf);
  textField9.setEditable(tf);
  textField10.setEditable(tf);
  textField11.setEditable(tf);
  textField12.setEditable(tf);
  textField13.setEditable(tf);
  textField14.setEditable(tf);
}
	
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
	  setLocation(50, 50);
	  super.setVisible(b);
	}
	


  void updateTextFields() {
    label1.setText("DF AGENT DESCRIPTION no. "+currentDFAgentDescriptorNumber); //+" of " + numberOfDFAgentDescriptors);
    textField1.setText(currentDFAgentDescriptor.getName());
    textField2.setText(currentDFAgentDescriptor.getType());
    textField7.setText(currentDFAgentDescriptor.getOntology());
    textField4.setText(currentDFAgentDescriptor.getOwnership());
    textField5.setText(currentDFAgentDescriptor.getDFState());
    String str = "";
    Enumeration e = currentDFAgentDescriptor.getAddresses();
    while (e.hasMoreElements())
      str = str + (String)e.nextElement()+ " ";
    textField3.setText(str);
    str = "";
    e = currentDFAgentDescriptor.getInteractionProtocols();
    while (e.hasMoreElements())
      str = str + (String)e.nextElement() + " ";
    textField14.setText(str); 
    label11.setText("Service no. " + (currentServiceDescriptorNumber+1) + " of " + numberOfServiceDescriptors); 
    if (currentServiceDescriptor != null) {
      setServiceVisible(true);
      textField8.setText(currentServiceDescriptor.getName());
      textField9.setText(currentServiceDescriptor.getType());
      textField10.setText(currentServiceDescriptor.getOntology());
      textField11.setText(currentServiceDescriptor.getFixedProps());
      textField12.setText(currentServiceDescriptor.getNegotiableProps());
      textField13.setText(currentServiceDescriptor.getCommunicationProps());
    } else {
      setServiceVisible(false);
    }
  }

  Enumeration allData;
  Enumeration allDFAgentDescriptors;
  AgentManagementOntology.DFAgentDescriptor currentDFAgentDescriptor;
  int         currentDFAgentDescriptorNumber;
  int         numberOfDFAgentDescriptors;
  Enumeration allServiceDescriptors;
  AgentManagementOntology.ServiceDescriptor currentServiceDescriptor;
  int         numberOfServiceDescriptors;
  int         currentServiceDescriptorNumber; 

  int         usage = 0; // this class variable indicates for what usage the
	                   // frame has been created
 
  df       myAgent; // pointer to the agent to send messages
       
	//{{DECLARE_CONTROLS
	java.awt.Label label1;
	java.awt.Label label2;
	java.awt.Label label3;
	java.awt.Label label4;
	java.awt.Label label5;
	java.awt.Label label6;
	java.awt.Label label7;
	java.awt.Label label8;
	java.awt.Label label9;
	java.awt.Label label10;
	java.awt.Label label11;
	java.awt.Label label12;
	java.awt.Label label13;
	java.awt.Label label14;
	java.awt.Label label15;
	java.awt.Label label16;
	java.awt.Label label17;

	java.awt.TextField textField1;
	java.awt.TextField textField2;
	java.awt.TextField textField3;
	java.awt.TextField textField4;
	java.awt.TextField textField5;
	java.awt.TextField textField6;
	java.awt.TextField textField7;
	java.awt.TextField textField8;
	java.awt.TextField textField9;
	java.awt.TextField textField10;
	java.awt.TextField textField11;
	java.awt.TextField textField12;
	java.awt.TextField textField13;
	java.awt.TextField textField14;



	java.awt.Button button1;
	java.awt.Button button2;
	java.awt.Button button3;
	java.awt.Button button4;
	java.awt.Button button5;
	java.awt.Button button6;
	java.awt.Button button7;
	java.awt.Button button8;

	java.awt.Canvas canvas1;
	//}}
	
	//{{DECLARE_MENUS
	//}}
	
	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == agentDescriptionFrame.this)
				DFGUI_WindowClosing(event);
		}
	}
	
	void DFGUI_WindowClosing(java.awt.event.WindowEvent event)
	{
	  miExit_Action(null);
	}
	
	
	
	void miAbout_Action(java.awt.event.ActionEvent event)
	{
		//{{CONNECTION
		// Action from About Create and show as modal
		(new AboutDialog(this, true)).setVisible(true);
		//}}
	}
	
	void miExit_Action(java.awt.event.ActionEvent event)
	{
	    setVisible(false);	// hide the Frame
	    dispose();			// free the system resources
	}
	
	



	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == button1)
				button1_MouseClicked(event);
			else if (object == button2)
				button2_MouseClicked(event);
			else if (object == button3)
				button3_MouseClicked(event);
			else if (object == button4)
				button4_MouseClicked(event);
			else if (object == button5)
				button5_MouseClicked(event);
			else if (object == button6)
				button6_MouseClicked(event);
			else if (object == button7)
				button7_MouseClicked(event);
			else if (object == button8)
				button8_MouseClicked(event);
		}
	}

	void button1_MouseClicked(java.awt.event.MouseEvent event)
  { // NextService
    if (usage == DFGUI.REGISTER) { // FIXME
      System.err.println("Next Service during Registration. Not yet implemented.");
      return;
    }
  if (allServiceDescriptors.hasMoreElements())
      currentServiceDescriptor       = (AgentManagementOntology.ServiceDescriptor)allServiceDescriptors.nextElement();    
  currentServiceDescriptorNumber++;
  updateTextFields();
  if (! allServiceDescriptors.hasMoreElements())
    button1.setVisible(false); // button NextService
  button2.setVisible(true); // button PreviousService
  }

  void button2_MouseClicked(java.awt.event.MouseEvent event)
  { 
    if (usage == DFGUI.REGISTER) { // FIXME
      System.err.println("Previous Service during Registration. Not yet implemented.");
      return;
    }
  if (currentServiceDescriptorNumber < 1) {
    button2.setVisible(false);
  } else {
    allServiceDescriptors = currentDFAgentDescriptor.getAgentServices();
    for (int i=0; i<currentServiceDescriptorNumber; i++)
      currentServiceDescriptor = (AgentManagementOntology.ServiceDescriptor)allServiceDescriptors.nextElement();
    currentServiceDescriptorNumber--; 
    updateTextFields();
    if (! allServiceDescriptors.hasMoreElements())
    button1.setVisible(false); // button NextService
    if (currentServiceDescriptorNumber > 0)
      button2.setVisible(true); // button PreviousService
    else button2.setVisible(false);
  }
  }

  void button3_MouseClicked(java.awt.event.MouseEvent event)
  { // NextAgent
    initializeDFAgentDescriptors(allDFAgentDescriptors);
    updateTextFields();
    if (! allDFAgentDescriptors.hasMoreElements())
      button3.setVisible(false); // button NextAgent
    if (! allServiceDescriptors.hasMoreElements())
      button1.setVisible(false); // button NextService
    button2.setVisible(false); // button PreviousService
  }

	void button4_MouseClicked(java.awt.event.MouseEvent event)
  { // OK
    // can only be called to register/deregister/modify
    if (usage == DFGUI.REGISTER) {
      if (textField1.getText().length() > 0) currentDFAgentDescriptor.setName(textField1.getText());
      if (textField2.getText().length() > 0) currentDFAgentDescriptor.setType(textField2.getText());
      if (textField7.getText().length() > 0) currentDFAgentDescriptor.setOntology(textField7.getText());
      if (textField4.getText().length() > 0) currentDFAgentDescriptor.setOwnership(textField4.getText());
      if (textField5.getText().length() > 0) currentDFAgentDescriptor.setDFState(textField5.getText());
      if (textField3.getText().length() > 0) currentDFAgentDescriptor.addAddress(textField3.getText());
      if (textField13.getText().length() > 0) currentDFAgentDescriptor.addInteractionProtocol(textField13.getText());
      addAgentService();
      myAgent.postRegisterEvent(myAgent.getName(),currentDFAgentDescriptor);
    } else if (usage == DFGUI.DEREGISTER) {
      // currentDFAgentDescriptor already contains the description
      try {
	myAgent.deregisterWithDF(myAgent.getName(),currentDFAgentDescriptor);
      } catch (jade.domain.FIPAException f) {
	// FIXME far vedere il testo della eccezione su schermo
	System.err.println("deregisterWithDF Exception: "+f.getMessage());
      } 
    } else if (usage == DFGUI.MODIFY) {
      ACLMessage msg = new ACLMessage("request");
      msg.setSource(myAgent.getName());
      msg.setDest(myAgent.getName());
      msg.setOntology("fipa-agent-management");
      msg.setLanguage("SL0");
      msg.setProtocol("fipa-request");
      msg.setReplyWith(myAgent.getLocalName()+"-dfgui"+(new Date()).getTime());
      AgentManagementOntology.DFAction a = new AgentManagementOntology.DFAction();
      AgentManagementOntology.DFAgentDescriptor dfd = new AgentManagementOntology.DFAgentDescriptor();
      if (textField1.getText().length() > 0) dfd.setName(textField1.getText());
      if (textField2.getText().length() > 0) dfd.setType(textField2.getText());
      if (textField7.getText().length() > 0) dfd.setOntology(textField7.getText());
      if (textField4.getText().length() > 0) dfd.setOwnership(textField4.getText());
      if (textField5.getText().length() > 0) dfd.setDFState(textField5.getText());
      if (textField3.getText().length() > 0) dfd.addAddress(textField3.getText());
      if (textField13.getText().length() > 0) dfd.addInteractionProtocol(textField13.getText());
      // FIXME what to do here?
      addAgentService();
      a.setName(AgentManagementOntology.DFAction.MODIFY);
      a.setActor(myAgent.getName());
      a.setArg(dfd);
      StringWriter text = new StringWriter();
      a.toText(text);
      msg.setContent(text.toString()); 
      //msg.toText(new BufferedWriter(new OutputStreamWriter(System.out)));
      myAgent.send(msg);    
    }    

    dispose(); // close the window
  }

  void button5_MouseClicked(java.awt.event.MouseEvent event)
  { // Exit
    dispose();
  }

  /**
   * This method creates a new ServiceDescriptor, fills all its fields
   * with all those text fields that have been filled in the GUI, 
   * resets the textfields in the GUI, add the service description to the
   * current agent description, and updates the value of the variable 
   * numberOfServiceDescriptors
   */
  void addAgentService() {
	  AgentManagementOntology.ServiceDescriptor sd  = new AgentManagementOntology.ServiceDescriptor(); 
	  boolean sd_isnotempty=false;
	  if (textField8.getText().length() > 0) {
	    sd.setName(textField8.getText());
	    textField8.setText("");
	    sd_isnotempty = true; }
	  if (textField9.getText().length() > 0) {
	    sd.setType(textField9.getText());
	    textField9.setText("");
	    sd_isnotempty = true; }
	  if (textField10.getText().length() > 0) {
	    sd.setOntology(textField10.getText());
	    textField10.setText("");
	    sd_isnotempty = true; }
	  if (textField11.getText().length() > 0) {
	    sd.setFixedProps(textField11.getText());
	    textField11.setText("");
	    sd_isnotempty = true; }
	  if (textField12.getText().length() > 0) {
	    sd.setNegotiableProps(textField12.getText());
	    textField12.setText("");
	    sd_isnotempty = true; }
	  if (textField13.getText().length() > 0) {
	    sd.setCommunicationProps(textField13.getText());
	    textField13.setText("");
	    sd_isnotempty = true; }
	  
	  if (sd_isnotempty) {
	    currentDFAgentDescriptor.addAgentService(sd);
	    numberOfServiceDescriptors++;
	  }
  }

  void button6_MouseClicked(java.awt.event.MouseEvent event)
  { // Add New Service
    addAgentService();
    currentServiceDescriptorNumber++;
    label11.setText("Service no. " + currentServiceDescriptorNumber + " of " + numberOfServiceDescriptors);     
    setServiceVisible(true);
  }

  void button7_MouseClicked(java.awt.event.MouseEvent event)
  { // Delete this Service
    //FIXME. To DO
    System.err.println("Delete this service. Not yet Implemented.");
  }

  void button8_MouseClicked(java.awt.event.MouseEvent event)
  { // Previous Agent
    //FIXME. To DO
  if (currentDFAgentDescriptorNumber < 1) {
    button8.setVisible(false);
  } else {
    currentDFAgentDescriptorNumber = -1;
    initializeDFAgentDescriptors(allData);
    for (int i=0; i<currentDFAgentDescriptorNumber; i++)
      initializeDFAgentDescriptors(allDFAgentDescriptors);
    currentDFAgentDescriptorNumber--; 
    updateTextFields();
    if (! allServiceDescriptors.hasMoreElements())
    button1.setVisible(false); // button NextService
    if (currentServiceDescriptorNumber > 0)
      button2.setVisible(true); // button PreviousService
    else button2.setVisible(false);
  }
  }
}



