package data.xml;

public abstract class SigMatcher<T> implements ISigVisitor<T> {
	public T defaultVisit(ISig s) {
		throw new RuntimeException(getClass().getSimpleName() + ": Unhandled match for " + s);
	}
	public <C> T visit(PrimSig s) { return defaultVisit(s); }
	public T visit(CompSig<?> s) {	return defaultVisit(s); }
	public T visit(ListSig s) {	return defaultVisit(s); }
}
