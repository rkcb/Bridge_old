package com.bridge.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bridge.ui.BridgeUI;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.SimpleStringFilter;

public class C<T> {

    private JPAContainer<T> container;

    public C(Class<T> entityClass) {
        container = JPAContainerFactory.make(entityClass, BridgeUI.unitName);
        container.setAutoCommit(true);
        container.setFireContainerItemSetChangeEvents(true);
    }

    public C(JPAContainer<T> c) {
        container = c;
    }

    public void refresh() {
        container.refresh();
    }

    public void commit() {
        container.commit();
    }

    public void autoCommit(boolean isOn) {
        container.setAutoCommit(isOn);
    }

    @SuppressWarnings("unchecked")
    public void set(Object id, String propertyId, Object value) {
        try {
            container.getItem(id).getItemProperty(propertyId).setValue(value);
            container.commit();
        } catch (ReadOnlyException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    public <T> List<T> all() {
        if (!empty()) {
            List<T> l = new ArrayList<>();
            int sz = container.size();
            for (int i = 0; i < sz; i++) {
                l.add((T) at(i));
            }
            return l;
        }
        return null;
    }

    public boolean empty() {
        return container != null && container.size() == 0;
    }

    public void setContainer(JPAContainer<T> c) {
        container = c;
    }

    /***
     * sets the entity item's property
     *
     * @param i
     *            index of the item in the container
     * @param propertyId
     *            name of the property in the container
     * @param value
     *            new value of the property
     */
    public void set(Integer i, String propertyId, Object value) {
        if (i < size()) {
            Object id = container.getIdByIndex(i);
            try {
                set(id, propertyId, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object get(Object id, String propertyId) {
        EntityItem<T> i = container.getItem(id);
        return i.getItemProperty(propertyId).getValue();
    }

    public T get(Object id) {
        return container.getItem(id).getEntity();
    }

    public JPAContainer<T> c() {
        return container;
    }

    /***
     * add commits the entity to the database
     * 
     * @return id of the entity
     */
    public Object add(T entity) {
        Object id = container.addEntity(entity);
        container.commit();
        return id;
    }

    /***
     * rem removes the item
     */
    public void rem(Object id) {
        if (id != null) {
            container.removeItem(id);
            // container.commit();
        }

    }

    public Filter filter(Filter f) {
        container.addContainerFilter(f);
        return f;
    }

    /***
     * filterEq filters all entities which are equal
     */
    public <S> Filter filterEq(String propertyId, S value) {
        Filter f = new Compare.Equal(propertyId, value);
        container.addContainerFilter(f);
        return f;
    }

    /***
     * filterNeq filters out all entities which are not equal to the value
     */
    public <S> Filter filterNeq(String propertyId, S value) {
        Filter f = new Not(new Compare.Equal(propertyId, value));
        container.addContainerFilter(f);
        return f;
    }

    public void removeFilter(Filter f) {
        container.removeContainerFilter(f);
    }
    // public <S, T> T getFirst(String propertyId, S value) {
    // Filter f = filterEq(propertyId, value);
    // T match = (T) at(0);
    // container.removeContainerFilter(f);
    // return match;
    // }

    /***
     * nest adds a nested property x.y
     */
    public void nest(String s) {
        container.addNestedContainerProperty(s);
    }

    public void nest(String... propertyIds) {
        for (String id : propertyIds) {
            nest(id);
        }
    }

    public void filter(String propertyId, String filter, boolean ignoreCase,
            boolean prefix) {
        Filter f = new SimpleStringFilter(propertyId, filter, ignoreCase,
                prefix);
        container.addContainerFilter(f);
    }

    public void removeFilters() {
        container.removeAllContainerFilters();
    }

    public T at(int i) {
        return container.size() > i ? container.getItem(id(i)).getEntity()
                : null;
    }

    public Object id(int i) {
        return container.size() > i ? container.getIdByIndex(i) : null;
    }

    public EntityItem<T> item(Object itemId) {
        EntityItem<T> i = container.getItem(itemId);
        i.setBuffered(false);
        return i;
    }

    public int size() {
        return container.size();
    }

    public Collection<String> props() {
        return container.getContainerPropertyIds();
    }

    public <S> boolean exists(String propertyId, S value) {
        Filter f = filterEq(propertyId, value);
        boolean e = container.size() > 0;
        removeFilter(f);
        return e;
    }

    /***
     * has shows whether this container contains this entity id
     */
    public boolean has(Object id) {
        if (container != null) {
            return container.containsId(id);
        } else {
            return false;
        }
    }

    /***
     * ids returns container's item ids
     */

    public Collection<Object> ids() {
        return container.getItemIds();
    }

}
