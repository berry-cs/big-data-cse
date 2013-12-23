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

Use the same static methods as above to load from a URL. Additionally, query parameters may be specified using the `set(key, value)` method, prior to `load()`:

```
DataSource ds = DataSource.connectXML("http://api.worldweatheronline.com/free/v1/weather.ashx");
ds.set("q", "30149").set("key", "...").set("format", "xml").load();
```

Note, that the `connect`, `set`, and `load` methods may be composed in a single expression.

Upon `load`, if no further information is provided, the `big.data` library will attempt to analyze the structure of the data source. In general, this will work if the first row of a CSV/TSV file is a header row, or if an XML file contains a simple list of nodes with identical structure. 


## Data Source Specifications

To provide a more directed (?) way to load data sources (for example, as an instructor might wish to provide to students), a _data source specification_ file may be used with the static `connectUsing(<path>)` method.

A data source specification file is an XML file (with a top-level `<datasourcespec>` element) that specifies the following:

- Path to the data source (inside `<path>`)
- A human-friendly name for the data source (`<name>`)
- The file format (`<format>`): either XML, CSV, or TSV
- A link to web site with further information about the data set (`<infourl>`)
- A descriptive paragraph about the data set (`<description>`)
- Options, inside an `<options>` element, specific to the loader for the particular data format (see below)
- Parameters, inside a `<params>` elements (see below)
- Cache settings, inside a `<cache>` element (`timeout` and `directory`)
- A specification of the data set structure, inside `<dataspec>`

Of the above elements, `path`, `name`, and `format` should be provided, if nothing else.

### Parameters

Parameters are either 
- query parameters (typically appended to a URL, `http://....?key1=val1&key2=val2&...&keyn=valn`)
- path parameters (substitutions for placeholders in the path or url of a data source)






# Fetching Data




# Data Cache






