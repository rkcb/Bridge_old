package com.bridge.ui.club;


import com.bridge.database.C;
import com.bridge.database.HtmlItem;
import com.bridge.view.ClubInternalsView;
import com.vaadin.ui.MenuBar;

@SuppressWarnings("serial")
public class ClubInternalsAdminMenu extends MenuBar {

	protected MenuItem frontPage;
	protected MenuItem createNews;
	protected MenuItem editNews;
	protected C<HtmlItem> news;
	protected ClubInternalsView clubInternals;
	
	
	public ClubInternalsAdminMenu(ClubInternalsView view) {
		clubInternals = view;
		news = new C<HtmlItem>(HtmlItem.class);
		
//		frontPage = addItem("Front Page", new Command() {
//			@Override
//			public void menuSelected(MenuItem selectedItem) {
//			}
//		});
		
		createNews = addItem("Create News", new Command() {
			@Override
			public void menuSelected(MenuItem selectedItem) {
				clubInternals.createNews();
			}
		});
	}
	
	protected void loadNews() {
	}

	
}
