package test;


import easy.data.DataSource;

public class ForestCover {

	public static void main(String[] args)  {
		DataSource ds = DataSource.connectCSV("/Users/nhamid/Downloads/train.csv");
		ds.load();
		ds.printUsageString();
		
	}

}
