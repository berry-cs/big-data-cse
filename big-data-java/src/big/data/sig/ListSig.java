package big.data.sig;

import java.util.ArrayList;

/**
 * Represents a list of some type
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public class ListSig implements ISig {
	private ISig elemType;

	public ListSig(ISig elemType) {
		this.elemType = elemType;
	}
	
	public ISig getElemType() { 
		return elemType; 
	}

	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}
	
	public ISig unifyWith(Class<?> c) throws SignatureUnificationException {
		if (c.isArray()) {
			Class<?> ce = c.getComponentType();
			return new ListSig(this.elemType.unifyWith(ce));
		} else if (c == ArrayList.class) {
			// not possible to reflectively get element type of arraylist
			return this;
		} else {
			throw new SignatureUnificationException("Cannot unify " + c + " as a list");
		}
	}
	
	public boolean unifiesWith(Class<?> c) {
		try {
			return this.unifyWith(c) != null;
		} catch (SignatureUnificationException e) {
			return false;
		}
	}

	public String toString() {
		return "[listof " + elemType + "]";
	}
	
	public boolean equals(Object o) {
		 if (o.getClass() == this.getClass()) {
		   ListSig that = (ListSig) o;
		   return this.elemType.equals(that.elemType);
		 }
		 return false;
	}
	
    public int hashCode() {
    	return (41 * (41 + elemType.hashCode()));
    }
}
