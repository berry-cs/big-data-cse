package big.data.csv;

public class TSVtoXMLDataSource extends CSVtoXMLDataSource {

	public TSVtoXMLDataSource(String name, String path) {
		super(name, path, '\t');
	}

}
