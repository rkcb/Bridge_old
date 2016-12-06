package scala.bridge
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap

class ComparisonTableContents (events: List[PbnEvent]){
   
   private val headerSet = HashSet("PairId_NS","PairId_EW", 
         "PlayerId_North", "PlayerId_South", "PlayerId_East", "PlayerId_West", 
         "Contract", "Declarer", "Result", "Lead", 
         "IMP_NS", "IMP_EW", "MP_NS", "MP_EW", "Percentage_NS", "Percentage_EW",
         "Percentage_North", "Percentage_East", "MP_North", "MP_East",
         "IMP_North", "IMP_East", "BAM_North", "BAM_East")
	
   private val bHdr = bHeader
         
   def header(): Array[String] = {
       if (!events.isEmpty) {
	       events.head.header("ScoreTable") filter headerSet.contains
       } else Array()
   }
   
   def bHeader(): List[Boolean] = {
      if(!events.isEmpty) {
         events.head.header("ScoreTable").toList map headerSet.contains
      } else List()
   } 
   
   def data(board: String): Array[Array[String]] = {
	  val ev = events.find(_.objs("Board").value == board).get
	  val d = ev.data("ScoreTable")
	  
	  def dataFilter(d: List[String]): Array[String] = {
	      val zip = d zip bHdr filter (_._2)
	      (zip unzip)._1.toArray
	  }
	  (d map dataFilter).toArray
   }
   
   val translations = HashMap("PairId_NS" -> "PE","PairId_EW" -> "IL", "Contract" -> "Sitoumus",
		      "Declarer" -> "P", "Result" -> "Tikit", "Lead" -> "Lahtija", 
		      "IMP_NS" -> "IMP, PE", "IMP_EW" -> "IMP, IL", "MP_NS" -> "MP, PE", "MP_EW" -> "MP, IL", 
		      "Percentage_NS" -> "% PE", "Percentage_EW" -> "% IL")
		      
   def translate(s: String): String = translations.getOrElse(s, s)
   
}