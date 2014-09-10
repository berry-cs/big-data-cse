package big.data.xml;

import java.lang.reflect.*;
import java.util.*;

import big.data.DataInstantiationException;
import big.data.field.CompField;
import big.data.field.IDFVisitor;
import big.data.field.IDataField;
import big.data.field.ListField;
import big.data.field.PrimField;
import big.data.sig.*;
import big.data.util.*;


/**
 * Attempt to instantiate an object of the given signature
 * using the structure of the xml data described by the field
 * to which this visitor is applied
 *  
 * @author Nadeem Abdul Hamid
 * 
 */
@SuppressWarnings("unchecked")
public class XMLInstantiator<T> implements IDFVisitor<T> {

	private XML xml;
	private ISig s;

	public XMLInstantiator(XML xml, ISig s) {
		this.xml = xml;
		this.s = s;
	}

	public T defaultVisit(IDataField df) {
		throw new RuntimeException("Unable to instantiate data field " + df);
	}

	
	
	public T visitPrimField(final PrimField f, String basePath, String description) {
		final XML basexml = f.findMyNode(xml);
		if (basexml == null) {
			System.err.println("Could not find a node labeled \"" + basePath + "\" in XML node <" + xml.getName() + ">");
			return null;
		}
		return s.apply(new SigMatcher<T>() {
			public <C> T visit(PrimSig s) {
				if (s == PrimSig.INT_SIG) {
					return (T)(Integer)asInt(basexml);
				} else if (s == PrimSig.BOOLEAN_SIG) {
					return (T)(Boolean)asBoolean(basexml);
				} else if (s == PrimSig.DOUBLE_SIG) {
					return (T)(Double)asDouble(basexml);
				} else if (s == PrimSig.FLOAT_SIG) {
					return (T)(Float)asFloat(basexml);
				} else if (s == PrimSig.STRING_SIG || s == PrimSig.WILDCARD_SIG) {
					return (T)asString(basexml);
				} else {
					throw new RuntimeException("Can't instantiate: unknown PrimSig");
				}
			}

			public T visit(CompSig<?> s) {
				throw new DataInstantiationException("Cannot instantiate " + f + " as " + s);
			}

			public T visit(ListSig s) {
				ISig elemsig = s.getElemType();
				ArrayList<Object> lst = new ArrayList<Object>();
				lst.add(elemsig.apply(this));
				return (T)lst;
			}
		});
	}

	
	
	public T visitCompField(final CompField f, final String basePath, final String description,
			final HashMap<String, IDataField> fieldMap) {
		
		final XML basexml = f.findMyNode(xml);
		if (basexml == null) {
			System.err.println("Could not find a node labeled \"" + basePath + "\" in XML node <" + xml.getName() + ">");
			return null;
		}

		return s.apply(new SigMatcher<T>() {
			public T visit(CompSig<?> s) {
				Constructor<T> cr = (Constructor<T>) s.findConstructor();
				if (s.getFieldCount() == 0) {
					System.err.println("Cannot instantiate " + s.getAssociatedClass() + " with no fields");
					return null;
				}
				if (cr == null) {
					throw new RuntimeException("Constructor not found for: " + s);
				}
				Class<?>[] paramTys = cr.getParameterTypes();
				Object[] args = new Object[paramTys.length];
				int start = (ProcessingDetector.inProcessing() && s.getFieldCount()+1 == paramTys.length) ? 1 : 0;   // this funny business for extra constructor parameter added in processing
				boolean allNullArgs = true;  // at least one argument was successfully parsed as non-null?
				for (int i = start; i < args.length; i++) {
					String fieldname = s.getFieldName(i-start);
					IDataField df = fieldMap.get(fieldname);
					if (df == null) {
						System.err.println(f + " does not contain field named \"" + fieldname + "\"");
						return null;
					}					
					ISig fs = s.getFieldSig(i-start).unifyWith(paramTys[i]);  
					args[i] = df.apply(new XMLInstantiator<Object>(basexml, fs)); //   df.instantiate(basexml, fs);
					if (args[i] != null && allNullArgs) allNullArgs = false;
				}
				if (allNullArgs) {
					System.err.println("Parsed null for all subfields of " + f + " in XML node <" + basexml.getName() + ">");
					return null;
				}

				try {
					cr.setAccessible(true);
					return cr.newInstance(args);
				} catch (InstantiationException  e) {
					e.printStackTrace();
				} catch ( IllegalAccessException e) {
					e.printStackTrace();
				} catch ( IllegalArgumentException  e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} 
				return null;
			}

			/*
			 * So here, trying to instantiate a list of something against this
			 * compound XML structure - so attempt to instantiate against the
			 * list element signature (which hopefully matches this compound
			 * structure) and then make a list out of it 
			 */
			public T visit(ListSig s) {
				ISig elemsig = s.getElemType();
				ArrayList<Object> lst = new ArrayList<Object>();
				lst.add(elemsig.apply(this));
				return (T)lst;
			}

			/* here, trying to instantiate a primitive data value against
			 * this compound XML structure - so try to simply produce the
			 * field with the name specified in signature s
			 */
			public T visit(PrimSig s) {
				return fieldMap.get(s.getName()).apply(new XMLInstantiator<T>(basexml, s));   //instantiate(basexml, s);
			}
		});
	}

	
	
