package big.data.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DataCacherTest.class, DataSourceLoaderTest.class,
		ISigTests.class, XMLInstantiatorTest.class })
public class AllTests {

}
