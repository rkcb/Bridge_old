package com.bridge.ui;


import com.bridge.database.Player;
import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PlayerFinder extends VerticalLayout {

	// searched properties -- all fields must match
	private final TextField firstName = new TextField("First name");
	private final TextField lastName = new TextField("Last name");
	private final TextField federationCode = new TextField("Fed code");
	
	private int firstNameLength = 0;
	private int lastNameLength = 0;
	private int federationCodeLength = 0;
	
	private final HorizontalLayout hLayout = new HorizontalLayout();
	
	private Table table = new Table();
	private JPAContainer<Player> players = 
			JPAContainerFactory.make(Player.class, "BridgeResults");
	@SuppressWarnings("unused")
	private JPAContainer<User> users = 
			JPAContainerFactory.make(User.class, "BridgeResults");
	
	private Filter firstNameFilter;
	private Filter lastNameFilter;
	private Filter federationCodeFilter;
	
	private PopupView popup;
	private final int visibleRows = 5;
	
	private IndexedContainer suggestions;
	
	public PlayerFinder() {
		
//		System.out.println("players size = "+players.size());
//		System.out.println("users size = "+users.size());
//		
		if (players != null) {
			
			setMargin(true);
			setSpacing(true);
			table.setSizeUndefined();
			
			hLayout.setSpacing(true);
			setPageSize();
			players.addNestedContainerProperty("user.firstName");
			players.addNestedContainerProperty("user.lastName");
			
			// adds all table logic and the related
			addTableToPopupView();
		}
		hLayout.addComponents(firstName, lastName, federationCode);
		popup = new PopupView("popup", table);
		popup.setHideOnMouseOut(false);
		popup.setStyleName("mypopupview");
		super.addComponents(hLayout,popup);
		addSearchTextChangedListeners();
	}
	
	private void addTableToPopupView() {
		createSuggestionsContainer();
//		addTablePrefixBolding();
		table.setSelectable(true);
		table.setContainerDataSource(suggestions);
		table.setVisibleColumns(new Object[] {"firstName", "lastName", "federationCode"});
		table.setPageLength(0);
		addSelectedPlayer();
	}

	private String bold(String word, int prefixLength) {
		if (word != null && prefixLength <= word.length()) {
			String s1 = word.substring(0,prefixLength);
			String s2 = word.substring(prefixLength, word.length());
			return "<b>"+s1+"</b>"+s2;
		}
		else return null; 
	}
	
	

	/***
	 * addSearchTextChangedListeners adds listeners to search fields
	 * */
	private void addSearchTextChangedListeners() {
		firstName.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				addFirstNameFilter(event.getText());
			}
		});
	
		lastName.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				addLastNameFilter(event.getText());
			}
		});
		
		federationCode.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				addFederationCodeFilter(event.getText());
			}
		});
	}
	
	// add filter or update filter for each search field
	private void addFirstNameFilter(String text) {
		firstNameLength = text.length();
		players.removeContainerFilter(firstNameFilter);
		firstNameFilter = new SimpleStringFilter(
				"user.firstName", text, true, true); 
		players.addContainerFilter(firstNameFilter);
		addSuggestions();
	}
	
	private void addLastNameFilter(String text) {
		lastNameLength = text.length();
		players.removeContainerFilter(lastNameFilter);
		lastNameFilter = new SimpleStringFilter(
				"user.lastName", text, true, true);
		players.addContainerFilter(lastNameFilter);
		addSuggestions();
	}
	
	private void addFederationCodeFilter(String text) {
		federationCodeLength = text.length();
		players.removeContainerFilter(federationCodeFilter);
		federationCodeFilter = new SimpleStringFilter(
				"federationCode", text, true, true); 
		players.addContainerFilter(federationCodeFilter);
		addSuggestions();
	}
	
	private void setPageSize() {
		int s = players.size();
		
		table.setPageLength(s < visibleRows ? s : visibleRows);
		table.setVisible(true);
	}
	
	private void addSelectedPlayer() {
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				playerClicked(event);
			}
		});
	}
	
	/***
	 * playerClick adds selected item values to search fields 
	 * and sets the filters accordingly
	 * @param event
	 */
	
	private void playerClicked(ItemClickEvent event) {
		Item item = event.getItem();
		EntityItem<Player> pItem = players.getItem(item.getItemProperty("playersid").getValue());
		
		String firstNameValue = 
				(String) pItem.getItemProperty("user.firstName").getValue();
		String lastNameValue =  
				(String) pItem.getItemProperty("user.lastName").getValue();
		String federationCodeValue = 
				(String) pItem.getItemProperty("federationCode").getValue();
		
		firstName.setValue(firstNameValue);
		lastName.setValue(lastNameValue);
		federationCode.setValue(federationCodeValue);
		
		players.removeAllContainerFilters();	
		
		addFirstNameFilter(firstNameValue);
		addLastNameFilter(lastNameValue);
		addFederationCodeFilter(federationCodeValue);
	}
	
	private void createSuggestionsContainer() {
		suggestions = new IndexedContainer();
		suggestions.addContainerProperty("firstName", Label.class, null);
		suggestions.addContainerProperty("lastName", Label.class, null);
		suggestions.addContainerProperty("federationCode", Label.class, null);
		// playersid marks the item from which each suggestion item is built from
		suggestions.addContainerProperty("playersid", Object.class, null);
		
		
	}
	
	@SuppressWarnings("unused")
	private void o(String s) {
		System.out.println(s);
	}
	
	@SuppressWarnings("unchecked")
	private void addSuggestions() {
	
		int count = players.size() > visibleRows ? visibleRows : players.size();
		
		suggestions.removeAllItems();
		
		popup.setPopupVisible(true);
		
		for (int i = 0; i<count; i++) {
			final Object id = players.getIdByIndex(i);
			EntityItem<Player> p = players.getItem(id);
			
			String fn = (String) p.getItemProperty("user.firstName").getValue();
			fn = bold(fn, firstNameLength);
			
			String ln = (String) p.getItemProperty("user.lastName").getValue();
			ln = bold(ln, lastNameLength);
			
			String fc = (String) p.getItemProperty("federationCode").getValue();
			fc = bold(fc, federationCodeLength);
			
			Label fnl = new Label(fn, ContentMode.HTML);
			Label lnl = new Label(ln, ContentMode.HTML);
			Label fcl = new Label(fc, ContentMode.HTML);
			
			final Object id2 = suggestions.addItem();
			Item item = suggestions.getItem(id2);
			
			
			item.getItemProperty("firstName").setValue(fnl);
			item.getItemProperty("lastName").setValue(lnl);
			item.getItemProperty("federationCode").setValue(fcl);
			item.getItemProperty("playersid").setValue(id);
			
		}
		
	}
	
	
	
}
