package data.xml;

import big.data.util.*;

public class PrimField implements IDataField {
	private String path;

	public PrimField() {
		this(null);
	}
	
	public PrimField(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public String asString(XML xml) {
		return findMyNode(xml).getContent().trim();
	}
	
	public int asInt(XML xml) {
		String s = asString(xml).trim();
		int v = 0;
		try {
			v = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as an int");
		}
		return v;
	}
	
	public double asDouble(XML xml) {
		String s = asString(xml);
		double v = 0.0;
		try {
			v = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as an int");
		}
		return v;
	}
	
	public boolean asBoolean(XML xml) {
		String s = asString(xml).toLowerCase();
		boolean b = false;
		try {
			if (s.equals("true") || s.equals("1") || s.equals("y") || s.equals("yes"))
				b = true;
			else if (s.equals("false") || s.equals("0") || s.equals("n") || s.equals("no"))
				b = false;
			else 
				throw new RuntimeException("not boolean");			
		} catch (RuntimeException e) {
			System.err.println("Could not parse \"" + s + "\" as an int");
		}
		return b;
	}
	
	protected XML findMyNode(XML xml) {
		XML node = xml;
		//System.err.println(" From node: " + node.getName());
		if (path != null && !path.equals("")) {
			node = node.getChild(path);
		}
		//System.err.println(" Found node: " + node.getName());
		return node;
	}

	public <T> T instantiate(final XML xml, ISig s) {
		//System.err.println(" PrimField.instantiate(" + xml.getName() + ", " + s + ")");
		return s.apply(new SigMatcher<T>() {
			public <C> T visit(PrimSig s) {
				if (s == PrimSig.INT_SIG) {
					return (T)(Integer)asInt(xml);
				} else if (s == PrimSig.BOOLEAN_SIG) {
					return (T)(Boolean)asBoolean(xml);
				} else if (s == PrimSig.DOUBLE_SIG) {
					return (T)(Double)asDouble(xml);
				} else if (s == PrimSig.STRING_SIG || s == PrimSig.WILDCARD_SIG) {
					return (T)asString(xml);
				} else {
					throw new RuntimeException("Can't instantiate: unknown PrimSig");
				}
			}

			public T visit(CompSig s) {
				// TODO Auto-generated method stub
				return null;
			}

			public T visit(ListSig s) {
				// TODO : Check s.elemType compatible with this
				return null;
			}
			
		});
	}
	
	public String toString() {
		return "<" + path + ">";
	}
	
	public String toString(int indent) {
		return this.toString(indent, true);
	}
	
	public String toString(int indent, boolean indentFirst) {
		String s = (indentFirst ? IOUtil.repeat(' ', indent) : "") + "*";
		return s;
	}

	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visit(this);
	}
}

