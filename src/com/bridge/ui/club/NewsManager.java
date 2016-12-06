package com.bridge.ui.club;

import java.util.List;
import java.util.stream.Collectors;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.HtmlItem;
import com.bridge.ui.BridgeUI;

public class NewsManager {

    /***
     * addNews adds a news item to the data base note: this routine does not
     * create a directory for that returns news entity item id
     ***/

    public static Object create(Object clubId) {
        C<Club> cs = new C<>(Club.class);
        cs.filterEq("id", clubId);
        Object newsId = null;
        if (!cs.empty()) {
            // update the DB relation from both ends
            List<HtmlItem> list = cs.at(0).getNews();
            C<HtmlItem> is = new C<>(HtmlItem.class);
            newsId = is.add(new HtmlItem(cs.get(clubId)));
            HtmlItem item = is.get(newsId);
            list.add(0, item); // news appear in chronological order?
            cs.set(clubId, "news", list);
        }

        return newsId;
    }

    /***
     * remove removes item and updates the DB relation accordingly
     *
     * @param newsId
     *            the id of the entity to be removed
     */

    public static void remove(Object newsId) {
        C<HtmlItem> items = new C<>(HtmlItem.class);
        C<Club> cs = new C<>(Club.class);
        Object cid = BridgeUI.user.getCurrentClub().getId();
        if (newsId != null && cid != null) {
            ResourceManager.removeNewsFileResources(newsId);

            Long id = (Long) newsId;
            List<HtmlItem> list = cs.get(cid).getNews();
            list = list.stream().filter(h -> !h.getId().equals(id))
                    .collect(Collectors.toList());
            cs.set(cid, "news", list);
            items.set(newsId, "club", null);
            items.rem(newsId);

        }
    }

}
