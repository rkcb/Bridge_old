package scala.bridge
import scala.bridge
import java.util.{List => JList}
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions
import scala.collection.convert.WrapAsScala
import java.util.Date
import java.text.SimpleDateFormat
import scala.collection.immutable.HashSet
import scala.collection.convert.WrapAsJava
//import com.vaadin.ui.Table
import java.lang.Object
//import com.vaadin.ui.Notification
import scala.collection.immutable.HashMap


object PbnResult {
   
}

class PbnResult (fileName: String/*jpbn: JList[String]*/){
  
	def events() = PbnEvent.getEventsFromFile(fileName) //PbnEvent.getEvents(pbn)  // pbn must be filtered somewhere
   
	
	
	/***
	 * pairNames returns a HashMap which stores first names for a PairId
	 * note: in the case TotalScoreTable is not found empty HashMap is returned 
	 * */
	
	
	//     val tableFactory = new TableFactory(events()) commented 31.8.05
 
//    def scoreTable(id: String): Table = tableFactory.scoreTableWithBoard(id)
    // commented out on 25.11.2015
//	private def toScalaList(jlist: JList[String]): List[String] = 
//	  jlist match {
//	  case null => null
//	  case _ => WrapAsScala.asScalaBuffer(jpbn).toList
//	}
	  
//	val pbn = toScalaList(jpbn)
	   

	
	def getIdfromTotalScoreTableRow(row: Array[String]): String = {
	   val index = totalScoreTableHeader.indexOf("PairId")
	   row(index)
	}
		     
	/***
	 * dealScoreHeader gets the ScoreTable header found 
	 * */
	
	def scoreTableHeader(): Array[String] = {
	   val ev = events.find(_.objs.isDefinedAt("ScoreTable")).get
	   ev.objs("ScoreTable").header 
	}
	
	
	/***
	 * totalScoreTableData prepends deal numbers to deals
	 * */
	def totalScoreTableData(): JList[Array[String]] = { 
	  val tag = "TotalScoreTable"
	  val dataTmp = events.find(_.objs.isDefinedAt(tag)).get.objs(tag).data 
	  val data = dataTmp map (_.toArray)
	  PbnEvent.listToJList(data)
		
	}
	   
	def totalScoreTableHeader(): Array[String] = {
	  
	   val ev = events.find(_.objs.isDefinedAt("TotalScoreTable")).get
	   ev.objs("TotalScoreTable").header
	}
	   
	/***
	 * totalScoreTable constructs a list representing the TotalScoreTable data rows
	 * */
//	def totalScoreTableData(): JList[Array[String]] = {
//	   val ev = events.find(_.objs.isDefinedAt("TotalScoreTable")).get
//	   def prependDealNumber(ev: PbnEvent): Array[]
//	   val totSc = ev.objs("TotalScoreTable").data map (_.toArray)
//	   PbnEvent.listToJList(totSc)
//	}
	
	def eventDate(): Date = {
	   val ev = events.find(_.objs.isDefinedAt("Date")).get
	   new SimpleDateFormat("yyyy.MM.dd").parse(ev.objs("Date").value)
	}
	
	def eventName(): String = {
	   val ev = events.find(_.objs.isDefinedAt("Date")).get
	   ev.objs("Date").value
	}
	
	/***
	 * fullScoreTableHeader gives the PBN header plus "Board" prepended
	 * */   
	   
	def fullScoreTableHeader(): Array[String] = { 
	   val pbnHeader = scoreTableHeader.toList
	   ("Board"::pbnHeader).toArray
	}
}

