package big.data.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import big.data.DataCacher;


public class DataCacherTest {

	DataCacher dc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dc = DataCacher.defaultCacher();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNotCached() {
		assertEquals(dc.updateTimeout(DataCacher.NEVER_CACHE).resolvePath("hello"), "hello");
		assertEquals(dc.resolvePath("local/path"), "local/path");
		assertEquals(dc.updateTimeout(DataCacher.NEVER_CACHE).resolvePath("http://example.org/data"), "http://example.org/data");
		assertSame(dc.updateDirectory("/dev/null").resolvePath("http://example.org/data"), "http://example.org/data");
	}

	@Test
	public void testCached() {
		dc.updateDirectory("/var/tmp/bigdata-cache").resolvePath("http://example.org");
	//	assertNotSame(dc.updateDirectory("/var/tmp/bigdata-cache").resolvePath("http://example.org"), "http://example.org");
	}
}
