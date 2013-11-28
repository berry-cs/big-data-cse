package datasrc;

import java.util.HashMap;
import ext.*;

public class XMLDataRecord implements IDataRecord {

	private XML xml;   // the parsed xml data source
	private HashMap<String,XMLDataField> fields;
	
	public XMLDataRecord(XML xml) {
		this.xml = xml;
		this.fields = new HashMap<String,XMLDataField>();
	}
	
	public IDataField addField(String path) {
		String[] pcs = path.split("/");
		return addField(pcs[pcs.length-1], path);
	}
	
	public IDataField addField(String name, String path) {
		XMLDataField df = new XMLDataField(this.xml, name, path);
		fields.put(name, df);
		return df;		
	}
	
	@Override
	public String[] fieldNames() {
		return fields.keySet().toArray(new String[fields.size()]);
	}

	@Override
	public IDataField get(String name) {
		return fields.get(name);
	}

	/*@Override
	public IDataField get(int i) {
		return get(fieldNames()[i]);
	}*/

	@Override
	public boolean contains(String name) {
		return fields.containsKey(name);
	}
	
	@Override
	public String toString() {
		String m = "{";
		for (String k : fieldNames()) {
			if (m.length() > 1) { m += ", "; }
			m += (k + ": " + get(k).asString());
		}
		m += "}";
		return m;
	}

}
