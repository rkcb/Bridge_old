package scala.bridge 
import scala.collection.immutable.HashMap

class OptimumScoreContents (events: List[PbnEvent]) {
	
    private val optimumScore = null
    private val hdr = Array("", "S", "H", "D", "C", "NT")
    private val boards = events map (_.value("Board"))
    private val tables = dataMapper(events)
    private val optimumScoreDB = optimumScoreMapper(events)
    private val optimumContractDB = optimumContractMapper(events)
    
	def header(): Array[String] = {
	   if(!events.isEmpty) hdr
	   else Array() }
	
    def data(board: String): Array[Array[String]] = tables getOrElse (board, Array())
    
    def contract(board: String) = optimumContractDB getOrElse(board, "")
    
    def score(board: String) = optimumScoreDB getOrElse(board, "")
    
	private def dataMapper(evs: List[PbnEvent]): HashMap[String, Array[Array[String]]] = {
	   val dir = List("W", "N", "E", "S")
	   def buildTable(ev: PbnEvent): Array[Array[String]] = {
		      val resultRows = ev.tableColumn("OptimumResultTable", "Result").grouped(5).toList
		      val partTr = (dir :: (resultRows.transpose)).transpose
		      partTr map (_.toArray) toArray
	   }
	   val tables = evs map buildTable
	   new HashMap[String, Array[Array[String]]] ++ (boards zip tables)
	}
	
	
	
	private def optimumContractMapper(evs: List[PbnEvent]): HashMap[String, String] = {
	   new HashMap[String, String] ++ (boards zip (events map (_.value("OptimumContract"))))
	}
	 
	
	
	private def optimumScoreMapper(evs: List[PbnEvent]): HashMap[String, String] = {
	   new HashMap[String, String] ++ (boards zip (events map (_.value("OptimumScore"))))
	}
	 
	
	
}
	
	
