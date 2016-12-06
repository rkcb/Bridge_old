package scala.bridge 

import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._

/***
 * MPTools extracts federation codes and master points from the PbnEvent which 
 * must contain pbn TotalScoreTable
 * */

class MPTools(ev: PbnEvent) {
  
  def individualIds(): List[List[java.lang.String]] = {
		  val col = ev.tableColumn("TotalScoreTable", "MemberID")
      for (i <- col) yield List(i)
  } 
  
  def pairIds(): List[List[java.lang.String]] = {
		  val xs = ev.tableColumn("TotalScoreTable", "MemberID1")
      val ys = ev.tableColumn("TotalScoreTable", "MemberID2") 
      def toPair(t:Tuple2[String, String]): List[String] = {
        List(t._1, t._2)  
      }
      xs zip ys map toPair
  }

  def teamIds(): List[List[java.lang.String]] = {
      val rDetails = ev.tableColumn("TotalScoreTable", "RosterDetails")
      
      /* ids finds all non-overlapping integer strings */
      def ids(s: java.lang.String): List[String] = {
        val re = """\d+""".r
        (re findAllIn s).toList
      }
      rDetails map ids 
  }
  
  /***
   * mps returns a list of master points from top to bottom
   * in other words "MP" column from pbn TotalScoreTable
   * */
  
  def mps(): List[java.lang.Double] = {
    val ps = ev.tableColumn("TotalScoreTable", "MP")
    
    def toDouble(x: java.lang.String):java.lang.Double= {
      val re = """\d+.{0,1}\d*""".r
    	val r = re findFirstIn x
      val y = r.getOrElse("0")
      y.toDouble
    }
    ps map toDouble
  }
  
  def getCompetitionType(): java.lang.String = {
		  ev.header("TotalScoreTable") match { 
		  case x if x.contains("PlayerId") => "individual"
		  case x if x.contains("PairId") => "pair"
		  case x if x.contains("TeamId") => "team"
		  case _ => ""
		  }
  } 
  
  /***
   * mpsEarned builds a hash map where the fed code is the key and mps for this player
   * is the value 
   * */
  
  def mpsEarned(): java.util.Map[java.lang.String, java.lang.Double]= {
    val t = getCompetitionType        
    val ids = t match {
      case "invidual" => individualIds 
      case "pair" => pairIds 
      case "team" => teamIds 
      case _ => List()
    }
    // copy "unit" average mp to a list filled with that average 
    val playerMps = for (l <- mps zip ids) yield List.fill(l._2.size){new java.lang.Double(l._1/l._2.size)}
    // now ids are in 1-1 relation to average mps 
    Map[java.lang.String, java.lang.Double]() ++ (ids.flatten zip playerMps.flatten)
  }
  
  def posMpsEarned(): java.util.Map[java.lang.String, java.lang.Double] = {
    def f(t: Tuple2[String, java.lang.Double]): Boolean = { t._2 > 0 }
    mapAsScalaMap(mpsEarned).filter(f)
  }
    
  /***
   * allIds returns all fed codes as a Java list
   * */
  
  def allIds(): java.util.List[java.lang.String] = {
    val t = getCompetitionType        
    val ids = t match {
      case "invidual" => individualIds 
      case "pair" => pairIds 
      case "team" => teamIds 
      case _ => List()
    }
    val jlist = new java.util.ArrayList[java.lang.String]()
    val ids2 = asJavaCollection(ids flatten)
    jlist.addAll(ids2)
    jlist
  }
  
  
  
  
//  def pairIds: List[String]
}

