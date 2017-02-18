package scala.bridge 
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap

class TotalScoreContents (events: List[PbnEvent]){
	 
	private val headerSet = HashSet("Rank", "PlayerId", "PairId", "TeamId",
	     "TotalScoreMP", "TotalScoreVP", "TotalScoreIMP", "TotalScoreBAM", "TotalPercentage",
	     "Name", "Names", "TeamName", "Roster", "ScorePenalty", "Club", "MP")
	     
	def header(): Array[String] = {
	   if (!events.isEmpty) {
		   events.head.header("TotalScoreTable") filter headerSet.contains
	   } else Array()
	}
	
	private def bHeader(): List[Boolean] = {
	   if (!events.isEmpty) {
		   events.head.header("TotalScoreTable").toList map headerSet.contains
	   } else List()
	}
	
	def data(): Array[Array[String]] = {
	   if (!events.isEmpty) {
	      val bh = bHeader
	      def filterData(l: List[String]): Array[String] = {
	         val zip = (l zip bh) filter (_._2)
	         (zip unzip)._1.toArray
	      }
	      val data = events.head.data("TotalScoreTable")
	      (data map filterData).toArray
	   } else Array()
	}
	 
	 
}