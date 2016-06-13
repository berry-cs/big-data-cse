package easy.data.xml;

import java.util.ArrayList;
import java.util.HashMap;

import easy.data.*;
import easy.data.field.*;
import easy.data.sig.*;
import easy.data.util.*;


public class XMLDataSource extends DataSource {
	protected XML xml;
	protected ArrayList<IPostProcessor> procs = new ArrayList<IPostProcessor>();
	//protected IPostProcessor proc;
	
	public XMLDataSource(String name, String path) {
		super(name, path);
		procs.add(new AttributeToChildNodeProcessor());
	}

	public XMLDataSource(String name, String path, IDataField spec) {
		super(name, path, spec);
		procs.add(new AttributeToChildNodeProcessor());
	}

	public XMLDataSource setXML(XML xml) {
		this.xml = xml;
		doPostProcess();
		return this;
	}
	
	public XMLDataSource addPostProcessor(IPostProcessor proc) {
		this.procs.add(proc);
		return this;
	}
	
	public XMLDataSource addPostProcessor(String clsName) {
		try {
			addPostProcessor((IPostProcessor) SigBuilder.classFor(clsName).newInstance());
		} catch (InstantiationException e) {
			System.err.println("Could not load post-processor: " + clsName);
		} catch (IllegalAccessException e) {
			System.err.println("Could not load post-processor: " + clsName);
		}
		return this;
	}
	
	protected void doPostProcess() {
		for (IPostProcessor proc : procs) {
			xml = proc.process(xml);
			if (xml == null) {
				System.err.println(((getName()==null)?"":getName()+": ") + "XML post-process failed");
			}
		}
	}
	
	public DataSource load() {
		return this.load(true);
	}
	
	public DataSource load(boolean forceReload) {
		if (!readyToLoad())
			throw new DataSourceException("Not ready to load; missing parameters: " + IOUtil.join(missingParams().toArray(new String[]{}), ','));

		boolean newlyLoaded = false;
		
		String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
		if (resolvedPath != null && (xml == null || forceReload)) { 
			xml = IOUtil.loadXML(resolvedPath);
			newlyLoaded = true;
		}

		if (xml == null) {
			//System.err.println("Failed to load: " + this.getFullPathURL() + " (CHECK NETWORK CONNECTION, if applicable)");
			return this;
		} else {
			if (newlyLoaded) doPostProcess();
		}
		
		if (spec == null)
			spec = XMLDataFieldInferrer.inferDataField(xml);
			spec.apply(new IDFVisitor<Void>() {
				public Void visitCompField(CompField f, String basePath,
						String description, HashMap<String, IDataField> fieldMap) {
					CompField collected = new CompField();
					// here f == spec
					for (String name : f.fieldNames()) {
						IDataField subfield = f.getField(name);
						subfield.apply(new SubFieldCollector(collected, name));
					}
					// have to break out collecting and adding to spec, because otherwise
					// a ConcurrentModificationException happens - adding fields to f while
					// it is being traversed
					for (String name : collected.fieldNames()) {
						f.addField(name, collected.getField(name));
					}
					return null;
				}

				public Void defaultVisit(IDataField df) { return null; }
				public Void visitPrimField(PrimField f, String basePath,
						String description) { return defaultVisit(f); }
				public Void visitListField(ListField f, String basePath,
						String description, String elemPath,
						IDataField elemField) { return defaultVisit(f); }
			});
			//System.out.println("Inferred: " + spec);
		if (spec == null)
			System.err.println("Failed to load: missing data field specification");
		
		if (xml != null && spec != null) {
			this.loaded = true;
		}
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
	
	public DataSourceIterator iterator() {
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		return new XMLDataSourceIterator(this, this.spec, this.xml);
	}

	public <T> T fetch(Class<T> cls, String... keys) {
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		ISig sig = SigBuilder.buildCompSig(cls, keys);
		return spec.apply(new XMLInstantiator<T>(xml, sig));
	}

	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		//System.out.println("fetchList: " + keys[0] + " -> " + cls);		
		
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		ISig sig = new ListSig(SigBuilder.buildCompSig(cls, keys));
		//System.out.println("Spec: " + spec);
		//System.out.println("Sig: " + sig);
		//System.out.println("XML: " + xml);
		return spec.apply(new XMLInstantiator<ArrayList<T>>(xml, sig));
	}

	public DataSource setOption(String op, String value) {
		if ("postprocess".equals(op) && value != null)
			return addPostProcessor(value);
		else
			return super.setOption(op, value);
	}
}
