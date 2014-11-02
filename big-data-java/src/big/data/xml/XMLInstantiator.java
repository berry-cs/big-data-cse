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
		throw new DataInstantiationException("Unable to instantiate data field " + df);
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
				} else if (s == PrimSig.LONG_SIG) {
					return (T)(Long)asLong(basexml);
				} else if (s == PrimSig.BOOLEAN_SIG) {
					return (T)(Boolean)asBoolean(basexml);
				} else if (s == PrimSig.DOUBLE_SIG) {
					return (T)(Double)asDouble(basexml);
				} else if (s == PrimSig.FLOAT_SIG) {
					return (T)(Float)asFloat(basexml);
				} else if (s == PrimSig.CHAR_SIG) {
					return (T)(Character)asChar(basexml);
				} else if (s == PrimSig.STRING_SIG || s == PrimSig.WILDCARD_SIG) {
					return (T)asString(basexml);
				} else {
					throw new DataInstantiationException("Can't instantiate: unknown PrimSig");
				}
			}

			@SuppressWarnings("rawtypes")
			public T visit(CompSig<?> s) {
				if (s.getFieldCount() == 1 && s.getFieldName(0).equals("")) {
					String name = basexml.getName();
					//XML wrap = new XML(name);
					//wrap.addChild(basexml);
					CompField newfld = new CompField();
					newfld.addField(name, f);
					CompSig<?> newsig = new CompSig(s.getAssociatedClass());
					newsig.addField(s.getFieldSig(0),  name);
					return newfld.apply(new XMLInstantiator<T>(basexml, newsig));
				} else {
					throw new DataInstantiationException("Cannot instantiate " + f + " as " + s);
				}
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

		//System.out.println(basexml.format(1));
		//System.out.println(f);
		//System.out.println(s);
		
		return s.apply(new SigMatcher<T>() {
			public T visit(CompSig<?> s) {
				Constructor<T> cr = (Constructor<T>) s.findConstructor();
				if (s.getFieldCount() == 0) {
					System.err.println("Cannot instantiate " + s.getAssociatedClass() + " with no fields");
					return null;
				}
				if (cr == null) {
					throw new DataInstantiationException("Constructor not found for: " + s);
				}
				Class<?>[] paramTys = cr.getParameterTypes();
				Object[] args = new Object[paramTys.length];
				int start = 0;
				if (ProcessingDetector.inProcessing() && s.getFieldCount()+1 == paramTys.length) {
					start = 1; // this funny business for extra constructor parameter added in processing
					args[0] = ProcessingDetector.getPapplet();
				}
				
				boolean allNullArgs = true;  // at least one argument was successfully parsed as non-null?
				for (int i = start; i < args.length; i++) {
					String fieldname = s.getFieldName(i-start);
					IDataField df = fieldMap.get(fieldname);
					if (df == null) {
						throw new DataInstantiationException("No field named \"" + fieldname + "\" was found in " + f);
					}					
					ISig fs = s.getFieldSig(i-start).unifyWith(paramTys[i]);  
					args[i] = df.apply(new XMLInstantiator<Object>(basexml, fs)); //   df.instantiate(basexml, fs);
					if (args[i] != null && allNullArgs) allNullArgs = false;
					if (args[i] == null && fs instanceof PrimSig) {
						args[i] = ((PrimSig)fs).getNullValue();
					}
				}
				if (allNullArgs) {
					System.err.println("Parsed null for all subfields of " + f + " in XML node <" + basexml.getName() + ">");
					return null;
				}

				try {
					cr.setAccessible(true);
					return cr.newInstance(args);
				} catch (InstantiationException  e) {
					//System.err.println(e.getMessage());
					e.printStackTrace();
				} catch ( IllegalAccessException e) {
					e.printStackTrace();
				} catch ( IllegalArgumentException  e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					//System.err.println(e.getMessage());
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
				
				// see what this list signature is of - if singleton structure (structure with one named field),
				// then see if f, the spec, contains field with that same name, and attempt to instantiate 
				// against that field
				// TODO: this is bad design - use of instanceof, so reconsider it at some point ?
				CompSig<?> cs;
				if (elemsig instanceof CompSig) {
					cs = (CompSig<?>) elemsig;
					/*if (false && cs.getFieldCount() == 1
						&& f.hasField(name = cs.getFieldName(0))
						&& f.getField(name) instanceof ListField) { 
						System.out.println("took out: " + f.getField(name) + " from " + f + " / " + s + " " + basexml);
						//System.out.println(XMLInstantiator.this.xml);
						
						return f.getField(name).apply(XMLInstantiator.this);
						
						/*
						ListField subfld = (ListField) f.getField(name);
						CompField newInner = new CompField();
						newInner.addField(name, subfld.getElemField());
						IDataField newfld = new ListField(subfld.getBasePath(), subfld.getElemPath(), newInner); 
						
						System.out.println("built: " + newfld);
						
						
						
						ArrayList<Object> lst = new ArrayList<Object>();
						for (Object o : f.getField(name).apply(new XMLInstantiator<ArrayList<Object>>(xml, new ListSig(cs.getFieldSig(0))))) {
							Constructor<?> cstr = cs.findConstructor();
							cstr.setAccessible(true);
							try {
								lst.add(cstr.newInstance(o));
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
						
						return (T) lst;
						
						
					} else { */
						String[] sigflds = new String[cs.getFieldCount()];
						for (int i = 0; i < sigflds.length; i++) {
							sigflds[i] = cs.getFieldName(i);
						}
						String commonPrefix = longestCommonPrefix(sigflds);
						if (commonPrefix == null) commonPrefix = "";
						//System.out.println("prefix: " + commonPrefix);
						int prefixLength = commonPrefix.length();
						if (commonPrefix.endsWith("/"))
							commonPrefix = commonPrefix.substring(0, prefixLength-1);
						if (f.hasField(commonPrefix)) {
							@SuppressWarnings("rawtypes")
							CompSig<?> newsig = new CompSig(cs.getAssociatedClass());
							for (int i = 0; i < sigflds.length; i++) {
								String oldname = sigflds[i];
								String newname = oldname.substring(prefixLength);
								newsig.addField(cs.getFieldSig(i), newname);
							}

							IDataField fsubfld = f.getField(commonPrefix);
							XML fbasexml = basexml;
							if (!(fsubfld instanceof ListField)) { // only one element of the spec field in the data source
								fbasexml = basexml.getChild(commonPrefix); // need to explicitly burrow into the xml
							}
							
							//System.out.println("fsubfld: " + fsubfld);
							//System.out.println("newsig: " + new ListSig(newsig));
							//System.out.println("fbasexml: " + fbasexml);
							return fsubfld.apply(new XMLInstantiator<T>(fbasexml, new ListSig(newsig)));
						}
					/* } */ 
				} 
				
				//System.out.println("Here " + s + "/" + f);
				ArrayList<Object> lst = new ArrayList<Object>();
				lst.add(elemsig.apply(this));
				return (T)lst;
			}

			/* here, trying to instantiate a primitive data value against
			 * this compound XML structure - so try to simply produce the
			 * field with the name specified in signature s
			 */
			public T visit(PrimSig s) {
				throw new DataInstantiationException("Cannot instantiate " + f + " as " + s);
				//return fieldMap.get(s.getName()).apply(new XMLInstantiator<T>(basexml, s));   //instantiate(basexml, s);
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
				//System.out.println(elemField + "  > " + elemPath + "\n" + basexml + "\n" + child);
				return elemField.apply(new XMLInstantiator<T>(child, s));  //instantiate(child, s);
			}
			
		});
	}

	
	/* -------------------------- helper functions ----------------------------------- */

	
	public static String asString(XML xml) {
		return xml.getContent().trim();
	}
	
	public static char asChar(XML xml) {
		String s = xml.getContent().trim();
		if (s.length() == 0) return 0;
		else return s.charAt(0);
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
	
	public static long asLong(XML xml) {
		String s = asString(xml).trim();
		long v = 0;
		try {
			v = Long.parseLong(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as a long");
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
	
	public static String longestCommonPrefix(String[] strings) {
	    if (strings.length <= 0) {
	        return null;
	    }

	    for (int prefixLen = 0; prefixLen < strings[0].length(); prefixLen++) {
	        char c = strings[0].charAt(prefixLen);
	        for (int i = 1; i < strings.length; i++) {
	            if ( prefixLen >= strings[i].length() ||
	                 strings[i].charAt(prefixLen) != c ) {
	                // Mismatch found
	                return strings[i].substring(0, prefixLen);
	            }
	        }
	    }
	    return strings[0];
	}
}
