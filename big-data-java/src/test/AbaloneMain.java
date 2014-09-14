package test;

import java.util.List;
import big.data.*;

// character data

class Abalone {
	char sex;  // M F I(nfant)
	double len;  // in mm
	double diam;
	double hgt;
	double wholeWgt;  // grams
	double shellWgt;
	int rings;   // +1.5 = age in years
	
	public Abalone(char sex, double len, double diam, double hgt,
			double wholeWgt, double shellWgt, int rings) {
		super();
		this.sex = sex;
		this.len = len;
		this.diam = diam;
		this.hgt = hgt;
		this.wholeWgt = wholeWgt;
		this.shellWgt = shellWgt;
		this.rings = rings;
	}

	public String toString() {
		return "Abalone [sex=" + sex + ", len=" + len + ", diam=" + diam
				+ ", hgt=" + hgt + ", wholeWgt=" + wholeWgt + ", shellWgt="
				+ shellWgt + ", rings=" + rings + "]";
	}
	
	
}

public class AbaloneMain {

	public static void main(String[] args) {
		DataSource ds = DataSource.connectCSV("http://archive.ics.uci.edu/ml/machine-learning-databases/abalone/abalone.data");
		ds.setOption("header", "sex, length, diameter, height, whole weight, shucked weight, viscera weight, shell weight, rings");
		ds.load();
		ds.printUsageString();
		List<Abalone> things = ds.fetchList("test.Abalone", "row/sex", "row/length", "row/diameter", "row/height", "row/whole_weight", 
				"row/shell_weight", "row/rings");
		
		System.out.println(things.size());
		for (int i = 0; i < 10; i++) {
			System.out.println(things.get(i));
		}
		System.out.println("---");
		for (Abalone a : things) {
			if (a.rings == 19) System.out.println(a);
		}
	}

}


