package com.bridge.ui;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ETable extends Table {

    private Object[] props;

    public ETable() {
        super.enableContentRefreshing(true);

        setImmediate(true);
    }

    public ETable(String caption) {
        super(caption);
        super.enableContentRefreshing(true);
        setImmediate(true);
    }

    public ETable(String caption, Container dataSource) {
        super(caption, dataSource);
        enableContentRefreshing(true);
        setImmediate(true);

    }

    /***
     * with sets the visible columns
     */

    public ETable with(Object... ps) {
        props = ps;
        setVisibleColumns(ps);
        return this;
    }

    public ETable headers(String... hs) {

        if (hs.length > 0) {
            for (int i = 0; i < props.length; i++) {
                setColumnHeader(props[i], hs[i]);
            }
        }

        return this;
    }

}
