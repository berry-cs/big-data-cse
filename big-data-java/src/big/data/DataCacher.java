package big.data;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import big.data.util.IOUtil;
import big.data.util.ProcessingDetector;
import big.data.util.XML;




public class DataCacher {
	public static final String DEFAULT_CACHE_DIR = "cache";
	public static final long NEVER_CACHE = 0;
	public static final long NEVER_RELOAD = -1;
	
	public static final long MINIMUM_CACHE_VALUE = 1000;  // disallow setting cacheExpiration to less than this  

	private static final DataCacher dc = makeDefaultDataCacher(); 
	private static boolean CachingEnabled = true;
	
	private String cacheDirectory;
	private long cacheExpiration;  // how many millis from lastRead;  0 = no cache;  -1 = never update if cache
	
	private DataCacher(String cacheDirectory, long cacheExpiration) {
		this.cacheDirectory = cacheDirectory;
		if (cacheExpiration > 0 && cacheExpiration < MINIMUM_CACHE_VALUE) {
			System.err.println("Warning: cannot set cache timeout less than " + MINIMUM_CACHE_VALUE + " msec.");
			cacheExpiration = MINIMUM_CACHE_VALUE;
		}
		this.cacheExpiration = cacheExpiration;
	}
	
	private static DataCacher makeDefaultDataCacher() {
		if (ProcessingDetector.inProcessing()) 
			return new DataCacher(ProcessingDetector.sketchPath(DEFAULT_CACHE_DIR), NEVER_RELOAD);
		else 
			return new DataCacher(DEFAULT_CACHE_DIR, NEVER_RELOAD);	
	}
	
	public static DataCacher defaultCacher() { return dc; }
	
	public DataCacher updateDirectory(String path) {
		File f = new File(path);
		if (!f.exists()) f.mkdirs();
		if (!f.exists() || (!f.isDirectory() && !path.equals("/dev/null")) )
			throw new RuntimeException("Cannot access cache directory: " + path);
		return new DataCacher(path, this.cacheExpiration);
	}
	
	public static void setCaching(boolean val) {
		CachingEnabled = val;
	}
	
	public DataCacher updateTimeout(long value) {
		return new DataCacher(this.cacheDirectory, value);
	}
	
	public long getTimeout() {
		return this.cacheExpiration;
	}

	public String resolvePath(String path) {
		if (!CachingEnabled || !isCacheable(path)) {
			return path;
		} else {
			String cacheIndexName = getCacheIndexFile(path);
			if (cacheIndexName == null) { return path; }
			XML cacheXML = IOUtil.loadXML(cacheIndexName);
			XML myNode = findMyEntry(cacheXML, path);
			String cachepath = (myNode==null ? null : myNode.getChild("cachefile").getContent());
			//System.out.println(path + " > " + cachepath + " / " + myNode + " / " + this.cacheExpiration);
			if (cachepath == null || cacheExpired(myNode) || cacheInvalid(myNode)) {
				try {
					// update cache
					if (myNode == null) {
						String pathEsc = escapeData(path);
						myNode = XML.parse("<cacheentry><sourcepath>" + pathEsc + "</sourcepath><timestamp>0</timestamp><cachefile></cachefile></cacheentry>");
					} else {
						cacheXML.removeChild(myNode);
					}

					String cachedFilePath = readAndCache(path);
					if (cachepath != null) {  // need to remove old cached file
						File oldcache = new File(cachepath);
						oldcache.delete();
					}
					myNode.getChild("cachefile").setContent(cachedFilePath);
					myNode.getChild("timestamp").setContent(System.currentTimeMillis() + "");
					cacheXML.addChild(myNode);
					//System.err.println(myNode);
					//System.err.println(cacheXML);
					IOUtil.saveXML(cacheXML, cacheIndexName);
					return cachedFilePath;
				} catch (IOException e ) {
					//e.printStackTrace();
					return null;
				} catch ( ParserConfigurationException e) {
					System.err.println("ParserConfigurationException: " + e);
					//e.printStackTrace();
					return null;
				} catch ( SAXException e) {
					System.err.println("SAXException: " + e);
					//e.printStackTrace();
					return null;
				} 
			}
			return cachepath;
		}
	}
	
	@SuppressWarnings("deprecation")
	private String escapeData(String path) {
		return StringEscapeUtils.escapeXml(path);
	}

	private String readAndCache(String path) throws IOException {
		byte[] stuff = IOUtil.loadBytes(IOUtil.createInput(path));
		if (stuff == null) {
			throw new IOException("Failed to load: " + path + "\nCHECK NETWORK CONNECTION, if applicable");
		}
		File cacheDir = new File(cacheDirectory + File.separator + path.hashCode());
		if (!cacheDir.exists()) cacheDir.mkdirs();
		File tempFile = File.createTempFile("cache", ".dat", cacheDir);
		IOUtil.saveBytes(IOUtil.createOutput(tempFile), stuff);		
		return tempFile.getCanonicalPath();
	}
	
	private boolean cacheInvalid(XML node) {
		String path = node.getChild("cachefile").getContent();
		return !(new File(path)).exists();
	}
	
	private boolean cacheExpired(XML node) {
		long ts = node.getChild("timestamp").getLongContent();
		long diff = (System.currentTimeMillis() - ts);
		//System.err.println("exp: " + cacheExpiration + "/ " + diff);
		return cacheExpiration >= 0 && diff > cacheExpiration; 
	}
	
	private XML findMyEntry(XML xml, String path) {
		for (XML e : xml.getChildren("cacheentry")) {
			String epath = e.getChild("sourcepath").getContent();
			if (epath.equals(path)) return e;
		}
		return null;
	}
	
	private String getCacheIndexFile(String path) {
		File cacheDir = new File(cacheDirectory);
		if (cacheDir.exists() && !cacheDir.isDirectory()) return null;  // no cache directory
		if (!cacheDir.exists()) { if (!cacheDir.mkdirs()) return null; } // failed creating cache directory

		String cacheFileName;
		try {
			cacheFileName = cacheDir.getCanonicalPath() + File.separator + path.hashCode() + ".xml";

			File cacheFile = new File(cacheFileName);
			if (!cacheFile.exists()) {
				XML cacheXML = new XML("entries");
				cacheXML.write(IOUtil.createWriter(cacheFile));
			} 		

			return cacheFile.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isCacheable(String path) {
		// for now, only things that look like URLs are cacheable
		return path.contains("://") && cacheExpiration != NEVER_CACHE; 
	}
}




/*

<hash....>.xml
<entries>
	<cacheentry>
   		<sourcepath>...</sourcepath>
   		<timestamp>####</timestamp>
   		<cachefile>filename...</cachefile>
 	</cacheentry>
 	...
</entries>





*/