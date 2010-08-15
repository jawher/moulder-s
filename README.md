Moulder-S
=======================

A tiny jQuery-like HTML templating library written in Scala.

Building
--------

You need a Java 5 (or newer) environment and Maven 2.0.9 (or newer) installed:

    $ mvn --version
    Apache Maven 3.0-alpha-5 (r883378; 2009-11-23 16:53:41+0100)
    Java version: 1.6.0_15
    Java home: /usr/lib/jvm/java-6-sun-1.6.0.15/jre
    Default locale: en_US, platform encoding: UTF-8
    OS name: "linux" version: "2.6.31-12-generic" arch: "i386" Family: "unix"

You should now be able to do a full build of `neo4j-resources`:

    $ git clone git://github.com/jawher/moulder-s.git
    $ cd moulder-s
    $ mvn clean install

To use this library in your projects, add the following to the `dependencies` section of your
`pom.xml`:

    <dependency>
      <groupId>jawher</groupId>
      <artifactId>moulder-s</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>

If you don't use Maven, take `target/moulder-s-1.0.0-SNAPSHOT.jar` and all of its dependencies, and add them to your classpath.


Troubleshooting
---------------

Please consider using [Github issues tracker](http://github.com/jawher/moulder-s/issues) to submit bug reports or feature requests.


Using this library
------------------

Here's a quick sample of how `moulder-s` can be used to manipulate html:

Given this markup:

    <html>
        <body>
            <h1>[...]</h1>
        </body>
    </html>

This moulder based snippet:

    val document = Jsoup.parse("<html><body><h1>[...]</h1></body></html>")
    val s = MoulderShop()
    s.register("h1", 
               repeat("Summer" :: "Autumn" :: "Winter" :: "Spring" :: Nil)) 
               :: attr("class", Values("even" :: "odd" :: Nil).cycle) 
               :: text(eData()) 
               :: append(h(tr(eData(), (c:String)=>"<p>"+ c +"</p>"))) 
               :: Nil)
    s.process(document)


Will generate the following:

    <html>
        <head>
        </head>
        <body> 
            <h1 class="even">Spring</h1> 
            <p>Spring</p>
            <h1 class="odd">Summer</h1> 
            <p>Summer</p>
            <h1 class="even">Autumn</h1> 
            <p>Autumn</p>
            <h1 class="odd">Winter</h1> 
            <p>Winter</p>
        </body>
    </html>

Or in plain english:

* For each item in the list of seasons, repeat the h1 element
* For each generated h1 element, set its class to even or odd
* Also set it's text content to the corresponding season
* And finally, append a paragraph after it with the season name as its content



License
-------

See `LICENSE` for details.
