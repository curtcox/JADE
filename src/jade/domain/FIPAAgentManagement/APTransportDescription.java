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


package jade.domain.FIPAAgentManagement;

import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import java.io.Writer;
import java.io.IOException;

import jade.content.*;

/**
   Description of the transport services available on an agent
   platform.

   @author Fabio Bellifemine - CSELT
   @version $Date$ $Revision$

 */
public class APTransportDescription implements Concept {

  private List mtps = new ArrayList(); 

  public void addAvailableMtps(MTPDescription s) {
    mtps.add(s);
  }

  public boolean removeAvailableMtps(MTPDescription s) {
    return mtps.remove(s);
  }

  public void clearAllAvailableMtps() {
    mtps.clear();
  }

  public Iterator getAllAvailableMtps() {
    return mtps.iterator();
  
  }

    public String toString() {
	StringBuffer str = new StringBuffer("( ap-transport-description ");
	if(mtps.size() > 0) {
	    str.append(" :available-mtps (set ");
	    for(int i=0; i<mtps.size(); i++){
		try{
		    str.append(((MTPDescription)mtps.get(i)).toString());
		}catch(IndexOutOfBoundsException iob){iob.printStackTrace();}
		str.append(" ");
	    }
	    str.append(")");
	}
	str.append(")");
	return str.toString();
    }

    /**
     * @deprecated the <code>toString</code> method should be used instead
     * of this method.
     **/

	public void toText(Writer w) {
    	  try {
	  		w.write(toString());
	  		w.flush();
      	} catch(IOException ioe) {
	  		ioe.printStackTrace();
      	}
	}

}
