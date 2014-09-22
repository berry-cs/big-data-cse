package big.data.field;

public class ListField extends ADataField implements IDataField {
	private String elemPath;
	private IDataField elemField;

	public ListField(String basepath, String elempath, IDataField basefld) {
		this(basepath, elempath, basefld, null);
	}
	
	public ListField(String basepath, String elemPath, IDataField elemField, String description) {
		super(basepath, description);
		this.elemPath = elemPath;
		this.elemField = elemField;
	}
	
	public String getElemPath() {
		return this.elemPath;
	}
	
	public IDataField getElemField() {
		return this.elemField;
	}

	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visitListField(this, this.basePath, this.description, this.elemPath, this.elemField);
	}

	public String toString() {
		return "[_<" + basePath + "," + elemPath + "> " + elemField + "]";
	}

}
