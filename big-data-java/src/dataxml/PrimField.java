package dataxml;

import ext.*;

public class PrimField implements IDataField {
	private String path;

	public PrimField() {
		this(null);
	}
	
	public PrimField(String path) {
		this.path = path;
	}
	
	public String asString(XML xml) {
		return findMyNode(xml).getContent();
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
		String s = asString(xml).trim();
		double v = 0.0;
		try {
			v = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse \"" + s + "\" as an int");
		}
		return v;
	}
	
	protected XML findMyNode(XML xml) {
		XML node = xml;
		if (path != null) {
			node = node.getChild(path);
		}
		return node;
	}

	@Override
	public <T> T instantiate(final XML xml, ISig s) {	
		return s.apply(new SigMatcher<T>() {
			public <C> T visit(PrimSig s) {
				if (s == PrimSig.INT_SIG) {
					return (T)(Integer)asInt(xml);
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
}

