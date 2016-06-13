package test;

import java.lang.reflect.Constructor;

import easy.data.*;

/**
 * Test JSON using Kiva website
 * @author sbenzel
 *
 */
public class JSON_KivaTest {

	private static final String path = //"file:///Users/sbenzel/Desktop/loans.json";
			"http://api.kivaws.org/v1/loans/newest.json";
	
	public static void main(String[] args) throws ClassNotFoundException
	{
		Loan loan;
		
		Class<?> testClass = Class.forName("test.Loan");
		Constructor<?>[] constr = testClass.getConstructors();
		System.out.println("number of constructors: "+constr.length);
		DataSource kivaData = DataSource.connectJSON(path);
		System.out.println("past declaration");
		kivaData.load();
		System.out.println("past load, size = "+kivaData.size());
		kivaData.printUsageString();
		
		System.out.println(kivaData.fetchStringArray("loans/name").length);
		
		DataSourceIterator iterator = kivaData.iterator();
		System.out.println("iterator declared");
		while(iterator.hasData()) {
			
			loan = iterator.fetch("test.Loan","loans/id","loans/loan_amount", "loans/location/country", "loans/name","loans/use");
			System.out.println(loan+"\n-------------------------");
			iterator.loadNext();
		}
			
		

	}

}

class Loan
{
	private int id;
	private String name;
	private String country;
	private int loan_amount;
	private String use;
	
	public Loan(int id, int loan_amount, String country, String name, String use)
	{
		this.id = id;
		this.name = name;
		this.country = country;
		this.loan_amount = loan_amount;
		this.use = use;
	}
	
	public String toString()
	{
		String output = id+" "+name+"\n";
		output += country+"\n";
		output += loan_amount+"\n";
		output += use;
		return output;
	}
	
}
