package dataxml;

import java.util.ArrayList;
import java.util.HashMap;
import ext.*;

public class XMLFileDS {
	private String fileName;
	private XML xml;
	
	private HashMap<String, IDataField> servs; 
	private IDataField currentService;

	
	public XMLFileDS(String fileName) {
		this.fileName = fileName;
		this.xml = IOUtil.loadXML(fileName);
		this.servs = new HashMap<String, IDataField>();
		this.currentService = inferDataField();
		registerService("default", this.currentService);
	}

	public void registerService(String name, IDataField fld) {
		this.servs.put(name, fld);
	}
	
	private IDataField inferDataField() {
		int childCount = xml.getChildCount();
		String firstChildTag = xml.getChild(0).getName();

		// TODO: check that all have the same tag
		
		XML[] innerTags = xml.getChild(0).getChildren();
		CompField cf = new CompField(firstChildTag);
		for (XML t : innerTags) {
			PrimField pf = new PrimField(t.getName());
			cf.addField(t.getName(), pf);
		}
		System.err.println(cf);
		
		IDataField fin = cf;
		if (childCount > 1) {
			fin = new ListField(null, cf);
		}
		
		return fin;
	}
	
	
	private <T> Class<T> classFor(String clsName) {
		try {
			Class<T> cls;
			cls = (Class<T>)Class.forName(clsName);
			return cls;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public <T> T fetch(String clsName, String... keys) {
		return (T) fetch(classFor(clsName), keys);
	}

    public <T> T fetch(Class<T> cls, String... keys) {
		IDataField df = currentService;
		XML xmlBase = xml;
		if (df instanceof ListField) {
			df = ((ListField)df).baseField();
		}
		ISig sig = buildCompSig(cls, keys);
		return df.instantiate(xmlBase, sig);
	}
    
    public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		IDataField df = currentService;
		ISig sig = buildCompSig(cls, keys);
		ISig lsig = new ListSig(sig);
		return df.instantiate(xml, lsig);
    }
    
    private ISig buildCompSig(Class<?> cls, String... keys) {
    	CompSig cs = new CompSig(cls);
    	for (String k : keys) {
    		cs.addField(PrimSig.WILDCARD_SIG, k);
    	}
    	return cs;
    }
    
	
}
