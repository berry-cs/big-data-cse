package big.data.xml;

import java.util.ArrayList;
import java.util.HashMap;

import big.data.*;
import big.data.field.*;
import big.data.sig.*;
import big.data.util.*;

@SuppressWarnings("unchecked")
public class XMLDataSource extends DataSource {
	protected XML xml;
	
	public XMLDataSource(String name, String path) {
		super(name, path);
	}

	public XMLDataSource(String name, String path, IDataField spec) {
		super(name, path, spec);
	}

	public XMLDataSource setXML(XML xml) {
		this.xml = xml;
		return this;
	}
	
	public DataSource load() {
		if (!readyToLoad())
			throw new DataSourceException("Not ready to load; missing parameters: " + IOUtil.join((String[])missingParams().toArray(), ','));

		if (xml == null)
			xml = IOUtil.loadXML(this.cacher.resolvePath(this.getFullPathURL()));
		if (xml == null) {
			System.err.println("Failed to load: " + this.getFullPathURL());
			return null;
		}
		
		if (spec == null) {
			spec = XMLSigBuilder.inferDataField(xml);
		}
		if (spec == null) {
			System.err.println("Failed to load: missing data field specification");
			return null;
		}

		this.loaded = true;
		return this;
	}

	public int size() {
		if (!loaded) return 0;

		return
		spec.apply(new IDFVisitor<Integer>() {
			public Integer defaultVisit(IDataField df) {
				XML node = ((ADataField)df).findMyNode(xml);
				return (node == null) ? 0 : 1;
			}

			public Integer visitPrimField(PrimField f, String basePath, String description) {
				return defaultVisit(f);
			}

			public Integer visitCompField(CompField f, String basePath,
					String description, HashMap<String, IDataField> fieldMap) {
				return defaultVisit(f);
			}

			public Integer visitListField(ListField f, String basePath,
					String description, String elemPath, IDataField elemField) {
				XML node = f.findMyNode(xml);
				if (node == null) return 0;
				XML[] children = node.getChildren(elemPath);
				return children.length;
			}
		});
	}

	public <T> T fetch(Class<T> cls, String... keys) {
		ISig sig = SigBuilder.buildCompSig(cls, keys);
		return spec.apply(new XMLInstantiator<T>(xml, sig));
	}

	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		ISig sig = new ListSig(SigBuilder.buildCompSig(cls, keys));
		return spec.apply(new XMLInstantiator<ArrayList<T>>(xml, sig));
	}

	@Override
	public String usageString() {
		// TODO Auto-generated method stub
		return null;
	}

}
