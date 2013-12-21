package big.data.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import big.data.field.CompField;
import big.data.field.IDataField;
import big.data.field.ListField;
import big.data.field.PrimField;
import big.data.sig.*;
import big.data.util.*;
import big.data.xml.*;

import java.util.*;



public class XMLInstantiatorTest {

	XML xml1;
	PrimField pf1;
	PrimField pf2;	
	PrimField pf3;
	PrimField pf4;
	CompField cf1;
	CompField cf2;
	ListField lf1;
	ListField lf2;
	ListField lf3;
	
	CompSig<Pair> cspair;

	static class Pair { 
		String n; 
		String v;
		public Pair(String n, String v) {
			this.n = n;
			this.v = v;
		}
		public boolean equals(Object other) {
			if (!(other instanceof Pair)) return false;
			Pair that = (Pair) other;
			return (that.n==this.n || that.n.equals(this.n)) && 
					(that.v==this.v || that.v.equals(this.v));
		}
		public String toString() { return n + ": " + v; }
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		xml1 = IOUtil.loadXML("src/big/data/tests/dsspec1.xml");
		pf1 = new PrimField("name", "option name");
		pf2 = new PrimField("value", "option setting");
		pf3 = new PrimField(null);
		pf4 = new PrimField("dataspec/compfield/fields/field/name");
		cf1 = new CompField(null, "an option setting pair");
		cf2 = new CompField("options/option", "an option setting pair");
		lf1 = new ListField("options", "option", cf1, "list of option settings");
		lf2 = new ListField("blah", "blup", pf3, "messed up");
		lf3 = new ListField(null, "dataspec/compfield/fields/field/name", pf3, "top level field names");
		
		cf1.addField("value", pf2);
		cf1.addField("name", pf1);
		cf2.addField("value", pf2);
		cf2.addField("name", pf1);
		
		cspair = new CompSig<Pair>(Pair.class);
		cspair.addField(PrimSig.STRING_SIG, "name");
		cspair.addField(PrimSig.STRING_SIG, "value");
	}

	@After
	public void tearDown() throws Exception {
	}

	private static <T> T instantiate(IDataField f, XML xml, ISig s) {
		return f.apply(new XMLInstantiator<T>(xml, s));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public final void test() {
		assertNull(instantiate(pf2, xml1, PrimSig.WILDCARD_SIG));
		assertEquals(instantiate(pf4, xml1, PrimSig.WILDCARD_SIG), "City");
		assertArrayEquals(new String[] { "City" },
						  ((ArrayList)instantiate(pf4, xml1, new ListSig(PrimSig.WILDCARD_SIG))).toArray());

		ArrayList<String> lst = instantiate(lf3, xml1, new ListSig(PrimSig.WILDCARD_SIG));
		assertArrayEquals(new String[] { "City", "State", "Name", "Status Comment", "Status" },
						  lst.toArray());
		
		assertEquals(new Pair("xmlpreprocclass", "WeatherConsolidate"), 
					 instantiate(cf2, xml1, cspair));
		assertEquals(new Pair("FAA Airport Status", null), 
					 instantiate(cf1, xml1, cspair));
		assertNull(instantiate(cf1, xml1.getChild("dataspec"), cspair));   // no subfields successfully parsed
		assertNull(instantiate(cf1, xml1, new CompSig<Thread>(Thread.class))); 
		
		assertNull(instantiate(lf2, xml1, new ListSig(PrimSig.WILDCARD_SIG)));
		assertArrayEquals(new Pair[] { new Pair("xmlpreprocclass", "WeatherConsolidate"),
										new Pair("question", "life"),
										new Pair("answer", "42")},
						  ((ArrayList)instantiate(lf1, xml1, new ListSig(cspair))).toArray());
		
		assertArrayEquals(new Object[] {}, 
						((ArrayList)instantiate(new ListField("options", "top", cf1), xml1, new ListSig(cspair))).toArray());
	}

}
