package jade.onto;


public interface Ontology {

  /**
     Boolean constant for <it>Optional</it>.
   */
  public static final boolean O = true;

  /**
     Boolean constant for <it>Mandatory</it>.
   */
  public static final boolean M = false;

  // Constants for the various term types.

  /**
     Constant for <code>boolean</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short BOOLEAN_TYPE = 0;

  /**
     Constant for <code>byte</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short BYTE_TYPE = 1;

  /**
     Constant for <code>char</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short CHARACTER_TYPE = 2;

  /**
     Constant for <code>double</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short DOUBLE_TYPE = 3;

  /**
     Constant for <code>float</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short FLOAT_TYPE = 4;

  /**
     Constant for <code>int</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short INTEGER_TYPE = 5;

  /**
     Constant for <code>long</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short LONG_TYPE = 6;

  /**
     Constant for <code>short</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short SHORT_TYPE = 7;

  /**
     Constant for <code>String</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short STRING_TYPE = 8;

  /**
     Constant for <code>byte[]</code> type in a <code>TermDescriptor</code>.
     @see jade.onto.TermDescriptor
   */
  static final short BINARY_TYPE = 9;

  /**
     Constant for <code>Frame</code> type in a
     <code>TermDescriptor</code>. Ontology concepts can be represented
     as <code>Frame</code> instances or as instances of a user-defined
     class, obeying to some rules.
     @see jade.onto.TermDescriptor
     @see jade.onto.Frame
     @see jade.onto.Ontology#addClass(String roleName, Class c)
  */
  static final short CONCEPT_TYPE = 10;


  /**
     Constant for <code>Action</code> type in a
     <code>TermDescriptor</code>. Ontology actions can be represented
     as <code>Action</code> instances or as instances of a user-defined
     class, obeying to some rules.
     @see jade.onto.TermDescriptor
     @see jade.onto.Action
     @see jade.onto.Ontology#addClass(String roleName, Class c)
  */
  static final short ACTION_TYPE = 11;

  /**
     Constant for <code>Predicate</code> type in a
     <code>TermDescriptor</code>. Ontology predicates can be represented
     as <code>Predicate</code> instances or as instances of a user-defined
     class, obeying to some rules.
     @see jade.onto.TermDescriptor
     @see jade.onto.Predicate
     @see jade.onto.Ontology#addClass(String roleName, Class c)
  */
  static final short PREDICATE_TYPE = 12;

  /**
     String array of names of the various types allowed for elements
     of ontological elements. It can be indexed using the constants in
     the Ontology interface.
  */
  static final String typeNames[] = { "boolean", "byte", "char", "double",
				      "float", "int", "long", "short",
				      "String", "Binary", "Concept", "Action", "Predicate" };

