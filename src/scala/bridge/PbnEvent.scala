
package scala.bridge
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import java.util.regex.PatternSyntaxException
import scala.Array.canBuildFrom
import java.util.regex.PatternSyntaxException
import scala.util.matching.Regex
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import java.util.{List=>JList}
import scala.collection.convert.WrapAsJava
import scala.collection.JavaConversions
	
object PbnEvent {
	
//  def main(args: Array[String]) {
//     val events = getEventsFromFile
//     println(events.head.getIdIndex("141"))
//  }
  val scoreIdSet = HashSet("PlayerId_North", "PlayerId_East", "PlayerId_South", "PlayerId_West",
    		 			 "PairId_NS", "PairId_EW", "TeamId_Home", "TeamId_Away") 
   
  def start(tag: String)(s:String) = !("""^\[\s*"""+tag+"""\s+""").r.findFirstIn(s.trim).isEmpty	
  def end(s:String) = !"""^\[\s*|^$""".r.findFirstIn(s.trim).isEmpty
  
  val tag = """^\s*\[""".r
  val table = """^\w+Table$""".r
  val tagValuePair = """^\s*\[\s*(\w+)\s+\"(.*)\"\s*\]\s*""".r
  val dataCell = """\"[\s\S&&[^\"]]+\"|[\S&&[^\"]]+""".r
  val emptyValueOrCommentOrSpace = """^\s*\[\s*\w+\s+\"\"\s*\]\s*$|^\s*%|^\s*$""".r
  
    
  def getEventsFromFile(name: String): List[PbnEvent] = {
		  implicit val isoCodec = scala.io.Codec("ISO-8859-1")
				  val pbn = scala.io.Source.fromFile("/home/esa/pbn/"+name).getLines().toList
          val pbn2 = pbn filter (emptyValueOrCommentOrSpace.findAllMatchIn(_).isEmpty)
				  getEvents(pbn2)
  }
  
   def getFirstEvent(pbn_raw: List[String]): List[PbnEvent] = {
    val pbn = pbn_raw filter (emptyValueOrCommentOrSpace.findAllMatchIn(_).isEmpty)
    pbn match {
      case List() => List()
      case y::ys if start("Event")(y) => {
        val (curr, rest) = ys span (!start("Event")(_))
        List(new PbnEvent(getEvent(curr, new HashMap[String, PbnObject])))
      }
    }
  }
  
  def getEvents(pbn_raw: List[String]): List[PbnEvent] = {
	  val pbn = pbn_raw filter (emptyValueOrCommentOrSpace.findAllMatchIn(_).isEmpty)
	  pbn match {
      case List() => List()
      case y::ys if start("Event")(y) => {
    	  val (curr, rest) = ys span (!start("Event")(_))
    	  new PbnEvent(getEvent(y::curr, new HashMap[String, PbnObject]))::getEvents(rest)
      }
    }
  }

  def listToJList[T](l: List[T]): JList[T] = 
    WrapAsJava.seqAsJavaList(l.toSeq)
  
  // split a value (ie. [tag "value"]) an array of quoteless values 
  def header(value: String): Array[String] = {
    val word = """^[\w_]+""".r
    def toWord(s:String): String =  word.findFirstIn(s).get
	value.split(';') map toWord
  }
  
  def data(s:String): List[String] = {
    s.trim match {
    case "" => List()
    case x => { 
      val (word, rest) = x match {
      case y if y(0) != '"' =>  y span (_ != ' ') // bare word
      case y => { val (w,r) =  y.tail span (_ != '"') // quoted word 
      	(w, r.tail) }
      }
      word::data(rest) }}}
    
  
  def getEvent(pbn: List[String], objs: HashMap[String, PbnObject]): HashMap[String, PbnObject] = {
    pbn match { // pbn always starts with a tag line 
   
      case List() => objs
      case x::xs => {
        val (tableData, rest) = xs span (_.trim.head != '[')
        val tagValuePair(t,v) = x
        val obj = x match { 
        case y if (!xs.isEmpty && tag.findFirstMatchIn(xs(0)).isEmpty) => { // table case
          val hdr = header(v)
          val rws = tableData match {
            case List() => List()
            case _ => tableData map (data) }
            new PbnObject(null, hdr, rws)
        } 
        case _ =>  // value case
          new PbnObject(v, null, null)
        }
        getEvent(rest, objs+(t->obj))
      } 
    }  
  }
 
  /***
   * scoreDB returns a HashMap with ids mapped to corresponding pbn scores
   * */
  
  	def scoreDB(evs: List[PbnEvent]): HashMap[String, List[String]] = {
  	   if (evs.isEmpty || !evs.head.objs.isDefinedAt("ScoreTable")) HashMap() else {
  	     null
  	   }
  	}
  	
