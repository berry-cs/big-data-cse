package big.data.xml;

import big.data.util.XML;

/**
 * Performs some processing on XML data after it has been
 * loaded by an XMLDataSource
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public interface IPostProcessor {
	public XML process(XML xml);
}
