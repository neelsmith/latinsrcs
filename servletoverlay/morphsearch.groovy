
import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import edu.harvard.chs.cite.CtsUrn


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



html.html {
    head {
        title("Morphological search")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/steely.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            link(href : "http://folio.furman.edu/images/swiss.css", rel : "stylesheet", type : "text/css")
    	
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1("Occurrences")
    	}
    	
    	article {
	  //pre(queryString)
	  ul {
	    parsedReply.results.bindings.each { b ->
	      
	      try {
		CtsUrn urn = new CtsUrn(b.psg.value)
		li {
		  strong(b.formstr.value)
		  mkp.yield ": line ${urn.getPassageNode()} of ${urn.getUrnWithoutPassage()}"
		}

	      } catch (Exception e) {
	      }
	    }
	      /*

  String 
    categoryLabels[b.category.value] =  b.catlabel.value

    topicLabels[b.topic.value] =  b.topiclabel.value
    imageCount[b.topic.value] = b.images.value
    def topicsList 
    if (categoryTopicMap[b.category.value]) {
        topicsList = categoryTopicMap[b.category.value]
        topicsList.add(b.topic.value)
    } else {
        topicsList = [b.topic.value]
    }
    categoryTopicMap[b.category.value] = topicsList
    }*/


	    }
	}

        footer("")
    }
}