	public T visitListField(final ListField f, final String basePath, final String description,
			final String elemPath, final IDataField elemField) {
		
		final XML basexml = f.findMyNode(xml);
		if (basexml == null) {
			System.err.println("Could not find a node labeled \"" + basePath + "\" in XML node <" + xml.getName() + ">");
			return null;
		}
		
		/*	 
		 * So, we are given the structure described by field f -- i.e. a list of 
		 * some type of elements drawn from the xml -- attempt to instantiate 
		 * signature s
		 */
		return s.apply(new SigMatcher<T>() {
			/*
			 * If s is [listof ...] then fetch all subnodes of <elemPath>
			 * and for each of those instantiate them against the element
			 * type of signature s, producing an arraylist of them and
			 * returning it
			 */			
			public T visit(ListSig s) {
				ArrayList<Object> lst = new ArrayList<Object>();
				XML[] childs = basexml.getChildren(elemPath);
				if (childs.length == 0) 
					System.err.println("No elements labeled \"" + elemPath + "\" in XML node <" + basexml.getName() + ">");
				for (XML c : childs) {
					Object o = elemField.apply(new XMLInstantiator<Object>(c, s.getElemType()));   //instantiate(c, s.getElemType());
					if (o != null) lst.add(o);
				}
				if (lst.size() == 0)
					System.err.println("Did not successfully instantiate any elements of " + elemField + " in XML node <" + basexml.getName() + ">");
				return (T)lst;
			}
			
			/*
			 * If s is a structure, then just get a single (i.e. the first)
			 * subnode of <elemPath> and instantiate it against s
			 */
			public T visit(CompSig<?> s) {
				XML child = basexml.getChild(elemPath);
				return elemField.apply(new XMLInstantiator<T>(child, s));    //instantiate(child, s);
			}
			
			/*
			 * If s is a primitive type, then attempt to get a single
			 * (i.e. the first) subnode of <elemPath> and instantiate it
			 * against s
			 */
			public T visit(PrimSig s) {
				XML child = basexml.getChild(elemPath);
				return elemField.apply(new XMLInstantiator<T>(child, s));  //instantiate(child, s);
			}
			
		});
	}

	
	/* -------------------------- helper functions ----------------------------------- */
	
	public static String asString(XML xml) {
		return xml.getContent().trim();
	}
	
	public static int asInt(XML xml) {
		String s = asString(xml).trim();
		int v = 0;
		try {
			v = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as an int");
		}
		return v;
	}
	
	public static double asDouble(XML xml) {
		String s = asString(xml);
		double v = 0.0;
		try {
			v = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as an double");
		}
		return v;
	}
	
	public static float asFloat(XML xml) {
		String s = asString(xml);
		float v = 0.0f;
		try {
			v = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as a float");
		}
		return v;
	}
	
	public static boolean asBoolean(XML xml) {
		String s = asString(xml).toLowerCase();
		boolean b = false;
		if (s.equals("true") || s.equals("1") || s.equals("y") || s.equals("yes"))
			b = true;
		else if (s.equals("false") || s.equals("0") || s.equals("n") || s.equals("no"))
			b = false;
		else 
			System.err.println("Could not parse \"" + s + "\" as a boolean");
		return b;
	}
	
}
