package com.bridge.ui.club;

import org.pegdown.PegDownProcessor;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.HtmlItem;
import com.bridge.ui.markdown.Markdown;
import com.bridge.ui.markdown.MarkdownEditor;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class NewsList extends VerticalLayout {

    protected PegDownProcessor peg = Markdown.pegProcessor();
    protected Window window = null;
    protected Object newsId = null;
    protected NewsItem newsItem = null;
    protected boolean editingOld = false;
    protected boolean readOnly = true;
    protected C<Club> cs = new C<>(Club.class);

    public NewsList(boolean disableEditing) {
        readOnly = disableEditing;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    /***
     * edit creates an editor the item
     */

    public void edit(NewsItem item) {

        editingOld = true;
        newsItem = item;
        newsId = item.id();
        String path = ResourceManager.getNewsDirectory(newsId);
        MarkdownEditor editor = new MarkdownEditor(path, newsId, this);
        addWindow("Edit News", editor);
    }

    /***
     * create creates an editor and a new NewsItem data to get the details for
     * news
     */

    public void create(Object clubId) {

        editingOld = false;
        if (getComponentCount() == 0) {
            addComponent(new VerticalLayout());
        }

        newsId = NewsManager.create(clubId);
        String path = ResourceManager.getNewsDirectory(clubId, newsId);
        MarkdownEditor editor = new MarkdownEditor(path, newsId, clubId, this);
        addWindow("Create News", editor);
    }

    /***
     * editingDone is called after user hits "Done" in editor
     */

    public void editingDone(boolean saveNews) {

        if (!editingOld) {
            if (saveNews) {
                addComponent(new NewsItem(this, newsId, peg));
            } else {
                NewsManager.remove(newsId);
            }
        } else {
            newsItem.recreateContents();
        }

        endEditing();
    }

    public void endEditing() {

        newsId = null;
        newsItem = null;

        window.close();
    }

    private void addWindow(String string, MarkdownEditor editor) {
        window = new Window("Create News", editor);
        getUI().addWindow(window);
        window.center();
        window.setModal(true);
    }

    /***
     * filterNews adds all news for the given club -- clubId is null means any
     * club suits
     */

    public void filterAndAddNews(Object clubId) {
        cs.removeFilters();
        if (clubId != null) {
            cs.filterEq("id", clubId);
        }

        if (!cs.empty()) {
            Club cl = cs.at(0);
            removeAllComponents();
            addComponent(new VerticalLayout()); // circumvents a weird spacing
                                                // of the 1. news
            for (HtmlItem i : cl.getNews()) {
                addComponent(new NewsItem(this, i.getId(), peg));
            }
        }
    }

}
