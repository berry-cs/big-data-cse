package test;


// http://www.kaggle.com/c/forest-cover-type-prediction/data
// a little hard to work with becasue of the binary columns... 
// TODO: any way to improve binding this sort of thing to java code that might,
//    for example, simply keep a list of soil types?

import big.data.DataSource;

public class ForestCover {

	public static void main(String[] args)  {
		DataSource ds = DataSource.connectCSV("/Users/nhamid/Downloads/train.csv");
		ds.load();
		ds.printUsageString();
		
	}

}
