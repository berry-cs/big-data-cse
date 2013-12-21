package data.xml;

import java.util.ArrayList;

import big.data.util.*;


public interface IDataField {
	public <T> T instantiate(XML xml, ISig s);
	public <T> T apply(IDFVisitor<T> fv);
	public String toString(int indent);
	public String toString(int indent, boolean indentFirst);
}
