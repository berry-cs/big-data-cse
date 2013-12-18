package data.xml;

public interface ISigVisitor<T> {
	public T defaultVisit(ISig s);
	public <C> T visit(PrimSig s);
	public T visit(CompSig<?> s);
	public T visit(ListSig s);
}
