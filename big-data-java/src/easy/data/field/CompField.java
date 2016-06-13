package easy.data.field;

import java.util.Collection;
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
	
	public Collection<IDataField> fields() {
		return fieldMap.values();
	}
	
	public IDataField getField(String name) {
		return fieldMap.get(name);
	}
	
	public boolean hasField(String name) {
		return fieldMap.containsKey(name);
	}

	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visitCompField(this, this.basePath, this.description, this.fieldMap);
	}

	public String toString() {
		String m = "{_<" + basePath + "> ";
		boolean firstDone = false;
		for (String k : fieldNames()) {
			if (firstDone) { m += ", "; } else { firstDone = true; }
			m += (k + ": " + fieldMap.get(k));
		}
		m += "}";
		return m;
	}

}
