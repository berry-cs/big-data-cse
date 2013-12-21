package big.data;

import java.lang.reflect.Array;
import java.util.*;

import big.data.field.*;
import big.data.sig.*;
import big.data.util.*;


/**
 * Common functionality of data sources. 
 * Every data source has a path, which might be:
 *   - a (relative or absolute) path string to a local file
 *   - a URL
 * The path may contain substrings of the form @{key} which represent
 * portions that will be substituted with the value of a 
 * ParamType.PATH parameter. 
 * 
 * @author Nadeem Abdul Hamid
 */
@SuppressWarnings({"unchecked"})
public abstract class DataSource implements IDataSource {

	protected String name;
	protected String path;  
	protected String description;
	protected String infoURL;
	
	protected HashMap<String, IParam> params;
	protected ArrayList<String> paramValueKeys;
	protected HashMap<String, String> paramValues;
	
	protected DataCacher cacher;
	protected IDataField spec;
	
	protected boolean readyToLoad;
	protected boolean loaded;
	

	public DataSource(String name, String path) {
		this(name, path, null);
	}

	public DataSource(String name, String path, IDataField spec) {
		this.name = name;
		this.path = path;
		this.spec = spec;
		this.description = null;
		this.infoURL = null;
		this.readyToLoad = false;
		this.loaded = false;
		this.cacher = DataCacher.defaultCacher();
		this.params = new HashMap<String, IParam>();
		this.paramValues = new HashMap<String, String>();
		this.paramValueKeys = new ArrayList<String>();
	}

	public String getFullPathURL() {
		if (!readyToLoad) 
			throw new RuntimeException("Cannot finalize path: not ready to load");

		URLPrepper prepper = new URLPrepper(this.path);
		
		// add query params to request URL...
		if (URLPrepper.isURL(this.path)) {
			for (String k : paramValueKeys) {
				IParam p = findParam(k);
				if (p == null || p.getType() == ParamType.QUERY) {
					String v = paramValues.get(k);
					prepper.addParam(k, v);
				}
			}
		}
		
		// fill in substitutions
		String fullpath = prepper.getRequestURL();
		for (String k : paramValueKeys) {
			IParam p = findParam(k);
			if (p != null && p.getType() == ParamType.PATH) {
				fullpath = substParam(fullpath, k, paramValues.get(k));
			}
		}
		
		return fullpath;
	}
	
	protected IParam findParam(String key) {
		return params.get(key);
		/*int i;
		for (i = 0; i < params.size(); i++) {
			if (params.get(i).getKey().equals(key)) break;
		}
		return (i < params.size()) ? params.get(i) : null;*/
	}
	
	/*
	 * Replaces "...@{key}..." substring in fullpath with value
	 */
	protected String substParam(String fullpath, String key, String value) {
		return fullpath.replace((CharSequence)("@{" + key + "}"), (CharSequence)value);
	}
	
	
	//public abstract IDataSource setOption(String op, String val);
	public abstract DataSource load();
	public abstract int size();
	public abstract <T> T fetch(Class<T> cls, String... keys);
	public abstract <T> ArrayList<T> fetchList(Class<T> cls, String... keys);
	public abstract String usageString();
	
	
	
	public DataSource setFieldSpec(IDataField spec) {
		this.spec = spec;
		return this;
	}
	
	public DataSource setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getInfoURL() {
		return infoURL;
	}

	public void setInfoURL(String infoURL) {
		this.infoURL = infoURL;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Checks that all required parameter values have been supplied
	 */
	public boolean readyToLoad() {
		this.readyToLoad = this.readyToLoad || missingParams().size()==0;
		return this.readyToLoad;
	}
	
	public List<String> missingParams() {
		ArrayList<String> ps = new ArrayList<String>();
		for (IParam p : params.values()) {
			if (!paramValues.containsKey(p.getKey())
					|| paramValues.get(p.getKey()) == null) {
				ps.add(p.getKey());
			}
		}
		return ps;
	}

	public boolean hasData() {
		return this.loaded;
	}

	public DataSource addParam(IParam param) {
		if (param != null) 
			params.put(param.getKey(), param);
		return this;
	}

	public DataSource set(String op, String value) {
		if (op != null && value != null) {
			paramValues.put(op, value);
			paramValueKeys.add(op);
		}
		return this;
	}

	public DataSource setCacheTimeout(long val) {
		this.cacher = this.cacher.updateTimeout(val);
		return this;
	}

	public DataSource setCacheDirectory(String path) {
		if (path != null) {
			// TODO: need to do substitutions:   @{tempdir}  @{homedir}  @{curdir} =/= @{sketchdir} 
			this.cacher = this.cacher.updateDirectory(path);
		}
		return this;
	}
	
	public DataSource setOption(String op, String value) {
		System.err.println("Warning: " + op + " option ignored");
		return this;
	}

	public <T> T fetch(String clsName, String... keys) {
		return (T) fetch(SigBuilder.classFor(clsName), keys);
	}

	public <T> ArrayList<T> fetchList(String clsName, String... keys) {
		return (ArrayList<T>) fetchList(SigBuilder.classFor(clsName), keys);
	}

	public <T> T[] fetchArray(String clsName, String... keys) {
		return (T[]) fetchArray(SigBuilder.classFor(clsName), keys);
	}
	
    public <T> T[] fetchArray(Class<T> cls, String... keys) {
    	T[] ts = (T[]) Array.newInstance(cls, 1);
    	return (T[]) (fetchList(cls, keys).toArray(ts));
    }

}
