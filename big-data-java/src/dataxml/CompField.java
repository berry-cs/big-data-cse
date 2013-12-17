package dataxml;

import java.lang.reflect.*;
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
	
	public IDataField addField(String name, IDataField fld) {
		return fieldMap.put(name, fld);
	}
	
	public String[] fieldNames() {
		return fieldMap.keySet().toArray(new String[] {});
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
	
	@Override
	public <T> T instantiate(XML xml, ISig s) {
		final XML basexml = findMyNode(xml);
		//System.err.println(" CompField.instantiate(" + xml.getName() + ", " + s + ")   basexml: " + basexml.getName());
		return s.apply(new SigMatcher<T>() {
			public T visit(CompSig<?> s) {
				Constructor cr = s.findConstructor();
				Class[] paramTys = cr.getParameterTypes();
				Object[] args = new Object[paramTys.length];
				for (int i = 0; i < args.length; i++) {
					IDataField df = fieldMap.get(s.getFieldName(i));
					ISig fs = s.getFieldSig(i).unifyWith(paramTys[i]);  
					args[i] = df.instantiate(basexml, fs);
					//System.err.println(" " + i + ": " + args[i] + " " + args[i].getClass());
				}
				
				try {
					return (T) cr.newInstance(args);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
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
	

}