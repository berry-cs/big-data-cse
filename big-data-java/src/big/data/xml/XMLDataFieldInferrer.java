package big.data.xml;

import java.util.HashMap;

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
			if (!isEmptyXML(t) && !t.getName().equals("#text")) {
				IDataField sf;  // inferred field for t
				boolean isPrimField = t.getChildCount() <= 1;
				   // looks like t subnode has no nested nodes
				
				if (isPrimField) sf = new PrimField(t.getName());
				else sf = inferDataField(t);
				
				cf.addField(t.getName(), sf);
				
				if (xml.getChildren(t.getName()).length > 1) {  // there are several children like <t>...</t>
					cf.addField(t.getName() + "-list", new ListField(t.getName() + "/", t.getName(), sf));
				}

				//if (!isPrimField)
				//	sf.apply(new SubFieldCollector(cf, t.getName() + "/"));

				/*
				if (t.getChildCount() <= 1) {  // looks like t subnode has no nested nodes
					cf.addField(t.getName(), new PrimField(t.getName()));
				} else {  // looks like subnode t has further subnodes
					IDataField sf = inferDataField(t);
					cf.addField(t.getName(), sf);
					//sf.apply(new SubFieldCollector(cf, t.getName() + "/"));
				}
				*/
			}
		}	
		//System.out.println("Got: " + cf);
		return cf;
	}

	// this takes a target compfield and a prefix, and when appled to another
	// field, sf, that is a compfield, it extracts all of the subfields of sf
	// and adds them as top-level fields of the target field with names given
	// by the path to that subfield
	static class SubFieldCollector implements IDFVisitor<Void> {
		CompField targetField;
		String prefix;
		
		SubFieldCollector(CompField tf, String prefix) { 
			this.targetField = tf; 
			this.prefix = prefix;
		}
		
		public Void defaultVisit(IDataField df) { throw new RuntimeException("Unhandled"); }

		public Void visitPrimField(PrimField pf, String basePath, String description) {
			String pathName = prefix + basePath;
			targetField.addField(pathName, new PrimField(pathName));
			return null;
		}

		public Void visitCompField(CompField cf, String basePath,
				String description, HashMap<String, IDataField> fieldMap) {
			for (IDataField df : fieldMap.values())
				df.apply(this);
			return null;
		}

		public Void visitListField(ListField f, String basePath,
				String description, String elemPath, IDataField elemField) {
			String pathName = prefix + basePath;
			elemField.apply(new SubFieldCollector(targetField, pathName));
			return null;
		}
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
