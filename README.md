Moulder-S
=======================

A tiny jQuery-like HTML templating library written in Scala.

[On templating, and a shameless plug of Moulder](http://jawher.net/2011/01/06/on-templating-and-a-shameless-plug-of-moulder/)

[Moulder in action](http://jawher.net/2011/03/03/moulder-in-action/)


Building
--------

You'll need SBT 0.10 (or newer):

    $ git clone git://github.com/jawher/moulder-s.git
    $ cd moulder-s
    $ sbt package publish-local

You can then use this library in your projects by grabbing the generated jar (in the `target` directory) and its dependencies.



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
      repeat(items)
        :: attr("class", SeqValue("even" :: "odd" :: Nil).cycle)
        :: text(SeqValue(items))
        :: append(html(transform(SeqValue(items), (c: String) => "<p>" + c + "</p>")))
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
