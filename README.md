big-data-cse
============

This library provides a code framework to facilitate the incorporation of large, online data sets into introductory programming courses. The goal of the library is to relieve students from low-level issues of reading and parsing raw data from web-based data sources, while interfacing with data structures and representations defined by students themselves. In addition, the library requires minimal syntactic overhead to use its functionality.

# Supported Data Sources

The `big.data` library currently supports loading data files from either the local filesystem or a web URL. The following formats are supported:

- XML files
- CSV (comma-separated) files (with or without first row header)
- TSV (tab-separated)

Limited support for files that are compressed as `zip` or `gz` is available.

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

To provide a more directed (?) way to load data sources (for example, as an instructor might wish to provide to students), a _data source specification_ file may be used with the static `connectUsing(<path>)` method. (The `connect(<path>)` method mentioned above will always first attempt to parse the given path or URL as a data source specification file.)

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

Of the above elements, `path`, `name`, and `format` should be provided, if nothing else. Examples of data source specifications may be found at https://github.com/berry-cs/big-data-cse/tree/master/big-data-java/src/big/data/specs .

### Parameters

Parameters are either 
- query parameters (typically appended to a URL, `http://....?key1=val1&key2=val2&...&keyn=valn`)
- path parameters (substitutions for placeholders in the path or url of a data source)

Query parameters are described using a `queryparam` element. Within it, a `key` element specifies the key and `description` provides a short description of the parameter's purpose. To supply a default value, use a `value` element. Alternatively, to indicate that the user must `set()` the parameter's value before loading the data source, add a `required="true"` attribute to the `queryparam` tag. 

Path parameters specify values that will be substituted into a path or URL. For example, a data source with path element given by `<path>http://services.faa.gov/airport/status/@{airportCode}</path>` may define a path parameter as `<pathparam required="true"><key>airportCode</key><description>3-digit FAA code</description></pathparam>`. When the data source is instantiated, the user must (because the parameter is `required`) use the `set()` method to provide a value for the `airportCode` key. In this example, if the data source specification is stored in file `FAAAirportStatus.xml`, then `DataSource ds = DataSource.connect("FAAAirportStatus.xml").load();` will result in an exception: `big.data.DataSourceException: Not ready to load; missing parameters: airportCode` because the `airportCode` parameter has not been supplied. Once supplied,

```
DataSource ds = DataSource.connect("FAAAirportStatus.xml").set("airportCode", "JFK").load();
```

the data source loads from the path `http://services.faa.gov/airport/status/JFK` (note `"JFK"` has been substituted for `@{airportCode}`). 


### Data Source Options

For XML:
 - post-processing class

For CSV:
 - header



#Using the Data Source

## Fetching Data

```
	 <T> T fetch(String clsName, String... keys);
	 <T> ArrayList<T> fetchList(String clsName, String... keys);
	 <T> T[] fetchArray(String clsName, String... keys);

	 boolean fetchBoolean(String key);
	 byte fetchByte(String key);
	 char fetchChar(String key);
	 double fetchDouble(String key);
	 float fetchFloat(String key);
	 int fetchInt(String key);
	 String fetchString(String key);
	
	 boolean[] fetchBooleanArray(String key);
	 byte[] fetchByteArray(String key);
	 char[] fetchCharArray(String key);
	 double[] fetchDoubleArray(String key);
	 float[] fetchFloatArray(String key);
	 int[] fetchIntArray(String key);
	 String[] fetchStringArray(String key);
	
	 ArrayList<Boolean> fetchBooleanList(String key);
	 ArrayList<Byte> fetchByteList(String key);
	 ArrayList<Character> fetchCharList(String key);
	 ArrayList<Double> fetchDoubleList(String key);
	 ArrayList<Float> fetchFloatList(String key);
	 ArrayList<Integer> fetchIntList(String key);
	 ArrayList<String> fetchStringList(String key);
```


## Filtering Data

The `big.data` library intentionally does not provide any fancy filtering or aggregation operations, since the intent is for instructors to use the library simply to obtain collections of data upon which students may be asked to write various operations to process them. Nonetheless, the library does provide a very simplistic operation to select only certain records from a data source that provides a list of records. 

```
	 DataSource select(String key, String val);  // only records where 'key' field matches 'val'
	 DataSource selectContains(String key, String val); // only records 'key' field contains 'val' as a substring
```

The `select` operations must be applied _before_ `load()`ing the data source.




## Using an Iterator over the Data Source

```
      DataSourceIterator iter = ds.iterator();
      while (iter.hasData()) {
         String name = iter.fetchString("Name");
         String status = iter.fetchString("Status Comment");
         System.out.println(name + ": " + status);
         iter.loadNext();
      }
```

# Miscellaneous

## Data Caching


## Processing Support

When using Processing (http://processing.org), in order for the library to work properly, you must call

```
void setup() {
   ...
   DataSource.initializeProcessing(this);
   ...
}
```

in the `setup()` function.






