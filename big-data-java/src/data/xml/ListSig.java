package data.xml;

public class ListSig implements ISig {
	private ISig elemType;

	public ListSig(ISig elemType) {
		this.elemType = elemType;
	}
	
	public ISig getElemType() { return elemType; }

	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}
	
	@Override
	public String toString() {
		return "(listof " + elemType.toString() + ")";
	}

	public ISig unifyWith(Class<?> c) {
		throw new RuntimeException("unhandled");
	}
}
