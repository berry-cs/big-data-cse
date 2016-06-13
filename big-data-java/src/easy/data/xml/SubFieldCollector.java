package easy.data.xml;

import java.util.HashMap;

import easy.data.field.CompField;
import easy.data.field.IDFVisitor;
import easy.data.field.IDataField;
import easy.data.field.ListField;
import easy.data.field.PrimField;

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

		CompField collected = new CompField();
		
		for (String name : f.fieldNames()) {
			IDataField subfield = f.getField(name);
			subfield.apply(new SubFieldCollector(targetField, prefix + "/" + name, false, wrapList));
			subfield.apply(new SubFieldCollector(collected, name));
		}
		
		// have to break out collecting and adding to spec, because otherwise
		// a ConcurrentModificationException happens - adding fields to f while
		// it is being traversed
		for (String name : collected.fieldNames()) {
			f.addField(name, collected.getField(name));
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
