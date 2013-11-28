package datasrc;

//import java.util.ArrayList;

public interface IDataField {
	 public int asInt();
	 public long asLong();
	 public String asString();
	 public double asDouble(); 
	 public float asFloat();
	 public boolean asBoolean(); 
	 public char asChar();
	 // TODO: these need work:
/*
	 public <T> ArrayList<T> asArrayList(Class<T> cls); 
	 public <T> T[] asArray(Class<T> cls);
	 public boolean isList();
	 public boolean isPrimitive();
	*/
}
