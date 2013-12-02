jcURL
=====

Java Client URL Request Library - Java implementation of a cURL (Client URL Request Library) like tool.

Synopsis
========

    java -jar jcurl.jar [options] [URL...]

Description
===========

jcurl is a tool to transfer data from or to a server, using one of the supported protocols (only HTTP supported at the moment). 
The command is designed to work without user interaction.

URL
===

The URL syntax is protocol dependent. You’ll find a detailed description in RFC 2396.

You can specify multiple URLs or parts of URLs by writing part sets within braces as in:

http://site.{one,two,three}.com

or you can get sequences of alphanumeric series by using [] as in:

http://www.numericals.com/page-[1-100].html 
http://www.numericals.com/page-[001-100].html (with leading zeros) 
http://www.letters.com/page-[a-z].html

No nesting of the sequences is supported at the moment, but you can use several ones next to each other:

http://any.org/archive-[1996-1999]/vol-[1-4]/part-{a,b,c}.html

You can specify any amount of URLs on the command line.

Each URL is encoding with default system charset. To encode in specific chareset, launch jcurl with JVM parameter -Dfile.encoding for example:

   java -Dfile.encoding=ISO-8859-1 -jar jcurl.jar [options] [URL...]


Options
=======

     -h,--help                       Usage help.
     -i,--include                    Include the HTTP-header in the output. The
                                     HTTP-header includes things like
                                     server-name, date of the document,
                                     HTTP-version and more...
     -inputFile <arg>                Specify file which contain a list of URL
                                     (one line per URL)
     -inputPattern <arg>             Specify regexp pattern to transform input
                                     data (see param 'inputFile' and
                                     'inputTransform') into URL
     -inputTransform <arg>           Specify substitution string to transform
                                     input data (see param 'inputFile' and
                                     'inputPattern') into URL. Use $1, $2, ...
                                     for catched group.
     -manual                         Specifies a custom request method to use
                                     when communicating with the HTTP server.
                                     The specified request will be used instead
                                     of the method otherwise used (which
                                     defaults to GET). Read the HTTP 1.1
                                     specification for details and explanations.
                                     Common additional HTTP requests include PUT
                                     and DELETE.
     -V,--version                    Displays information about jcURL version it
                                     uses.
     -X,--request <command>          Specifies a custom request method to use
                                     when communicating with the HTTP server.
                                     The specified request will be used instead
                                     of the method otherwise used (which
                                     defaults to GET). Read the HTTP 1.1
                                     specification for details and explanations.
                                     Common additional HTTP requests include PUT
                                     and DELETE.
