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

package jade.core;

/**
   @author Giovanni Rimassa - Universita` di Parma
   @version $Date$ $Revision$
 */
class JADEClassLoader extends ClassLoader {

  private AgentContainer classServer;

  public JADEClassLoader(AgentContainer ac) {
    classServer = ac;
  }

  protected Class findClass(String name) throws ClassNotFoundException {
    byte[] classFile;

    // System.out.println("JADEClassLoader: findClass: " + name);

    try {
      classFile = classServer.fetchClassFile(name);
    }
    catch (IMTPException re) {
      throw new ClassNotFoundException();
    } 

    if (classFile != null) {
      return defineClass(null, classFile, 0, classFile.length);
    }
    else
      throw new ClassNotFoundException();
  }
  
  /*__PJAVA_COMPATIBILITY__BEGIN In PersonalJava loadClass(String, boolean) is abstract --> we must implement it
  protected Class loadClass(String name,	
    	                    boolean resolve) throws ClassNotFoundException {
  	// 1) Try to see if the class has already been loaded
  	Class c = findLoadedClass(name);
  	
		// 2) Try to load the class using the system class loader
  	if (c == null) {
  		try {
  			c = findSystemClass(name);
  		}
  		catch (ClassNotFoundException cnfe) {
  		}
  	}
  	
  	// 3) If still not found, try to load the class from the proper site
  	if (c == null) {
  		c = findClass(name);
  	}
  	
  	if (resolve) {
  		resolveClass(c);
  	}
  	return c;
	}  	
	__PJAVA_COMPATIBILITY__END*/
}
