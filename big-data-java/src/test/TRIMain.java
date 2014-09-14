package test;

// http://www2.epa.gov/toxics-release-inventory-tri-program/tri-basic-data-files-calendar-years-1987-2012

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import big.data.*;

class Facility {
	String name;
	String zip;
	boolean fed;  // federal facility?
	String type;
	ArrayList<String> chems;
	
	public Facility(String name, String zip, boolean fed, String type) {
		super();
		this.name = name;
		this.zip = zip;
		this.fed = fed;
		this.type = type;
		this.chems = new ArrayList<String>();
	}
	
	public void addChem(String chem) {
		chems.add(chem);
	}
	
	public String toString() {
		return String.format("%s (%s%s) Type: %s", name, zip, fed ? ", federal facility" : "", type);
	}
}

class TRIEntry {
	String name;
	String zip;
	boolean fed;
	int naics;
	String chem;
	
	public TRIEntry(String name, String zip, boolean fed, int naics, String chem) {
		super();
		this.name = name;
		this.zip = zip;
		this.fed = fed;
		this.naics = naics;
		this.chem = chem;
	}
	
}




public class TRIMain {

	public static void main(String[] args) {
		NAICS codeMap = new NAICS();
		
		DataSource ds = DataSource.connectCSV("http://ofmpub.epa.gov/enviro/efservice/MV_TRI_BASIC_DOWNLOAD/st/=/GA/year/=/2012/EXCEL");
		ds.load();
		//ds.printUsageString();
		List<TRIEntry> all = ds.fetchList("test.TRIEntry", "row/FACILITY_NAME", "row/ZIP", "row/FEDERAL_FACILITY",
															"row/PRIMARY_NAICS", "row/CHEMICAL");
		System.out.println("Total entries: " + all.size());
		
		HashMap<String,Facility> facs = new HashMap<String,Facility>();
		
		for (TRIEntry e : all) {
			String ntype = codeMap.lookup(e.naics);
			Facility f;
			if (facs.containsKey(e.name)) f = facs.get(e.name);
			else {
				f = new Facility(e.name, e.zip, e.fed, ntype);
				facs.put(e.name, f);
			}
			f.addChem(e.chem);
		}
		
		System.out.println("Number of Facilities: " + facs.size());
		if (facs.containsKey("ADVANCED STEEL TECHNOLOGY")) {
			Facility f = facs.get("ADVANCED STEEL TECHNOLOGY");
			System.out.println(f);
			for (String chem : f.chems) {
				System.out.println("  " + chem);
			}
		}

		System.out.println(DataCacher.defaultCacher().resolvePath("http://ofmpub.epa.gov/enviro/efservice/MV_TRI_BASIC_DOWNLOAD/st/=/GA/year/=/2012/EXCEL"));
	}
	
	
	

}


class NAICS {
	private HashMap<Integer, String> map;
	
	public NAICS() {
		map = new HashMap<Integer, String>();
		loadCodes(2012);
		loadCodes(2007);
	}
	
	public boolean has(int code) {
		return map.containsKey(code);
	}
	
	public String lookup(int code) {
		return map.get(code);
	}
	
	private void loadCodes(int year) {
		DataSource nds = DataSource.connectJSON("http://naics.codeforamerica.org/v0/q?year=" + year);		
		if (nds.load() == null) {
			System.out.println("Could not load NAICS data for year " + year);
			return;
		}
		
		//nds.printUsageString();
		int[] codes = nds.fetchIntArray("data/code");
		String[] titles = nds.fetchStringArray("data/title");
		if (codes.length != titles.length) System.out.println("something fishy...");
		for (int i = 0; i < codes.length; i++) {
			map.put(codes[i], titles[i]);
		}
	}
}
