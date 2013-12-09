package datasrc;

public interface IDataRecord {
	public String[] fieldNames();
	public boolean containsField(String name);
	public boolean containsRecord(String name);
	public IDataField get(String name);
	public IDataRecord getRecord(String name);
	public IDataRecord[] getRecords(String name);
}
