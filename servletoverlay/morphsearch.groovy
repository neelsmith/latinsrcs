
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
String queryString = getQuery(params.lemma)
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))

def firstReply = parsedReply.results.bindings[0]
String lemmaStr = firstReply.lemma.value


html.html {
    head {
      title("Morphological search: ${lemmaStr}")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/latin.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1('Morphological search matching lemma form "' + lemmaStr + '"')
    	}
    	
    	article {
	  ul {
	    parsedReply.results.bindings.each { b ->
	      try {
		CtsUrn urn = new CtsUrn(b.psg.value)
		li {
		  strong(b.formstr.value)
		  mkp.yield " (from ${b.lex.value}): occurs in line ${URLDecoder.decode(urn.getPassageNode())} of ${urn.getUrnWithoutPassage()}.  "
		  a(href:"facsimile.groovy?urn=${urn}", "Read passage")
		  mkp.yield "."
		}

	      } catch (Exception e) {
	      }
	    }
	  }
	}

        footer("")
    }
}
