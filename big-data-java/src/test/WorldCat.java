package test;

// Data Source: http://xisbn.worldcat.org

import big.data.*;
/*
import big.data.field.FieldToXMLSpec;
import big.data.field.IDataField;
import big.data.util.XML;
*/

import java.util.Scanner;

public class WorldCat {
  
  public static void main(String[] args) {
	 
	  Scanner in = new Scanner(System.in);
	  String isbn = "9780073376905"; // in.nextLine();
	  
     DataSource ds = DataSource.connect("http://xisbn.worldcat.org/webservices/xid/isbn/" + isbn);
     ds.set("method", "getMetadata").set("fl", "*").set("format", "xml").load();

     ds.printUsageString();
  
     String title = ds.fetchString("isbn/title");
     String author = ds.fetchString("isbn/author");
     int year = ds.fetchInt("isbn/year");
     
     System.out.println(isbn + ": " + title + ", " + author + ", " + year + ".");
     in.close();
     
     /*
     IDataField fs = ds.getFieldSpec();
     XML fsxml = fs.apply(new FieldToXMLSpec());
     System.out.println(fsxml.format(2));
     */
  }
  
}

