package com.bridge.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PbnFile implements Serializable {

    private static final long serialVersionUID = 2425327782572964802L;

    private String name = "";
    private Boolean masterPoints = false; // this file contains master points
    private Boolean finalResults = false; // indicates whether this file is the
                                          // main result file
                                          // note that not all tours has mp
                                          // point column
    private String json; // the json for JsonEvents
    // maps fed code to master points earned
    // only nonzero cases are stored
    // this is used e.g. when the pbn file is removed
    // and MP registry is updated accordingly
    private Map<String, Double> fedCodeToMps = new HashMap<>();

    private List<String> fileLines = new ArrayList<>(0);

    @Override
    public String toString() {
        return name;
    }

    public PbnFile() {
    }

    public PbnFile(String fileName, Boolean mps, List<String> lines) {
        masterPoints = mps;
        name = fileName;
        fileLines = lines;
    }

    public PbnFile(String fileName, Boolean mps, List<String> lines,
            Map<String, Double> map, String json) {
        masterPoints = mps;
        name = fileName;
        fileLines = lines;
        fedCodeToMps = map;
        this.json = json;
    }

    public PbnFile(String fileName, Boolean mps, List<String> lines,
            Map<String, Double> map, boolean isFinalResults) {
        masterPoints = mps;
        name = fileName;
        fileLines = lines;
        fedCodeToMps = map;
        finalResults = isFinalResults;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFileLines() {
        return fileLines;
    }

    public void setFileLines(List<String> fileLines) {
        this.fileLines = fileLines;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Boolean getMasterPoints() {
        return masterPoints;
    }

    public void setMasterPoints(Boolean masterPoints) {
        this.masterPoints = masterPoints;
    }

    public Map<String, Double> getFedCodeToMps() {
        return fedCodeToMps;
    }

    public void setFedCodeToMps(Map<String, Double> fedCodeToMps) {
        this.fedCodeToMps = fedCodeToMps;
    }

    public Boolean getFinalResults() {
        return finalResults;
    }

    public void setFinalResults(Boolean finalResults) {
        this.finalResults = finalResults;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
