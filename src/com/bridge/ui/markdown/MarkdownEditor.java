package com.bridge.ui.markdown;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.database.C;
import com.bridge.database.HtmlItem;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.club.NewsList;
import com.bridge.ui.club.NewsManager;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import elemental.events.KeyboardEvent.KeyCode;

@SuppressWarnings("serial")
public class MarkdownEditor extends VerticalLayout {

    protected TextArea markdownTa = new TextArea();
    protected RichTextArea richTa = new RichTextArea("Preview");
    protected HashSet<String> fileNames = new HashSet<>(); // contains
                                                           // uploaded
                                                           // file names
    protected String resourcePath;
    protected MarkdownMenu menu;
    protected HorizontalLayout hlayout = new HorizontalLayout();
    protected Button preview = new Button("Preview");
    protected Button save = new Button("Save");
    protected Button cancel = new Button("Cancel");
    protected PegDownProcessor peg;
    protected LayoutBuilder builder;
    protected Object newsId = null;
    protected Object clubId = null;
    protected NewsList news = null;
    protected boolean saveNews = true;
    protected boolean removeNews = false;

    @SuppressWarnings("unchecked")
    public MarkdownEditor(String path, Object htmlId, Object clubId,
            NewsList newsList) {
        // this.topics = BridgeUI.user.getCurrentClub().getTopics();
        news = newsList;
        addPegProcessor();
        resourcePath = path;
        newsId = htmlId;
        C<HtmlItem> is = new C<>(HtmlItem.class);
        fileNames.addAll((Set<String>) is.get(newsId, "fileNames"));
        this.clubId = clubId == null ? is.get(newsId).getClub().getId()
                : clubId;
        doWidgetUISettings(path);
        markdownTa.setValue(is.get(htmlId).getMarkdown());

        menu.initTopics(htmlId);
    }

    /***
     * MarkdownEditor constructor for existing news only
     */

    public MarkdownEditor(String path, Object htmlId, NewsList newsList) {
        this(path, htmlId, null, newsList);
        menu.initTopics(htmlId);
    }

    /***
     * delete all those news whose topic matched name and belong to user's club
     * NOTE: this is needed when news topics are deleted by a topic
     */

    public void deleteTopics(Collection<Object> ids) {

    }

    protected void doWidgetUISettings(String path) {

        menu = new MarkdownMenu(markdownTa, fileNames, path, this);
        markdownTa.setImmediate(true);
        menu.setPath(path);

        markdownTa.addStyleName("markdowneditorarea");

        setSpacing(true);
        setMargin(true);

        hlayout.setMargin(true);
        hlayout.setSpacing(true);
        markdownTa.setCaption("Markdown");

        markdownTa.setWidth("500px");
        markdownTa.setHeight("450px");
        richTa.setWidth("500px");
        richTa.setHeight("450px");
        richTa.setReadOnly(true);

        hlayout.addComponents(markdownTa, richTa);

        addComponents(menu, hlayout, preview);
        addSubMenu();
    }

    private void addSubMenu() {
        preview.addClickListener(event -> {
            richTa.setReadOnly(false);
            String s = peg.markdownToHtml(markdownTa.getValue());
            richTa.setValue(s);
            BridgeUI.o(richTa.getValue());
            richTa.setReadOnly(true);

        });

        preview.setClickShortcut(KeyCode.P, ModifierKey.CTRL);
        preview.setDescription("Translate Markdown to html");

        save.addClickListener(event -> {
            // store markdown news details and the news
            if (!menu.isTopicsValid()
            /* && !markdownTa.getValue().trim().isEmpty() */) {
                Notification.show("Choose or Add a News Topic",
                        Type.ERROR_MESSAGE);
                menu.focusTopic();
            } else {

                C<HtmlItem> c = new C<>(HtmlItem.class);

                c.set(newsId, "markdown", markdownTa.getValue());
                Calendar cal = Calendar.getInstance();
                c.set(newsId, "date", cal.getTime());
                c.set(newsId, "fileNames", fileNames);
                c.set(newsId, "topic", menu.getTopic());

                news.editingDone(saveNews);
            }
        });

        save.setClickShortcut(KeyCode.S, ModifierKey.CTRL);

        cancel.addClickListener(listener -> {
            news.editingDone(removeNews);
        });
        cancel.setClickShortcut(KeyCode.C, ModifierKey.CTRL);

        HorizontalLayout hl = new HorizontalLayout(preview, cancel, save);
        hl.setSpacing(true);
        addComponent(hl, 2);
        setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);
    }

    protected void suggestDeletion() {
        ConfirmDialog.show(getUI(), "Please Confirm:",
                "Your news is empty. Delete it?", "Yes", "No",
                (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                    if (dialog.isConfirmed()) {
                        NewsManager.remove(newsId);
                        news.endEditing();
                    } else {
                        news.editingDone(false);
                    }
                });
    }

    protected void addPegProcessor() {
        int p = Extensions.ABBREVIATIONS;
        int t = Extensions.TABLES;
        int q = Extensions.QUOTES;
        int ab = Extensions.ABBREVIATIONS;
        int hw = Extensions.HARDWRAPS;
        int sm = Extensions.SMARTS;
        int sty = Extensions.SMARTYPANTS;
        int al = Extensions.AUTOLINKS;
        peg = new PegDownProcessor(p | t | q | ab | hw | sm | sty | al);
    }

}
