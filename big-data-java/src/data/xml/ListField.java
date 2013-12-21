package data.xml;

import java.util.ArrayList;
import java.util.Arrays;

import big.data.util.IOUtil;
import big.data.util.XML;



public class ListField implements IDataField {
	private String basePath;
	private String elemPath;
	private IDataField elemField;
	
	public ListField(String basepath, String elempath, IDataField basefld) {
		this.basePath = basepath;
		this.elemPath = elempath;
		this.elemField = basefld;
	}
	
	public IDataField elemField() {
		return elemField;
	}
	
	public String elemPath() {
		return elemPath;
	}

	public <T> T instantiate(XML xml, ISig s) {
		final XML basexml = findMyNode(xml);
		//System.err.println(" ListField.instantiate(" + (xml==null ? "null" : xml.getName()) + ", " + s + ")   basexml: " + ((basexml==null)?"null":basexml.getName()) + "  elempath: " + elemPath);
		
		return s.apply(new SigMatcher<T>() {
			public T visit(ListSig s) {
				XML[] childs = basexml.getChildren(elemPath);
				ArrayList<Object> lst = new ArrayList<Object>();
				for (XML c : childs) {
					Object o = elemField.instantiate(c, s.getElemType());
					lst.add(o);
				}
				return (T)lst;
			}
			
			public T visit(CompSig s) {
				XML child = basexml.getChild(elemPath);
				return elemField.instantiate(child, s);
			}
			
		});
	}
	
	
	protected XML findMyNode(XML xml) {
		XML node = xml;
		if (basePath != null && !basePath.equals("")) {
			node = node.getChild(basePath);
		}
		return node;
	}
	
	
	public String toString() {
		return "[" + elemField + "]";
	}
	
	public String toString(int indent) {
		return this.toString(indent, true);
	}
	
	public String toString(int indent, boolean indentFirst) {
		String s = (indentFirst ? IOUtil.repeat(' ', indent) : "") + "List of:\n";
		s += elemField.toString(indent+2);
		return s;
	}

	public <T> T apply(IDFVisitor<T> fv) {
		return fv.visit(this);
	}
}