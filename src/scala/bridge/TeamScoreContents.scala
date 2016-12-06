package scala.bridge
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet

class TeamScoreContents(events: List[PbnEvent]) {
  // BAM is missing -- no info of header items, possibly BAM_Home an BAM_Away
	private val ev = events.head
	private val pbnHeader = ev.header("ScoreTable")
	private val nameDB = buildNameDB //teamNameDB
	private val visibleHdrSet = HashSet("Round", "TeamId_Away", "VP_Home", 
      "BAM_Home" )
 
	val homeInd = pbnHeader indexWhere (_ == "TeamId_Home")
	val awayInd = pbnHeader indexWhere (_ == "TeamId_Away")

	private def buildNameDB(): HashMap[String, String] = {
	   val names = ev.tableColumn("TotalScoreTable", "TeamName")
	   val ids = ev.tableColumn("TotalScoreTable", "TeamId")
	   new HashMap[String, String] ++ (ids zip names)
	}
    
    /***
    * header returns a header which is a subarray of visibleHdr which consists items 
    * of the corresponding pbn header
    * */
   
   def header(): Array[String] = {
       pbnHeader filter (visibleHdrSet contains) }
   
   /***
    * indexes gives indexes of the following items
    * precondition: Round, TeamdId_Home, TeamId_Away exist always and 
    * exclusively VPs or BAMs // BAMs are a guess!!
    * */
   
   def indexes(): List[Int] = {
     val h0 = List("Round", "TeamId_Home", "TeamId_Away", "VP_Home", "VP_Away", "BAM_Home", "BAM_Away")
     h0 filter (pbnHeader.contains(_)) map (pbnHeader.indexOf(_))
   }
   
    /***
     * data returns data which contains data corresponding the filtered header (see header())
     * and then replaces team ids by respective names
     * */ 
   
	def data(id: String): Array[Array[String]] = {

    def containsTeamId(id: String)(l: List[String]): Boolean = { 
      l(homeInd).matches(id) || l(awayInd).matches(id) }
		
		def replaceIdsByName(l: List[String]): List[String] = {
      l.updated(1, nameDB.getOrElse(l(1), l(1)))
		}
		
    def pickCols(ind: List[Int])(l: List[String]): List[String] = {
      ind map (l(_))
    }
    
    def pickCols2(id: String)(l : List[String]): List[String] = {
      val idInHome = l(1).matches(id)
      val indHome = List(0, 2, 3) // check indexes(), h0 and prexcondition
      val indAway = List(0, 1, 4)
      
      if (idInHome) pickCols(indHome)(l) else pickCols(indAway)(l)
    }
    
    val pbn = ev.data("ScoreTable") filter containsTeamId(id)
    if (pbn.flatten.nonEmpty) {
      val res = pbn map pickCols(indexes()) map pickCols2(id)

      ((res map replaceIdsByName) map (_.toArray)).toArray 
    } else Array()
	}

}