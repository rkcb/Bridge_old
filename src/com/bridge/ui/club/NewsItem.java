package com.bridge.ui.club;

import org.pegdown.PegDownProcessor;
import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.ui.BridgeUI;
import com.bridge.ui.markdown.LayoutBuilder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class NewsItem extends VerticalLayout {

    protected Panel panel;
    protected MenuBar menu = new MenuBar();
    protected Object newsId = null;
    protected NewsList parent = null;
    protected PegDownProcessor peg = null;
    protected CssLayout cssl = null;
    protected boolean readOnly = true;
    protected String header = "?";

    public NewsItem(NewsList parent, Object newsId, PegDownProcessor p) {
        readOnly = parent.isReadOnly();
        peg = p;
        this.newsId = newsId;
        this.parent = parent;
        if (!readOnly) {
            addMenu();
        }

        createContents();
    }

    public Object id() {
        return newsId;
    }

    /***
     * createContents creates a layout and the contents for an existing news
     */

    protected void createContents() {
        LayoutBuilder b = new LayoutBuilder(newsId, peg);
        CustomLayout layout = b.createVaadinCustomLayout();
        String club = BridgeUI.user.getPlayerClubName();
        club = club == null ? "" : club;
        Label newsOwner = new Label(club);
        newsOwner.addStyleName("newsowner");
        cssl = new CssLayout(newsOwner, layout);
        cssl.setStyleName("newsitem");
        addComponent(cssl);
    }

    public void recreateContents() {
        removeComponent(cssl);
        createContents();
    }

    /***
     * editable sets whether the item is editable or not
     */

    public void editable(boolean state) {
        menu.setVisible(state);
    }

    protected void remove() {
        parent.removeComponent(this);
        NewsManager.remove(newsId);
    }

    protected void addMenu() {
        menu.addItem("Remove",
                selectedItem -> ConfirmDialog.show(getUI(), "Please Confirm:",
                        "Delete news", "Yes", "No",
                        (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                            if (dialog.isConfirmed()) {
                                remove();
                            }
                        }));

        menu.addItem("Edit", selectedItem -> parent.edit(NewsItem.this));

        setSizeUndefined();
        menu.setStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        addComponents(menu);
        setComponentAlignment(menu, Alignment.MIDDLE_RIGHT);
    }

}
