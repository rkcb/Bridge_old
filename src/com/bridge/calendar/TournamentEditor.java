package com.bridge.calendar;

import com.bridge.database.Tournament;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.GridLayout;

@SuppressWarnings("serial")
public class TournamentEditor extends GridLayout {

    private PlayerList organizers = new PlayerList("Organizers");
    private PlayerList directors = new PlayerList("Directors");
    private Tournament tour;
    private EntityItem<Tournament> tournamentItem;

    public TournamentEditor() {

        setColumns(2);
        setMargin(true);
        setSpacing(true);

        organizers.setRequired(true);
        directors.setRequired(true);
    }

    public boolean areFieldsValid() {
        return !organizers.isEmpty() && !directors.isEmpty();
    }

    /***
     * initialize initializes the editor with the given object
     */

    public void initialize(Tournament t) {

        t = t == null ? new Tournament() : t;
        organizers.setSelectedPlayers(t.getOrganizers());
        directors.setSelectedPlayers(t.getDirectors());
        tour = t;
    }

    /***
     * setItemSource sets the entity item which will be edited
     */

    public void setItemSource(EntityItem<Tournament> ti) {
        tournamentItem = ti;
        ti.setBuffered(true);
        initialize(tournamentItem.getEntity());
    }

    /***
     * getDirectors returns the chosen directors
     */
    public PlayerList getDirectors() {
        return directors;
    }

    /***
     * getOrganizers returns the chosen organizers
     */
    public PlayerList getOrganizers() {
        return organizers;
    }

    /***
     * getValue
     */

    public Tournament getValue() {
        tour.setDirectors(directors.getSelected());
        tour.setOrganizers(organizers.getSelected());
        return tour;
    }

    /***
     * commit commits the changes to entity item
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void commit() {

        tournamentItem.setBuffered(false); // if true then update is problem
        tournamentItem.getItemProperty("directors")
                .setValue(directors.getSelected());
        tournamentItem.getItemProperty("organizers")
                .setValue(organizers.getSelected());
    }

    @SuppressWarnings("unused")
    private void o(String string) {
        System.out.println(string);
    }

    public void insertFields(GridLayout fields) {
        fields.addComponents(organizers, directors);
    }

    public int getDirectorsSize() {
        return directors.size();
    }

    public int getOrganizersSize() {
        return organizers.size();
    }

    public void setListsEmpty() {
        organizers.clear();
        directors.clear();
    }
}