  	/***
  	 * idIndices returns the positions of id fields in score lines
  	 * */
  	def idIndices(ev: PbnEvent): List[Int] = {
  	   if (ev.isInstanceOf[PbnEvent] && !ev.header("ScoreTable").isEmpty) {
	  	   val hdr = ev.header("ScoreTable")
	  	   val ind = hdr.indices
	  	   def f(i: Int): Boolean = scoreIdSet.contains(hdr(i))
	  	   (ind filter f).toList
  	   } else List()
  	}
  
  	
   /***
    * lineIds extracts ids from each score line
    * */	
   private def lineIds(data: List[String], idInd: List[Int]): List[String] = {
      if (!idInd.isEmpty) {
         if (idInd.size == idInd.last - idInd.head + 1) { 
            data.slice(idInd.head, idInd.last+1) }
         else { idInd map data }
      } else List()
   } 
   
   
}

class PbnEvent(val objs: HashMap[String, PbnObject]) {
   
  private val idToScoreLinePosition = indexToScores

   /*** 
    * pairint is supposed to take list of ids of each score line and
    * then make pairs of the form (id, index) 
    * */
   def pairing(data: List[List[String]]): List[Tuple2[String, Int]] = {
      def linePairing(t: Tuple2[List[String], Int]): List[Tuple2[String, Int]] = {
         t._1 zip (List.fill(t._1.size)(t._2))
      }
      ((data zipWithIndex) map linePairing) flatten
   }
   
  /***
   * lineIds returns a map of ids in this line 
   * */
  
   def indexToScores(): HashMap[String, Int] = {
     if (objs.isDefinedAt("ScoreTable")) {
    	val idInd = PbnEvent.idIndices(this)
    	val scoreLineIds = data("ScoreTable") map (PbnEvent.lineIds(_, idInd)) 
    	val pairs = pairing(scoreLineIds)
    	new HashMap[String, Int] ++ pairs
     } else HashMap()
   }
   
   def getIdIndex(id: String): Int = {
      idToScoreLinePosition getOrElse(id, -1)
   }
   
   /***
    * boolHeader returns a boolean list in which items show "positions" 
    * of ids in the pbn header
    * */
   
   private def boolHeaderForIds(): List[Boolean] = {
      if (objs.isDefinedAt("ScoreTable")) {
         objs("ScoreTable").header.toList map (PbnEvent.scoreIdSet.contains)
      } else List()
   }

   /***
    * 
    * */
   
//   def mps()
   
   def score(id: String): List[String] = {
      if (objs.isDefinedAt("ScoreTable")) {
         val i = getIdIndex(id)
         if ( i >= 0)
        	 objs("ScoreTable").data(i)
         else List()
      } else List()
   }
   
   /***
    * maxImp returns the maximum imp score from both directions
    * */
   
   def maxImp(): Float = {
    def toNumber(s: String): Float = {
      try {
        s.toFloat
      } catch {
        case t: Throwable => 0
      }
    }
    val l = tableColumn("ScoreTable", "IMP_EW")
    val x = l map toNumber
    val max = x.max
    val min = x.min
    val ll = List(min, max, -min, -max) 
    ll.max
  }
  
  
   
  /***
   * value returns the string inside quotes in e.g. [Deal "South"]
   * @param: tag  
   * */
   
  def value(tag: String): String = 
    if (objs.contains(tag)) objs(tag).value else ""
      
  /***
   * value returns a parsed header of an line of the form  [ScoreTable "..."]
   * @param: tag
   * returns: Array[String]
   * */    
      
  def header(tag: String): Array[String] = 
    if (objs.contains(tag)) objs(tag).header else Array()

  def data(tag: String): List[List[String]] = 
    if (objs.contains(tag)) objs(tag).data else List()
  
    
  /***
   * tableColumn returns a table column corresponding the pbn tag 
   * and pbn header column id -- empty list is returned if the tag 
   * or id does not exist in this table
   * */
    
  def tableColumn(tag: String, rowId: String): List[String] = {
       if (objs.isDefinedAt(tag) && 
             objs(tag).header.indexOf(rowId) != -1) {
          val index = objs(tag).header.indexOf(rowId)
          objs(tag).data map (_(index))
       } 
       else List()
    }
  
  
   /***
    * deal returns the hands separated to suits in the order S, H, D and C
    * */
   def deal(): Array[Array[String]] = {
      val hand = (value("Deal").drop(2) split ' ') map (_ split '.') 
      val decl = value("Deal").head
      val rotations = Array('N','E','S','W')
      
      // permutates hands unless north is the first hand 
      def rotate(amount: Int, ar: Array[Array[String]]): Array[Array[String]] = {
         if (amount != 0) {
    	   val split = ar splitAt amount
    	   split._2 ++: split._1
         } else ar 
      }
      
      rotate(rotations.indexOf(decl), hand)
   }
    	
   def hcp(direction: Int): String = {
      // north = 0, east = 1, south = 2, west = 3
      val dl = deal
      if (dl.isEmpty || direction < 0 || direction > 3) "" else {
         def toPoint(card: Char): Int = 
            card match {
            	case 'A' => 4
	            case 'K' => 3
	            case 'Q' => 2
	            case 'J' => 1
	            case _ => 0
         	}
         
         val hand = dl(direction) mkString("")
         val points = (hand map toPoint).toArray 
         (points sum).toString
      }
   }
  
}