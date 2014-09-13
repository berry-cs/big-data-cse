package big.data.json;

/* requires  https://github.com/douglascrockford/JSON-java/  */

import java.io.*;
import java.net.*;

import org.apache.commons.io.input.BOMInputStream;
import org.json.*;
import big.data.*;
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
		//System.out.println("inside JSON load");
		BufferedReader reader = null;
		URL url = null;
		try {
		    url = new URL(path);
		} catch(MalformedURLException e) {
			System.out.println("bad url: "+url);
		}
	    if(url.getProtocol().equals("http")) {
	    	try {
		      URLConnection connection = url.openConnection();
	          HttpURLConnection con = (HttpURLConnection) connection; 	
    	   	  String acceptString = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	          //String encodingString = "gzip, deflated";
    	      String useragent = "Mozilla/5.0 (Macintosh; Intel Mac OS x 10.6; rv:14.0) "+
	            "Geck0/20100101 Firefox/14.0.1";
	          con.setRequestProperty("Accept",acceptString);
	          //con.addRequestProperty("Accept-Encoding",encodingString);
    	      con.addRequestProperty("DNT","1");
    	      con.setRequestProperty("User-Agent",useragent);
    	      con.connect();
	          reader = new BufferedReader(new InputStreamReader( new BOMInputStream(con.getInputStream())));
	    	} catch(IOException e) {
	    		System.out.println("IOexception at URLConnection");
	    	}
		} else if (url.getProtocol().equals("file")) {
			try {
			  reader = new BufferedReader(new FileReader( new File(url.getPath())));
			} catch(FileNotFoundException e) {
				System.out.println("File not found: "+url.getPath());
			}
			//System.out.println("PATH: "+url.getPath());
		  }
	
	    try {
	    	//Scanner sc = new Scanner(reader);
	    	//System.out.println(sc.nextLine());
	    	
    	    JSONTokener tokener = new JSONTokener(reader);
    		JSONObject jsonObject = new JSONObject(tokener);
    		
    		String jsonString = org.json.XML.toString(jsonObject);
    		String xmlString = "<wrapper>"+jsonString+"</wrapper>";
    		//System.out.println(xmlString.substring(0, 200));
    		XML xml = XML.parse(xmlString);	
    		//System.out.println(xml.format(2));
    		this.setXML(xml);
    		return super.load(false);
	    }
	    catch (IOException e) {
			System.err.println("IO exception in load: " + e);
		} catch (org.xml.sax.SAXException e) {
			System.err.println("SAX exception: " + e);
		} catch (javax.xml.parsers.ParserConfigurationException e){
			System.err.println("xml parser exception: " + e);
		} catch (JSONException e) {
			System.err.println("JSON exception: " + e);
		}
		return null;				
	}
}
