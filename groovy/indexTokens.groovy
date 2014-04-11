
class XmlUtils {

  boolean debug = true

  groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")
  String description = "This is an instance of the XmlUtils class."

  /** Constructor creates an object in the XmlUtils class 
   */
  XmlUtils() {
    if (debug) {
      System.err.println "Constructed an XmlUtils object."
    }
  }

  /** Constructor takes a boolean flag for debugging */
  XmlUtils(boolean toDebugOrNot) {
    this.debug = toDebugOrNot
    if (this.debug) {
      System.err.println "Constructed an XmlUtils object."
    }
  }
  
  /** Recursively collects content of text nodes from a parsed Node.
   * Special treatment for:
   * <w> (keep text as single word)
   * <choice> (always choose one!  abbr/expan)
   * @param n A parsed node from groovy's XmlParser, which will be 
   * either a groovy.util.Node, or a String.
   * @param cumulative All String data collected so far.
   */
  String collectText (Object n, String cumulative, boolean inWord) {
    if (n instanceof java.lang.String) {
      if (inWord) {
	cumulative = "${cumulative}${n}"
      } else {
	cumulative = "${cumulative} ${n}"
      }



    } else {
      // recognize tei <w> element!      
      switch (n.name().getLocalPart()) {
      case "w":
      inWord = true
      break


      case "choice":
      	cumulative = collectText(n[tei.expan][0], cumulative, inWord)
      break

      default:
      n.children().each { child ->
	cumulative = collectText(child, cumulative, inWord)
      }
      }
    }

    return cumulative
  }

} // end of XmlUtils class definition



boolean showDebug = true
// This is just a plain old groovy script.
XmlUtils xmlHelper = new XmlUtils(showDebug)




for (id in 1..104) {
  File f = new File("doc${id}.xml")
  groovy.util.Node rootNode = new XmlParser().parse(f)
  groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")

  rootNode[tei.text][tei.body][tei.ab].each { ln ->
    String txt = xmlHelper.collectText(ln,"",false).toLowerCase()
    def tokens = txt.split(/\s+/)
    def seen = [:]

    tokens.each { t ->
      if (t.size() > 0) {
	if (seen[t]) {
	  seen[t] = seen[t] + 1
	} else {
	  seen[t] = 1
	}
	println "urn:cts:hclat:inscriptions.doc${id}:${ln.'@n'}@" + t + "[${seen[t]}]" + "\turn:cite:hclat:tokens.${t}"
      }
    }
  }
}

