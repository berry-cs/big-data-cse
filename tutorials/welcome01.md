
# Introduction

Welcome! This set of tutorials provides an introduction to *Sinbad* - a Java library that enables you to incorporate data from live, online web services into your programs. In this first tutorial, we'll be connecting to the National Weather Service's data feeds of current weather conditions. With each tutorial, we'll make a note of the Java concepts that you need to have covered to be able to follow along.

### Required Java Concepts

* Defining a `main()` (or `setup()` in Processing) function
* Basic data types - String, int, float
* Variables
* Using (i.e. calling) methods
* `import`ing a library
* (Optional) Using a Scanner to read from the keyboard

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


