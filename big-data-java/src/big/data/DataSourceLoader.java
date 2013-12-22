package big.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.ArrayUtils;

import big.data.field.*;
import big.data.util.*;


public class DataSourceLoader {
	DataSource ds;

	public static final Object[][] FORMAT_MAP
	   = { { "XML", big.data.xml.XMLDataSource.class },
		   { "CSV", big.data.csv.CSVtoXMLDataSource.class },
		   { "TSV", big.data.csv.TSVtoXMLDataSource.class }
	     };
	
	public DataSourceLoader(String specpath) {
		if (!isValidDataSourceSpec(specpath))
			throw new DataSourceException("Invalid data source specification: " + specpath);
		XML xml = IOUtil.loadXML(specpath);
		
		String name = getContentOf(xml, "name");
		String path = getContentOf(xml, "path");
		
		try {
			Class<?> dsclass = lookupClass(getContentOf(xml, "format"));
			Constructor<?> cr = dsclass.getConstructor(String.class, String.class);
			cr.setAccessible(true);
			ds = (DataSource) cr.newInstance((name!=null)?name:path, path);
			
			String infoURL = getContentOf(xml, "infourl");
			if (infoURL != null) ds.setInfoURL(infoURL);
			
			String description = getContentOf(xml, "description");
			if (description != null) ds.setDescription(description);
			
			setCacheOptions(xml.getChild("cache"), ds);
			setOptions(xml.getChild("options"), ds);
			setParams(xml.getChild("params"), ds);
			setDataSpec(xml.getChild("dataspec"), ds);
			
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new DataSourceException("Failed initializing data source: " + specpath);
		}
		
	}
		
	public DataSource getDataSource() {
		return ds;
	}
		
	private Class<?> lookupClass(String format) {
		if (format == null)
			throw new DataSourceException("Missing data format in specification");
		
		for (Object[] pair : FORMAT_MAP) {
			if (pair[0].equals(format)) return (Class<?>)pair[1];
		}
		
		throw new DataSourceException("Unrecognized data format in specification: " + format);
	}
	
	private void setDataSpec(XML node, DataSource ds) {
		if (node == null) return;
		IDataField df = extractAndParseFieldSpec(node);
		if (df == null) System.err.println("Failed to parse data specification");	
		else ds.setFieldSpec(df);
	}
	
	
	private IDataField extractAndParseFieldSpec(XML node) {
		XML[] children = node.getChildren();
		for (XML child : children) {
			String tag = child.getName();
			if (ArrayUtils.contains(new String[] {"compfield",  "listfield", "primfield"}, tag)) {
				return parseFieldSpec(tag, child);
			}
		}
		return null;
	}
	
	
	private IDataField parseFieldSpec(String tag, XML node) {
		String basepath = getContentOf(node, "basepath");
		String description = getContentOf(node, "description");
		
		if (tag.equals("primfield")) {
			return new PrimField(basepath, description); 
		} else if (tag.equals("compfield")) {
			CompField cf = new CompField(basepath, description);
			XML fields = node.getChild("fields");
			if (fields != null) {
				for (XML fn : fields.getChildren("field")) {
					String fieldname = getContentOf(fn, "name");
					IDataField df = extractAndParseFieldSpec(fn);
					if (fieldname == null || df == null) {
						System.err.println("Failed to parse compfield field"); 
						return null;
					}
					cf.addField(fieldname, df);
				}
				return cf;
			}
			System.err.println("No fields specified for compfield");
		} else if (tag.equals("listfield")) {
			String elempath = getContentOf(node, "elempath");
			XML elemnode = node.getChild("elemfield");
			IDataField elemfield = extractAndParseFieldSpec(elemnode);
			if (elempath == null || elemfield == null) {
				System.err.println("Failed to parse listfield specification (missing elempath or elemfield)");
			} else {
				return new ListField(basepath, elempath, elemfield, description);
			}
		}		
		return null;
	}


	private void setParams(XML node, DataSource ds) {
		if (node == null) return;
		XML[] queryps = node.getChildren("queryparam");
		XML[] pathps = node.getChildren("pathparam");
		for (XML qp: queryps) setParam(qp, ds, ParamType.QUERY);
		for (XML pp: pathps) setParam(pp, ds, ParamType.PATH);
	}
	

	private void setParam(XML node, DataSource ds, ParamType type) {
		boolean required = node.hasAttribute("required");
		String key = getContentOf(node, "key");
		if (key == null) {
			System.err.println("Error reading data source param (key not specified)");
			return;
		}
		String val = getContentOf(node, "value");
		String description = getContentOf(node, "description");
		
		if (val != null) {
			ds.set(key, val);
		} else {
			ds.addParam(new Param(key, description, type, required));
		}
	}
	
	
	private void setOptions(XML node, DataSource ds) {
		if (node == null) return;
		XML[] opnodes = node.getChildren("option");
		for (XML opnode : opnodes) {
			String name = getContentOf(opnode, "name");
			String val = getContentOf(opnode, "value");
			if (name == null || val == null) {
				System.err.println("Error setting data source option (name or value not specified)");
				continue;
			}
			ds.setOption(name, val);
		}
	}

	
	private void setCacheOptions(XML node, DataSource ds) {
		if (node == null) return;
		String timeout = getContentOf(node, "timeout");
		String dir = getContentOf(node, "directory");
		if (timeout != null && !"".equals(timeout)) { 
			try {
				ds.setCacheTimeout(Long.parseLong(timeout)); 
			} catch (NumberFormatException e) {
				System.err.println("Error loading cache timeout value: " + timeout + " (" + e.getMessage() + ")");
			}
		}
		if (dir != null && !"".equals(dir)) {
			ds.setCacheDirectory(dir);
		}
	}

	
	public static boolean isValidDataSourceSpec(String path) {
		XML xml = IOUtil.loadXML(path);
		return (xml != null
				&& xml.getName().equals("datasourcespec"));		
	}
	
	
	private String getContentOf(XML xml, String xmlpath) {
		XML node = xml.getChild(xmlpath);
		if (node == null) return null;
		return node.getContent().trim();
	}

}
