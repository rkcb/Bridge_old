package scala.bridge
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet

/***
 * IndividualScoreContents filters the lines and columns which are 
 * interesting
 * */

class IndividualScoreContents (events: List[PbnEvent]) {

   private val pbnHeader = events.head.objs("ScoreTable").header
   private val ev = events.head
   private val nameDB = shortNameDB
   private val scoreDB = PbnEvent.scoreDB(events)
   private val hdrSet = HashSet("Board", "PlayerId_North", "PlayerId_South", "PlayerId_East",
         "PlayerId_West", "Contract", "Declarer", "Result", "Lead", "Score_NS",
         "Score_EW","MP_North", "MP_South", "MP_East", "MP_West", 
         "IMP_North", "IMP_South", "IMP_East", "IMP_West",
         "Percentage_North", "Percentage_South", "Percentage_East", "Percentage_West",
         "BAM_North", "BAM_South", "BAM_East", "BAM_West")
   
   private val boards = events map (_.value("Board"))        
   private val idInd = PbnEvent.idIndices(ev)// positions of ids in the pbn header in the event
  
   
   private def idToNames(id: String) = nameDB.getOrElse(id, id)
   private val bNorth = pbnHeader.toList map filterSet("North").contains
   private val bSouth = pbnHeader.toList map filterSet("South").contains
   private val bEast = pbnHeader.toList map filterSet("East").contains
   private val bWest = pbnHeader.toList map filterSet("West").contains
   private val filterTmp = HashMap("PlayerId_North" -> bNorth,
		   							"PlayerId_South" -> bSouth,
		   							"PlayerId_East" -> bEast,
		   							"PlayerId_West" -> bWest)
   	   							
   private val filterMap = HashMap(0 -> filterTmp(pbnHeader(idInd(0))),
		   							1 -> filterTmp(pbnHeader(idInd(1))),
		   							2 -> filterTmp(pbnHeader(idInd(2))),
		   							3 -> filterTmp(pbnHeader(idInd(3))))
   
   def indexToPos = HashMap(bNorth -> "N", bSouth -> "S", bEast -> "E", bWest -> "W")
		   					
		   							
   def header(): Array[String] = {
      // note: by convention opponent plays in the EW direction which means that
      // all columns whose label ends with EW are opponent values and
      // respective NS columns are "our" values
      // opponent id renaming  depends on this setting, see data(...) below
      //
      val h = pbnHeader filter filterSet("South").contains
      h.+:("Board") // prepend board
   }
   
   /***
    * dataFilter finds by player id the correct filter
    * */
   private def dataFilter(id: String, data: List[String]): List[Boolean] = {
      val ind = idInd indexWhere(data(_) == id)
      if (ind == -1) List() else filterMap(ind)
   }   
   /* namesById returns pair's first names given the id */
   
   private def namesById(): HashMap[String, String] = {
      if (ev.objs.isDefinedAt("TotalScoreTable")) {
         val ids = ev.tableColumn("TotalScoreTable", "PlayerId")
         val names = ev.tableColumn("TotalScoreTable", "Names")
         new HashMap[String, String] ++ (ids zip names)
      } else HashMap()
   }
   
   /* shortNameDB returns a relationship between pair ids and respective short names */ 
   private def shortNameDB(): HashMap[String, String] = {

      val nameRe = """[\w-]+""".r
      
      /* shortName maps full nameto first name */
      def shortName(name: String): String = {
         if (!nameRe.findFirstMatchIn(name).isEmpty) {
        	 nameRe.findFirstMatchIn(name).get.toString } else name }
      
      val ids = ev.tableColumn("TotalScoreTable", "PairId")
      val shortNames = ev.tableColumn("TotalScoreTable", "Names") map shortName
      
      new HashMap[String, String] ++ (ids zip shortNames)
   }
   
   /***
    * filterHeader constructs a set which shows which items in the resp. data should
    * be kept 
    * */
   
   private def filterSet(direction: String): HashSet[String] = {
      HashSet("Board", "Contract", "Declarer", "PlayerId_"+direction,
           "Result", "Lead", "Score_"+direction, "IMP_"+direction,"MP_"+direction, "Percentage_"+direction)
   }
  
   
   def data(id: String): Array[Array[String]] = {
      
      val pbnData = events map (_.score(id))
      
      def dataWithBoards(pbnData: List[List[String]]): List[List[String]] = { 
         val zip = boards zip pbnData
         def pairing(t: Tuple2[String, List[String]]): List[String] = t._1::t._2 
      	 zip map pairing }
            
      /* preserves only data that is relevant to the pair with the id  */      
      def filterAllData(id: String, data: List[List[String]]): List[List[String]] = {
        par("pbnheader", pbnHeader)
    	  def filterLine(id: String)(line: List[String]): List[String] = {
    	     val flt = dataFilter(id, line)
    	     val zip = (line zip flt) filter (_._2)
    	     val x = (zip unzip)._1 
           x updated(0, indexToPos(flt)) // put seat N, E, S, W instead of id
    	  }
    	  val l = data map filterLine(id)
        l
        
      }
     
      def replaceIdByName(idInd: Int)(l: List[String]): List[String] = {
         l updated(idInd, idToNames(l(idInd)))
      }
      
      if (pbnData.flatten.nonEmpty) {
      
    	  val filteredData = filterAllData(id, pbnData)

    			  // add board numbers 
    			  val boarded = dataWithBoards(filteredData)

    			  boarded map (_.toArray) toArray
      } else Array()
     
   }
   
   // print tools
   
   def pllist(h: String, ll: List[List[String]]) = {
     println(h)
     for (l <- ll) {
       for (i <- l)
         print(i+" ")
       println
     }
   }
   
   def plist(h: String, l: List[String]) = {
     println(h)
     for (x <- l) 
         print(x+" ")
     println
   }
   
   
   def par(h: String, a:Array[String]) {
     for (i <- a)
       print(i + " ")
     println
   }
   
   def pars(h: String, ll: Array[Array[String]]) = {
     println(h)
     for (l <- ll) {
       for (i <- l)
         print(i+" ")
       println
     }
   }
   def pblist(ar: List[Boolean]) = {
     for (b <- ar)
       print(b + " ")
     println
   }
   
   
}