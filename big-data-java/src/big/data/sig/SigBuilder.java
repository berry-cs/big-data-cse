package big.data.sig;

import big.data.util.*;

@SuppressWarnings("unchecked")
public class SigBuilder {

	public static <T> Class<T> classFor(String clsName) {
		if (clsName.equals("String")) return classFor("java.lang.String");
		else if (clsName.equals("Double") || clsName.equals("double")) return classFor("java.lang.Double");
		else if (clsName.equals("Boolean") || clsName.equals("boolean")) return classFor("java.lang.Boolean");
		
		try {
			Class<T> cls;
			cls = (Class<T>)Class.forName(clsName);
			return cls;
		} catch (ClassNotFoundException e) {
			if (ProcessingDetector.inProcessing()) {
				String sketchName = ProcessingDetector.getProcessingSketchClassName();
				if (sketchName != null && !clsName.startsWith(sketchName + "$"))
					return classFor(sketchName + "$" + clsName); 
				else 
					e.printStackTrace();
			} else {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
    /*
     * TODO: does this need to be generalized???
     */
    public static <C> ISig buildCompSig(Class<C> cls, String... keys) {
    	CompSig<C> cs = new CompSig<C>(cls);
    	for (String k : keys) {
    		cs.addField(PrimSig.WILDCARD_SIG, k);
    	}
    	return cs;
    }

}
