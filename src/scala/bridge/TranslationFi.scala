package scala.bridge
import scala.collection.immutable.HashMap

object TranslationFi {
    private val totalScoreTable = HashMap("Rank"->"Sij", "TotalScoreVP"->"VP", "PlayerId"->"Id", "PairId"->"Id", "TeamId"->"Id",
          "TeamId"->"Id", "TotalScoreMP"->"Kokonais-MP", "TotalScoreIMP"->"Kokonais-IMP", 
          "TotalScoreBAM"->"Kokonais-BAM", "TotalPercentage"->"Kokonais-%",
	     "Name"->"Nimi", "Names"->"Nimet", "TeamName"->"Nimi", "Roster"->"Rosteri", 
	     "ScorePenalty"->"Rangaistus", "Club"->"Kerho", "MP"->"MP")
	     
	private val scoreTable = HashMap(
	      
	      "PlayerId_North"-> "P", "PlayerId_South"-> "E", "PlayerId_East"-> "I", 
	      "PlayerId_West"-> "L", "PairId_NS"->"PE", "PairId_EW"->"IL",
	      "TeamId_Home"->"Koti", "TeamId_Away"->"Vieras", 
	      
	      "VP_Home"->"VP, koti", "VP_Away"->"VP, vieras",
	      "Round"->"Kierros", "Board"->"Jako", "Contract"-> "Sit", "Declarer"->"Pv",
           "Result"-> "Tik", "Lead"->"Lk", "Score_NS"->"Pisteet", "Score_EW"->"Pisteet", 
        
           "Score_North"->"Pisteet", "Score_South"->"Pisteet", "Score_East"->"Pisteet", 
           "Score_West"->"Pisteet", "BAM_North"->"BAM", "BAM_South"->"BAM", "BAM_East"->"BAM",
           "BAM_West"->"BAM", "BAM_NS"->"BAM, PE", "BAM_EW"->"BAM, IL",
           "IMP_North"->"IMP", "IMP_South"->"IMP", "IMP_East"->"IMP", "IMP_West"->"IMP", 
           "IMP_NS"->"IMP", "IMP_EW"->"IMP", "Percentage_North"->"%", "Percentage_South"->"%",
           "Percentage_East"->"%","Percentage_West"->"%",
           "Percentage_NS"->"%", "Percentage_EW"->"%")
    
    
	private val comparisonTable = HashMap(
	      "PlayerId_North"-> "P", "PlayerId_South"-> "E", "PlayerId_East"-> "I", 
	      "PlayerId_West"-> "L", "PairId_NS"->"PE", "PairId_EW"->"IL",
	      
	      "Board"->"Jako", "Contract"-> "Sit", "Declarer"->"Pv",
           "Result"-> "Tik", "Lead"->"Lk", 
           
           "Score_NS"->"Pisteet, PE", "Score_EW"->"Pisteet, IL", "MP_North"->"MP, PE", 
           "MP_South"->"MP, PE", "MP_East"->"MP, IL", "MP_West"->"MP, IL", 
           "IMP_North"->"IMP, PE", "IMP_South"->"IMP, PE", "IMP_East"->"IMP, IL", "IMP_West"->"IMP, IL", 
           "IMP_NS"->"IMP, PE", "IMP_EW"->"IMP, IL", "Percentage_North"->"%, PE", "Percentage_South"->"%, IL",
           "Percentage_East"->"%, IL","Percentage_West"->"%, IL",
           "Percentage_NS"->"%, PE", "Percentage_EW"->"%, IL",
           
           "BAM_NS"->"BAM, PE", "BAM_EW"->"BAM, IL", 
           "BAM_North"->"BAM, PE", "BAM_South"->"BAM, PE", "BAM_East"->"BAM, IL",
           "BAM_West"->"BAM, IL")
    
           
    def total(s: String): String = totalScoreTable.getOrElse(s, s)
    
    def score(s: String): String = scoreTable.getOrElse(s, s)
    
    def comparison(s: String): String = comparisonTable.getOrElse(s, s)
    
    
	      
}