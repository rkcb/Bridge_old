package com.bridge.ui.markdown;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.pegdown.PegDownProcessor;
import org.vaadin.hene.expandingtextarea.ExpandingTextArea;

import com.bridge.database.C;
import com.bridge.database.HtmlItem;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.club.ResourceManager;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/***
 * LayoutBuilder turns html to a custom layout
 */

public class LayoutBuilder {

    private HashSet<String> fileNames = new HashSet<>();
    private HashSet<String> foundFiles = new HashSet<>();
    private String path = "";
    private String html = ""; // need not be sanitized
    private Object htmlId = null;
    private int i = 0; // customlayout location index; every link and image will
                       // get one

    public LayoutBuilder(Object newsId, PegDownProcessor p) {
        htmlId = newsId;
        C<HtmlItem> c = new C<>(HtmlItem.class);
        HtmlItem i = c.get(newsId);
        html = p.markdownToHtml(i.getMarkdown());
        path = ResourceManager.getNewsDirectory(newsId);
        fileNames.addAll(i.getFileNames());
    }

    /***
     * addImageDivs converts img tags to divs for the Vaadin CustomLayout to
     * which Images are dropped and updates foundFiles
     */

    protected Document addImageDivs(Document d,
            HashMap<String, String> styles) {
        Elements imgs = d.select("img");

        for (Element e : imgs) {
            i++;
            String src = e.attr("src").trim();
            src = src.replaceFirst("^http://", "");
            if (fileNames.contains(src)) {
                foundFiles.add(src);
                Element p = e.parent();
                String style = styles.getOrDefault(src, "");
                String w = e.attr("width");
                String h = e.attr("height");
                Element e2 = p.appendElement("div").attr("location", "" + i)
                        .attr("style", style).attr("caption", e.attr("alt"))
                        .attr("image", src).attr("height", h).attr("width", w);
                e.replaceWith(e2);
            } else {
                BridgeUI.o("remove e: " + e.toString());
                e.remove();
            }
        }

        return d;
    }

    /***
     * addLinkDivs finds links and updates foundFiles if link refers to a
     * uploaded file
     */

    protected Document addLinkDivs(Document d) {
        Elements links = d.select("a[href]");
        for (Element e : links) {
            String href = e.attr("href").trim();
            String stripped = href.replaceFirst("^http[s]{0,1}://", "");

            String loc = "" + i++;
            String linkText = e.text();

            Element p = e.parent();
            Element e2 = p.appendElement("div").attr("location", loc)
                    .attr("linktext", linkText);

            if (fileNames.contains(stripped)) {
                e2.attr("file", stripped);
                foundFiles.add(stripped);
            } else {
                e2.attr("href", href);
            }
            e.replaceWith(e2);
        }
        return d;
    }

    protected void addLinks(CustomLayout l, Document d) {
        Elements es = d.select("div[file]");
        // BridgeUI.o("es div[file] size: "+es.size());
        for (Element e : es) {
            File f = new File(path + e.attr("file"));
            FileResource res = new FileResource(f);
            Link lk = new Link(e.attr("linktext"), res);
            lk.setTargetName("_blank");
            l.addComponent(lk, e.attr("location"));
        }

        es = d.select("div[href]");
        // BridgeUI.o("es div[href] size: "+es.size());
        for (Element e : es) {
            Link lk = new Link(e.attr("linktext"),
                    new ExternalResource(e.attr("href")));
            lk.setTargetName("_blank");
            l.addComponent(lk, e.attr("location"));
        }
    }

    /***
     * getImage builds a Vaadin Image from the element
     */

    protected Image getImage(Element e) {
        File f = new File(path + e.attr("image"));
        BridgeUI.o("getImage: path=" + path + " attr=" + e.attr("image"));
        if (f.exists() && f.isFile()) {
            FileResource res = new FileResource(f);
            Image i = new Image("", res);
            i.setWidth(e.attr("width") + "px");
            i.setHeight(e.attr("height") + "px");
            i.setStyleName("newsimage");
            ExpandingTextArea cap = new ExpandingTextArea();
            cap.addStyleName("newsimagecaption");
            cap.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
            cap.addStyleName(ValoTheme.TEXTAREA_SMALL);
            cap.setValue(e.attr("caption"));
            cap.setReadOnly(true);
            VerticalLayout vl = new VerticalLayout(i, cap);
            vl.setComponentAlignment(cap, Alignment.MIDDLE_CENTER);
            vl.setComponentAlignment(i, Alignment.MIDDLE_CENTER);
            vl.setSizeUndefined();
            cap.setWidth(e.attr("width") + "px");
            return i;
        } else {
            return null;
        }
    }

    protected void addImages(CustomLayout l, Document d) {
        for (Element e : d.select("div[image]")) {
            Image i = getImage(e);
            if (i != null) {
                l.addComponent(i, e.attr("location"));
            } else {
                BridgeUI.o("image null");
            }
        }

    }

    /***
     * createVaadinCustomLayout builds a CustomLayout from the markdown
     */

    public CustomLayout createVaadinCustomLayout() {

        Document d = Jsoup.parse(html);
        HashMap<String, String> styles = new HashMap<>();

        getImageStyles(d, styles);

        String clean = Jsoup.clean(d.html(), Whitelist.relaxed().addTags("hr"));

        d = Jsoup.parse(clean);
        d = addImageDivs(d, styles);
        d = addLinkDivs(d);

        ResourceManager.cleanResources(htmlId, foundFiles);

        ByteArrayInputStream str = new ByteArrayInputStream(
                d.toString().getBytes());
        CustomLayout layout = null;
        try {
            layout = new CustomLayout(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (layout != null) {
            addImages(layout, d);
            addLinks(layout, d);
        }

        return layout;
    }

    /***
     * getImageStyles collects html img tag properties
     */

    protected void getImageStyles(Document d, HashMap<String, String> styles) {
        Elements es = d.select("img[src]");
        for (Element e : es) {
            String k = e.attr("src").trim();
            k = k.replaceFirst("^http://", "");
            if (!styles.containsKey(k)) {
                styles.put(k, e.attr("style"));
            }
        }
    }

}
