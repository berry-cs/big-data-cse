package big.data.json;

/* requires  https://github.com/douglascrockford/JSON-java/  */

import java.io.*;
import org.json.*;
import big.data.*;
import big.data.util.IOUtil;
import big.data.util.XML;
import big.data.xml.XMLDataSource;

public class JSONtoXMLDataSource extends XMLDataSource {

	
	
	public JSONtoXMLDataSource(String name, String path)
	{
		super(name,path);
//		System.out.println("my path is: "+ this.path);
	}
	
	public DataSource load()
	{
	    try {
	    	String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
			if (resolvedPath == null) return null; 

			BufferedReader reader = IOUtil.createReader(resolvedPath);
	    	
			String jsonString;
			String xmlString;
    	    JSONTokener tokener = new JSONTokener(reader);
    	    try {
    	    	JSONObject jsonObject = new JSONObject(tokener);
    	    	jsonString = org.json.XML.toString(jsonObject);
    	    	xmlString = "<wrapper>"+jsonString+"</wrapper>";
    	    } catch (JSONException e) {
    	    	// might be because a JSON array instead of an object...
    	    	tokener.back();
    	    	JSONArray jsonArray = new JSONArray(tokener);
    	    	
    	    	jsonString = "";
    	    	for (int i = 0; i < jsonArray.length(); i++) {
    	    		JSONObject jo = jsonArray.getJSONObject(i);
    	    		jsonString += "<data>" + org.json.XML.toString(jo) + "</data>";
    	    	}
    	    	
    	    	xmlString = "<wrapper>"+jsonString+"</wrapper>";
    	    }
	    	//System.out.println(xmlString.substring(0, 200));
	    	XML xml = XML.parse(xmlString);	
	    	//System.out.println(xml.format(2));
	    	this.setXML(xml);
	    	return super.load(false);

	    }
	    catch (IOException e) {
			e.printStackTrace();
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
		} catch (javax.xml.parsers.ParserConfigurationException e){
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
