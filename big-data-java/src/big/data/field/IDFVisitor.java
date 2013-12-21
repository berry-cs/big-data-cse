package big.data.field;

import java.util.HashMap;

public interface IDFVisitor<T> {
	public T defaultVisit(IDataField df);
	public T visitPrimField(PrimField f, String basePath, String description);
	public T visitCompField(CompField f, String basePath, String description, HashMap<String, IDataField> fieldMap);
	public T visitListField(ListField f, String basePath, String description, String elemPath, IDataField elemField);
}
