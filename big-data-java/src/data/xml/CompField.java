package data.xml;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import ext.*;

public class CompField implements IDataField {
	private String basePath;
	private HashMap<String, IDataField> fieldMap;
	
	public CompField() {
		this(null);
	}
	
	public CompField(String basepath) {
		this.basePath = basepath;
		this.fieldMap = new HashMap<String, IDataField>();
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	public IDataField addField(String name, IDataField fld) {
		return fieldMap.put(name, fld);
	}
	
	public String[] fieldNames() {
		return fieldMap.keySet().toArray(new String[] {});
	}
	
	public IDataField getField(String name) {
		return fieldMap.get(name);
	}
	
	@Override
	public String toString() {
		String m = "{";
		for (String k : fieldNames()) {
			if (m.length() > 1) { m += ", "; }
			m += (k + ": " + fieldMap.get(k));
		}
		m += "}";
		return m;
	}

	protected XML findMyNode(XML xml) {
		XML node = xml;
		if (basePath != null && !basePath.equals("")) {
			node = node.getChild(basePath);
		}
		return node;
	}
	
	public <T> T instantiate(XML xml, ISig s) {
		final XML basexml = findMyNode(xml);
		//System.err.println(" CompField.instantiate(" + xml.getName() + ", " + s + ")   basexml: " + basexml.getName());
		return s.apply(new SigMatcher<T>() {
			public T visit(CompSig<?> s) {
				Constructor cr = s.findConstructor();
				if (cr == null) throw new RuntimeException("Constructor not found for: " + s);
				Class[] paramTys = cr.getParameterTypes();
				Object[] args = new Object[paramTys.length];
				int start = (s.getFieldCount() == paramTys.length) ? 0 : 1;   // this funny business for extra constructor parameter added in processing
				for (int i = start; i < args.length; i++) {
					IDataField df = fieldMap.get(s.getFieldName(i-start));
					ISig fs = s.getFieldSig(i-start).unifyWith(paramTys[i]);  
					args[i] = df.instantiate(basexml, fs);
					//System.err.println(" " + i + ": " + args[i] + " " + args[i].getClass());
				}
				
				try {
					cr.setAccessible(true);
					return (T) cr.newInstance(args);
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

			public T visit(ListSig s) {
				return null;
			}
			
		});
	}
	
	/*
	public <T> T asObject(XML xml, Class<T> c, String... keys) {
		
		try {
			Constructor[] constrs = c.getConstructors();
			Constructor theCons = null;
			//System.out.println(constrs.length);
			for (Constructor cr : constrs) {
				Class[] paramTys = cr.getParameterTypes();
				//				  if (paramTys[0] == int.class) System.out.println("int");
				if (paramTys.length == keys.length) {
					theCons = cr;
					break;
				}
			}
			if (theCons != null) {
				//System.out.println(theCons);
				Class[] paramTys = theCons.getParameterTypes();
				Object[] args = new Object[keys.length];
				for (int i = 0; i < args.length; i++) {
					String value = ((PrimField)fieldMap.get(keys[i])).asString(findMyNode(xml));
					if (paramTys[i] == Integer.TYPE)
						args[i] = Integer.parseInt(value);
					else
						args[i] =  value;
				}
				return (T) theCons.newInstance(args);
			}
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visit(this);
	}
	
	public String toString(int indent) {
		return this.toString(indent, true);
	}
	
	public String toString(int indent, boolean indentFirst) {
		String initSpaces = IOUtil.repeat(' ', indent);
		String s = (indentFirst ? initSpaces : "") + "Structure\n" + initSpaces + "{\n";
		String spaces = IOUtil.repeat(' ', indent + 2);
		ArrayList<String> keys = new ArrayList<String>(fieldMap.keySet());
		Collections.sort(keys);
		for (String name : keys) {
			IDataField df = fieldMap.get(name);
			if (df instanceof PrimField) {
				String leader = spaces + name + " : ";
				s += leader + df.toString(leader.length(), false) + "\n";
			}
		}
		for (String name : keys) {
			IDataField df = fieldMap.get(name);
			if (df instanceof CompField) {
				String leader = spaces + name + " : ";
				s += leader + df.toString(leader.length(), false) + "\n";
			}
		}
		for (String name : keys) {
			IDataField df = fieldMap.get(name);
			if (df instanceof ListField) {
				String leader = spaces + name + " : ";
				s += leader + df.toString(leader.length(), false) + "\n";
			}
		}
		s += initSpaces + "}";
		return s;
	}
}