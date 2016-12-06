package com.bridge.resultui;
import scala.bridge.TableFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class OptimumScoreTable extends Table {
	
	private IndexedContainer container = new IndexedContainer();
	private TableFactory factory = null;
	
	public OptimumScoreTable(TableFactory factory) {
		this.factory = factory; 
		setContainerDataSource(container);
	}
	
	void addContainerProperties() {
		String[] header = factory.optimumHeader();
		for (int i = 0; i<header.length; i++) {
			container.addContainerProperty(header[i], String.class, null);
		}
	}
	
	public void score(String board) {
		container.removeAllItems();
		String[] header = factory.optimumHeader();
		Object[][] scores = factory.optimumData(board);
		for (int i = 0; i<scores.length; i++) {
			Object itemId = container.addItemAt(i);
			Item item = container.getItem(itemId);
			for (int j = 0; j<header.length; j++) {
				@SuppressWarnings("unchecked")
				Property<Object> p =  item.getItemProperty(header[j]);
				p.setValue(scores[i][j]);
			}
		}
	}
}
