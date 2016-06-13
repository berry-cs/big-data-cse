package test;
import easy.data.*;

public class EbayFinder {

	public static void main(String[] args) {
		
		DataSource ds = DataSource.connect( "http://svcs.ebay.com/services/search/FindingService/v1");
		ds.set("OPERATION-NAME", "findItemsByKeywords").set("SERVICE-VERSION", "1.0.0");
		ds.set("SECURITY-APPNAME", "StephenJ-4d75-4f49-b701-c21f0c089826").set("GLOBAL-ID", "EBAY-US");
//		ds.set("paginationOutput.totalEntries", "1000");
		ds.set("format", "xml").set("keywords", "hp 1606");
		//System.out.println(ds.getFullPathURL());
		ds.load();
		ds.printUsageString();

		//String[] items = ds.fetchStringArray("searchResult/item/title");
		//for (String i : items) System.out.println(i);

		Item[] items = ds.fetchArray("test.Item", 
				"searchResult/item/title",
				"searchResult/item/primaryCategory/categoryName",
				"searchResult/item/sellingStatus/bidCount",
				"searchResult/item/sellingStatus/currentPrice/value",
				"searchResult/item/listingInfo/listingType");
		System.out.println(items.length);
		for (Item i : items) System.out.println(i);


	}

}


class Item {
	String name;
	String category;
	int bids;
	double price;
	String listingType;


	public Item(String name, String category, int bids, double price,
			String listingType) {
		super();
		this.name = name;
		this.category = category;
		this.bids = bids;
		this.price = price;
		this.listingType = listingType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Item [name=" + name + ", category=" + category + ", bids="
				+ bids + ", price=" + price + ", listingType=" + listingType
				+ "]";
	}



}