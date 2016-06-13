package easy.data.sig;

/**
 * An abstraction of a Java class constructor signature
 * 
 * @author Nadeem Abdul Hamid
 */
public interface ISig {
	/**
	 * Hook for the visitor pattern
	 * @param sv a visitor object
	 * @return the result of the visitor operation
	 */
	public <A> A apply(ISigVisitor<A> sv);
	
	/** 
	 * Checks that the type represented by this object is consistent
	 * with the type represented by the given Class and produces a
	 * specialization of this ISig, if appropriate
	 * @param c a class
	 * @return a specialization of this ISig to the given class
	 * @throws ISigUnificationException
	 */
	public ISig unifyWith(Class<?> c) throws SignatureUnificationException;
	
	/**
	 * Determines if unifyWith returns successfully without exception
	 */
	public boolean unifiesWith(Class<?> c);
}

