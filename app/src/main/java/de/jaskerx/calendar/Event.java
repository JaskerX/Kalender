package de.jaskerx.calendar;

import de.jaskerx.calendar.db.CalendarData;

public class Event {

    private final String title;
    private final CalendarData calendarData;

    public Event(String title, CalendarData calendarData) {
        this.title = title;
        this.calendarData = calendarData;
    }

    public String getTitle() {
        return title;
    }

    public CalendarData getCalendarData() {
        return calendarData;
    }

}
