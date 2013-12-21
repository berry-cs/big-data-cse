package big.data;

/**
 * A parameter specification for a data source
 *  
 * @author Nadeem Abdul Hamid
 */
public interface IParam {
	public String getKey();
	public String getDescription();
	public ParamType getType();
	public boolean isRequired();
	
}
