
package scala.bridge
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import scala.collection.JavaConverters._
//import com.vaadin.ui.Table
import java.lang.Float
//import com.vaadin.ui.Notification
//import com.vaadin.data.util.IndexedContainer
import scala.bridge._


/***
 * gives the contents to pbn result tables
 * pbn1 contains the pbn file as files
 * onlyFirst is true if only first PbnEvent is parsed and false if all
 * */

class TableFactory(val pbn1:  java.util.List[String], val onlyFirstEvent: Boolean) {
    
    //val events: List[PbnEvent] = PbnEvent.getEvents(pbn1.)//PbnEvent.getEventsFromFile(name)
	  val events =  if(onlyFirstEvent) PbnEvent.getFirstEvent(pbn1.asScala.toList) 
                  else PbnEvent.getEvents(pbn1.asScala.toList)
    val totalScoreSupported = isTotalScoreTableSupported
    val competitionType = getCompetitionType
    val scoreTableSupported = isScoreTableSupported 
    val dealSupported = isDealSupported 
    val comparisonTableSupported = scoreTableSupported && 
       (competitionType == "individual" || competitionType == "pair") 
    val optimumResultTableSupported = !events.isEmpty && 
   									  events.head.objs.isDefinedAt("OptimumResultTable")
    val translationFi = TranslationFi
    val hasMP = containsMPs 
    val MPScoring = matchPointScoring
    val IMPScorning = impScoring
    val VPScoring = vpScoring
    val eventDescription = getDescription 
    
    private val scoring = getScoring
    private val totalScore = new TotalScoreContents(events)
    private val optimum = new OptimumScoreContents(events)
    private val comparison = new ComparisonTableContents(events)
    private val eventsByBoard = makeHashMapEvent
   	
    private def getDescription(): String = {
      val os = events.head.objs
      if (os.isDefinedAt("Event"))
        os("Event").value
      else ""
    }
    
    private def matchPointScoring(): Boolean = {
       events.head.header("TotalScoreTable").contains("TotalScoreMP"); 
    }  
    private def impScoring(): Boolean = {
        events.head.header("TotalScoreTable").contains("TotalScoreIMP"); 
    }
    
    private def vpScoring(): Boolean = {
      events.head.header("ScoreTable").contains("VP_Home")
    }
    
    private def containsMPs(): Boolean = {
      events.head.header("TotalScoreTable").contains("MP")
    }
    private def makeHashMapEvent(): HashMap[String, PbnEvent] = {
       val boards = events map (_.value("Board"))
       new HashMap[String, PbnEvent] ++ (boards zip events)
    } 
     
    private def isTotalScoreTableSupported(): Boolean = {
    												// was ScoreTable 
        !events.isEmpty && events.head.objs.isDefinedAt("TotalScoreTable") }

    private def isScoreTableSupported(): Boolean = {
       !events.isEmpty && events.head.objs.isDefinedAt("ScoreTable") }
    
    private def isDealSupported(): Boolean = {
       !events.isEmpty && events.head.objs.isDefinedAt("Deal") }
      
    private def getCompetitionType(): String = {
       if (totalScoreSupported) { 
          events.head.header("TotalScoreTable") match { 
    		case x if x.contains("PlayerId") => "individual"
    		case x if x.contains("PairId") => "pair"
    		case x if x.contains("TeamId") => "team"
    		case _ => ""
       }} else ""
    }
    
    private def getScoring(): Any = {
       competitionType match {
          case "individual" => new IndividualScoreContents(events)
          case "pair" => new PairScoreContents(events)
          case "team" => new TeamScoreContents(events)
          case _ => ""
       }
    }
    
    def idField(): String = {
       if(totalScoreSupported) {
          val re = """Id$""".r
          val id = (totalScoreHeader find (!re.findFirstMatchIn(_).isEmpty)).get
          if (id == "PairId" || id == "PlayerId" || id == "TeamId") id else "" 
       } else ""
    }
    
    def eventByBoard(board: String): PbnEvent = eventsByBoard.getOrElse(board, null)
    
    def deal(board: String): Array[Array[String]] = {
       val ev = events find (_.value("Board") == board)
       if (!ev.isEmpty) ev.get.deal else Array()
    }
    
    def totalScoreHeader(): Array[String] = 
       if (totalScoreSupported) totalScore.header else Array()
    
    def totalScoreData(): Array[Array[String]] = 
       if (totalScoreSupported) totalScore.data else Array()
    
    def maxImp(): Float = { (events map (_.maxImp())).max }
    
    def averageMaxImp(): Float = {
      (events map (_.maxImp())).sum / events.size
    }
    
    /***
     * header returns the header of ScoreTable 
     ***/
    
    def scoreTableHeader(): Array[String] = {
       if (scoreTableSupported)
    	scoring match {
          case x: IndividualScoreContents => x.header
          case x: PairScoreContents => x.header
          case x: TeamScoreContents => x.header
          case _ => Array()
       }
       else Array()
    }
    
    /***
     * data returns data lines of ScoreTable  
     ***/
    
    def scoreData(id: String = ""): Array[Array[String]] = {
       if (scoreTableSupported)
	       scoring match {
	          case x: IndividualScoreContents => x.data(id)
	          case x: PairScoreContents => x.data(id)
	          case x: TeamScoreContents => x.data(id)
	          case _ => Array()
	       }
       else Array()
    }
    
    def comparisonHeader(): Array[String] = {
       if(comparisonTableSupported) comparison.header else Array() 
    }
    
    def comparisonData(board: String): Array[Array[String]] = {
       if(comparisonTableSupported) comparison.data(board) else Array()
    }
    
    def optimumHeader(): Array[String] = {
       if (optimumResultTableSupported) 
          optimum.header
       else Array()
    }
   
    def optimumData(board: String): Array[Array[String]] = {
       if (optimumResultTableSupported)
          optimum.data(board)
       else Array()
    }
    
    def optimumContract(board: String): String = optimum.contract(board)
    
    def optimumScore(board: String): String = optimum.score(board)
    
    def translateFi(contentType: String, word: String): String = {
       contentType match {
          case "total" => TranslationFi.total(word)
          case "score" => TranslationFi.score(word)
          case "comparison" => TranslationFi.comparison(word)
          case _ => word
       }}
    
    
}