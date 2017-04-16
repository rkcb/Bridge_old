package com.bridge.resultui;

import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import scala.bridge.TableFactory;

/***
 * OptimumScoreTable represents the Pbn equivalent as a table
 */
@SuppressWarnings("serial")
public class OptimumScoreTable extends Table {

	private IndexedContainer container = new IndexedContainer();
	private TableFactory factory = null;
	private JsonEvents jevents;

	public OptimumScoreTable(JsonEvents jevents) {
		setContainerDataSource(container);
	}

	void addContainerProperties() {
		String[] header = factory.optimumHeader();
		for (String element : header) {
			container.addContainerProperty(element, String.class, null);
		}
	}

	public void score(String board) {
		container.removeAllItems();
		String[] header = factory.optimumHeader();
		Object[][] scores = factory.optimumData(board);
		for (int i = 0; i < scores.length; i++) {
			Object itemId = container.addItemAt(i);
			Item item = container.getItem(itemId);
			for (int j = 0; j < header.length; j++) {
				@SuppressWarnings("unchecked")
				Property<Object> p = item.getItemProperty(header[j]);
				p.setValue(scores[i][j]);
			}
		}
	}
}
