package scala.bridge


/***
 * PbnSyntaxCheck contains tools to check whether a 
 * text complies pbn syntax or not 
 * note: the text file must not contain comment or "empty" line which 
 * have only white space
 * */

class PbnSyntaxCheck (rawPbn: List[String]){
	/***
	 * Pbn v.2.1 has three line types
	 * 1. Line contains only whitespace or is comment line
	 * 2. Line is of the form [ Tag "..."]
	 * 3. Line is a table row which contains words which 
	 * are like xxx, "xxx", -, 1.3, "1.3", "100"
	 * */
   
   val pbn = rawPbn filter (emptyValueOrCommentOrSpace.findAllMatchIn(_).isEmpty)
   val bracketLines = pbn filter isBracket
   
   val emptyValueOrCommentOrSpace = """^\s*\[\s*\w+\s+\"\"\s*\]\s*$|^\s*%|^\s*$""".r
   val tagValuePair = """^\s*\[\s*(\w+)\s+\"(.*)\"\s*\]\s*$""".r
   val event = """^\s*\[\s*Event\s+\"(.*)\"\s*\]\s*$""".r
   val tag = """^\s*\[""".r
   
   private def isData(s: String) = tagValuePair.findFirstMatchIn(s).isEmpty
   private def isTagValuePair(s: String) = !tagValuePair.findFirstMatchIn(s).isEmpty
   private def isBracket(s: String) = !tag.findFirstMatchIn(s).isEmpty
   private def isTableHeader(s: String) = s match {
      case x if isTagValuePair(x) => x.contains(';') 
      case _ => false
   }
   
   private def isEvent(s: String) = 
       !event.findFirstMatchIn(s).isEmpty
   
   /***
    * checks if the first line starts a table -- table is assumed to have at least one 
    * data row 
    * */
   
   private def isTableHeader(pbn: List[String]): Boolean = {
      pbn match {
         case x if x == x.isEmpty || x.size == 1 => false
         case x::xs if (!xs.isEmpty)=>  
            !tagValuePair.findFirstMatchIn(x).isEmpty &&  
            !tag.findFirstMatchIn(xs.head).isEmpty
      }
   }
   
   private def getEvents(pbn: List[String]): List[List[String]] = {
      def isEvent(s: String): Boolean = {
        if (isTagValuePair(s)) {
           val tagValuePair(t,v) = s
           t == "Event"
        } else false
      }
      
      pbn match {
         case List() => List()
         case _ => {
            val (nonEvents, rest) = pbn span (!isEvent(_))
            (pbn.head::nonEvents)::getEvents(rest)
         }
      }
   }
   
   private def columnCount(count: Int)(s:String): Int = {
    	   s.trim match {
    	   case "" => count
    	   case x => { 
    		   val (word, rest) = x match {
    		   case y if y(0) != '"' =>  y span (_ != ' ') // bare word
    		   case y => { val (w,r) =  y.tail span (_ != '"') // quoted word 
    		   (w, r.tail) }
    		   }
    		   columnCount(count+1)(rest) }}}
   
   def isPbnCorrect(pbn: List[String]): Boolean = {

      /***
       * 1. Check bracket lines' syntax -- line beginning with [ must be 
       *     of the form [ Tag "Value"] 
       * 2. Check that Event line starts the file 
       * 3. Check that the next line after Event line is bracket line
       * */
      
      val (error, rest) = pbn match {
         case List() => (false, List())
         case x::xs if isData(x) => (true, pbn)
         case x::xs if isEvent(x) => if (xs.isEmpty) (true, pbn) 
         								 else (false, xs)
         case x::xs if isTableHeader(x) => xs match {
            case List() => (true, pbn)
            case y => if (isData(y.head)) { 
               val hdrCount = (x count (_ == ';'))+1
               val (data, rst) = xs span isData
               val dataCounts = data map columnCount(0)
               (dataCounts exists (_ != hdrCount), y) // chech consistency
            } else (true, pbn) // no table data
         }
         case x::xs if isTagValuePair(x) => (false, xs) // bracket line which is not event or table
         case _ => (true, pbn) // should not ever occur
         
         
      }
      if (error) false else isPbnCorrect(rest)
   }
	
   
   /***
    * Check that the various result tables have data rows and the header equal length
    * note: pbn bracket lines must be correct -- run check before calling this
    * */

   private def areTablesOk(pbn: List[String]): Boolean = {
	   
      val seekedTables = List("TotalScoreTable", "ScoreTable", "OptimumResultTable")
	  
      def isSeekedTable(pbn: String): Boolean = {
    	   if (isTagValuePair(pbn)) {
             val tagValuePair(t,v) = pbn
             seekedTables.contains(t)  
    	   } else false
      }
      
      def columnCount(count: Int)(s:String): Int = {
    	   s.trim match {
    	   case "" => count
    	   case x => { 
    		   val (word, rest) = x match {
    		   case y if y(0) != '"' =>  y span (_ != ' ') // bare word
    		   case y => { val (w,r) =  y.tail span (_ != '"') // quoted word 
    		   (w, r.tail) }
    		   }
    		   columnCount(count+1)(rest) }}}
      
      def isTableConsistent(hdr: String, data: List[String]): Boolean = {
             val hdrCount = (hdr count (_ == ';')) + 1
             val columnCounts = data map columnCount(0)
             columnCounts exists (_ != hdrCount)
       }
      
       val newPbn = pbn dropWhile (!isSeekedTable(_))
       newPbn match {
          case List() => true
          case x::xs => {
        	 val (data, rest) = xs span isData
        	 isTableConsistent(x, data) match {
        	    case false => false
        	    case _ => areTablesOk(rest)
        	 }
          }
       }
   }
}
