
/*
Read a list of Latin words from a file, get their morphological
analysis.  Results are written to standard output.

Additional details are written on separate lines for
verbs and adjectives, 



Usage: groovy morphfull.groovy FILENAME [DEBUGLEVEL]

where FILENAME is the name of the file to read, and
DEBUGLEVEL is an optional integer indicating level of
debugging output to spew.


*/



groovy.xml.Namespace rdf = new groovy.xml.Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
groovy.xml.Namespace oac = new groovy.xml.Namespace("http://www.openannotation.org/ns/")
groovy.xml.Namespace cnt = new groovy.xml.Namespace("http://www.w3.org/2008/content#")




Integer debug = 0

if (args.size() == 2) {
  debug = args[1] as Integer
}

String urlBase= "http://services.perseids.org/bsp/morphologyservice/analysis/word?lang=lat&engine=morpheuslat&word="



String verbDetail(NodeList inflectionNodes, groovy.xml.Namespace ns, String id) {
  String reply = ""

  inflectionNodes.each { inflection ->

    // all forms
    String perseusType = inflection[ns.stemtype][0]?.text()
    String mood = inflection[ns.mood][0]?.text()

    String tense = ""
    String voice  = "" // left implicit in some perseus output :-(

    // conjugated forms:
    String person = ""
    String num = ""

    // ptcpls:
    String morphCase   = ""
    String gender = ""

    // degree of participles?

    if (mood) {
      switch (mood) {
      case "participle":
      analysisFormat = "participle"

      morphCase = inflection[ns.case][0]?.text()
      gender = inflection[ns.gend][0]?.text()
      num = inflection[ns.num][0]?.text()

      tense = inflection[ns.tense][0]?.text()      
      if (tense == "perfect") {
	voice = "passive"
      } else if (tense == "present") {
	voice = "active"
      } else {
	voice = "ERROR"
      }
      break



      case "infinitive":
      tense = inflection[ns.tense][0]?.text()      
      voice = inflection[ns.voice][0]?.text()
      break

      case "gerundive":
      voice = "passive"
      gender = inflection[ns.gend][0]?.text()
      morphCase = inflection[ns.case][0]?.text()
      num = inflection[ns.num][0]?.text()
      break


      case "imperative":
      case "subjunctive":
      case "indicative":
      person = inflection[ns.pers][0]?.text()      
      num = inflection[ns.num][0]?.text()      
      tense = inflection[ns.tense][0]?.text()      
      voice = inflection[ns.voice][0]?.text()      
      break
      
      default:
      System.err.println "Unrecognized mood: '" +  mood + "'"
      break
      }
    } else {
      System.err.println "Verbal form, but NO MOOD given"
    }
    reply += "${id},verbDetail,${person},${num},${tense},${mood},${voice},${gender},${morphCase},${perseusType}\n"    
  }

  return reply
}


  /* **** ADJS **/
  /*
 <pofs order="2">adjective</pofs>
      <decl>1st &amp; 2nd</decl>
      <case order="7">nominative</case>
      <gend>masculine</gend>
      <num>singular</num>
      <stemtype>us_a_um</stemtype>
      <morph>irreg_superl</morph>
  */
/*
      <term xml:lang="lat">
         <stem>rect</stem>
         <suff>issimus</suff>
      </term>
      <pofs order="2">adjective</pofs>
      <decl>1st &amp; 2nd</decl>
      <case order="1">vocative</case>
      <gend>masculine</gend>
      <comp>superlative</comp>
      <num>singular</num>
      <stemtype>us_a_um</stemtype>
*/

/* **** NOUNS AND PRONOUNS **** 



   <infl>
      <term xml:lang="lat">
         <stem>agricol</stem>
         <suff>a_s</suff>
      </term>
      <pofs order="3">noun</pofs>
      <decl>1st</decl>
      <case order="4">accusative</case>
      <gend>masculine</gend>
      <num>plural</num>
      <stemtype>a_ae</stemtype>
   </infl>


      <case order="7">nominative</case>
      <gend>masculine</gend>
      <num>singular</num>
      <stemtype>demonstr</stemtype>
      <morph>indeclform</morph>

 */
 /* **** ADJS **/
  /*
 <pofs order="2">adjective</pofs>
      <decl>1st &amp; 2nd</decl>
      <case order="7">nominative</case>
      <gend>masculine</gend>
      <num>singular</num>
      <stemtype>us_a_um</stemtype>
      <morph>irreg_superl</morph>
  */

Integer count = 0


File wordList = new File(args[0])
wordList.eachLine { ln ->
  count++;
  def url = new URL(urlBase + ln)
  String parseReply = url.getText("UTF-8")
  if (debug > 1) { System.err.println parseReply}

  def root = new XmlParser().parseText(parseReply)
  root[oac.Annotation][oac.Body][cnt.rest][cnt.entry].each { ent ->
    String urn = ent.'@uri'?.replaceFirst('http://data.perseus.org/collections/','')
    String pos = ""
    String lemma  = ""
    ent[cnt.dict].each {  dict ->
      pos = dict[cnt.pofs][0].text()
      lemma = dict[cnt.hdwd][0].text()
      String core = "analyses.${count},${pos},${ln},${lemma},${urn}"
      String details = ""
      switch (pos) {
      case "verb":
      details = verbDetail(ent[cnt.infl], cnt, "analyses.${count}")
      break

      case "noun":
      case "pronoun":
      break

      case "adjective":
      break

      case "adverb":
      case "conjunction":
      case "exclamation":
      case "numeral":
      case "preposition":
      break;

      default:
      System.err.println "Unrecognized part of speech: '" + pos + "'"
      break
      }
      println "${core}\n${details}"
    }

  }
  
}