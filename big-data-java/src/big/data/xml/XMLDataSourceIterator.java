package big.data.xml;

import java.util.HashMap;

import big.data.*;
import big.data.field.*;
import big.data.sig.*;
import big.data.util.*;

public class XMLDataSourceIterator implements DataSourceIterator {
	XMLDataSource xds;
	IDataField df;
	XML[] nodes;
	int curIndex;
	
	public XMLDataSourceIterator(XMLDataSource xds, final IDataField df, final XML xml) {
		this.xds = xds;
		this.curIndex = 0;
		df.apply(new IDFVisitor<Void>() {
			public Void defaultVisit(IDataField df) {
				XMLDataSourceIterator.this.df = df;
				XMLDataSourceIterator.this.nodes = new XML[] { xml };
				return null; 
			}
			public Void visitListField(ListField f, String b, String d, String ep, IDataField ef) { 
				XMLDataSourceIterator.this.df = ef;
				XMLDataSourceIterator.this.nodes = xml.getChildren(ep);
				return null; 
			}
			public Void visitPrimField(PrimField f, String b, String d) { return defaultVisit(f); }
			public Void visitCompField(CompField f, String b, String d, HashMap<String, IDataField> fm) { return defaultVisit(f); }
		});
	}

	public boolean hasData() {
		return curIndex < nodes.length;
	}

	public DataSourceIterator loadNext() {
		if (hasData()) curIndex++;
		//if (!hasData()) throw new DataSourceException("Attempted to move past end of iterator");
		//curIndex++;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T fetch(String clsName, String... keys) {
		return (T) fetch(SigBuilder.classFor(clsName), keys);
	}

	public <T> T fetch(Class<T> cls, String... keys) {
		//if (curIndex < 0) throw new DataSourceException("Must call loadNext() before attempting to fetch from iterator");
		if (!hasData()) throw new DataSourceException("No more data available through iterator: " + xds.getName());
		ISig sig = SigBuilder.buildCompSig(cls, keys);
		return df.apply(new XMLInstantiator<T>(nodes[curIndex], sig));
	}

	public boolean fetchBoolean(String key) { return fetch(Boolean.class, key); }
	public byte fetchByte(String key) { return fetch(Byte.class, key); }
	public char fetchChar(String key) { return fetch(Character.class, key); }
	public double fetchDouble(String key) { return fetch(Double.class, key); }
	public float fetchFloat(String key) { return fetch(Float.class, key); }
	public int fetchInt(String key) { return fetch(Integer.class, key); }
	public String fetchString(String key) { return fetch(String.class, key); }

	public String usageString() {
		String s = "\nThe following data is available through iterator for: " + xds.getName() + "\n";
		s += df.apply(new FieldStringPrettyPrint(3, true)) + "\n";
		return s;
	}
}
