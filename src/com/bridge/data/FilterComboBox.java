package com.bridge.data;

import java.util.Objects;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

/***
 * FilterComboBox filters entities with ids
 */

@SuppressWarnings("serial")
public class FilterComboBox extends ComboBox {

    protected Filter filter;
    protected Container.Filterable container;
    protected Object propertyId;
    protected HeaderRow headerRow;
    protected Container searchContainer;

    public FilterComboBox(Container.Filterable container, Object propertyId,
            HeaderRow headerRow, Container searchContainer) {

        Objects.requireNonNull(container);
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(headerRow);
        Objects.requireNonNull(searchContainer);

        filter = null;
        this.container = container;
        this.propertyId = propertyId;
        this.headerRow = headerRow;
        this.searchContainer = searchContainer;
    }

    public static FilterComboBox addFilterIdComboBox(
            Container.Filterable container, Object propertyId,
            HeaderRow headerRow, Container searchContainer) {
        FilterComboBox comboBox = new FilterComboBox(container, propertyId,
                headerRow, searchContainer);

        comboBox.setContainerDataSource(searchContainer);
        comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
        comboBox.setItemCaptionMode(ItemCaptionMode.ITEM);
        comboBox.setTextInputAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        comboBox.setInputPrompt("Filter Text");
        comboBox.addValueChangeListener(event -> {
            if (comboBox.filter != null) {
                container.removeContainerFilter(comboBox.filter);
            }
            Object id = event.getProperty().getValue();
            if (id != null) {
                comboBox.filter = new Compare.Equal(propertyId, id);
                container.addContainerFilter(comboBox.filter);
            }
        });
        headerRow.getCell(propertyId).setComponent(comboBox);

        return comboBox;
    }

    /***
     * addFilterStringComboBox filters the column by a string representation
     */

    public static FilterComboBox addFilterStringComboBox(
            Container.Filterable container, Object propertyId,
            HeaderRow headerRow, Container searchContainer) {
        FilterComboBox comboBox = new FilterComboBox(container, propertyId,
                headerRow, searchContainer);

        comboBox.setContainerDataSource(searchContainer);
        comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
        comboBox.setItemCaptionMode(ItemCaptionMode.ITEM);
        comboBox.setTextInputAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        comboBox.setInputPrompt("Filter Text");
        comboBox.addValueChangeListener(event -> {
            if (comboBox.filter != null) {
                container.removeContainerFilter(comboBox.filter);
            }
            String str = (String) event.getProperty().getValue();
            if (str != null && !str.isEmpty()) {
                comboBox.filter = new SimpleStringFilter(propertyId, str, true,
                        false);
                container.addContainerFilter(comboBox.filter);
            }
        });
        headerRow.getCell(propertyId).setComponent(comboBox);

        return comboBox;
    }

}
