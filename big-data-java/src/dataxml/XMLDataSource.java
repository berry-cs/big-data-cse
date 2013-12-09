package dataxml;

import java.util.HashMap;

public class XMLDataSource {
	
	private HashMap<String, IDataField> servs; 
	private IDataField defaultService;
	
	public XMLDataSource() {
		servs = new HashMap<String, IDataField>();
	}

	public void registerService(String name, IDataField fld) {
		servs.put(name, fld);
	}
	
	
	
}


