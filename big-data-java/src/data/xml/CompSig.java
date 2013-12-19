package data.xml;

import java.lang.reflect.*;
import java.util.ArrayList;

import ext.ProcessingDetector;

@SuppressWarnings("unused")
public class CompSig<C> implements ISig {
	private Class<C> cls;
	private ArrayList<FieldSpec> fields;
	
	private static class FieldSpec { 
		String name;
		ISig type;
		
		public FieldSpec(String name, ISig type) {
			this.name = name;
			this.type = type;
		}
	}

	public CompSig(Class<C> cls) {
		this.cls = cls;
		this.fields = new ArrayList<FieldSpec>();
	}

	public void addField(ISig ty, String nm) {
		this.fields.add(new FieldSpec(nm, ty));
	}
	
	public ISig getFieldSig(int i) {
		return this.fields.get(i).type;
	}
	
	public String getFieldName(int i) {
		return this.fields.get(i).name;
	}
	
	public int getFieldCount() {
		return this.fields.size();
	}

	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}
	
	@Override
	public String toString() {
		String m = "{";
		for (FieldSpec f : fields) {
			if (m.length() > 1) { m += ", "; }
			m += (f.name + ": " + f.type);
		}
		m += "}";
		return m;
	}
	
	public Constructor<C> findConstructor() {
		try {
			Constructor<C>[] constrs = (Constructor<C>[]) cls.getDeclaredConstructors(); // cls.getConstructors();
			Constructor<C> theCons = null;
			for (Constructor<C> cr : constrs) {
				int m = cr.getModifiers();
				if (Modifier.isPrivate(m) || Modifier.isProtected(m))
					continue;
				//System.err.println(" >> Checking " + cr);
				if (unifies(this, cr) || (ProcessingDetector.inProcessing() && unifies_processing(this, cr))) {
					//System.err.println(" >> OK");
					theCons = cr;
					break;
				}
			}
			return theCons;
		} catch (SecurityException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private static <T> boolean unifies(CompSig<T> s, Constructor<T> cr) {
		Class[] paramTys = cr.getParameterTypes();
		if (paramTys.length != s.fields.size()) return false;   // different number of pararmeters
		for (int i = 0; i < paramTys.length; i++) {
			Class<?> c = paramTys[i];
			FieldSpec fs = s.fields.get(i);
			//System.out.println(" >> Unifies " + fs.type + " | " + c );
			if (!unifies(fs.type, c)) {
				return false;
			}
		}
		return true;
	}
	
	private static <T> boolean unifies_processing(CompSig<T> s, Constructor<T> cr) {
		Class[] paramTys = cr.getParameterTypes();
		if (paramTys.length != 1 + s.fields.size()) return false;   // different number of pararmeters
		if (!ProcessingDetector.pappletClass.isAssignableFrom(paramTys[0])) return false;
		for (int i = 1; i < paramTys.length; i++) {
			Class<?> c = paramTys[i];
			FieldSpec fs = s.fields.get(i-1);
			//System.out.println(" >> Unifies " + fs.type + " | " + c );
			if (!unifies(fs.type, c)) {
				return false;
			}
		}
		return true;
	}
	
	private static <T> boolean unifies(ISig s, final Class<T> c) {
		return s.apply(new ISigVisitor<Boolean>() {
			public Boolean defaultVisit(ISig s) { return false;	}

			public <C> Boolean visit(PrimSig s) {
				if (s == PrimSig.WILDCARD_SIG) {
					return c.equals(Integer.class) || c.equals(int.class)
						    || c.equals(Double.class) || c.equals(double.class)
						    || c.equals(Boolean.class) || c.equals(boolean.class)
						    || c.equals(String.class);
				} else {
					return
							s == PrimSig.DOUBLE_SIG && (c.equals(Double.class) || c.equals(double.class))
						|| s == PrimSig.INT_SIG && (c.equals(Integer.class) || c.equals(int.class))
						|| s == PrimSig.BOOLEAN_SIG && (c.equals(Boolean.class) || c.equals(boolean.class))
						|| s == PrimSig.STRING_SIG && c.equals(String.class);
				}
			}

			public Boolean visit(CompSig<?> s) {
				if (s.cls.equals(c)) {
					return s.findConstructor() != null;
				}		
				return false;
			}

			public Boolean visit(ListSig s) {
				if (!c.equals(ArrayList.class)) return false;
				ISig se = s.getElemType();
				// just stop here for now - don't examine element types...
				return true;
			}
		});
	}

	public ISig unifyWith(Class<?> c) {
		if (cls.equals(c)) 
			return this;
		// TODO: do we check findConstructor() != null?
	    throw new RuntimeException("Could not unify " + this + " with " + c);
	}
}
