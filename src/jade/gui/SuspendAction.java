/*
  $Log$
  Revision 1.4  1999/02/04 14:47:31  rimassa
  Changed package specification for Swing: now it's 'javax.swing' and no more
  'com.sun.swing'.

  Revision 1.3  1998/10/10 19:37:27  rimassa
  Imported a newer version of JADE GUI from Fabio.

  Revision 1.2  1998/10/04 18:01:41  rimassa
  Added a 'Log:' field to every source file.
*/

package jade.gui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.*;

/**
 * SuspendAction suspends selected agents
 * @see jade.gui.AMSAbstractAction
 */
public class SuspendAction extends AMSAbstractAction
{
	public SuspendAction()
	{
		super ("SuspendActionIcon","Suspend Selected Agents");
	}

	public void actionPerformed(ActionEvent evt)
    {
        for (int i=0;i<listeners.size();i++)
		{
			System.out.print(ActionName);
			System.out.print(listeners.elementAt(i).toString());
			if (listeners.elementAt(i) instanceof TreeData)
				( (TreeData)listeners.elementAt(i)).setState(TreeData.SUSPENDED);
			System.out.println();
		}
		listeners.removeAllElements();
    }


}

