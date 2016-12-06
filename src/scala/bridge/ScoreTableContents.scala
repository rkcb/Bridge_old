package scala.bridge
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap

//import com.vaadin.ui.Notification
import scala.collection.immutable.HashMap

/***
 * ScoreTableContents is used to collect pbn information and 
 * put that into 
 * Note: this class assumes ScoreTable data exists in every 
 * PbnEvent in the events below
 * */

class ScoreTableContents(events: List[PbnEvent]) {
   val translations = HashMap("Board" -> "#", "PairId_NS" -> "Vastustajat","PairId_EW" -> "Vastustajat", "Contract" -> "Sitoumus",
		      "Declarer" -> "P", "Result" -> "Tikit", "Lead" -> "Lahtija", 
		      "IMP_NS" -> "IMP_PE", "IMP_EW" -> "IMP_IL", "MP_NS" -> "MP_PE", "MP_EW" -> "MP_IL", 
		      "Score_NS" -> "Pisteet", "Score_EW" -> "Pisteet",
		      "Percentage_NS" -> "%", "Percentage_EW" -> "%")
		      
   def translate(s: String): String = translations.getOrElse(s, s)
   val scoring = makeScoring
   
   def makeScoring(): Any = {
      if (!events.isEmpty) {
         events.head.header("TotalScoreTable") match {
            case x if (x.contains("PairId")) => new PairScoreContents(events)
            case _ => null
         }
      } else null
   }
   
   def  header(): Array[String] = {
      scoring match {
         case x: PairScoreContents => {
            val s: PairScoreContents = x
            s.header
         }
         case _ => Array()   
      }
      null
   }
   
   def data(id: String = ""): Array[Array[String]] = {
      scoring match {
         case x: PairScoreContents => {
            val s: PairScoreContents = x
            s.data(id)
         }
         case _ => Array()   
      }
      null
   }
   
//   def score(id: String = ""): Array[Array[String
  
  
    
  
   
}