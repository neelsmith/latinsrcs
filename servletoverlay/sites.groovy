
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

String getQuery() {
String reply = """
SELECT ?site ?label WHERE {
?site <http://www.homermultitext.org/hmt/citedata/epigsite_Label>     ?label .

}
ORDER BY ?label 
"""

return reply
}



def slurper = new groovy.json.JsonSlurper()
String queryString = getQuery()
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))


html.html {
    head {
      title("Find inscriptions by location")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/latin.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1('Find inscriptions by location')
    	}
    	
    	article {
	  form (action: "textsForSite.groovy", method: "GET") {
	    select (name: "site") {
	      parsedReply.results.bindings.each { b ->
		option(value: "${b.site.value}", "${b.label.value}")
	      }
	    }
	    input(type: "submit", value: "Find inscriptions")
	  }
	}

        footer("")
    }
}
