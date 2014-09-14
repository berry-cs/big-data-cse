package big.data.csv;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import au.com.bytecode.opencsv.*;
import big.data.*;
import big.data.util.*;
import big.data.xml.XMLDataSource;

public class CSVtoXMLDataSource extends XMLDataSource {
	protected String[] header;
	protected char separator = ','; 
	
	public CSVtoXMLDataSource(String name, String path) {
		super(name, path);
		this.header = null;
	}
	
	public CSVtoXMLDataSource(String name, String path, char sep) {
		super(name, path);
		this.header = null;
		this.separator = sep;
	}

	public DataSource load() {
		try {
			String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
			if (resolvedPath != null) {
				CSVReader reader = new CSVReader( IOUtil.createReader(resolvedPath), this.separator );
				List<String[]> lines = reader.readAll();
				if (this.header == null) {  // then assume first line is header
					this.header = trimRow(lines.get(0));
					lines.remove(0);
					//System.err.println("header (" + header.length + "): " + IOUtil.join(header, ",") );
					//System.err.println("|" + header[header.length-1].length() + "|");
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

		
		for (int i = 0; i < header.length; i++)
			header[i] = fixXMLtag(header[i]);
		xmlEscapeRow(header);
		
		for (String[] row : rows) {
			XML record = xml.addChild("row");
			xmlEscapeRow(row);
			//System.err.println("Row: " + IOUtil.join(row, ",") + " (" + row.length + ")");

			if (row.length >= header.length) {
				for (int i = 0; i < header.length; i++) {
					String tag = header[i];
					try {
						XML child = record.addChild(tag);
						child.setContent(row[i].trim());
					} catch (org.w3c.dom.DOMException e) {
						e.printStackTrace();
						System.err.println("Tag: " + tag + " row[i]: " + row[i].trim());
					}
				}
			} else {
				System.err.printf("CSV data: skipping row, length %d vs header row length %d.\n", row.length, header.length);
			}
		}
		
		//System.out.println(xml);

		return xml;
	}

	protected static char[][] replaces = { {' ', '_'}, {'/', '-'} };

	protected static String fixXMLtag(String orig) {
		String tag = orig.trim();
		for (char[] r : replaces) {
			tag = tag.replace(r[0], r[1]);
		}
		for (char r : "!\"#$%&'()*+,/;<=>?@[\\]^`{|}~".toCharArray()) {
			String re = Pattern.quote("" + r);
			tag = tag.replaceAll(re, "");
		}
		if (Character.isDigit(tag.charAt(0))) {
			tag = "n" + tag;
		}
		return tag;
	}
	
	protected static void xmlEscapeRow(String[] row) {
		for (int i = 0; i < row.length; i++) {
			row[i] = StringEscapeUtils.escapeXml11(row[i]);
		}
	}
	
	protected static String[] trimRow(String[] row) {
		int start = 0, end = row.length;
		for (int i = 0; 
		     i < row.length
				&& (row[i] == null || row[i].trim().length() == 0); 
			 i++) {
			start = i;
		}
		if (start == row.length)  
			return null;    // nothing in row

		for (int i = row.length-1;
			 i > start 
				&& (row[i] == null || row[i].trim().length() == 0);
			 i--) {
			end = i;
		}
		
		String[] newrow = new String[end - start];
		for (int i = start; i < end; i++) 
			newrow[i - start] = row[i];
		//System.out.printf("start: %d end: %d\n", start, end);
		return newrow;
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
