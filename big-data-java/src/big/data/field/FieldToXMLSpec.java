package big.data.field;

import java.util.HashMap;
import big.data.util.XML;

public class FieldToXMLSpec implements IDFVisitor<XML> {

	public FieldToXMLSpec() {
	}

	public XML defaultVisit(IDataField df) {
		return null;
	}

	public XML visitPrimField(PrimField f, String basePath, String description) {
		XML node = new XML("primfield");
		node.addChild("basepath").setContent(basePath);
		node.addChild("description").setContent(description);
		return node;
	}

	public XML visitCompField(CompField f, String basePath, String description,
			HashMap<String, IDataField> fieldMap) {
		XML node = new XML("compfield");
		node.addChild("basepath").setContent(basePath);
		node.addChild("description").setContent(description);
		XML fieldsnode = node.addChild("fields");
		// primitive fields first...
		for (String k : fieldMap.keySet()) {
			IDataField df = fieldMap.get(k);
			if (df instanceof PrimField) {
				XML fnode = fieldsnode.addChild("field");
				fnode.addChild("name").setContent(k);
				fnode.addChild(df.apply(this));
			}
		}
		// then comp fields...
		for (String k : fieldMap.keySet()) {
			IDataField df = fieldMap.get(k);
			if (df instanceof CompField) {
				XML fnode = fieldsnode.addChild("field");
				fnode.addChild("name").setContent(k);
				fnode.addChild(df.apply(this));
			}
		}
		// then list fields...
		for (String k : fieldMap.keySet()) {
			IDataField df = fieldMap.get(k);
			if (df instanceof ListField) {
				XML fnode = fieldsnode.addChild("field");
				fnode.addChild("name").setContent(k);
				fnode.addChild(df.apply(this));
			}
		}
		return node;
	}

	public XML visitListField(ListField f, String basePath, String description,
			String elemPath, IDataField elemField) {
		XML node = new XML("listfield");
		node.addChild("basepath").setContent(basePath);
		node.addChild("description").setContent(description);
		node.addChild("elempath").setContent(elemPath);
		node.addChild("elemfield").addChild(elemField.apply(this));
		return node;
	}

}
