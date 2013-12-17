package dataxml;

import java.util.ArrayList;

import ext.XML;

public class ListField implements IDataField {
	private String basePath;
	private IDataField baseField;
	
	public ListField(String basepath, IDataField basefld) {
		this.basePath = basepath;
		this.baseField = basefld;
	}
	
	public IDataField baseField() {
		return baseField;
	}

	@Override
	public <T> T instantiate(XML xml, ISig s) {
		final XML basexml = findMyNode(xml);
		//System.err.println(" ListField.instantiate(" + (xml==null ? "null" : xml.getName()) + ", " + s + ")   basexml: " + ((basexml==null)?"null":basexml.getName()) + "  basepath: " + basePath);
		
		XML[] childs = basexml.getChildren();
		ArrayList<Object> lst = new ArrayList<Object>();
		for (XML c : childs) {
			XML wrap = new XML("wrap");
			wrap.addChild(c);
			Object o = baseField.instantiate(wrap, ((ListSig)s).getElemType());
			lst.add(o);
		}
		
		return (T)lst;
	}
	
	
	protected XML findMyNode(XML xml) {
		XML node = xml;
		if (basePath != null && !basePath.equals("") /*&& !node.getName().equals(basePath)*/) {
			node = node.getChild(basePath);
		}
		return node;
	}

}