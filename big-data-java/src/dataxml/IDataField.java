package dataxml;

import ext.*;

public interface IDataField {
	public <T> T instantiate(XML xml, ISig s);
}
