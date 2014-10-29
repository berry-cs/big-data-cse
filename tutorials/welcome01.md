
# Introduction

Welcome! This set of tutorials provides an introduction to *Sinbad* - a Java library that enables you to incorporate data from live, online web services into your programs. In this first tutorial, we'll be connecting to the National Weather Service's (NWS) data feeds of current weather conditions. With each tutorial, we'll make a note of the Java concepts that you need to have covered to be able to follow along.

### Required Java Concepts

* Defining a `main()` (or `setup()` in [Processing](http://processing.org)) function
* Basic data types - String, int, float
* Variables
* Using (i.e. calling) methods
* `import`ing a library
* Using `System.out.println` to print text to the console

### Downloading and Installing *Sinbad*

* Follow the instructions here.

### The Basics

To access any data source using *Sinbad*, there are three basic steps you carry out:

1. _Connect_ to the data source by URL or filename
2. _Load_ all the data
3. _Fetch_ elements of interest from the data

There are several steps that may be required for step 1, and there are a variety of ways that you can fetch elements from the data in step 3 - we'll cover these eventually.


## Getting Started

* Open up a new program file in your Java programming environment and define a `main()` function (or `setup()` in Processing) in your file. 

* Import the *Sinbad* library by typing:

        import big.data.*; 

  at the top of your file.

  * If you are using [Processing](http://processing.org), you will need to also always include the following line at the very beginning of the `setup()` function of your sketch:

          void setup() {
            DataSource.initializeProcessing(this);
            ...
          }

  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.
  
* Alright! So, let's now go through the three basic steps above to get data from the NWS' site. 

  1. First, we use the `connect` method to create a DataSource object and assign it to a variable. The `connect` method requires one argument (or, parameter): the URL of the data service. We'll talk more about figuring out URLs later, but for now, let's use `http://w1.weather.gov/xml/current_obs/KATL.xml`, which provides a data feed for current weather conditions at Hartsfield-Jackson International Airport in Atlanta, GA.

     Add the following statement to your `main` method (or `setup` in [Processing](http://processing.org)):
  
          DataSource ds = DataSource.connect("http://w1.weather.gov/xml/current_obs/KATL.xml");


  2. Now, the `ds` variable refers to a DataSource object that is set up to connect to the URL you provided. The next step is to have the data actually loaded - this goes out to the URL and downloads whatever data it provides. Add the following statement to your program, which invokes (calls) the `load` method on the `ds` object we created in the previous step:
  
          ds.load();
         
  3. And, finally, let's fetch the current temperature (in Fahrenheit). To fetch elements of data, you will need to know their labels (or, tags). Again, we'll see later how you go about finding what elements of data are available and what their labels are. For now, the label of interest to us is `temp_f`. Let's fetch that piece of data and assign it to a variable of type `float` using the `fetchFloat` method of the DataSource object:
  
          float temp = ds.fetchFloat("temp_f");

* At this point, we've connected, loaded, and fetched some data. It might now by handy to have our program display the temperature value, so let's add a `println` statement:

       System.out.println("Temperature: " + temp);

* Now run your program. You should see a temperature value printed out that matches what is shown at the URL `http://w1.weather.gov/xml/current_obs/KATL.xml` if you load it in your web browser.


### Data Elements and Labels

In the program you just wrote, we told you that the label for the piece of data representing the current temperature in Fahrenheit was `temp_f`. How might you figure out what other pieces of data are available? There are at least two ways to do so. 

1. The first is to look for documentation on the web site that provides the data. In our case, if you go to the main web site for the "Current Weather Conditions" data that is provided by the NWS, `http://w1.weather.gov/xml/current_obs/`, the last sentence of the first paragraph contains a link to a "Product Description Document". If you click on that, you get a PDF document with a example, on the second page, of a data set in XML format. 

   It is not very friendly-looking, and indeed, different web sites will provide better or worse documentation of the available pieces of data they supply. If you are working on an assignment for class, the instructor or teaching assistant can help you find and figure out the documentation for a given data source.

   In any event, you might be able to pick out some labels from the XML text you see in this PDF - in addition to "temp_f" (can you find it?), there's "temp_c", "location", "wind_mph", etc. Now, again, a lot of web services will provide a _much_ better listing of the data labels that they supply and what they mean - the NWS site unfortunately doesn't.

2. The second way to figure out what data labels are available is actually by using a method of the `DataSource` object in our program. Once the data has been loaded, the library analyzes it and can provide you a summary of the labels it has found. Do this by adding the following statement _after_ the `ds.load()` statement in your program:

        ds.printUsageString();
       
   When you run your program, you should get a listing that looks something like this: 

````
-----
Data Source: http://w1.weather.gov/xml/current_obs/KATL.xml

The following data is available:
   A structure with fields:
   {
     ...
     dewpoint_f : *
     location : *
     temp_c : *
     temp_f : *
     weather : *
     wind_degrees : *
     wind_dir : *
     wind_mph : *
     ...
   }
-----
````

   This listing displays the available fields of data you can extract using the `fetch` method. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented.





* Done

