package de.jaskerx.calendar;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Day {

    private final LocalDate localDate;
    private final List<Event> events;

    public Day(LocalDate localDate) {
        this.localDate = localDate;
        this.events = new ArrayList<>();
    }

    public Day(LocalDate localDate, List<Event> events) {
        this.localDate = localDate;
        this.events = events;
    }

    public String getLocalizedDayOfWeek() {
        return this.localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public List<Event> getEvents() {
        return events;
    }

}
