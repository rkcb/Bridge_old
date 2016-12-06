package com.bridge.database;


public class I<T> {

    private C<T> entities;
    private Object id;
    
    public I(Class<T> entityClass, Object id) {
    	entities = new C<T>(entityClass);
    	this.id = id;
    	if (id == null) throw new NullPointerException("id cannot be null");
    	if (id != null && !entities.c().containsId(id)) {
    		throw new NullPointerException("id not found");
    	}
    }
    
    public void refresh() {
    	entities.refresh();
    }

    public void set(String propertyId, Object value) {
    	entities.set(id, propertyId, value);
    }

    public T get() {
    	return entities.get(id);
    }

    public Object id() {
    	return id;
    }

    public boolean remove() {
    	if (id != null && entities.c().containsId(id)){
    		entities.rem(id);
    		return true;
    	} return false;
    }
    
}
