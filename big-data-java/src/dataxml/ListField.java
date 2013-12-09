package dataxml;

import ext.XML;

public class ListField implements IDataField {
	private String basePath;
	private IDataField baseField;
	
	public ListField(String basepath, IDataField basefld) {
		this.basePath = basepath;
		this.baseField = basefld;
	}

	@Override
	public <T> T instantiate(XML xml, ISig s) {
		// TODO Auto-generated method stub
		return null;
	}
}