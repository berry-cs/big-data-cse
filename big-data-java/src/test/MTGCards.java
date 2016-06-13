package test;

import easy.data.*;

public class MTGCards {

	public static void main(String[] args) {
		DataSource ds = DataSource.connect("http://mtgjson.com/json/LEA.json");
		ds.load();
		ds.printUsageString();
		System.out.println(ds.fetchStringArray("cards/name").length);
		for (String s : ds.fetchStringArray("cards/name")) {
			System.out.println(s);
		}
	}

}
