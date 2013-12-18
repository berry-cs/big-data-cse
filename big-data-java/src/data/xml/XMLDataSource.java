package data.xml;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import data.*;
import ext.*;

public class XMLDataSource extends DataSource {
	private DataCacher dc;
	private XML xml;
	private HashMap<String, IDataField> servs; 
	private IDataField currentService;

	
	public XMLDataSource(String fileNameOrURL) {
		this(fileNameOrURL, DataCacher.defaultCacher());
	}
	
	public XMLDataSource(String fileNameOrURL, long millis) {
		this(fileNameOrURL, new DataCacher(millis));
	}
	
	public XMLDataSource(String fileNameOrURL, DataCacher dc) {
		String respath = dc.resolvePath(fileNameOrURL);
		//System.err.println("Reading from " + respath);
		this.xml = IOUtil.loadXML(respath);
		this.servs = new HashMap<String, IDataField>();
		this.dc = dc;
		if (this.xml != null) { 
			this.currentService = inferDataField(this.xml);
			registerService("default", this.currentService);
		} else {
			this.currentService = null;
		}
	}
	
	public XMLDataSource(XML xml) {
		this.xml = xml;
		this.servs = new HashMap<String, IDataField>();
		this.currentService = inferDataField(this.xml);
		registerService("default", this.currentService);
	}

	public void registerService(String name, IDataField fld) {
		this.servs.put(name, fld);
	}
	
	public boolean dataAvailable() {
		return this.currentService != null;
	}
	
	private XML firstNonemptyChild(XML xml) {
		XML[] children = xml.getChildren();
		XML chosen = null;
		for (int i = 0; i < children.length; i++) {
			XML c = children[i];
			if (c.getContent() != null && !c.getContent().trim().equals("")) {
				chosen = c;
				break;
			}
		}
		return chosen;
	}
	
	private boolean isEmptyXML(XML node) {
		return node.getContent().trim().equals("");
	}
	
	
	private IDataField inferDataField(XML xml) {
		XML firstChild = firstNonemptyChild(xml);
		if (firstChild == null) {
			// TODO: this is ineffective?
			return new PrimField(xml.getName());
		} else {
			String firstChildTag = firstChild.getName();
			XML[] children = xml.getChildren(firstChildTag);

			if (children.length > 1) {
				IDataField cf = inferCompField(xml.getChild(firstChildTag));
				IDataField lf = new ListField(null, firstChildTag, cf);

				//System.err.println(lf);
				return lf;
			} else {
				CompField cf = inferCompField(xml);

				//System.err.println(cf);
				return cf;					
			}
		}
	}
	
	
	private static class SubFieldCollector implements IDFVisitor<Void> {
		CompField targetField;
		String prefix;
		
		SubFieldCollector(CompField cf, String prefix) { this.targetField = cf; this.prefix = prefix; }
		
		public Void defaultVisit(IDataField df) { throw new RuntimeException("Unhandled"); }

		public Void visit(PrimField pf) {
			String pathName = prefix + pf.getPath();
			//System.err.println(">>>" + prefix + "/" + pf.getPath());
			targetField.addField(pathName, new PrimField(pathName));
			return null;
		}

		public Void visit(CompField cf) {
			for (String n : cf.fieldNames()) {
				cf.getField(n).apply(new SubFieldCollector(targetField, prefix));
			}
			return null;
		}

		public Void visit(ListField lf) {
			return null;
		}		
	}
	
	
	private CompField inferCompField(XML xml) {
		XML[] children = xml.getChildren();
		CompField cf = new CompField();
		for (XML t : children) {
			if (!isEmptyXML(t) && !t.getName().equals("#text")) {
				if (t.getChildCount() > 1) {
					IDataField pf = inferDataField(t);		
					cf.addField(t.getName(), pf);
					//System.err.println("Added " + t.getName() + ": " + pf);
					pf.apply(new SubFieldCollector(cf, t.getName() + "/"));
				} else {
					PrimField pf = new PrimField(t.getName());
					cf.addField(t.getName(), pf);
				}
			}
		}
		return cf;	
	}
	
	
	private <T> Class<T> classFor(String clsName) {
		if (clsName.equals("String")) return classFor("java.lang.String");
		else if (clsName.equals("Double") || clsName.equals("double")) return classFor("java.lang.Double");
		
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
		/*XML xmlBase = xml;
		if (df instanceof ListField) {
			ListField lf = (ListField)df;
			xmlBase = xml.getChild(lf.elemPath());
			df = lf.elemField();
		}*/
		ISig sig = buildCompSig(cls, keys);
		return df.instantiate(xml, sig);
	}
    
    public <T> ArrayList<T> fetchList(String clsName, String... keys) {
    	return (ArrayList<T>) fetchList(classFor(clsName), keys);
    }
    
    public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		IDataField df = currentService;
		ISig sig = buildCompSig(cls, keys);
		ISig lsig = new ListSig(sig);
		return df.instantiate(xml, lsig);
    }
    
    public <T> T[] fetchArray(String clsName, String... keys) {
    	return (T[]) fetchArray(classFor(clsName), keys);
    }
    
    public <T> T[] fetchArray(Class<T> cls, String... keys) {
    	T[] ts = (T[]) Array.newInstance(cls, 1);
    	return (T[]) (fetchList(cls, keys).toArray(ts));
    }
       
    private ISig buildCompSig(Class<?> cls, String... keys) {
    	CompSig cs = new CompSig(cls);
    	for (String k : keys) {
    		cs.addField(PrimSig.WILDCARD_SIG, k);
    	}
    	return cs;
    }
    
	public String usageString() {
		String s = servs.size() + " services.\n\n";
		for (String name : servs.keySet()) {
			IDataField df = servs.get(name);
			s += "Service: " + name + (currentService == df ? " [*currently selected]" : "") + "\n";
			s += df.toString(3);
			
		}
		
		return s;
	}
}
