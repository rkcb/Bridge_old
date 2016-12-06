package com.bridge.data;

import java.util.Objects;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid.HeaderRow;

@SuppressWarnings("serial")
public class FilterCheckBox extends CheckBox {

    protected Filter filter;
    protected Container.Filterable container;
    protected Object propertyId;
    protected HeaderRow headerRow;

    public FilterCheckBox(Container.Filterable container, Object propertyId,
            HeaderRow headerRow) {
        this.container = container;
        this.propertyId = propertyId;
        this.headerRow = headerRow;
    }

    public static CheckBox addFilterCheckBox(Container.Filterable container,
            Object propertyId, HeaderRow headerRow) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(headerRow);
        Objects.requireNonNull(propertyId);

        FilterCheckBox checkBox = new FilterCheckBox(container, propertyId,
                headerRow);
        checkBox.setCaption("Filter");
        checkBox.addValueChangeListener(event -> {
            if (checkBox.filter != null) {
                container.removeContainerFilter(checkBox.filter);
            }
            checkBox.filter = new Compare.Equal(propertyId,
                    (boolean) event.getProperty().getValue());
            container.addContainerFilter(checkBox.filter);

        });

        headerRow.getCell(propertyId).setComponent(checkBox);

        return checkBox;
    }
}
