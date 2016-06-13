package easy.data.field;

import java.util.*;

import easy.data.util.IOUtil;

public class FieldStringPrettyPrint implements IDFVisitor<String> {
	int indent;
	boolean indentFirst;
	boolean omitPaths;
	
	public FieldStringPrettyPrint(int indent, boolean indentFirst, boolean omitPaths) {
		this.indent = indent;
		this.indentFirst = indentFirst;
		this.omitPaths = omitPaths;
	}

	public String defaultVisit(IDataField df) {
		throw new RuntimeException("Unhandled data field: " + df);
	}

	public String visitPrimField(PrimField f, String basePath,
			String description) {
        String s = (indentFirst ? IOUtil.repeat(' ', indent) : "") + 
        		((description == null) ? "*" : description);
        return s;
	}

	public String visitCompField(CompField f, String basePath,
			String description, HashMap<String, IDataField> fieldMap) {
		 String initSpaces = IOUtil.repeat(' ', indent);
         String s = (indentFirst ? initSpaces : "") + "A structure with fields:\n" + initSpaces + "{\n";
         String spaces = IOUtil.repeat(' ', indent + 2);
         ArrayList<String> keys = new ArrayList<String>(fieldMap.keySet());
         Collections.sort(keys);
         for (String name : keys) {
                 IDataField df = fieldMap.get(name);
                 if ((!omitPaths || !name.contains("/")) && df instanceof PrimField) {
                         String leader = spaces + name + " : ";
                         s += leader + df.apply(new FieldStringPrettyPrint(leader.length(), false, omitPaths)) + "\n";
                 }
         }
         for (String name : keys) {
                 IDataField df = fieldMap.get(name);
                 if ((!omitPaths || !name.contains("/")) && df instanceof CompField) {
                         String leader = spaces + name + " : ";
                         s += leader + df.apply(new FieldStringPrettyPrint(leader.length(), false, omitPaths)) + "\n";
                 }
         }
         for (String name : keys) {
                 IDataField df = fieldMap.get(name);
                 if ((!omitPaths || !name.contains("/")) && df instanceof ListField) {
                         String leader = spaces + name + " : ";
                         s += leader + df.apply(new FieldStringPrettyPrint(leader.length(), false, omitPaths)) + "\n";
                 }
         }
         s += initSpaces + "}";
         return s;
	}

	public String visitListField(ListField f, String basePath,
			String description, String elemPath, IDataField elemField) {
		String s = (indentFirst ? IOUtil.repeat(' ', indent) : "") + "A list of:\n";
        s += elemField.apply(new FieldStringPrettyPrint(indent+2, true, omitPaths));
        return s;
	}

}
