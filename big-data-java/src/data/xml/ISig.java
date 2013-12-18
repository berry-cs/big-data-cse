package data.xml;

public interface ISig {
	public <A> A apply(ISigVisitor<A> sv);
	public ISig unifyWith(Class<?> c);
}

