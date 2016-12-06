package com.bridge.ui;

import org.vaadin.maddon.layouts.MVerticalLayout;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Indy;
import com.bridge.database.Player;
import com.bridge.database.Tournament;
import com.bridge.database.User;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class CompetitorsIndy extends MVerticalLayout {

    protected C<Indy> inds;
    protected C<Player> pls = new C<>(Player.class);
    protected Tournament tour;
    protected User user;
    protected BridgeEvent event;
    protected ETable table;
    protected Window window = new Window();
    protected TextField pw = new TextField("Password");
    protected Button ok;
    private Button add = new Button("Add");
    private Button remove = new Button("Remove");
    protected EHorizontalLayout widgets = new EHorizontalLayout();
    // private Object clickedItemId = null;
    // private FormLayout pwFl = new FormLayout();
    // private int unitSize;
    // private Set<Long> playingIds = new HashSet<>();
    // private ComboBox id = new ComboBox();

    public CompetitorsIndy() {
    }

    protected <T> void createTable(C<T> c, Class<?> entityClass) {
        table = new ETable(null, inds.c());
        table.setSelectable(true);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setPageLength(18);
        c.filterEq("tournament.id", tour.getId());
        table.addItemClickListener(event -> {
            // clickedItemId = event.getItemId();
            remove.setEnabled(true);
            add.setEnabled(false);
            loadClickedItemToEditor();
        });
    }

    private void loadClickedItemToEditor() {

    }

}
