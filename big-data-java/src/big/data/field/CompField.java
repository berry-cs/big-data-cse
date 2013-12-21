package big.data.field;

import java.util.HashMap;

/**
 * @author Nadeem Abdul Hamid
 *
 */
public class CompField extends ADataField implements IDataField {
	private HashMap<String, IDataField> fieldMap;

	public CompField() {
		this(null);
	}

	public CompField(String basePath) {
		this(basePath, null);
	}

	public CompField(String basePath, String description) {
		super(basePath, description);
		this.fieldMap = new HashMap<String, IDataField>();
	}

	public IDataField addField(String name, IDataField fld) {
		return fieldMap.put(name, fld);
	}
	
	public String[] fieldNames() {
		return fieldMap.keySet().toArray(new String[] {});
	}
	
	public IDataField getField(String name) {
		return fieldMap.get(name);
	}

	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visitCompField(this, this.basePath, this.description, this.fieldMap);
	}

	public String toString() {
		String m = "{";
		for (String k : fieldNames()) {
			if (m.length() > 1) { m += ", "; }
			m += (k + ": " + fieldMap.get(k));
		}
		m += "}";
		return m;
	}

}
