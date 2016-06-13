package easy.data.xml;

import easy.data.util.XML;

/**
 * This post-processor goes over the XML and moves all attributes in tags
 * into child nodes directly under the tag.
 */

public class AttributeToChildNodeProcessor implements IPostProcessor {

	@Override
	public XML process(XML xml) {
		for (XML child : xml.getChildren()) {
			this.process(child);
		}
		if (xml.getAttributeCount() > 0) {
			if (xml.getChildCount() == 1 
				&& xml.getChild(0).getName().equals("#text")) {
				String content = xml.getContent();
				xml.removeChild(xml.getChild(0));
				XML valueNode = xml.addChild("value");
				valueNode.setContent(content);
			}
			for (String attrname : xml.listAttributes()) {
				String attrval = xml.getAttribute(attrname);
				XML subnode = xml.addChild(attrname);
				subnode.setContent(attrval);
			}
		}
		return xml;
	}

}