  /**
     Adds a user-defined class to this Ontology, to play a certain
     role. A role can be a <b><it>concept</it></b>, an
     <b><it>action</it></b>, or a <b><it>predicate</it></b> of the
     ontology. The role played by the given class must have been
     previously inserted into this Ontology using one of the three
     methods <code>addConcept()</code>, <code>addAction()</code> or
     <code>addPredicate()</code>. This associates an array of
     <code>TermDescriptor</code> objects with a given role. The
     supplied class must obey to some rules, in order to be accepted
     by the Ontology object:

     <ol>

     <li><it> The class must have an accessible method, named
     <code>init()</code>, with the following signature:</it>
       <ul>
       <li> <code>void init(Frame f)</code> <it>, for concept roles</it>
       <li> <code>void init(Action a)</code> <it>, for action roles</it>
       <li> <code>void init(Predicate p)</code> <it>, for predicate roles</it>
       </ul>

     <li><it> For every <code>TermDescriptor</code> object of the
     array, of type <code>T</code> and named <code>XXX</code>, the
     class must have two accessible methods, with the following
     signature:</it>
       <ul>
       <li> <code>T getXXX()</code>
       <li> <code>void setXXX(T t)</code>
       </ul>

     <li><it> For an action role, the class must also have an
     accessible method, with the following signature:</it>
       <ul>
       <li> <code>String __actor()</code> <it>(double underscore</it>
       </ul>
     <it>This method is meant to return the agent name that is to do the action.

     </ol>

     As long as the above rules are followed, any user-defined class
     can be added to the Ontology object. As an useful technique, one
     can define compliant Java interfaces and add them to the
     Ontology; this way useful OO idioms such as polymorphism and
     mix-in inheritance can be exploited for the Java representations
     of ontological objects.

     Due to different lexical conventions between the Java language
     and FIPA ACL and content languages, some name translation is
     performed to map the name of a term (that is, of a frame slot or
     of an action argument) into the name of the corresponding method.
     Name translation works as follows:
     <ol>
     <li> Any <code>':'</code> character is removed.
     <li> Any <code>'-'</code> character is removed.
     </ol>
     Moreover, a case insensitive match is followed.

     As an example, a frame with an integer slot named
     <code>:user-age</code>, will require the following methods (case
     is not important, but according to a popular Java coding
     convention, the two methods have capital letters whenever a
     <code>'-'</code> is present in the slot name:

     <ul>
     <li><code>int getUserAge()</code>
     <li><code>void setUserAge(int age)</code>
     </ul>

     @param roleName The name of the role the given Java class wants
     to play.
     @param c An user-defined class, obeying to the rules given above.
     @exception OntologyException If no role named
     <code>roleName</code> is defined in this ontology, or if the
     given class violates some rule for representing the given
     concept.
   */
  void addClass(String roleName, Class c) throws OntologyException;

  /*
    Adds a new concept role to the ontology, defined by the structure
    of all its slots.
    @param conceptName The name of this concept role (names are case
    preserving but the match is case insensitive)
    @param slots An array of descriptors; each one of them describes a
    slot of the frame, providing:
    <ul>
    <li> The name of the slot.
    <li> The type of the slot.
    <li> The optionality of the slot (i.e. whether a value is required or not).
    <li> The position of the slot (implicitly defined by the position in the array).
    </ul>
    @see jade.onto.TermDescriptor
  */
  void addConcept(String conceptName, TermDescriptor[] slots) throws OntologyException;

  /**
    Adds a new action role to the ontology, defined by the structure
    of all its arguments.
    @param actionName The name of this action role (names are case
    preserving but the match is case insensitive)
    @param args An array of descriptors; each one of them describes an
    argument of the action, providing:
    <ul>
    <li> The name of the argument.
    <li> The type of the argument.
    <li> The optionality of the argument (i.e. whether a value is required or not).
    <li> The position of the argument (implicitly defined by the position in the array).
    </ul>
    @see jade.onto.TermDescriptor
  */
  void addAction(String actionName, TermDescriptor[] args) throws OntologyException; 

  /**
    Adds a new predicate role to the ontology, defined by the structure
    of all its terms.
    @param predicateName The name of this predicate role (names are case
    preserving but the match is case insensitive)
    @param terms An array of descriptors; each one of them describes a
    term of the predicate, providing:
    <ul>
    <li> The name of the term.
    <li> The type of the term.
    <li> The optionality of the term (i.e. whether a value is required or not).
    <li> The position of the term (implicitly defined by the position in the array).
    </ul>
    @see jade.onto.TermDescriptor
  */
  void addPredicate(String predicateName, TermDescriptor[] terms) throws OntologyException;

  /**
    Creates a Java object representing a given concept, getting the
    information from a given <code>Frame</code> object. A suitable
    Java class must be registered for the role played by the
    <code>Frame</code>.
    @param f A <code>Frame</code> object, from which a Java object is built.
    @return A Java Object, representing the given <code>Frame</code> as a user-defined type.
    @exception OntologyException If the given <code>Frame</code> does
    not play any role in the current ontology, or if the registered
    class does not follows the rules for representing a concept.
  */
  Object createObject(Frame f) throws OntologyException;

  /**
    Creates a Java object representing a given action, getting the
    information from a given <code>Action</code> object. A suitable
    Java class must be registered for the role played by the
    <code>Action</code>.
    @param a An <code>Action</code> object, from which a Java object is built.
    @return A Java Object, representing the given <code>Action</code> as a user-defined type.
    @exception OntologyException If the given <code>Action</code> does
    not play any role in the current ontology, or if the registered
    class does not follows the rules for representing a concept.
  */
  Object createObject(Action a) throws OntologyException;

