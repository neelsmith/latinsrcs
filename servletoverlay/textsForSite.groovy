import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import edu.harvard.chs.cite.CtsUrn

import java.net.URLDecoder

String sparql = "@sparqls@"


/**
* Submits an encoded query to the configured SPARQL endpoint,
* and returns the reply.
* @param acceptType MIME type to specify for reply.
* @param query SPARQL query to submit.
* @returns SPARQL reply, as a String.
*/
String getSparqlReply(String acceptType, String query) {
    String replyString
    def encodedQuery = URLEncoder.encode(query)
    def q = "@sparqls@query?query=${encodedQuery}"
    if (acceptType == "application/json") {
        q +="&output=json"
    }

    def http = new HTTPBuilder(q)
    http.request( Method.GET, ContentType.TEXT ) { req ->
        headers.Accept = acceptType
        response.success = { resp, reader ->
            replyString = reader.text
        }
    }
    return replyString
}

String getQuery(String siteUrn) {
String reply = """
SELECT ?site ?txt ?label  WHERE {
<${siteUrn}> <http://www.homermultitext.org/cite/rdf/illustratedBy> ?img .
 ?img    <http://www.homermultitext.org/cite/rdf/isDefaultImage>  ?txt .

?txt  <http://www.homermultitext.org/cts/rdf/belongsTo> <urn:cts:latepig:inscriptions>  .
?txt <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?label .

<${siteUrn}> <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?site .
}
"""

return reply
}



def slurper = new groovy.json.JsonSlurper()
String queryString = getQuery(params.site)
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))

def firstMatch = parsedReply.results.bindings[0]

String siteName 
if ((firstMatch) && (firstMatch.site?.value)) {
  siteName = firstMatch.site.value
}


html.html {
    head {
      title("Inscriptions for site ${siteName}")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/latin.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
	    String h1text = ""

	    switch (parsedReply.results.bindings.size()) {
	    case 1:
	    h1text = "${siteName}: 1 inscription"
	    break
	    default:
	    h1text = "${siteName}: ${parsedReply.results.bindings.size()} inscriptions"
	    break
	    }
            h1(h1text)
    	}
    	
    	article {
	  p("${siteName} (${params.site})")
	  ul {
	    parsedReply.results.bindings.each { b ->
	      li {
		a (href: "facsimile.groovy?urn=${b.txt.value}", b.label.value)
	      }
	    }
	  }
	}

        footer("")
    }
}
