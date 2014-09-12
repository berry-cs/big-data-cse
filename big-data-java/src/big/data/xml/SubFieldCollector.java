package big.data.xml;

import java.util.HashMap;

import big.data.field.CompField;
import big.data.field.IDFVisitor;
import big.data.field.IDataField;
import big.data.field.ListField;
import big.data.field.PrimField;

/**
 * When applied to a data field, it takes all subfields of that data field,
 * and adds them as a top-level field to the targetfield, with the given path.
 * If 'toplevel' is true, it skips primitive and list fields in the process.
 * If 'wraplist' is true, it wraps any fields as a list when adding them to 
 * targetfield.
 */
public class SubFieldCollector implements IDFVisitor<Void> {
	CompField targetField;
	String prefix;
	boolean topLevel;
	boolean wrapList;
	
	public SubFieldCollector(CompField targetField, String prefix) {
		this(targetField, prefix, true, false);
	}
	
	public SubFieldCollector(CompField targetField, String prefix,
			boolean topLevel, boolean wrapList) {
		this.targetField = targetField;
		this.prefix = prefix;
		this.topLevel = topLevel;
		this.wrapList = wrapList;
	}

	public Void defaultVisit(IDataField df) {
		throw new RuntimeException("Unhandled"); 
	}

	public Void visitPrimField(PrimField f, String basePath, String description) {
		if (!topLevel) {
			//System.out.println(prefix);
			IDataField newfield;
			if (wrapList) {
				newfield = new ListField(null, prefix, new PrimField(null));
			} else {
				newfield = new PrimField(prefix);
			}
			
			targetField.addField(prefix, newfield);
		}
		return null;
	}

	public Void visitCompField(CompField f, String basePath,
			String description, HashMap<String, IDataField> fieldMap) {

		for (String name : f.fieldNames()) {
			IDataField subfield = f.getField(name);
			subfield.apply(new SubFieldCollector(targetField, prefix + "/" + name, false, wrapList));
		}
		
		if (!topLevel) {
			if (wrapList) {
				targetField.addField(prefix, new ListField(null, prefix, f));
			} else {
				targetField.addField(prefix, f);
			}
		}
		
		return null;
	}

	public Void visitListField(ListField f, String basePath,
			String description, String elemPath, IDataField elemField) {
		
		IDataField elemfld = f.getElemField();
		elemfld.apply(new SubFieldCollector(targetField, prefix, false, true));
		
		if (!topLevel) {
			targetField.addField(prefix, f);
		}
		
		return null;
	}

}
