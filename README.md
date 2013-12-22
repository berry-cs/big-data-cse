big-data-cse
============

This library provides a code framework to facilitate the incorporation of large, online data sets into introductory programming courses. The goal of the library is to relieve students from low-level issues of reading and parsing raw data from web-based data sources, while interfacing with data structures and representations defined by users themselves. In addition, the library requires minimal syntactic overhead to use its functionality.

# Supported Data Sources

The `big.data` library currently supports loading data files from either the local filesystem or a web URL. The following formats are supported:

- XML files
- CSV (comma-separated) files (with or without first row header)
- TSV (tab-separated)

A file that is compressed as `zip` or `gz` may also be loaded directly.

# Specifying and Loading a Data Source

## Quick-start

To get started,

```
import big.data.*;
````

Then declare a variable of type `DataSource` and use one of the static methods `connectXML`, `connectCSV`, or `connectTSV` as appropriate to specify a path string to the data file:

```
DataSource ds = DataSource.connectXML("datafile.xml");
DataSource ds = DataSource.connectCSV("datafile.csv");
DataSource ds = DataSource.connectTSV("datafile.dat");
```

(There is also a simple `connect()` method that will attempt to make the correct choice based on file extensions or other heuristics.)

If the data source is simple enough that no other options or processing is required, load the data using the `load()` method on the `DataSource` object:

```
ds.load();
```

At this point, `ds.printUsageString()` will print (to standard out) a summary of the data structure available from the data source. If everything goes well, you can now proceed to "Fetching Data."

## Loading from URLs





## Data Source Specifications




# Fetching Data




# Data Cache






