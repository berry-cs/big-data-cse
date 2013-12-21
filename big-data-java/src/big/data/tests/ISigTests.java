package big.data.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import big.data.sig.*;

import java.util.ArrayList;

import static big.data.sig.PrimSig.*;

public class ISigTests {

	ArrayList<String> al1;
	ArrayList<Integer> al2;
	//ArrayList<Thread> al3; cannot get reflection info about arraylist component type
	String[] as1;
	int[] as2;
	Thread[] as3;
	ListSig ls1;
	ListSig ls2;
	CompSig<C1> cs1;  // C1: int * string
	CompSig<C1> cs2;  // C1: int * int
	CompSig<C1> cs3;  // C1: int
	CompSig<C2> cs4;  // C2: int * int 
	static ISigVisitor<Integer> intVisitor = new ISigVisitor<Integer>() {
		public Integer defaultVisit(ISig s) { return 0; }
		public <C> Integer visit(PrimSig s) { return 1; }
		public Integer visit(CompSig<?> s) { return 2; }
		public Integer visit(ListSig s) { return 3; } 
	};

	@SuppressWarnings("unused")
	private static class C1 {
		C1(int x, String y) {}
		C1(int x, int y) {}
		C1(Double s) {}		
	}
	
	private static class C2 {}
		
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
		al1 = new ArrayList<String>();
		al2 = new ArrayList<Integer>();
		//al3 = new ArrayList<Thread>();
		as1 = new String[] {};
		as2 = new int[] {};
		as3 = new Thread[] {};
		ls1 = new ListSig(STRING_SIG);
		ls2 = new ListSig(INT_SIG);
		cs1 = new CompSig<C1>(C1.class);
		cs2 = new CompSig<C1>(C1.class);
		cs3 = new CompSig<C1>(C1.class);
		cs4 = new CompSig<C2>(C2.class);
		cs1.addField(INT_SIG, "xf");
		cs1.addField(STRING_SIG, "yf");
		cs2.addField(INT_SIG, "xf");
		cs2.addField(INT_SIG, "yf");
		cs3.addField(INT_SIG, "xf");
		cs4.addField(INT_SIG, "xf");
		cs4.addField(INT_SIG, "yf");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApply() {
		assertSame(BOOLEAN_SIG.apply(intVisitor), 1);
		assertSame(cs1.apply(intVisitor), 2);
		assertSame(ls1.apply(intVisitor), 3);
	}

	@Test
	public void testUnifyWithPrimSig() throws SignatureUnificationException {
		assertEquals(BOOLEAN_SIG.unifyWith(Boolean.class), BOOLEAN_SIG);
		assertEquals(BOOLEAN_SIG.unifyWith(boolean.class), BOOLEAN_SIG);
		assertEquals(BYTE_SIG.unifyWith(Byte.class), BYTE_SIG);
		assertEquals(BYTE_SIG.unifyWith(byte.class), BYTE_SIG);
		assertEquals(CHAR_SIG.unifyWith(Character.class), CHAR_SIG);
		assertEquals(CHAR_SIG.unifyWith(char.class), CHAR_SIG);
		assertEquals(DOUBLE_SIG.unifyWith(double.class), DOUBLE_SIG);
		assertEquals(STRING_SIG.unifyWith(String.class), STRING_SIG);

		assertEquals(WILDCARD_SIG.unifyWith(byte.class), BYTE_SIG);
		assertEquals(WILDCARD_SIG.unifyWith(String.class), STRING_SIG);
		assertEquals(WILDCARD_SIG.unifyWith(Boolean.class), BOOLEAN_SIG);
		assertEquals(WILDCARD_SIG.unifyWith(Double.class), DOUBLE_SIG);
		assertEquals(WILDCARD_SIG.unifyWith(int.class), INT_SIG);

		assertEquals(BOOLEAN_SIG.unifyWith(String.class), STRING_SIG);
		assertEquals(BYTE_SIG.unifyWith(String.class), STRING_SIG);
		assertEquals(INT_SIG.unifyWith(String.class), STRING_SIG);
		assertEquals(DOUBLE_SIG.unifyWith(String.class), STRING_SIG);
	}

	@Test
	public void testUnifyFailBoolean() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		exception.expectMessage("<boolean> cannot be unified with java.lang.Double");
		BOOLEAN_SIG.unifyWith(Double.class);
	}

	@Test
	public void testUnifyFailByte() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		BYTE_SIG.unifyWith(int.class);
	}

	@Test
	public void testUnifyFailNonprim() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		exception.expectMessage("java.lang.Thread is not a primitive class");
		WILDCARD_SIG.unifyWith(Thread.class);
	}

	@Test
	public void testUnifyWithArrayList() throws SignatureUnificationException {
		assertEquals(ls1.unifyWith(al1.getClass()), ls1);
		assertEquals(ls1.unifyWith(as1.getClass()), ls1);
		assertEquals(ls2.unifyWith(al2.getClass()), ls2);
		assertEquals(ls2.unifyWith(as2.getClass()), ls2);
	}

	@Test
	public void testUnifyWithArrayFail1() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		ls1.unifyWith(as2.getClass());
	}

	@Test
	public void testUnifyWithArrayFail2() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		ls1.unifyWith(as3.getClass());
	}

	@Test
	public void testUnifyWithComp() throws SignatureUnificationException {
		assertEquals(cs1.unifyWith(C1.class), cs1);
		assertEquals(cs2.unifyWith(C1.class), cs2);
	}
	
	@Test
	public void testUnifyWithCompFail1() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		cs3.unifyWith(C1.class);
	}

	@Test
	public void testUnifyWithCompFail2() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		cs4.unifyWith(C1.class);
	}

	@Test
	public void testUnifyWithCompFail3() throws SignatureUnificationException {
		exception.expect(SignatureUnificationException.class);
		cs2.unifyWith(C2.class);
	}
}




