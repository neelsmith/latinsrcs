
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

String getQuery(String lemma) {
String reply = """
select ?lex ?form ?lemma ?formstr ?psg where {
?lex <http://www.homermultitext.org/hmt/citedata/latlexent_Lemma> "${lemma}" .
?lex <http://www.w3.org/1999/02/22-rdf-syntax-ns#label>  ?lemma .
?lex  <http://shot.holycross.edu/rdf/hclat/hasForm> ?form .
?form   <http://www.homermultitext.org/cite/rdf/occursIn>  ?psg .
?form <http://www.homermultitext.org/hmt/citedata/tokens_String> ?formstr .
}
ORDER BY ?lex ?formstr 
"""

return reply
}



def slurper = new groovy.json.JsonSlurper()


// ADD ERROR CHECK ON PARAMS...
String queryString = getQuery(params.urn)
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))


html.html {
    head {
      title("Read passage: ${params.urn}")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/latin.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1('Read passage')
    	}
    	
    	article {
	  p("${params.urn}")
	}

        footer("")
    }
}