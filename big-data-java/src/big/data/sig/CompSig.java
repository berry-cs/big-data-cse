package big.data.sig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import big.data.util.ProcessingDetector;



/**
 * Represents the signature of a constructor for a compound data
 * type (i.e. a class)
 * In addition to the order of the types of the fields (corresponding
 * to constructor parameters), this object also captures the names
 * of the fields, in order to allow mapping from names of fields
 * in a data source to the parameters positions of constructor
 * 
 * @author Nadeem Abdul Hamid
 *
 * @param <C> a class type for which this represents a constructor signature 
 */
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

	public Class<C> getAssociatedClass() { 
		return cls;
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
	
	public ISig unifyWith(Class<?> c) throws SignatureUnificationException {
		if (cls.equals(c) && findConstructor() != null) 
			return this;
// TODO: should rebuild this ISig, actually, with unified parameter sigs ********		
	    throw new SignatureUnificationException("Could not unify " + this + " with " + c);
	}
	
	public boolean unifiesWith(Class<?> c) {
		try {
			return this.unifyWith(c) != null;
		} catch (SignatureUnificationException e) {
			return false;
		}
	}
	
	public String toString() {
		String m = cls.getName() + "{";
		boolean first = true;
		for (FieldSpec f : fields) {
			if (!first) { m += ", "; }
			m += (f.name + ": " + f.type);
			first = false;
		}
		m += "}";
		return m;
	}

	/**
	 * Finds the appropriate constructor of class C whose signature
	 * matches that represented by this object.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Constructor<C> findConstructor() {
		try {
			Constructor<C>[] constrs = (Constructor<C>[]) cls.getDeclaredConstructors();
			Constructor<C> theCons = null;
			for (Constructor<C> cr : constrs) {
				int m = cr.getModifiers();
				if (Modifier.isPrivate(m) || Modifier.isProtected(m))
					continue;
				//System.err.println(" >> Checking " + this + " with " + cr);
				if (this.unifiesWithConstructor(cr, false) || this.unifiesWithConstructor(cr, true)) {
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
	
	private boolean unifiesWithConstructor(Constructor<C> cr, boolean processingCompatible) {
		Class<?>[] paramTys = cr.getParameterTypes();
		int start = 0;
		if (processingCompatible && ProcessingDetector.inProcessing() && paramTys.length > 0
				&& paramTys.length == 1 + this.fields.size()
				&& ProcessingDetector.pappletClass.isAssignableFrom(paramTys[0])) {
			start = 1;
		} else if (paramTys.length != this.fields.size()) {
			//System.out.println("param count mismatch (" + ProcessingDetector.inProcessing() + "/" + processingCompatible + ")");
			return false;   // different number of pararmeters
		}

		//System.out.printf("start: %d; paramTys length: %d\n", start, paramTys.length);
		for (int i = start; i < paramTys.length; i++) {
			Class<?> c = paramTys[i];
			FieldSpec fs = this.fields.get(i-start);
			//System.out.println("  > " + fs.type + " -- " + c + " : " + fs.type.unifiesWith(c));
			if (!fs.type.unifiesWith(c)) 
				return false;
		}
		return true;
	}
	
}
