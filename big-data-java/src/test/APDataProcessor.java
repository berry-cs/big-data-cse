package test;

import big.data.util.XML;
import big.data.xml.IPostProcessor;

public class APDataProcessor implements IPostProcessor {

	public APDataProcessor() {
	}

	public XML process(XML xml) {
		for (XML rownode : xml.getChildren("row")) {
			String seriesID = rownode.getChild("series_id").getContent();
			String areaCode = seriesID.substring(3, 7);
			String itemCode = seriesID.substring(7);
			rownode.addChild("area_code").setContent(areaCode);
			rownode.addChild("item_code").setContent(itemCode);
		}

		return xml;
	}

}
