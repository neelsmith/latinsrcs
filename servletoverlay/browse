/** 

*/


import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

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
select ?category ?topic ?catlabel ?topiclabel (count(?img) as ?images) where {
?topic <http://www.w3.org/2000/01/rdf-schema#subClassOf>      ?category .
?category <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?catlabel .
?topic <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?topiclabel .
?topic <http://www.homermultitext.org/cite/rdf/illustratedBy> ?img .
}
GROUP BY ?category ?topic ?catlabel ?topiclabel
ORDER BY ?catlabel ?topiclabel
"""

return reply
}



def slurper = new groovy.json.JsonSlurper()
String queryString = getQuery()
def parsedReply = slurper.parseText(getSparqlReply("application/json", queryString))

def categoryLabels = [:]
def topicLabels = [:]
def categoryTopicMap = [:]
def imageCount = [:]
parsedReply.results.bindings.each { b ->
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
}




html.html {
    head {
        title("Latin sources")
        link(type : "text/css", rel : "stylesheet", href : "css/normalize.css", title : "CSS stylesheet")
        link(type : "text/css", rel : "stylesheet", href : "css/steely.css", title : "CSS stylesheet")
    }
    
    body {
    	header {
            link(href : "http://folio.furman.edu/images/swiss.css", rel : "stylesheet", type : "text/css")
    	
            nav (role : "navigation") {
                a(href : '@homeUrl@', "Home")
            }
            h1("Categories and topics")
    	}
    	
    	article {

            ul {
                categoryTopicMap.keySet().each { cat ->
                    li {
                        mkp.yield "${categoryLabels[cat]}"
                        ul {
                            categoryTopicMap[cat].each { topic ->
                                li {
                                    mkp.yield "${topicLabels[topic]}"
                                    if (imageCount[topic] == "1") {
                                        a (href : "ills?topic=${topic}&label=${topicLabels[topic]}","1 image")
                                    } else {
                                        a (href : "ills?topic=${topic}&label=${topicLabels[topic]}","${imageCount[topic]} images")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        footer("")
    }
}
