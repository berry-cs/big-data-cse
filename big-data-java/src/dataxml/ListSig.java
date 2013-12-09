package dataxml;

public class ListSig implements ISig {
	private ISig elemType;

	public ListSig(ISig elemType) {
		this.elemType = elemType;
	}
	
	public ISig getElemType() { return elemType; }

	@Override
	public <A> A apply(ISigVisitor<A> sv) {
		return sv.visit(this);
	}
	
	@Override
	public String toString() {
		return "(listof " + elemType.toString() + ")";
	}

	@Override
	public ISig unifyWith(Class<?> c) {
		throw new RuntimeException("unhandled");
	}
}
