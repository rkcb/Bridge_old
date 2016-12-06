package com.bridge.ui.markdown;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.HtmlItem;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MarkdownMenu extends HorizontalLayout {

    protected MenuBar menu = new MenuBar();
    protected TextArea area;
    protected HashSet<String> fileNames;
    protected String resourcePath;
    protected ComboBox topics;
    protected C<Club> cs = new C<>(Club.class);
    protected MarkdownEditor editor = null;

    public MarkdownMenu(final TextArea ta, HashSet<String> names,
            final String resourcePath, MarkdownEditor ed) {
        this.resourcePath = resourcePath;
        area = ta;
        area.setWidth("500px");
        fileNames = names;
        menu.setAutoOpen(true);
        editor = ed;
        MenuItem item = menu.addItem("Header", null);

        item.setDescription("Add a header");

        item.addItem("Header 1", selectedItem -> Markdown.insert(area, "# "));

        item.setDescription("Add a main header");

        item.addItem("Header 2", selectedItem -> Markdown.insert(area, "## "));

        item.setDescription("Add a section header");

        item.addItem("Header 3", selectedItem -> Markdown.insert(area, "### "));

        item = menu.addItem("Bar",
                selectedItem -> Markdown.insert(area, "\n---\n\n"));

        item = menu.addItem("Text Format", null);
        item.addItem("Bold", selectedItem -> Markdown.insert(area, "**bold**"));

        item.addItem("Italic",
                selectedItem -> Markdown.insert(area, "*italic*"));

        item.setDescription("Add a horizontal bar");

        item = menu.addItem("Image", selectedItem -> {

            ImageDetailsWindow w = new ImageDetailsWindow(area, resourcePath,
                    fileNames);
            getUI().addWindow(w);
        });

        MenuItem item2 = menu.addItem("List", null);

        item2.addItem("Unordered List",
                selectedItem -> Markdown.insert(area, "- "));
        item2.setDescription("Add an unordered list");

        item2.addItem("Ordered List",
                selectedItem -> Markdown.insert(area, "1. "));
        item2.setDescription("Add an ordered list");

        item = menu.addItem("Link", null);

        item2 = item.addItem("Ordinary", selectedItem -> {
            OrdinaryLinkWindow w = new OrdinaryLinkWindow(area);
            getUI().addWindow(w);
        });

        item2 = item.addItem("Document Link", selectedItem -> {
            DocumentLinkDetailsWindow w = new DocumentLinkDetailsWindow(
                    resourcePath, fileNames, area);
            getUI().addWindow(w);
        });

        item2.setDescription("Link to a document");

        item2 = item.addItem("Email Link", selectedItem -> {
            EmailLinkWindow w = new EmailLinkWindow(area);
            getUI().addWindow(w);
        });

        item2.setDescription("Send mail to a recipient");

        item = menu.addItem("Edit Topics", selectedItem -> {
            // create a topics window to add, edit and delete topics

            Window w = new Window("Edit Topics");
            w.setModal(true);
            w.center();

            addTopicsEditor(w);

            getUI().addWindow(w);
        });

        addComponent(menu);

        createTopics(); // to choose or add a topic for a news

        addComponent(topics);
        setSpacing(true);
    }

    /***
     * initTopic initializes topic and updates it to the DB whenever user
     * changes its value to a valid one
     */

    public void initTopics(Object htmlId) {
        C<HtmlItem> hs = new C<>(HtmlItem.class);
        topics.setValue(hs.get(htmlId).getTopic());
        topics.addValueChangeListener(listener -> {
            if (topics.isValid()) {
                hs.set(htmlId, "topic", topics.getValue());
            }
        });
    }

    public boolean isTopicsValid() {
        return topics.isValid();
    }

    /***
     * createTopics creates a topics selector which can add a new topic Note: a
     * topic must contain two non-space letters
     */

    private void createTopics() {

        Object cid = BridgeUI.user.getCurrentClubId();
        @SuppressWarnings("unchecked")
        Set<String> s = (Set<String>) cs.get(cid, "topics");

        topics = new ComboBox();
        topics.addItems(s);
        topics.setImmediate(true);
        topics.setInputPrompt("Set or add a topic");
        topics.setDescription("To add: write a topic and press enter");
        topics.setRequired(true);
        topics.setNewItemsAllowed(true);
        topics.addItemSetChangeListener(listener -> {
            @SuppressWarnings("unchecked")
            Set<String> s2 = new HashSet<>(
                    (Collection<? extends String>) topics.getItemIds());
            cs.set(cid, "topics", s2);
        });
        topics.addValidator(value -> {
            String s1 = value == null ? "" : ((String) value).trim();
            if (s1.isEmpty() || s1.length() < 2) {
                throw new InvalidValueException("Too few letters!");
            }
        });
    }

    private void addTopicsEditor(Window w) {

        w.setClosable(false);
        ComboBox topics2 = createTopics2();
        TextField newName = createNewName();

        Button rename = new Button("Rename");
        rename.addClickListener(listener -> renameTopic(topics2, newName));
        Button remove = new Button("Delete");
        remove.addClickListener(listener -> {
            if (topics2.getValue() != null) {
                deleteTopic(topics2);
            }
        });
        Button done = new Button("Done");
        done.addClickListener(listener -> done(topics2.getItemIds(), w));
        HorizontalLayout hl0 = new HorizontalLayout(topics2, newName);
        hl0.setSpacing(true);
        HorizontalLayout hl = new HorizontalLayout(rename, remove, done);
        hl.setSpacing(true);
        EVerticalLayout vl = new EVerticalLayout(hl0, hl);
        vl.setSizeUndefined();
        w.setContent(vl);
    }

    private ComboBox createTopics2() {
        ComboBox topics2 = new ComboBox("Topics");
        Object cid = BridgeUI.user.getCurrentClubId();
        @SuppressWarnings("unchecked")
        Set<String> s = (Set<String>) cs.get(cid, "topics");
        topics2.addItems(s);
        topics2.setInputPrompt("Topic's Name");
        topics2.setDescription("Add a topic by pressing enter");
        topics2.setNewItemsAllowed(true);
        topics2.setTextInputAllowed(true);
        topics2.setRequired(true);
        topics2.setFilteringMode(FilteringMode.CONTAINS);
        topics2.setImmediate(true);
        topics2.addValidator(value -> {
            String s1 = value == null ? "" : ((String) value).trim();
            if (s1.isEmpty() || s1.length() < 2) {
                throw new InvalidValueException("Too few letters!");
            }
        });

        return topics2;
    }

    private TextField createNewName() {
        TextField newName = new TextField("Topic's New Name");
        newName.setValidationVisible(false);
        newName.addValidator(value -> {
            String s = value == null ? "" : ((String) value).trim();
            if (s.isEmpty() || s.length() < 2) {
                throw new InvalidValueException("Too few letters!");
            }
        });
        newName.addTextChangeListener(
                listener -> newName.setValidationVisible(true));

        return newName;
    }

    private void deleteTopic(ComboBox topics2) {
        Object cid = BridgeUI.user.getCurrentClubId();
        if (!topics2.isValid() || cid == null) {
            return;
        }

        String topic = (String) topics2.getValue();
        @SuppressWarnings("unchecked")
        Set<String> oldTopics = (Set<String>) cs.get(cid, "topics");
        if (oldTopics.contains(topic)) {
            oldTopics.remove(topic);
            cs.set(cid, "topics", oldTopics);

            C<HtmlItem> hs = new C<>(HtmlItem.class);
            hs.filterEq("club", cid);
            hs.filterEq("topic", topic);
            for (Object id : hs.ids()) {
                hs.rem(id); // is this sufficient?
            }
        }
        topics2.removeItem(topic);
    }

    @SuppressWarnings("unchecked")
    private void renameTopic(ComboBox topics2, TextField newName) {
        if (!topics2.isValid() || !newName.isValid()) {
            return;
        }

        Object cid = BridgeUI.user.getCurrentClubId();
        if (cid == null) {
            return;
        }

        Set<String> oldTopics = (Set<String>) cs.get(cid, "topics");

        String topic = (String) topics2.getValue();
        String name = newName.getValue();

        if (oldTopics.contains(topics) && !topic.equals(name)) {
            // change topic name and the news under this topic

            C<HtmlItem> hs = new C<>(HtmlItem.class);
            hs.filterEq("club", BridgeUI.user.getCurrentClubId());
            hs.filterEq("topic", topic);
            for (Object id : hs.ids()) {
                hs.set(id, "topic", name); // is this sufficient?
            }

        } else if (!topic.equals(name)) { // renaming a new topic -- no need to
                                          // update DB yet
            topics2.removeItem(topic);
            topics2.addItem(name);
        }

    }

    @SuppressWarnings("unused")
    private void renameTopicList(String oldName, String newName) {
        C<Club> cs = new C<>(Club.class);
        @SuppressWarnings("unchecked")
        Set<String> ts = (Set<String>) cs.get(BridgeUI.user.getCurrentClubId(),
                "topics");
        ts.remove(oldName);
        ts.add(newName);
    }

    /***
     * done ends topics editing
     *
     * @param collection
     */

    @SuppressWarnings("unchecked")
    private void done(Collection<?> ids, Window w) {

        Set<String> s = new HashSet<>((Collection<? extends String>) ids);
        Object cid = BridgeUI.user.getCurrentClubId();
        if (cid == null) {
            w.close();
            return;
        }

        topics.removeAllItems();
        topics.addItems(s);

        cs.set(cid, "topics", s);

        w.close();
    }

    public void focusTopic() {
        topics.focus();
    }

    /***
     * getTopic returns the topic of this news
     */

    public String getTopic() {
        return (String) topics.getValue();
    }

    /***
     * setPath sets the path to which uploaded files are stored
     */

    public void setPath(String path) {
        resourcePath = path;
    }
}
