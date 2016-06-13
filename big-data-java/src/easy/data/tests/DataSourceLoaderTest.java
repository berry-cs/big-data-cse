package easy.data.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import easy.data.*;
import easy.data.xml.XMLDataSource;


public class DataSourceLoaderTest {
	static String spec1path = "src/big/data/tests/dsspec1.xml";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		DataCacher.setCaching(false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsValidDataSourceSpec() {
		assertTrue(DataSourceLoader.isValidDataSourceSpec(spec1path));
		assertFalse(DataSourceLoader.isValidDataSourceSpec("src/big/data/tests/DataSourceLoaderTest.java"));
		assertFalse(DataSourceLoader.isValidDataSourceSpec("src/big/data/tests/nonexistent"));
	}
	
	//@Test
	public void testXMLDataSourceLoad() {
		DataSource ds = new DataSourceLoader(spec1path).getDataSource();
		assertEquals("FAA Airport Status", ds.getName());
		assertEquals("http://services.faa.gov/docs/services/airport/", ds.getInfoURL());
		assertTrue(ds instanceof XMLDataSource);
		
	}
	
	//@Test
	public void testNotReadyToLoad() {
		DataSource ds = new DataSourceLoader(spec1path).getDataSource();
		assertFalse(ds.readyToLoad());
		assertFalse(ds.hasData());
		exception.expect(RuntimeException.class);
		ds.getFullPathURL();
	}
	
	//@Test
	public void testReadyToLoad() {
		DataSource ds = new DataSourceLoader(spec1path).getDataSource().set("airportCode", "JFK").set("extra", "blah");
		assertTrue(ds.readyToLoad());
		assertFalse(ds.hasData());
		assertEquals("http://services.faa.gov/airport/status/JFK?format=application%2Fxml&extra=blah", ds.getFullPathURL());
	}


}
