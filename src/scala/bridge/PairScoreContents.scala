package scala.bridge
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet

class PairScoreContents(events: List[PbnEvent]) {
   
   private val pbnHeader = events.head.objs("ScoreTable").header
   private val ev = events.head
   private val nameDB = shortNameDB
   private val scoreDB = PbnEvent.scoreDB(events)
   private val hdrSet = HashSet("Board", "PairId_NS", "PairId_EW", "Contract", "Declarer",
           "Result", "Lead", "Score_NS", "Score_EW", "IMP_NS", "IMP_EW", "Percentage_NS", "Percentage_EW")
   
   private val boards = events map (_.value("Board"))        
   
   private val nsIn = pbnHeader indexOf("PairId_NS")
   private val ewIn = pbnHeader indexOf("PairId_EW")
   
   private val filterSetNS = filterSet("NS")
   private val filterSetEW = filterSet("EW")
   
   private val bHdrNS = pbnHeader map filterSetNS.contains
   private val bHdrEW = pbnHeader map filterSetEW.contains
   
   private def idToNames(id: String) = nameDB.getOrElse(id, id)
 
   def header(): Array[String] = {
      // note: by convention opponent plays in the EW direction which means that
      // all columns whose label ends with EW are opponent values and
      // respective NS columns are "our" values
      // opponent id renaming  depends on this setting, see data(...) below
      //
      val h = pbnHeader filter filterSetNS.contains
      h.+:("Board") // prepend board
   }
   
   /* namesById returns pair's first names given the id */
   
   private def namesById(): HashMap[String, String] = {
      if (ev.objs.isDefinedAt("TotalScoreTable")) {
         val ids = ev.tableColumn("TotalScoreTable", "PairId")
         val names = ev.tableColumn("TotalScoreTable", "Names")
         new HashMap[String, String] ++ (ids zip names)
      } else HashMap()
   }
   
   /* shortNameDB returns a relationship between pair ids and respective short names */ 
   private def shortNameDB(): HashMap[String, String] = {

      val nameRe = """[\w-]+""".r
      
      /* shortName maps full names to first names, e.g. x1 x2 - y1 y2 => x1 & y1 */
      def shortName(name: String): String = {
         if (name.contains(" - ")) {
        	 val names = name split (" - ") map (_.trim)
        	 val name1 = nameRe.findFirstMatchIn(names(0))
        	 val name2 = nameRe.findFirstMatchIn(names(1))
        	 if (!name1.isEmpty && !name2.isEmpty) {
        	    name1.get+" & "+name2.get
        	 } else name
         } else name 
      }
      
      val ids = ev.tableColumn("TotalScoreTable", "PairId")
      val shortNames = ev.tableColumn("TotalScoreTable", "Names") map shortName
      
      new HashMap[String, String] ++ (ids zip shortNames)
   }
   
   private def oDirection(dir: String): String = {
         dir match {
         	case "NS" => "EW"
         	case _ => "NS"
         }
      }
   
   
   /***
    * filterHeader constructs a set which shows which items in the resp. data should
    * be kept 
    * */
   
   private def filterSet(direction: String): HashSet[String] = {
      HashSet("Board", "PairId_"+oDirection(direction), "Contract", "Declarer",
           "Result", "Lead", "Score_"+direction, "IMP_"+direction, "MP_"+direction, "Percentage_"+direction)
   }
  
   /***
    * boolProtoHeader returns a boolean list which shows which hdr-items  
    * are interesting and which not 
    * */
   private def direction(id: String, l: List[String]): String = 
         if (l(nsIn) == id) "NS" else  "EW"
  
   def data(id: String): Array[Array[String]] = {
      
      val pbnData = events map (_.score(id))
      
      def dataWithBoards(pbnData: List[List[String]]): List[List[String]] = { 
         val zip = boards zip pbnData
         def pairing(t: Tuple2[String, List[String]]): List[String] = t._1::t._2 
      	 zip map pairing }

      
      def direction(id: String, l: List[String]): String = 
         if (l(nsIn) == id) "NS" else  "EW"
            
      /* preserves only data that is relevant to the pair with the id  */      
      def filterData(id: String, data: List[List[String]]): List[List[String]] = {
    	  def dataFilter(id: String)( line: List[String]): List[String] = {
    	     val filterList = direction(id, line) match {
    	        case "NS" => bHdrNS
    	        case _ => bHdrEW 
    	     }
    	     val zip = (line zip filterList) filter (_._2)
    	     (zip unzip)._1
    	  }
    	  data map dataFilter(id)
      }
     
      def replaceIdByName(idInd: Int)(l: List[String]): List[String] = {
         l updated(idInd, idToNames(l(idInd))) 
      }
      
      if (pbnData.flatten.nonEmpty) {
    	  val filteredData = filterData(id, pbnData)
    			  val boarded = dataWithBoards(filteredData)
    			  val idEWInd = header indexOf("PairId_EW")
    			  val renamed = boarded map replaceIdByName(1)
    			  renamed map (_.toArray) toArray
      } else Array()
     
   }

}