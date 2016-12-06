package pbn

import scala.bridge.PbnEvent
import scala.util.{Try, Success, Failure}

/***
 * PlayerUnit stores various information from a data row of 
 * TotalScoreTable  
 * */

class MasterPoints(ev: PbnEvent) {
  
  def individualIds(): List[String] = {
		  ev.tableColumn("TotalScoreTable", "MemberID") 
  } 
  
  def pairIds(): List[List[String]] = {
		  val xs = ev.tableColumn("TotalScoreTable", "MemberID1")
      val ys = ev.tableColumn("TotalScoreTable", "MemberID2") 
      def toPair(t:Tuple2[String, String]): List[String] = {
        List(t._1, t._2)  
      }
      xs zip ys map toPair
  }

  def teamIds(): List[List[String]] = {
      val rDetails = ev.tableColumn("TotalScoreTable", "RosterDetails")
      
      /* ids finds all non-overlapping integer strings */
      def ids(s: String): List[String] = {
        val re = """\d+""".r
        (re findAllIn s).toList
      }
      rDetails map ids 
  }
  
  /***
   * mps returns a list of master points from top to bottom;
   * if no MP column was found an empty list is returned
   * */
  
  def mps(): List[Float] = {
    val ps = ev.tableColumn("TotalScoreTable", "MP")
    
    def toFloat(x: String):Float = {
      val re = """\d+.{0,1}\d*""".r
    	val r = re findFirstIn x
      val y = r.getOrElse("0")
      y.toFloat      
    }
    ps map toFloat
  }
  
  def posMps(): List[Float] = {
    mps filter (_ > 0)
  }
  
    
    
  
  
  
  
//  def pairIds: List[String]
}

