package datasrc;


import ext.*;

public class XMLDataField implements IDataField {

	private XML xml;   // parsed xml source
	private String name;
	private String path;
	
	public XMLDataField(XML xml, String name, String path) {
		this.xml = xml;
		this.name = name;
		this.path = path;
	}

	@Override
	public int asInt() {
		return xml.getChild(this.path).getIntContent();
	}

	@Override
	public long asLong() {
		return xml.getChild(this.path).getLongContent();
	}

	@Override
	public String asString() {
		return xml.getChild(this.path).getContent();
	}

	@Override
	public double asDouble() {
		return xml.getChild(this.path).getDoubleContent();
	}

	@Override
	public float asFloat() {
		return xml.getChild(this.path).getFloatContent();
	}

	@Override
	public boolean asBoolean() {
		String s = this.asString();
		try { return IOUtil.parseBoolean(Integer.parseInt(s)); } catch (NumberFormatException e) { }
		return IOUtil.parseBoolean(s);
	}

	@Override
	public char asChar() {
		return xml.getChild(this.path).getContent().charAt(0);
	}
/*
	@Override
	public <T> ArrayList<T> asArrayList(Class<T> cls) {
		return new ArrayList<T>(Arrays.asList(this.asArray(cls)));
	}

	@Override
	public <T> T[] asArray(Class<T> cls) {
		XML[] nodes = xml.getChildren(this.path);
		T[] vals = (T[])Array.newInstance(cls, nodes.length);
		for (int i = 0; i < nodes.length; i++) {
			vals[i] = nodes[i].getContent();
		}
	}

	@Override
	public boolean isList() {
		 xml.getChildren(this.path)
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}
*/
}
