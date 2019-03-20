package com.linkstec.bee.core.fw;

import java.util.List;

import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;

/**
 * A class called BClass will representing the design sheet. and if not
 * graphically needed,the temporary class will be created.
 * 
 * @author linkage
 *
 */
public interface BClass extends BType, Cloneable, BAnnotable {

	public static final String TYPE_STRING = "STRING";
	public static final String TYPE_DIGITAL = "DIGITAL";
	public static final String TYPE_CONTAINER = "CONTAINER";
	public static final String TYPE_COMPLEX = "COMPLEX";
	public static final String TYPE_DEFINED = "DEFINED";
	public static final String TYPE_LOGICAL = "LOGICAL";
	public static final String TYPE_OTHERS = "OTHERS";

	public boolean isNullClass();

	/**
	 * when this class representing union type,add the class name for union type to
	 * this class. this time,the class name will be null.
	 * 
	 * @param type
	 */
	public void addUnionType(String type);

	/**
	 * when this class representing union type,the list will be shown by this
	 * method.
	 * 
	 * @return
	 */
	public List<String> getUnionTypes();

	public static String VOID = "void";
	public static String NULL = "null";

	/**
	 * for the sake of memory,class will remember the superclass name but not the
	 * class object.
	 * 
	 * @param name
	 */
	public void setSuperClass(BValuable name);

	/**
	 * get the name of super class of this class
	 * 
	 * @return
	 */
	public BValuable getSuperClass();

	/**
	 * for the sake of memory,this class only remember the name of interfaces.
	 * 
	 * @param name
	 */
	public void addInterface(BValuable name);

	/**
	 * the interfaces ,which is implemented by this class
	 * 
	 * @return
	 */
	public List<BValuable> getInterfaces();

	/**
	 * if this class represents exception class,it will return true.
	 * 
	 * @return
	 */

	public boolean isException();

	/**
	 * if this class is interface,it will return true.
	 * 
	 * @return
	 */
	public boolean isInterface();

	public boolean isInnerClass();

	/**
	 * 
	 * @param inter
	 */
	public void setInterface(boolean inter);

	/**
	 * according to the modifier,it will be abstract.
	 * 
	 * @return
	 */
	public boolean isAbstract();

	/**
	 * according to the modifier,it will be final.
	 * 
	 * @return
	 */

	public boolean isFinal();

	/**
	 * set mod for this class,which will be private,public,final etc.
	 * 
	 * @param mod
	 */
	public void setModifier(int mod);

	/**
	 * get mod of this class
	 * 
	 * @return
	 */
	public int getModifier();

	/**
	 * set the package which will be the same will java package.
	 * 
	 * @param bpackage
	 */
	public void setPackage(String bpackage);

	/**
	 * get the package for the class.
	 * 
	 * @return
	 */
	public String getPackage();

	public void setImports(List<BImport> imports);

	// public void setVariables(List<BAssignment> variables);

	public List<BAssignment> getVariables();

	public List<BMethod> getMethods();

	public void setConstructors(List<BConstructor> constructors);

	public List<BConstructor> getConstructors();

	public void setData(boolean data);

	public boolean isData();

	public void setLogic(boolean logic);

	public boolean isLogic();

	public String getQualifiedName();

	public List<BImport> getImports();

	/**
	 * get the blocks implemented on this class
	 * 
	 * @return
	 */
	public List<BLogicBody> getBlocks();

	/**
	 * clone the class to another class
	 */
	public BClass cloneAll();

	/**
	 * when this class is an inner class,set the parent here.
	 * 
	 * @param name
	 */
	public void setInnerParentClassName(String name);

	/**
	 * get the parent of this class when this class is an inner class.
	 * 
	 * @return
	 */
	public String getInnerParentClassName();

	public boolean isPrimitive();

	public void setAnonymous(boolean anonymous);

	public boolean isAnonymous();

	public boolean isEnum();

	public void addVar(BAssignment var);

	public void addVar(int index, BAssignment var);

	public void removeVar(BAssignment var);

	public void removeMethod(BMethod method);

}
