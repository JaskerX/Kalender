package de.jaskerx.calendar;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class Day {

    private final LocalDate localDate;

    public Day(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getLocalizedDayOfWeek() {
        return this.localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

}
