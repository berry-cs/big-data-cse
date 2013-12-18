package data.xml;

import java.lang.reflect.*;

public class PrimSig implements ISig {
	public static final PrimSig DOUBLE_SIG = new PrimSig("double");
	public static final PrimSig INT_SIG = new PrimSig("int");
	public static final PrimSig STRING_SIG = new PrimSig("String");
	public static final PrimSig BOOLEAN_SIG = new PrimSig("boolean");
	public static final PrimSig WILDCARD_SIG = new PrimSig("?");
	
	private String name;

	protected PrimSig(String name) {
		this.name = name;
	}

	@Override
	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}
	
	public String getName() { return name; }

	@Override
	public String toString() {
		return "<" + name + ">";
	}

	@Override
	public ISig unifyWith(Class<?> c) {
		if (c == Integer.class || c == int.class) return PrimSig.INT_SIG;
		else if (c == Double.class || c == double.class) return PrimSig.DOUBLE_SIG;
		else if (c == Boolean.class || c == boolean.class) return PrimSig.BOOLEAN_SIG;
		else if (c == String.class) return PrimSig.STRING_SIG;
		else throw new RuntimeException(c + " is not a primitive type");
	}
}
