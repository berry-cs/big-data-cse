package big.data;

import java.util.ArrayList;
import java.util.List;

import big.data.field.IDataField;

/**
 * Represents a data source
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public interface IDataSource {
	
	/**
	 * Produce the name of this data source
	 * @return the name of this data source
	 */
	public String getName();
	
	/**
	 * Produce a description of this data source
	 * @return a description of this data source
	 */
	public String getDescription();
	
	/**
	 * Produce a URL to more information about this data source
	 * @return a URL to more information about this data source
	 */
	public String getInfoURL();
			
	/**
	 * Determines whether all required parameters have been supplied for this data source 
	 * @return whether all required parameters have been supplied for this data source 
	 */
	public boolean readyToLoad();
	
	/**
	 * Produces a list of keys of required parameters that have not
	 * been supplied yet
	 * @return a list of parameter keys
	 */
	public List<String> missingParams();
	
	/**
	 * Determines whether data has been successfully loaded and parsed from the data source 
	 * @return whether data has been successfully loaded and parsed from the data source 
	 */
	public boolean hasData();
	
	/**
	 * Registers a request parameter with this data source
	 * @param param the parameter specification
	 * @return this updated data source
	 */
	public IDataSource addParam(IParam param);
	
	/** 
	 * Sets a value for a parameter (either query or path)
	 * @param op key for the parameter
	 * @param val value for the parameter
	 * @return this updated data source
	 */
	public IDataSource set(String op, String val);
	
	/**
	 * Sets the cache timeout value, in <em>minutes</em>.
	 */
	public IDataSource setCacheTimeout(int val);
	public IDataSource setCacheDirectory(String path);

	/**
	 * Sets an internal option for the data source
	 * @return this updated data source
	 */
	public IDataSource setOption(String op, String val);
	
	/**
	 * Sets the data structure specification for extracting
	 * data from this source
	 */
	public IDataSource setFieldSpec(IDataField spec);
	
	/**
	 * Attempt to load and parse the data source
	 * Success may be checked using the hasData() method
	 * @return this updated data source
	 */
	public IDataSource load();
	
	/**
	 * How much elements of data are available from this
	 * data source. Produces 0 if data not loaded.
	 * @return number of elements of data available
	 */
	public int size();
	
	/**
	 * Produce an iterator over this data source
	 * 
	 * @return an iterator object
	 */
	public DataSourceIterator iterator();
	
	public <T> T fetch(String clsName, String... keys);
	public <T> T fetch(Class<T> cls, String... keys);
	public <T> ArrayList<T> fetchList(String clsName, String... keys);
	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys);
	public <T> T[] fetchArray(String clsName, String... keys);
	public <T> T[] fetchArray(Class<T> cls, String... keys);
	
	public boolean fetchBoolean(String key);
	public byte fetchByte(String key);
	public char fetchChar(String key);
	public double fetchDouble(String key);
	public float fetchFloat(String key);
	public int fetchInt(String key);
	public String fetchString(String key);
	
	public boolean[] fetchBooleanArray(String key);
	public byte[] fetchByteArray(String key);
	public char[] fetchCharArray(String key);
	public double[] fetchDoubleArray(String key);
	public float[] fetchFloatArray(String key);
	public int[] fetchIntArray(String key);
	public String[] fetchStringArray(String key);
	
	public ArrayList<Boolean> fetchBooleanList(String key);
	public ArrayList<Byte> fetchByteList(String key);
	public ArrayList<Character> fetchCharList(String key);
	public ArrayList<Double> fetchDoubleList(String key);
	public ArrayList<Float> fetchFloatList(String key);
	public ArrayList<Integer> fetchIntList(String key);
	public ArrayList<String> fetchStringList(String key);

	public String usageString(boolean verbose);
}
