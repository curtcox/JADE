/*
  $Log$
  Revision 1.3  1998/10/10 19:37:19  rimassa
  Imported a newer version of JADE GUI from Fabio.

  Revision 1.2  1998/10/04 18:01:41  rimassa
  Added a 'Log:' field to every source file.
*/

package jade.gui;

import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
 
public class PopupMouser extends MouseAdapter
{
    JPopupMenu popup;
    
    public PopupMouser(JPopupMenu p)
    {
        popup = p;
    }

    public void mouseReleased(MouseEvent e)
    {
        if (e.isPopupTrigger())
        { 
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void mousePressed(MouseEvent e)
    {
        if (e.isPopupTrigger())
        { 
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}

