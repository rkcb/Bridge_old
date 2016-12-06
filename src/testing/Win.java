package testing;

import org.pegdown.PegDownProcessor;

import com.bridge.ui.EHorizontalLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class Win extends Window {

    private TextArea ta = new TextArea();
    private RichTextArea ra = new RichTextArea();
    private Button b = new Button("Insert");
    private PegDownProcessor peg = new PegDownProcessor();
    private EHorizontalLayout l = new EHorizontalLayout(ta, ra, b);

    public Win() {
        ta.setWidth("450px");
        ra.setWidth("450px");
        ta.setHeight("400px");
        ra.setHeight("450px");
        ra.setImmediate(true);
        ra.setReadOnly(true);
        l.setComponentAlignment(b, Alignment.BOTTOM_CENTER);

        b.addClickListener(listener -> {
            ra.setReadOnly(false);
            ra.setValue(peg.markdownToHtml(ta.getValue()));
            ra.selectAll();
            ra.setReadOnly(false);
        });
        setContent(l);
    }
}
