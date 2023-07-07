package de.jaskerx.calendar;

import java.time.LocalTime;

import de.jaskerx.calendar.db.CalendarData;

public class Event {

    private final String title;
    private final CalendarData calendarData;
    private final LocalTime localTime;

    public Event(String title, CalendarData calendarData, LocalTime localTime) {
        this.title = title;
        this.calendarData = calendarData;
        this.localTime = localTime;
    }

    public String getTitle() {
        return title;
    }

    public CalendarData getCalendarData() {
        return calendarData;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

}
