package com.pbn.pbnjson;

import java.util.LinkedList;
import java.util.List;

public class JsonEvent {
	// See PBN v.2 specification
	private String event;
	private String site;
	private String date;
	private String board;
	private String west;
	private String north;
	private String east;
	private String south;
	private String dealer;
	private String vulnerable;
	private String scoring;
	private String declarer;
	private String contract;
	private String result;
	private String competition;
	private String optimumScore;
	private String optimumContract;

	private LinkedList<LinkedList<String>> deal; // north, east, south, west
	private JsonTotalScoreTable totalScoreTable;
	private JsonScoreTable scoreTable;
	private JsonOptimumResultTable optimumResultTable;

	/**
	 * initialize builds help variables in totalScoreTable and scoreTable if
	 * they exist
	 */
	public void initialize() {
		if (competition != null && scoreTable != null
				&& totalScoreTable != null) {
			scoreTable.initialize(competition);
			totalScoreTable.initialize(competition);
		}
	}

	/***
	 * initialize copy
	 *
	 * @param to
	 *            copy to this
	 * @param from
	 *            copy to this
	 */
	public static void initialize(JsonEvent to, JsonEvent from) {
		if (to != null && from != null && from.scoreTable != null) {
			JsonScoreTable.initialize(to.getScoreTable(), from.getScoreTable());
		}
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String getWest() {
		return west;
	}

	public void setWest(String west) {
		this.west = west;
	}

	public String getNorth() {
		return north;
	}

	public void setNorth(String north) {
		this.north = north;
	}

	public String getEast() {
		return east;
	}

	public void setEast(String east) {
		this.east = east;
	}

	public String getSouth() {
		return south;
	}

	public void setSouth(String south) {
		this.south = south;
	}

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getVulnerable() {
		return vulnerable;
	}

	public void setVulnerable(String vulnerable) {
		this.vulnerable = vulnerable;
	}

	/***
	 * getDeal
	 *
	 * @return hands in the NESW order and SHDC
	 */
	public LinkedList<LinkedList<String>> getDeal() {
		return deal;
	}

	public void setDeal(LinkedList<LinkedList<String>> deal) {
		this.deal = deal;
	}

	public String getScoring() {
		return scoring;
	}

	public void setScoring(String scoring) {
		this.scoring = scoring;
	}

	public String getDeclarer() {
		return declarer;
	}

	public void setDeclarer(String declarer) {
		this.declarer = declarer;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCompetition() {
		return competition;
	}

	public void setCompetition(String competition) {
		this.competition = competition;
	}

	public String getOptimumScore() {
		return optimumScore;
	}

	public void setOptimumScore(String optimumScore) {
		this.optimumScore = optimumScore;
	}

	public String getOptimumContract() {
		return optimumContract;
	}

	public void setOptimumContract(String optimumContract) {
		this.optimumContract = optimumContract;
	}

	public JsonTotalScoreTable getTotalScoreTable() {
		return totalScoreTable;
	}

	public void setTotalScoreTable(JsonTotalScoreTable totalScoreTable) {
		this.totalScoreTable = totalScoreTable;
	}

	public JsonScoreTable getScoreTable() {
		return scoreTable;
	}

	public void setScoreTable(JsonScoreTable scoreTable) {
		this.scoreTable = scoreTable;
	}

	public JsonOptimumResultTable getOptimumResultTable() {
		return optimumResultTable;
	}

	public void setOptimumResultTable(
			JsonOptimumResultTable optimumResultTable) {
		this.optimumResultTable = optimumResultTable;
	}

	/***
	 * absDouble parse double
	 *
	 * @param o
	 *            any Double
	 * @return parsed absolute value of double; return -1 if parsing fails
	 */
	private double absDouble(Object o) {
		double d = -1;
		try {
			d = (Double) o;
			d = Math.abs(d);
		} catch (Exception e) {
		}
		return d;
	}

	/***
	 * maxIMP
	 *
	 * @return max imp in this event and -1 if not possible
	 */
	public double maxIMP() {
		if (scoreTable != null && scoreTable.header != null
				&& scoreTable.header.indexOf("IMP_EW") >= 0) {
			return scoreTable.column("IMP_EW").stream()
					.mapToDouble(i -> absDouble(i)).max().orElse(-1);
		}
		return -1;
	}

	private int rankPoint(Character ch) {
		Character.toUpperCase(ch);
		if (ch.equals('A')) {
			return 4;
		} else if (ch.equals('K')) {
			return 3;
		} else if (ch.equals('Q')) {
			return 2;
		} else if (ch.equals('J')) {
			return 1;
		} else {
			return 0;
		}
	}

	private int suitPoints(String suit) {

		int sum = 0;
		for (int i = 0; i < suit.length(); i++) {
			sum += rankPoint(suit.charAt(i));
		}

		return sum;
	}

	/***
	 * hcp compute points for a hand
	 * 
	 * @param direction
	 *            (0 = north, 1 = east, 2 = south, 3 = west)
	 * @return sum of hcp points
	 */
	public String hcp(Integer direction) {
		if (deal != null && !deal.isEmpty()) {
			List<String> hand = deal.get(direction);
			int sum = 0;
			for (String suit : hand) {
				sum += suitPoints(suit);
			}
			return Integer.toString(sum);
		} else {
			return "";
		}
	}

}
