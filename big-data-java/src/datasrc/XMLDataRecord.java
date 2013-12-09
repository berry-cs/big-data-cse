package datasrc;

import java.util.HashMap;
import ext.*;

public class XMLDataRecord implements IDataRecord {

	private XML xml;   // the parsed xml data source
	String basetag; 
	private HashMap<String,XMLDataField> fields;
	private HashMap<String,XMLDataRecord> records;
	
	public XMLDataRecord(XML xml) {
		this.xml = xml;
		this.fields = new HashMap<String,XMLDataField>();
		this.records = new HashMap<String,XMLDataRecord>();
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
	public boolean containsField(String name) {
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

	@Override
	public boolean containsRecord(String name) {
		return records.containsKey(name);
	}

	@Override
	public IDataRecord getRecord(String name) {
		return getRecords(name)[0];
	}

	@Override
	public IDataRecord[] getRecords(String name) {
		IDataRecord idr = records.get(name);
		
		return null;
	}

}