  /**
    Creates a Java object representing a given predicate, getting the
    information from a given <code>Predicate</code> object. A suitable
    Java class must be registered for the role played by the
    <code>Predicate</code>.
    @param p A <code>Predicate</code> object, from which a Java object
    is built.
    @return A Java Object, representing the given
    <code>Predicate</code> as a user-defined type.
    @exception OntologyException If the given <code>Predicate</code> does
    not play any role in the current ontology, or if the registered
    class does not follows the rules for representing a concept.
  */
  Object createObject(Predicate p) throws OntologyException;

  /**
    Creates a <code>Frame</code> object from a given Java object. A
    suitable class must be registered in the ontology to play the
    given role, and the given object must be an instance of that class
    (or a subclass).
    @param o The Java object, from which the <code>Frame</code> will
    be built.
    @param roleName The name of the role played in this ontology by
    the class of the given object.
    @return A <code>Frame</code> object playing the given role, built
    from the given <code>Object</code>.
    @exception OntologyException If the given role does not exist, or
    the given object is not of the correct class.
  */
  Frame createConcept(Object o, String roleName) throws OntologyException;

  /**
    Creates an <code>Action</code> object from a given Java object. A
    suitable class must be registered in the ontology to play the
    given role, and the given object must be an instance of that class
    (or a subclass).
    @param o The Java object, from which the <code>Action</code> will
    be built.
    @param roleName The name of the role played in this ontology by
    the class of the given object.
    @return A <code>Action</code> object playing the given role, built
    from the given <code>Object</code>.
    @exception OntologyException If the given role does not exist, or
    the given object is not of the correct class.
   */
  Action createAction(Object o, String roleName) throws OntologyException;

  /**
    Creates a <code>Predicate</code> object from a given Java object. A
    suitable class must be registered in the ontology to play the
    given role, and the given object must be an instance of that class
    (or a subclass).
    @param o The Java object, from which the <code>Predicate</code> will
    be built.
    @param roleName The name of the role played in this ontology by
    the class of the given object.
    @return A <code>Predicate</code> object playing the given role, built
    from the given <code>Object</code>.
    @exception OntologyException If the given role does not exist, or
    the given object is not of the correct class.
   */
  Predicate createPredicate(Object o, String roleName) throws OntologyException;

  /**
    Checks whether the given <code>Frame</code> object is a valid
    instance of some role, making sure that every slot has the correct
    type and that no mandatory slot has a <code>null</code> value.
    @param f The <code>Frame</code> object to check.
    @exception OntologyException If the check fails.
  */
  void check(Frame f) throws OntologyException;

  /**
    Checks whether the given <code>Action</code> object is a valid
    instance of some role, making sure that every slot has the correct
    type and that no mandatory slot has a <code>null</code> value.
    @param a The <code>Action</code> object to check.
    @exception OntologyException If the check fails.
  */
  void check(Action a) throws OntologyException;

  /**
    Checks whether the given <code>Predicate</code> object is a valid
    instance of some role, making sure that every slot has the correct
    type and that no mandatory slot has a <code>null</code> value.
    @param p The <code>Predicate</code> object to check.
    @exception OntologyException If the check fails.
  */
  void check(Predicate p) throws OntologyException;

  /**
    Checks whether the given Java object is a valid instance of some
    role, making sure that every slot has the correct type and that no
    mandatory slot has a <code>null</code> value.
    @param o The Java object to check.
    @param roleName The role against which to check the given object.
    @exception OntologyException If the check fails.  */
  void check(Object o, String roleName) throws OntologyException;

  /**
    Returns the array of <code>TermDescriptor</code> objects that
    represent the elements of the given ontological role (concept,
    action or predicate).
    @param roleName The name of the ontological role to examine.
    @return The descriptors for the selected ontology role.
    @see jade.onto.TermDescriptor
   */
  TermDescriptor[] getTerms(String roleName) throws OntologyException;

}
