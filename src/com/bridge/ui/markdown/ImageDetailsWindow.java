package com.bridge.ui.markdown;

import java.util.HashSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ImageDetailsWindow extends Window {

    private Panel imageSize = new Panel("Image size");

    private TextField width = new TextField("Width");
    private TextField height = new TextField("Height");
    private TextField caption = new TextField("Caption");

    private ComboBox imgFloat = new ComboBox("Float");
    private Button done = new Button("Done");
    private MyFileUpload upload;

    private TextArea area;
    private String resourcePath = "";
    private HashSet<String> fileNames;

    public ImageDetailsWindow(TextArea area, String path,
            HashSet<String> fileNames) {

        this.area = area;
        resourcePath = path;
        upload = new MyFileUpload(resourcePath);
        upload.setImmediate(true);
        upload.addValueChangeListener(event -> {
        });
        this.fileNames = fileNames;

        width.setDescription("Set the width of the image");
        width.setImmediate(true);
        height.setImmediate(true);

        imgFloat.setDescription(
                "Set float direction; usually set to left or right");
        imgFloat.addItem("Right");
        imgFloat.addItem("Left");

        addDoneButton();
        addValidatorsAndValueListeners();
        addWidgets();

        width.setValue("100");
        height.setValue("100");
        setWidth("300px");
        setSizeUndefined();

    }

    private void addWidgets() {
        FormLayout fl = new FormLayout(width, height, caption, imgFloat);
        fl.setMargin(true);

        HorizontalLayout hl = new HorizontalLayout(fl, imageSize);
        hl.setComponentAlignment(imageSize, Alignment.MIDDLE_CENTER);
        VerticalLayout vl = new VerticalLayout(hl, upload, done);
        vl.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);
        vl.setMargin(true);
        vl.setComponentAlignment(done, Alignment.MIDDLE_RIGHT);

        vl.setSizeUndefined();
        setContent(vl);
        setSizeUndefined();
        setModal(true);
        setWidth("500px");
        setImmediate(true);

    }

    private void addDoneButton() {
        done.addClickListener(event -> {
            String fileName = upload.getLastFileName();
            if (fileName != null) {
                fileNames.add(upload.getLastFileName());
                // below http:// prefix is added because without
                // it Jsoup sanitizer will wipe the file name out
                String html = buildImageHtml(width.getValue(),
                        height.getValue(), (String) imgFloat.getValue(),
                        caption.getValue(), "http://" + fileName);
                Markdown.insert(area, html);
            }
            close();
        });
    }

    protected String buildImageHtml(String w, String h, String imgFloat,
            String caption, String fileName) {
        Document d = new Document("");
        Element e = d.appendElement("img");

        e = addImgAttribute(e, "width", w);
        e = addImgAttribute(e, "height", h);
        e = addImgAttribute(e, "alt", caption);
        e = addImgAttribute(e, "src", fileName);

        StringBuilder b = new StringBuilder("margin: 20px;");
        if (imgFloat != null && !imgFloat.isEmpty()) {
            b.append("float:" + imgFloat + ";");
        }
        e = addImgAttribute(e, "style", b.toString());

        return e.toString();
    }

    protected Element addImgAttribute(Element e, String attr,
            String attrValue) {
        if (attrValue != null && !attrValue.isEmpty()) {
            e.attr(attr, attrValue);
        }
        return e;
    }

    public void addValidatorsAndValueListeners() {
        width.addValidator(value -> {
            String s = (String) value;
            if (s != null && !s.isEmpty()) {
                Integer i = getValue(s);
                width.setComponentError(null);
                if (i == null || i < 10 || i > 1000) {
                    throw new InvalidValueException(
                            "Not empty value or an integer between 10-1000");
                }
            }
        });

        height.addValidator(value -> {
            String s = (String) value;
            if (s != null && !s.isEmpty()) {
                Integer i = getValue(s);
                height.setComponentError(null);
                if (i == null || i < 10 || i > 1000) {
                    throw new InvalidValueException(
                            "Not empty value or an integer between 10-1000");
                }
            }
        });

        width.addValueChangeListener(event -> {
            // imageSize.setWidth(""+(Integer) width.getConvertedValue());
            Integer i = getValue(width.getValue());
            if (i != null) {
                imageSize.setWidth(width.getValue());
                // width.markAsDirty();
            }
        });
        height.addValueChangeListener(event -> {
            Integer i = getValue(width.getValue());
            if (i != null) {
                imageSize.setHeight(height.getValue());
            }
        });

    }

    protected Integer getValue(Object value) {
        Integer i;

        if (value != null) {
            String s = (String) value;
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }

            return i;

        } else {
            return null;
        }
    }

}
