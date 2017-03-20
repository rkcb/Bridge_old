package com.bridge.resultui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

import scala.bridge.PbnEvent;
import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class D extends Panel {

    protected GridLayout grid = new GridLayout(3, 3);
    protected TableFactory factory;
    protected H[] hands = new H[4];
    protected DealNumber dealNumber;
    protected DealPoints points;
    protected VulZones zones;

    public D(TableFactory f) {
        factory = f;
        // a
        // String pbn = "";
        //
        // String[] hs = pbn.split(" ");
        // for (int i = 0; i<4; i++) {
        // String[] suits = hs[i].split("\\.");
        // hands[i] = new H(suits);
        // }
        // // hands
        // addComponent(hands[0], 1, 0);
        // addComponent(hands[1], 2, 1);
        // addComponent(hands[2], 1, 2);
        // addComponent(hands[3], 0, 1);
        // setComponentAlignment(hands[3], Alignment.MIDDLE_RIGHT);
        // // deal number
        // addComponent(new DealNumber("44"), 1,1);
        // String[] points = {"10", "2", "17", "11"};
        // DealPoints dp = new DealPoints(points);
        // addComponent(dp, 0, 2);
        //
        // VulZones z = new VulZones("NS");
        // addComponent(z);
    }

    public void update(String board) {

        grid.removeAllComponents();
        PbnEvent e = factory.eventByBoard(board);

        addHands(e);
        setContent(grid);
        grid.setMargin(true);
        grid.setSizeFull();
        setSizeUndefined();
        addPoints(e);
        addZones(e);
        addDealNumber(board);
    }

    protected void addHands(PbnEvent e) {

        for (int i = 0; i < 4; i++) {
            hands[i] = new H(e.deal()[i]);
        }
        grid.setWidth("280px");
        grid.setHeight("280px");
        grid.addComponent(hands[0], 1, 0);
        grid.setComponentAlignment(hands[0], Alignment.MIDDLE_CENTER);
        grid.addComponent(hands[1], 2, 1);
        grid.setComponentAlignment(hands[1], Alignment.MIDDLE_CENTER);
        grid.addComponent(hands[2], 1, 2);
        grid.setComponentAlignment(hands[2], Alignment.MIDDLE_CENTER);
        grid.addComponent(hands[3], 0, 1);
        grid.setComponentAlignment(hands[3], Alignment.MIDDLE_RIGHT);
        // grid.setComponentAlignment(hands[3], Alignment.MIDDLE_CENTER);
    }

    protected void addPoints(PbnEvent e) {
        String[] pts = new String[4];
        for (int i = 0; i < 4; i++) {
            pts[i] = e.hcp(i);
        }
        points = new DealPoints(pts);
        grid.addComponent(points, 0, 2);
        grid.setComponentAlignment(points, Alignment.MIDDLE_CENTER);
    }

    protected void addZones(PbnEvent e) {
        zones = new VulZones(e.value("Vulnerable"), e.value("Dealer"));
        grid.addComponent(zones, 0, 0);
        grid.setComponentAlignment(zones, Alignment.MIDDLE_CENTER);
    }

    protected void addDealNumber(String board) {
        dealNumber = new DealNumber(board);
        grid.addComponent(dealNumber, 1, 1);
        grid.setComponentAlignment(dealNumber, Alignment.MIDDLE_CENTER);
    }

    protected void addOptimum() {

    }

}
