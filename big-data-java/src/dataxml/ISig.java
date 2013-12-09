package dataxml;

public interface ISig {
	public <A> A apply(ISigVisitor<A> sv);
	public ISig unifyWith(Class<?> c);
}

