package data.csv;

import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.*;
import data.*;
import data.xml.XMLDataSource;
import ext.*;

public class CSVDataSourceFactory {
	private static DataCacher dc = DataCacher.defaultCacher();
	
	public static DataSource getDataSource(String path) {
		try {
			CSVReader reader = new CSVReader( IOUtil.createReader(dc.resolvePath(path)) );
			List<String[]> lines = reader.readAll();

			String[] header = lines.get(0);
			lines.remove(0);
			XML xml = buildXML(header, lines);

			//System.err.println(xml);
			reader.close();
			return new XMLDataSource(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public static DataSource getDataSource(String path, String[] labels) {
		try {
			CSVReader reader = new CSVReader( IOUtil.createReader(dc.resolvePath(path)) );
			List<String[]> lines = reader.readAll();
			XML xml = buildXML(labels, lines);
			reader.close();
			return new XMLDataSource(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static XML buildXML(String[] header, List<String[]> rows) {
		XML xml = new XML("rows");
		
		for (String[] row : rows) {
			XML record = xml.addChild("row");
			// TODO: check header.length == row.length
			for (int i = 0; i < header.length; i++) {
				String tag = fixXMLtag(header[i]);
				XML child = record.addChild(tag);
				child.setContent(row[i]);
			}
		}
		
		return xml;
	}
	
	
	private static char[][] replaces = { {' ', '_'}, {'/', '-'} };
	
	private static String fixXMLtag(String orig) {
		String tag = orig.trim();
		for (char[] r : replaces) {
			tag = tag.replace(r[0], r[1]);
		}
		return tag;
	}
	
}




