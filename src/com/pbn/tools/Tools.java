package com.pbn.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pbn.ast.Event;
import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.parser.PbnParser;
import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonOptimumResultTable;
import com.pbn.pbnjson.JsonScoreTable;
import com.pbn.pbnjson.JsonTotalScoreTable;
import com.vaadin.server.VaadinService;

public class Tools {

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    static private PbnParser parser = Parboiled.createParser(PbnParser.class);
    static private Gson gson = new GsonBuilder().create();

    /***
     * Event2JsonEvent copy Event information to JsonEvent
     */
    public static JsonEvent event2JsonEvent(Event e) {

        JsonEvent j = new JsonEvent();

        if (e == null) {
            return j;
        }

        if (e.get("Event") != null) {
            j.setEvent(e.get("Event").value());
        }
        if (e.get("Site") != null) {
            j.setSite(e.get("Site").value());
        }
        if (e.get("Date") != null) {
            j.setDate(e.get("Date").value());
        }
        if (e.get("Board") != null) {
            j.setBoard(e.get("Board").value());
        }
        if (e.get("West") != null) {
            j.setWest(e.get("West").value());
        }
        if (e.get("North") != null) {
            j.setNorth(e.get("North").value());
        }
        if (e.get("East") != null) {
            j.setEast(e.get("East").value());
        }
        if (e.get("South") != null) {
            j.setSouth(e.get("South").value());
        }
        if (e.get("Dealer") != null) {
            j.setDealer(e.get("Dealer").value());
        }
        if (e.get("Vulnerable") != null) {
            j.setVulnerable(e.get("Vulnerable").value());
        }
        if (e.get("Deal") != null) {
            j.setDeal(e.get("Deal").hands());
        }
        if (e.get("Scoring") != null) {
            j.setScoring(e.get("Scoring").value());
        }
        if (e.get("Declarer") != null) {
            j.setDeclarer(e.get("Declarer").value());
        }
        if (e.get("Contract") != null) {
            j.setContract(e.get("Contract").value());
        }
        if (e.get("Result") != null) {
            j.setResult(e.get("Result").value());
        }
        if (e.get("Competition") != null) {
            j.setCompetition(e.get("Competition").value());
        }
        if (e.get("ScoreTable") != null) {
            j.setScoreTable(new JsonScoreTable(e.get("ScoreTable").header(),
                    e.get("ScoreTable").rows()));
        }
        if (e.get("TotalScoreTable") != null) {
            j.setTotalScoreTable(
                    new JsonTotalScoreTable(e.get("TotalScoreTable").header(),
                            e.get("TotalScoreTable").rows()));
        }
        if (e.get("OptimumResultTable") != null) {
            j.setOptimumResultTable(new JsonOptimumResultTable(
                    e.get("OptimumResultTable").header(),
                    e.get("OptimumResultTable").rows()));
        }

        return j;
    }

    /***
     * getResult parses the PBN string
     *
     * @param pbn
     *            PBN file as a string
     *
     * @return ParsingResult<PBN> which contains possible errors
     */
    public static ParsingResult<Pbn> getPbnResult(String pbn) {
        ReportingParseRunner<Pbn> runner = new ReportingParseRunner<>(
                parser.Events());
        ParsingResult<Pbn> result = runner.run(pbn);
        return result;
    }

    /***
     * getJson serializes List<JsonEvent> of the parsing result to Json string
     * using Gson
     */
    public static String toJson(ParsingResult<Pbn> result) {
        Events events = (Events) result.resultValue;
        List<JsonEvent> jevents = events.events().stream()
                .map(e -> event2JsonEvent(e)).collect(Collectors.toList());

        return gson.toJson(jevents);
    }

    /***
     * fromJson
     *
     * @param json
     *            string serialized from LinkedList<JsonEvent>
     * @return deserialization
     */
    public static LinkedList<JsonEvent> fromJson(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jarray = jsonParser.parse(json).getAsJsonArray();
        LinkedList<JsonEvent> jsonEvents = new LinkedList<>();
        // this deserialization is a recommended way by Gson documentation
        for (JsonElement e : jarray) {
            jsonEvents.add(gson.fromJson(e, JsonEvent.class));
        }

        return jsonEvents;
    }

    /***
     * pbnStringToJson conterts pbn to json string by first checking the
     * validity of pbn and then converting the pbn to json
     *
     * @return json se
     */
    public static String pbnToJson(String pbn) {
        if (pbn == null || pbn.trim().isEmpty()) {
            return "";
        }

        ParsingResult<Pbn> results = getPbnResult(pbn);
        return results.matched ? toJson(results) : "";
    }

    public static String inputText(String fileName) {
        Charset charset = Charset.forName("ISO-8859-1");
        if (fileName == null) {
            return null;
        }

        String basepath = VaadinService.getCurrent().getBaseDirectory()
                .getAbsolutePath();
        File f = new File(basepath);
        f = f.getParentFile();
        String uri = f.getAbsolutePath() + "/src/resources/" + fileName
                + ".pbn";
        String line = null;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(uri),
                charset)) {

            StringBuilder builder = new StringBuilder("");

            while ((line = reader.readLine()) != null) {

                builder.append(line).append("\r\n");
            }

            line = builder.toString();

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        return line;
    }

    /***
     * rawEvents computes raw JsonEvent list i.e. it does not filter event
     * tables yet
     */
    public static List<JsonEvent> rawEvents(String fileName) {
        String pbn = inputText(fileName);
        return fromJson(toJson(getPbnResult(pbn)));
    }

    // @Test
    // public void testing() {
    // String input = ParserTest.inputText("sm1");
    // // parse pbn string
    // ParsingResult<Pbn> result = getPbnResult(input);
    // assertTrue(result.matched);
    //
    // // convert parsed pbn events to json string
    // String json = toJson(result);
    // assertTrue(json != null && !json.isEmpty());
    //
    // // deserialize json string to List<JsonEvent>
    // List<JsonEvent> jevents = fromJson(json);
    //
    // // test the deserialization
    // assertTrue(jevents != null);
    // assertTrue(jevents.size() == 66);
    // JsonTotalScoreTable t = jevents.get(0).getTotalScoreTable();
    // assertTrue(t != null);
    // assertTrue(!t.getHeader().isEmpty());
    // assertTrue(!t.getRows().isEmpty());
    //
    // }
}
