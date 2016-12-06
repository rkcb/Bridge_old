package com.bridge.ui.search;

import com.bridge.database.C;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class SearchComboBox<T> extends ComboBox {

    private C<T> container;

    public SearchComboBox(String caption, Class<T> c) {
        this.container = new C<>(c);
        setCaption(caption);
        setContainerDataSource(container.c());
        setTextInputAllowed(true);
        setFilteringMode(FilteringMode.CONTAINS);
        setItemCaptionMode(ItemCaptionMode.ITEM);
        setImmediate(true);
    }

    public SearchComboBox(String caption, Class<T> c, C<T> container) {
        if (container == null) {
            this.container = new C<>(c);
        }
        this.container = container;
        setCaption(caption);
        setContainerDataSource(container.c());
        setTextInputAllowed(true);
        setFilteringMode(FilteringMode.CONTAINS);
        setItemCaptionMode(ItemCaptionMode.ITEM);
        setImmediate(true);
    }

    protected void setValueChange() {
        addValueChangeListener(listener -> {
            if (getValue() != null) {
                container.removeFilters();
                container.filterEq("id", getValue());
            } else {
                container.removeFilters();
            }
        });
    }

    /***
     * getSelectedItem returns the selected bean if possible and otherwise
     * returns null
     */
    public T getItem() {
        if (getValue() != null) {
            return container.get(getValue());
        } else {
            return null;
        }
    }

    public void removeFilters() {
        container.removeFilters();
    }

    public void refresh() {
        if (container != null) {
            container.refresh();
        }
    }

    @Override
    public void clear() {
        container.removeFilters();
    }
}
