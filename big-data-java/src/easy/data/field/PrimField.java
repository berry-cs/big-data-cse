package easy.data.field;

public class PrimField extends ADataField implements IDataField {
	public PrimField() {
		super(null, null);
	}
	
	public PrimField(String path) {
		super(path, null);
	}
	
	public PrimField(String path, String description) {
		super(path, description);
	}
	
	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visitPrimField(this, this.basePath, this.description);
	}

	public String toString() {
		return "<" + basePath + ">";
	}
}

