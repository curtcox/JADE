/*
  $Log$
  Revision 1.1  1999/05/24 21:20:17  rimassa
  Added new DummyAgent tool agent.

  Revision 1.1  1999/05/20 12:59:21  bellifemine
  Initial revision


*/

package jade.tools.DummyAgent;

import javax.swing.*;
import java.util.Properties;
import java.io.*;
/**
 * This class encapsulates some informations used by the program
 */
public class GuiProperties {
  protected static UIDefaults MyDefaults;
  protected static GuiProperties foo = new GuiProperties();
  public static final String ImagePath = "";
  static {
    Object[] icons = {
      "delete",LookAndFeel.makeIcon(foo.getClass(), "images/delete.gif"),
      "new",LookAndFeel.makeIcon(foo.getClass(), "images/new.gif"),
      "open", LookAndFeel.makeIcon(foo.getClass(), "images/open.gif"),
      "openq",LookAndFeel.makeIcon(foo.getClass(), "images/openq.gif"),
      "save",LookAndFeel.makeIcon(foo.getClass(), "images/save.gif"),
      "saveq",LookAndFeel.makeIcon(foo.getClass(), "images/saveq.gif"),
      "send",LookAndFeel.makeIcon(foo.getClass(), "images/send.gif"),
      "set",LookAndFeel.makeIcon(foo.getClass(), "images/set.gif"),
      "view",LookAndFeel.makeIcon(foo.getClass(), "images/view.gif")
    };

    MyDefaults = new UIDefaults (icons);
  }

  public static final Icon getIcon(String key) {
    Icon i = MyDefaults.getIcon(key);
    if (i == null) {
      System.out.println(key);
      System.exit(-1);
      return null;
    }
    else return MyDefaults.getIcon(key);
  }
}

