package easy.data.field;

public interface IDataField {
	public String getDescription();
	public <T> T apply(IDFVisitor<T> fv);
}
