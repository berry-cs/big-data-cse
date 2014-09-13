package test;

import big.data.*;

public class MTGCards {

	public static void main(String[] args) {
		DataSource ds = DataSource.connect("http://mtgjson.com/json/LEA.json");
		ds.load();
		ds.printUsageString();
		for (String s : ds.fetchStringArray("cards/name")) {
			System.out.println(s);
		}
	}

}
