package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class JsonTotalScoreTable extends JsonTable {

    private transient HashSet<String> numberColumns;
    private transient HashSet<String> headerItems;

    public JsonTotalScoreTable(List<String> header, List<List<String>> rows) {
        super(header, rows);
        if (numberColumns == null) {
            numberColumns = new HashSet<>(20);

            Collections.addAll(numberColumns, "Rank", "TotalScoreMP",
                    "TotalPercentage", "TotalScore", "TotalIMP", "TotalMP",
                    "NrBoards", "MeanScore", "MeanIMP", "MeanMP");
        }
        if (headerItems == null) {
            headerItems = new HashSet<>(20);
            Collections.addAll(headerItems, "Rank", "PlayerId", "PairId",
                    "TeamId", "TotalScoreMP", "TotalScoreVP", "TotalScoreIMP",
                    "TotalScoreBAM", "TotalPercentage", "Name", "Names",
                    "TeamName", "Roster", "ScorePenalty", "Club", "MP");
        }
    }

    /***
     * filterTable removes header and the corresponding row items which are not
     * contained in headerItems
     */
    public void filterTable() {
        if (header == null || rows == null) {
            return;
        }

        // indexes to keep
        List<Integer> indexes = new LinkedList<>();

        for (String item : header) {
            for (int i = 0; i < header.size(); i++) {

            }
        }
    }

}
