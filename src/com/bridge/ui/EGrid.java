package com.bridge.ui;

import java.util.List;

import com.bridge.database.C;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

@SuppressWarnings("serial")
public class EGrid<T> extends Grid {

    private C<T> container;

    public EGrid(Class<T> c, C<T> container) {
        super(null, container.c());
        setHeightMode(HeightMode.ROW);
        setImmediate(true);
        setReadOnly(true);
    }

    /***
     * with selects only the chosen properties in this order NOTE: if the
     * underlying container does yet contain these properties then these will be
     * added if possible; that is nested properties
     */
    // public EGrid<T> with(Object... propIds) {
    // super.setColumns(propIds);
    // return this;
    // }

    /***
     * addProperties adds these (nested) properties to the container
     */
    public EGrid<T> addProperties(String... propId) {
        for (String pid : propId) {
            container.nest(pid);
        }

        return this;
    }

    public C<T> getContainer() {
        return container;
    }

    /***
     * headers sets header names in this order only if the name count and
     * property count are equal
     */
    public EGrid<T> headers(String... colNames) {
        List<Column> list = super.getColumns();
        if (list.size() == colNames.length) {
            int i = 0;
            for (String name : colNames) {
                list.get(i).setHeaderCaption(name);
                i++;
            }
            return this;
        } else {
            return null;
        }
    }
}
