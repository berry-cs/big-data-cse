package big.data.sig;

import big.data.util.*;

@SuppressWarnings("unchecked")
public class SigBuilder {

	public static <T> Class<T> classFor(String clsName) {
		if (clsName.equals("String")) return classFor("java.lang.String");
		else if (clsName.equals("Boolean") || clsName.equals("boolean")) return classFor("java.lang.Boolean");
		else if (clsName.equals("Byte") || clsName.equals("byte")) return classFor("java.lang.Byte");
		else if (clsName.equals("Character") || clsName.equals("char")) return classFor("java.lang.Character");
		else if (clsName.equals("Double") || clsName.equals("double")) return classFor("java.lang.Double");
		else if (clsName.equals("Float") || clsName.equals("float")) return classFor("java.lang.Float");
		else if (clsName.equals("Integer") || clsName.equals("int")) return classFor("java.lang.Integer");
		
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
    	// first attempt to see if cls unifies as a primitive type
    	try {
    		ISig p = PrimSig.WILDCARD_SIG.unifyWith(cls);
    		if (keys.length == 0) return p;
    		if (keys.length == 1) {
    			CompSig<C> cs = new CompSig<C>(cls);
    			cs.addField(p, keys[0]);
    			return cs;
    		}
    	} catch (SignatureUnificationException e) {
    		// ok, continue...
    	}
    	
    	CompSig<C> cs = new CompSig<C>(cls);
    	for (String k : keys) {
    		cs.addField(PrimSig.WILDCARD_SIG, k);
    	}
    	return cs;
    }

}
