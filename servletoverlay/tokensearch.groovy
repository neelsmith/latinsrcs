
import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

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

String getQuery(String form) {
String reply = """
select ?lex ?form ?lemma ?psg where {
?form <http://www.homermultitext.org/hmt/citedata/tokens_String> "${form}" .
?lex <http://www.w3.org/1999/02/22-rdf-syntax-ns#label>  ?lemma .
?lex  <http://shot.holycross.edu/rdf/hclat/hasForm> ?form .
?form   <http://www.homermultitext.org/cite/rdf/occursIn>  ?psg .

}
ORDER BY ?lex 
"""
return reply
}



def slurper = new groovy.json.JsonSlurper()


// ADD ERROR CHECK ON PARAMS...
String queryString = getQuery(params.word)
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))


html.html {
    head {
      title("Word search: ${params.word}")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/latin.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1('Search for literal form "' + params.word + '"')
    	}
    	
    	article {
	  mkp.yield "Num results: ${parsedReply.results.bindings.size()}"
	  ul {
	    parsedReply.results.bindings.each { b ->
	      try {
		CtsUrn urn = new CtsUrn(URLDecoder.decode(b.psg.value))
		CiteUrn lexUrn = new CiteUrn(b.lex.value)
		li {
		  mkp.yield "${urn} -> ${lexUrn}"
		  String nodeStr = urn.getPassageNode()
		  String psgUrn = urn.getUrnWithoutPassage() + ":" + nodeStr
		  mkp.yield " (${lexUrn.getObjectId()}): occurs in "
	  String linkStr = "line ${urn.getPassageNode()} of ${urn.getUrnWithoutPassage()}. "
		  		  a(href:"facsimile.groovy?urn=${psgUrn}", linkStr)
		  mkp.yield "."
		}

	      } catch (Exception e) {
		System.err.println "Something went wrong."
		System.err.println "Tried CTS URN " + b.psg.value + " and CITE lex ent " + b.lex.value
		
	      }
	    }
	  }
	}

        footer("")
    }
}
