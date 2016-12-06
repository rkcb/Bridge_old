package com.bridge.data;

import java.util.Objects;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class FilterTextField extends TextField {

    protected Container.Filterable container;
    protected Filter filter;
    protected Object propertyId;
    protected HeaderRow headerRow;

    protected FilterTextField(Container.Filterable container, Object propertyId,
            HeaderRow headerRow) {
        this.container = container;
        this.propertyId = propertyId;
        this.headerRow = headerRow;
    }

    public static FilterTextField addFilterField(Container.Filterable container,
            Object propertyId, HeaderRow headerRow) {
        FilterTextField field = new FilterTextField(container, propertyId,
                headerRow);
        Objects.requireNonNull(container);
        Objects.requireNonNull(headerRow);
        Objects.requireNonNull(propertyId);

        field.filter = null;

        field.addTextChangeListener(event -> {
            if (field.filter != null) {
                container.removeContainerFilter(field.filter);
            }
            if (!event.getText().isEmpty()) {
                field.filter = new SimpleStringFilter(propertyId,
                        event.getText(), true, false);
                container.addContainerFilter(field.filter);
            }
        });

        headerRow.getCell(propertyId).setComponent(field);
        field.setInputPrompt("Filter Text");
        field.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        return field;
    }
}
