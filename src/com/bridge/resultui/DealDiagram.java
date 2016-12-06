package com.bridge.resultui;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import scala.bridge.PbnEvent;
import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class DealDiagram extends Panel {

    private GridLayout gLayout = new GridLayout(3, 3);
    // private VerticalLayout topLeft = new VerticalLayout();
    // private OptimumScoreTable optimumTable = new Op

    private String[] points = new String[4];
    private TextField optimumScore = new TextField("");
    // private OptimumScoreTable optimumTable = null;
    // private TextField vulZones = new TextField("");
    private TextArea dealer = new TextArea();
    // private String vulText = "V: "; // TODO: localization
    // private String dealerText = "Jak: ";
    private TableFactory factory = null;

    // 0 = North, 1 = East, 2 = South, 3 = West
    private HandAsSymbols[] hands = new HandAsSymbols[4];

    public DealDiagram(TableFactory factory) {

        super();

        if (factory != null) {
            this.factory = factory;

            // size of the grid
            gLayout.setWidth(250, Unit.PIXELS);
            gLayout.setHeight(250, Unit.PIXELS);

            // topLeft

            for (int i = 0; i < 4; i++) {
                hands[i] = new HandAsSymbols();
                hands[i].setSizeFull();
            }

            gLayout.addComponent(dealer, 0, 0);

            dealer.setSizeFull();

            gLayout.addComponent(hands[0], 1, 0);
            gLayout.addComponent(hands[1], 2, 1);
            gLayout.addComponent(hands[2], 1, 2);
            gLayout.addComponent(hands[3], 0, 1);

            // gLayout.setColumnExpandRatio(0,);
            // for (int i=0; i<4; i++) {
            // hands[i].setHeight(h+"px");
            // hands[i].setWidth(w+"px");
            // }
            setContent(gLayout);

            for (int i = 0; i < 4; i++) {
                points[i] = new String();
            }

            // dealer and zones are in top right

            // points are in bottom left

            // optimum contract and score are in top right

            // if optimum score table exists it is in bottom right
            // if (factory.optimumResultTableSupported()) {
            // optimumTable = new OptimumScoreTable(factory);
            // gLayout.addComponent(optimumTable, 2,2);
            // }

            update("1");
        } else {
            throw new NullPointerException("DealDiagram: factory is null");
        }
    }

    private void setHands(String[][] cards) {
        for (int i = 0; i < cards.length; i++) {
            hands[i].setSuits(cards[i]);
        }
    }

    public void update(String board) {
        // hcp distribution
        PbnEvent ev = factory.eventByBoard(board);
        if (ev != null) {
            // hcp update
            for (int i = 0; i < 4; i++) {
                points[i] = ev.hcp(i);
            }
            // update cards
            setHands(ev.deal());
            //
            // // update declarer, vul. zones and optimum data
            optimumScore.setValue(ev.value("OptimumScore"));
            // vulZones.setValue(vulText+ev.value("Vulnerable"));
            // dealer.setValue(dealerText+ev.value("Dealer"));
            //
            // if (optimumTable != null) {
            // optimumTable.score(ev.value("Board"));
            // }
        }
    }

}
