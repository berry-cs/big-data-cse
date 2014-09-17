package test;

import big.data.*;

public class Psalms {

	public static void main(String[] args) {
		String PASSAGE = "Psalms121";
	    DataSource ds = DataSource.connect("http://api.preachingcentral.com/bible.php");
	    ds.set("passage", PASSAGE).load(); 
	    ds.printUsageString();
	    
	    System.out.println(ds.getFieldSpec());
	    System.out.println(ds.fetchString("cache"));
	    
		String[] verses = ds.fetchStringArray("range/item/text");
		System.out.println(verses.length);
		System.out.println(verses[0]);
	}
}
