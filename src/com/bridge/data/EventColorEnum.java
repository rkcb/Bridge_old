package com.bridge.data;

public enum EventColorEnum {

    MP_TOURNAMENT("#FF0090", "Master Point Tournament"), NONMP_TOURNAMENT(
            "#122DA8", "Tournament, no Master Points"), CALENDAR_EVENT(
                    "#6BC4C4", "Calendar Event"), CALENDAR_EVENT_REG("#F66F00",
                            "Calendar Event, Registration"), PRIVATE("#00B100",
                                    "Club Members Only");
    private String colorCode;
    private String description;

    EventColorEnum(String code, String description) {
        colorCode = code;
        this.description = description;
    }

    public String code() {
        return colorCode;
    }

    public String description() {
        return description;
    }

    /***
     * tournamentEvents gives normal event types used in a tournament calendar
     */
    public static EventColorEnum[] tournamentEvents() {
        return new EventColorEnum[] { MP_TOURNAMENT, NONMP_TOURNAMENT,
                PRIVATE };
    }

    /***
     * calendarEvents gives normal event types used in a tournament calendar
     */
    public static EventColorEnum[] calendarEvents() {
        return new EventColorEnum[] { CALENDAR_EVENT, CALENDAR_EVENT_REG,
                PRIVATE };
    }

    public static EventColorEnum[] allCalendarEvents() {
        return new EventColorEnum[] { MP_TOURNAMENT, NONMP_TOURNAMENT,
                CALENDAR_EVENT, CALENDAR_EVENT_REG, PRIVATE };
    }

    @Override
    public String toString() {
        return description;
    }
}
