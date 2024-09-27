# Dust-HTML

dust-html is a part of the Dust Actor framework (https://github.com/dust-ai-mr) and provides a lightweight wrapper around
* JSoup
* Jim Plush's 'Goose'

JSoup provides services for cleaning and parsing html files. 'Goose' provides a 
service which heuristically attempts to isolate the *important* parts of a web page.

Combining these via the notion of an HtmlDocumentMsg - which is a Dust message -
permits the construction of Dust pipelines which take a web page and isolate and
return the important textual content on web pages - as demo'd in a test example