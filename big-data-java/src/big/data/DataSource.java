package big.data;

import java.lang.reflect.Array;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import big.data.csv.CSVtoXMLDataSource;
import big.data.csv.TSVtoXMLDataSource;
import big.data.field.*;
import big.data.json.JSONtoXMLDataSource;
import big.data.sig.*;
import big.data.util.*;
import big.data.xml.XMLDataSource;


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
	
	public static DataSource connect(String path) {
		path = ProcessingDetector.tryToFixPath(path);
		if (DataSourceLoader.isValidDataSourceSpec(path)) {
			return connectUsing(path);
		} else if (path.toLowerCase().endsWith(".csv") || path.toLowerCase().contains(".csv.")) {
			return connectCSV(path);
		} else if (path.toLowerCase().endsWith(".json")) {
			return connectJSON(path);
		} else {
			return connectXML(path);
		}
	}
	
	public static DataSource connectUsing(String specpath) {
		specpath = ProcessingDetector.tryToFixPath(specpath);
		// TODO: validate
		return new DataSourceLoader(specpath).getDataSource();
	}
	
	public static DataSource connectXML(String path) {
		path = ProcessingDetector.tryToFixPath(path);
		//String[] pcs = IOUtil.split(path, "/");
		// TODO: generate more sensible name?
		return connectXML(path, path);
	}

	public static DataSource connectJSON(String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return connectJSON(path, path);
	}
	
	public static DataSource connectCSV(String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return connectCSV(path, path);
	}
	
	public static DataSource connectTSV(String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return connectTSV(path, path);
	}

	public static DataSource connectXML(String name, String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return new XMLDataSource(name, path);
	}
	
	public static DataSource connectJSON(String name, String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return new JSONtoXMLDataSource(name, path);
	}
	
	public static DataSource connectCSV(String name, String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return new CSVtoXMLDataSource(name, path);
	}
	
	public static DataSource connectTSV(String name, String path) {
		path = ProcessingDetector.tryToFixPath(path);
		return new TSVtoXMLDataSource(name, path);
	}
	
	public static void initializeProcessing(Object papp) {
		if (ProcessingDetector.inProcessing()) {
			ProcessingDetector.setPappletObject(papp);
		}
		else {
			System.err.println("initializeProcessing() should only be called if Processing is being used.");
		}
	}

	public IDataField getFieldSpec() {
		return this.spec;
	}
	
	public String getFullPathURL() {
		if (!readyToLoad()) 
			throw new RuntimeException("Cannot finalize path: not ready to load");

		String fullpath = this.path;
		
		// add query params to request URL...
		if (URLPrepper.isURL(this.path)) {
			URLPrepper prepper = new URLPrepper(this.path);
			for (String k : paramValueKeys) {
				IParam p = findParam(k);
				if (p == null || p.getType() == ParamType.QUERY) {
					String v = paramValues.get(k);
					prepper.addParam(k, v);
				}
			}
			fullpath = prepper.getRequestURL();
		}
		
		// fill in substitutions
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
	public abstract DataSourceIterator iterator();

	
	
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
    
	public boolean fetchBoolean(String key) { return fetch(Boolean.class, key); }
	public byte fetchByte(String key) { return fetch(Byte.class, key); }
	public char fetchChar(String key) { return fetch(Character.class, key); }
	public double fetchDouble(String key) { return fetch(Double.class, key); }
	public float fetchFloat(String key) { return fetch(Float.class, key); }
	public int fetchInt(String key) { return fetch(Integer.class, key); }
	public String fetchString(String key) { return fetch(String.class, key); }
	
	public boolean[] fetchBooleanArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Boolean.class, key));
	}
	public byte[] fetchByteArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Byte.class, key));
	}
	public char[] fetchCharArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Character.class, key));
	}
	public double[] fetchDoubleArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Double.class, key));
	}
	public float[] fetchFloatArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Float.class, key));
	}
	public int[] fetchIntArray(String key) {
		return ArrayUtils.toPrimitive(fetchArray(Integer.class, key));
	}
	public String[] fetchStringArray(String key) {
		return fetchArray(String.class, key);
	}
	
	public ArrayList<Boolean> fetchBooleanList(String key) {
		return fetchList(Boolean.class, key);
	}
	public ArrayList<Byte> fetchByteList(String key) {
		return fetchList(Byte.class, key);
	}
	public ArrayList<Character> fetchCharList(String key) {
		return fetchList(Character.class, key);
	}
	public ArrayList<Double> fetchDoubleList(String key) {
		return fetchList(Double.class, key);
	}
	public ArrayList<Float> fetchFloatList(String key) {
		return fetchList(Float.class, key);
	}
	public ArrayList<Integer> fetchIntList(String key) {
		return fetchList(Integer.class, key);
	}
	public ArrayList<String> fetchStringList(String key) {
		return fetchList(String.class, key);
	}
    
	
	public String usageString(boolean verbose) {
		String s = "-----\n";
		if (this.name != null) 
			s += "Data Source: " + this.name + "\n";
		if (description != null && !description.equals("")) s += description + "\n";
		if (infoURL != null) s += "(See " + infoURL + " for more information about this data.)\n";

		String[] paramKeys = params.keySet().toArray(new String[]{});
		if (paramKeys.length > 0) {
			Arrays.sort(paramKeys);
			s += "\nThe following options may/must be set on this data source:\n";
			for (String key : paramKeys) {
				IParam p = params.get(key);
				String v = paramValues.get(key);
				String desc = p.getDescription();
				boolean req = p.isRequired();
				s += "   - " + key
						+ ((v==null)?" (not set)":" (currently set to: " + v + ")") 
						+ ((desc==null)?"":" : " + desc) + ((v==null && req)?" [*required]":"")
						+ "\n";
			}
		}
			
		if (spec != null)
			s += "\nThe following data is available:\n" + spec.apply(new FieldStringPrettyPrint(3, true, !verbose)) + "\n";
		
		if (!this.hasData())
			s += "\n*** Data not loaded *** ... use .load()\n";
		
		s += "-----\n";
		return s;			
	}

	public void printUsageString() {
		printUsageString(false);
	}
	
	public void printUsageString(boolean verbose) {
		System.out.println(usageString(verbose));
	}

}

