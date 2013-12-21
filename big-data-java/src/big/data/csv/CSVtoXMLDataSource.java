package big.data.csv;

import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.*;
import big.data.*;
import big.data.util.*;
import big.data.xml.XMLDataSource;

public class CSVtoXMLDataSource extends XMLDataSource {
	private String[] header;
	
	public CSVtoXMLDataSource(String name, String path) {
		super(name, path);
		this.header = null;
	}

	public DataSource load() {
		try {
			String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
			if (resolvedPath != null) {
				CSVReader reader = new CSVReader( IOUtil.createReader(resolvedPath) );
				List<String[]> lines = reader.readAll();
				if (this.header == null) {  // then assume first line is header
					this.header = lines.get(0);
					lines.remove(0);
				}
				XML xml = buildXML(this.header, lines);
				reader.close();
				this.setXML(xml);
				return super.load(false);
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public DataSource setOption(String op, String value) {
		if ("header".equals(op)) {
			try {
				CSVReader reader = new CSVReader(new StringReader(value));
				this.header = reader.readNext();
				reader.close();
				return this;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return super.setOption(op, value);
		}
	}

	protected static XML buildXML(String[] header, List<String[]> rows) {
		XML xml = new XML("rows");

		for (String[] row : rows) {
			XML record = xml.addChild("row");
			//System.err.println("Row: " + IOUtil.join(row, ","));

			if (row.length == header.length) {
				for (int i = 0; i < header.length; i++) {
					String tag = fixXMLtag(header[i]);
					XML child = record.addChild(tag);
					child.setContent(row[i]);
				}
			} else {
				System.err.println("CSV data: skipping row");
			}
		}

		return xml;
	}

	protected static char[][] replaces = { {' ', '_'}, {'/', '-'} };

	protected static String fixXMLtag(String orig) {
		String tag = orig.trim();
		for (char[] r : replaces) {
			tag = tag.replace(r[0], r[1]);
		}
		return tag;
	}

	/*
	public int size() {
		return 0;
	}

	public <T> T fetch(Class<T> cls, String... keys) {
		throw new DataSourceException("No data available: " + this.getName());
	}

	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		throw new DataSourceException("No data available: " + this.getName());
	}
	*/
}
