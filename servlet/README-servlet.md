# Latin sources servlet #

## Overview ##


This servlet queries a SPARQL endpoint about the RDF graph that can be built from data in this project with [the `citemgr` tool set][1].


## Prerequisites ##


- [gradle][2]
- a SPARQL endpoint with appropriate data loaded

[1]: https://github.com/neelsmith/citemgr

[2]: http://www.gradle.org/

## Configuration ##

A configuration file defines run-time properties such as URLs for the SPARQL endpoint and the servlet's home URL.  The file is identified either by the `conf` project property or by default uses `conf.gradle`.  If the values in `conf.gradle` are not what you want to use, you can therefore pass the name of a different configuration file on the command line when building or running the project (see below), e.g.,

    gradle -Pconf=FILENAME war


## Building or running ##


To build a war file you can add to a servlet container:

    gradle war


To run the servlet locally in a supplied jetty container:

    gradle jettyRunWar

