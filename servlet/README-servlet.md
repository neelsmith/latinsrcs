# Latin sources servlet #

## Overview ##


This servlet queries a SPARQL endpoint about the RDF graph that can be built from data in this project with [the `citemgr` tool set][1].


## Prerequisites ##


- [gradle][2]
- a SPARQL endpoint with appropriate data loaded

[1]: https://github.com/neelsmith/citemgr

[2]: http://www.gradle.org/

## Configuration ##

- Make a copy of the file `conf.gradle-dist` named 

A configuration file defines run-time properties identifying URLs for the SPARQL endpoint and the servlet's home URL.  The file is identified either by the `conf` project property or by default uses `conf.gradle`.  You can therefore either make a copy of the file `conf.gradle-dist` named `conf.gradle` with appropriate values, or pass the name of a configuration file on the command line when building or running the project (see below), e.g.,

    gradle -Pconf=FILENAME war


## Building or running ##


To build a war file you can add to a servlet container:

    gradle war


To run the servlet locally in a supplied jetty container:

    gradle jettyRunWar

