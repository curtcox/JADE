/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.content.onto;

import jade.core.CaseInsensitiveString;
import jade.content.*;
import jade.content.abs.*;
import jade.content.schema.*;
import jade.util.leap.List;
import jade.util.leap.Iterator;
import java.lang.reflect.*;

/**
 * Backward Compatible reflective introspector. This Introspector 
 * uses Java Reflection to translate java object to/from abstract
 * descriptors as the <code>ReflectiveIntrospector</code> does, but 
 * it assumes the accessors methods for aggregate slots to be in the 
 * "old JADE style" i.e.
 * <i> For every aggregate <b>slot</b> named <code>XXX</code>,
 * with elements of type <code>T</code>, the Java class must have 
 * two accessible methods, with the following signature:</i>
 * <ul>
 *  	<li> <code>Iterator getAllXXX()</code>
 *  	<li> <code>void addXXX(T t)</code>
 * </ul>
 * @author Giovanni Caire - TILAB
 */
public class BCReflectiveIntrospector extends ReflectiveIntrospector {

	/**
   * Translate an object of a class representing an element in an
   * ontology into a proper abstract descriptor.  
   * @param obj The Object to be translated
   * @param schema The schema for the ontological element this object
   * is an instance of.
   * @param javaClass The class of the Object to be translated
   * @param referenceOnto The reference ontology in the context of
   * this translation. 
   * @return The Abstract descriptor produced by the translation 
   * @throws OntologyException If some error occurs during the translation
   */
  public AbsObject externalise(Object obj, ObjectSchema schema, Class javaClass, Ontology referenceOnto) 
    				throws OntologyException {
  	try {
      AbsObject    abs = schema.newInstance();
            
      String[]     names = schema.getNames();
      // Loop on slots
      for (int i = 0; i < names.length; ++i) {
      	String slotName = names[i];
      	ObjectSchema slotSchema = schema.getSchema(slotName);
      	
      	String methodName;
      	if (slotSchema instanceof AggregateSchema) {
					methodName = "getAll" + translateName(slotName);
      	}
      	else {
					methodName = "get" + translateName(slotName);
      	}

      	// Retrieve the accessor method from the class and call it
      	Method getMethod = findMethodCaseInsensitive(methodName, javaClass);
        AbsObject value = invokeGetMethod(referenceOnto, getMethod, obj);
        //DEBUG 
        //System.out.println("Attribute value is: "+value);

        if (value != null) {
          AbsHelper.setAttribute(abs, slotName, value);
        } 
      }
    	return abs;
  	}
    catch (OntologyException oe) {
    	throw oe;
    } 
    catch (Throwable t) {
    	throw new OntologyException("Schema and Java class do not match", t);
    } 
  } 

  /**
   * Translate an abstract descriptor into an object of a proper class 
   * representing an element in an ontology 
   * @param abs The abstract descriptor to be translated
   * @param schema The schema for the ontological element this abstract descriptor
   * is an instance of.
   * @param javaClass The class of the Object to be produced by the translation
   * @param referenceOnto The reference ontology in the context of
   * this translation. 
   * @return The Java object produced by the translation 
   * @throws UngroundedException If the abstract descriptor to be translated 
   * contains a variable
   * @throws OntologyException If some error occurs during the translation
   */
  public Object internalise(AbsObject abs, ObjectSchema schema, Class javaClass, Ontology referenceOnto) 
            throws UngroundedException, OntologyException {

  	try {     
      Object       obj = javaClass.newInstance();
      String[]     names = schema.getNames();

    	// LOOP on slots 
    	for (int i = 0; i < names.length; ++i) {
      	String slotName = names[i];
      	AbsObject value = abs.getAbsObject(slotName);
      	if (value != null) {
	      	ObjectSchema slotSchema = schema.getSchema(slotName);
      	
  	    	String methodName;
    	  	if (slotSchema instanceof AggregateSchema) {
						methodName = "add" + translateName(slotName);
      			Method addMethod = findMethodCaseInsensitive(methodName, javaClass);
      			invokeAddMethod(referenceOnto, addMethod, obj, (AbsAggregate) value);
      		}
      		else {
 						methodName = "set" + translateName(slotName);
      			// Retrieve the modifier method from the class and call it
      			Method setMethod = findMethodCaseInsensitive(methodName, javaClass);
          	invokeSetMethod(referenceOnto, setMethod, obj, value);
      		}
        } 
    	}
      return obj;
  	}
    catch (OntologyException oe) {
    	throw oe;
    } 
    catch (InstantiationException ie) {
      throw new OntologyException("Class "+javaClass+" can't be instantiated", ie);
    } 
    catch (IllegalAccessException iae) {
      throw new OntologyException("Class "+javaClass+" does not have an accessible constructor", iae);
    } 
    catch (Throwable t) {
    	throw new OntologyException("Schema and Java class do not match", t);
    } 
  } 

  private void invokeAddMethod(Ontology onto, Method method, Object obj, 
                                 AbsAggregate value) throws OntologyException {
  	try {
      List l = (List) onto.toObject(value);

      if (l == null) {
        return;
      } 

      Iterator it = l.iterator();
      while (it.hasNext()) {
      	Object objValue = it.next();
	      Object[] params = new Object[] {objValue};
      	try {
	      	method.invoke(obj, params);
      	}
      	catch (IllegalArgumentException iae) {
      		// Maybe the method required an Integer argument and we supplied 
        	// a Long. Similarly maybe the the method required a Float and 
        	// we supplied a Double. Try these possibilities
        	if (objValue instanceof Long) {
        		Integer i = new Integer((int) ((Long) objValue).longValue());
        		params[0] = i;
        	}
        	else if (objValue instanceof Double) {
        		Float f = new Float((float) ((Double) objValue).doubleValue());
        		params[0] = f;
        	}
        	method.invoke(obj, params);
      	}
      }
    } 
    catch (OntologyException oe) {
      // Forward the exception
      throw oe;
    } 
    catch (Exception e) {
      throw new OntologyException("Error invoking add method", e);
    } 
  } 

  /**
     Check the structure of a java class associated to an ontological element 
     to ensure that translations to/from abstract descriptors and java objects
     (instances of that class) can be accomplished by this introspector.
     @param schema The schema of the ontological element
     @param javaClass The java class associated to the ontologcal element
     @throws OntologyException if the java class does not have the correct 
     structure
   */
  public void checkClass(ObjectSchema schema, Class javaClass) throws OntologyException {
  	// FIXME: Not yet implemented
  }
    
}

