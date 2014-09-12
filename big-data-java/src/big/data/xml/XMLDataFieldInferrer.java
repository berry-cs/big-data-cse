package big.data.xml;

import big.data.field.*;
import big.data.util.*;

public class XMLDataFieldInferrer {

	public static IDataField inferDataField(XML xml) {
		XML firstChild = firstNonemptyChild(xml);
		if (firstChild == null) {
			System.err.println("No data in XML (" + xml.getName() + ")");
			return null;
		} else {
			String firstChildTag = firstChild.getName();
			XML[] children = xml.getChildren(firstChildTag); // note: children.length should be > 0

			if (children.length == 1) {  
				//System.out.println("firstChildTag: " + firstChildTag + " compfield");
				return inferCompField(xml);
			} else {   // looks like a list of nodes
				IDataField cf = inferCompField(children[0]); // use the first child as model
				return new ListField(null, firstChildTag, cf);
			}
		}
	}
	
	static CompField inferCompField(XML xml) {
		CompField cf = new CompField();
		//System.out.println("inferCompField:\n" + xml);
		for (XML t : xml.getChildren()) {  // for each subnode of xml
			if (!isEmptyXML(t) && !t.getName().equals("#text") 
					&& !cf.hasField(t.getName())) {
				IDataField sf;  // inferred field for t
				boolean isPrimField = t.getChildCount() <= 1;
				   // looks like t subnode has no nested nodes
				
				if (isPrimField) sf = new PrimField(t.getName());
				//else sf = inferDataField(t);
				else sf = inferCompField(t);

//				if (t.getName().equals("eventParameters"))
//					System.out.println("Node name: " + t.getName() + " " + isPrimField + " inferred: " + sf);

				if (xml.getChildren(t.getName()).length > 1) {  // there are several children like <t>...</t>
					cf.addField(t.getName(), new ListField(t.getName() + "/", t.getName(), sf));
				} else {
					cf.addField(t.getName(), sf);
				}
			}
		}	
		//System.out.println("Got: " + cf);
		return cf;
	}

	static XML firstNonemptyChild(XML xml) {
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; i++) {
			XML c = children[i];
			if (!isEmptyXML(c)) return c;
		}
		return null;
	}
	
	static boolean isEmptyXML(XML node) {
		return node.getContent()==null || node.getContent().trim().equals("");
	}
}
