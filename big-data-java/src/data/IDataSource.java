package data;

import java.util.ArrayList;

public interface IDataSource {
	public boolean dataAvailable();
	
	public <T> T fetch(String clsName, String... keys);
	public <T> T fetch(Class<T> cls, String... keys);
	public <T> ArrayList<T> fetchList(String clsName, String... keys);
	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys);
	public <T> T[] fetchArray(String clsName, String... keys);
	public <T> T[] fetchArray(Class<T> cls, String... keys);
	
	public String usageString();
}
