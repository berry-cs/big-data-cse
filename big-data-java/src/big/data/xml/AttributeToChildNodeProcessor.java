package big.data.xml;

import big.data.util.XML;

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
			for (String attrname : xml.listAttributes()) {
				String attrval = xml.getAttribute(attrname);
				XML subnode = xml.addChild(attrname);
				subnode.setContent(attrval);
			}
		}
		return xml;
	}

}
