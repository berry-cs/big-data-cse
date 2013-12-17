package dataxml;

import java.util.ArrayList;

import ext.*;

public interface IDataField {
	public <T> T instantiate(XML xml, ISig s);
	//public <T> ArrayList<T> instantiate(XML xml, ISig lsig, int lim);
}
