package datasrc;

public interface IDataRecord {
	public String[] fieldNames();
	public IDataField get(String name);
	//public IDataField get(int i);
	public boolean contains(String name);
}
