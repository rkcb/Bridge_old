package com.bridge.data;

import java.util.Date;
import java.util.Objects;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class FilterDateField extends DateField {

    protected Container.Filterable container;
    protected Filter filter;
    protected Object propertyId;
    protected HeaderRow headerRow;

    protected FilterDateField(Container.Filterable container, Object propertyId,
            HeaderRow headerRow) {
        this.container = container;
        this.propertyId = propertyId;

        setDateFormat("dd.MM.yyyy");
        setResolution(Resolution.DAY);
    }

    public static FilterDateField addFilterDateField(
            Container.Filterable container, Object propertyId,
            HeaderRow headerRow) {
        FilterDateField field = new FilterDateField(container, propertyId,
                headerRow);
        Objects.requireNonNull(container);
        Objects.requireNonNull(headerRow);
        Objects.requireNonNull(propertyId);

        field.filter = null;

        field.addValueChangeListener(event -> {
            if (field.filter != null) {
                container.removeContainerFilter(field.filter);
            }
            Date date = field.getValue();
            if (date != null) {
                field.filter = new Compare.GreaterOrEqual(propertyId, date);
                container.addContainerFilter(field.filter);
            }
        });

        headerRow.getCell(propertyId).setComponent(field);
        field.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        return field;
    }

}
