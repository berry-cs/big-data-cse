package easy.data.field;

import easy.data.util.XML;

public class ADataField {
	protected String basePath;
	protected String description;

	public ADataField() {
		this(null, null);
	}
	
	public ADataField(String basePath) {
		this(basePath, null);
	}
	
	public ADataField(String basePath, String description) {
		this.basePath = basePath;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
	
	public String getBasePath() {
		return this.basePath;
	}

	public XML findMyNode(XML xml) {
		XML node = xml;
		if (basePath != null && !basePath.equals("")) {
			node = node.getChild(basePath);
		}
		return node;
	}
}