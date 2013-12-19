package test;

import java.lang.reflect.*;

public class TestHier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		A1 a1 = new A1();
		IA a2 = new A2();
		IB b = new B1();
		b.foo(a1);
		b.foo(a2);

		System.out.println(Integer.TYPE.getName());
		Class c = (new String[] {"a", "b"}).getClass();
		System.out.println(c.isArray());
		System.out.println(c.getComponentType());
		
		C co = new C();
		co.foo(new A1(), new A2());
		co.foo(new A2(), new A1());
	}

}


class C {
	public void foo(IA x, IA y) {
		System.out.println("foo IA");
	}
	
	public void foo(A2 x, IA y) {
		System.out.println("foo A2");
	}
}




/* visitor design pattern... */
interface IAVisitor {
	void defaultVisit(IA def);
	void visit(A1 o);
	void visit(A2 o);
}

abstract class IAMatcher implements IAVisitor {
	public void defaultVisit(IA def) {
		System.out.println("generic");
	}

	public void visit(A1 o) {
		defaultVisit(o);
	}

	public void visit(A2 o) {
		defaultVisit(o);
	}
	
}

interface IA {
	void apply(IAVisitor v);
}

class A1 implements IA {

	public void apply(IAVisitor v) {
		v.visit(this);
	}
	
}

class A2 implements IA {
	public void apply(IAVisitor v) {
		v.visit(this);
	}	
}


interface IB {
	void foo(IA o);
}

class B1 implements IB {
	public void foo(IA o) {
		o.apply(new IAMatcher() {
			public void visit(A1 o) {
				 System.out.println("handling A1");
			}		
		 });
	}
}