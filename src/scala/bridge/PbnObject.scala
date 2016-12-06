package scala.bridge

class PbnObject(val value: String, val header: Array[String], val data: List[List[String]]) {
	
   
   /***
    * checkTableIntegrity checks that header has equally many columns as each data row
    * */
   def checkTableIntegrity(): Boolean = {
	   header != null && data != null match {
	      case true =>  (data find (_ != header.size)).isEmpty
	      case _ => false
	   } 
	}
   
   
   
   
}
