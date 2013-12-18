package data.xml;

public interface IDFVisitor<T> {
	public T defaultVisit(IDataField df);
	public T visit(PrimField pf);
	public T visit(CompField cf);
	public T visit(ListField lf);
}
