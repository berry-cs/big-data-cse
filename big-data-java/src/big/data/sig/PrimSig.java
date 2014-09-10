package big.data.sig;

import java.util.HashMap;

import org.apache.commons.lang3.*;

/**
 * Represents a primitive type (includes String)
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public class PrimSig implements ISig {
	public static final PrimSig BOOLEAN_SIG = new PrimSig("boolean");
	public static final PrimSig BYTE_SIG = new PrimSig("byte");
	public static final PrimSig CHAR_SIG = new PrimSig("char");
	public static final PrimSig DOUBLE_SIG = new PrimSig("double");
	public static final PrimSig FLOAT_SIG = new PrimSig("float");
	public static final PrimSig INT_SIG = new PrimSig("int");
	public static final PrimSig STRING_SIG = new PrimSig("String");
	public static final PrimSig WILDCARD_SIG = new PrimSig("?");
	
	private String name;
	
	protected PrimSig(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see data2.sig.ISig#apply(data2.sig.ISigVisitor)
	 */
	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}

	/* (non-Javadoc)
	 * @see data2.sig.ISig#unifyWith(java.lang.Class)
	 * 
	isig     class --> ok/what
	anything string -> string
	wildcard c      -> c_sig
	s        s      -> s
	 */
	private static HashMap<Class<?>,PrimSig> ctos = setupClassSigMap();
	
	public ISig unifyWith(Class<?> c) throws SignatureUnificationException {
		if (ArrayUtils.indexOf(new Object[] { Boolean.class, Byte.class, Character.class, Integer.class,
												 Double.class, String.class }, c) == ArrayUtils.INDEX_NOT_FOUND
			&& !c.isPrimitive()) throw new SignatureUnificationException(c.getName() + " is not a primitive class");
		
		PrimSig csig = ctos.get(c);   // sig for Class c 
		//System.out.println("c: " + c + " / csig: " + csig);
		if (c == String.class) return STRING_SIG;
		else if (this == WILDCARD_SIG) return csig;
		else if (this == csig) return csig;
		else throw new SignatureUnificationException(this + " cannot be unified with " + c.getName());
	}
	
	public boolean unifiesWith(Class<?> c) {
		try {
			return this.unifyWith(c) != null;
		} catch (SignatureUnificationException e) {
			return false;
		}
	}
	
	private static HashMap<Class<?>, PrimSig> setupClassSigMap() {
		ctos = new HashMap<Class<?>, PrimSig>();
		ctos.put(Boolean.class, BOOLEAN_SIG);
		ctos.put(boolean.class, BOOLEAN_SIG);
		ctos.put(Byte.class, BYTE_SIG);
		ctos.put(byte.class, BYTE_SIG);
		ctos.put(Character.class, CHAR_SIG);
		ctos.put(char.class, CHAR_SIG);
		ctos.put(Integer.class, INT_SIG);
		ctos.put(int.class, INT_SIG);
		ctos.put(Double.class, DOUBLE_SIG);
		ctos.put(double.class, DOUBLE_SIG);
		ctos.put(float.class, FLOAT_SIG);
		ctos.put(Float.class, FLOAT_SIG);
		ctos.put(String.class, STRING_SIG);
		return ctos;
	}

	/**
	 * Produces the name of the primitive type represented by this object 
	 * @return the name of a primitive type
	 */
	public String getName() {
		return name; 
	}

	@Override
	public String toString() {
		return "<" + getName() + ">";
	}

}
