package de.jaskerx.calendar.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.provider.CalendarContract;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import de.jaskerx.calendar.RRule;

public class CalendarHolder {

    private final ContentResolver contentResolver;
    private final List<CalendarData> calendars;
    private final List<RRule> rRules;

    public CalendarHolder(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.calendars = new ArrayList<>();
        this.rRules = new ArrayList<>();
    }

    public CompletableFuture<List<CalendarData>> loadCalendars() {
        return CompletableFuture.supplyAsync(() -> {
            final String[] projection = new String[] {
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR
            };

            final int calendarIdIndex = 0;
            final int calendarDisplayNameIndex = 1;
            final int calendarColorIndex = 2;

            Uri uri = CalendarContract.Calendars.CONTENT_URI;

            try(Cursor cur = this.contentResolver.query(uri, projection, null, null, null)) {

                this.calendars.clear();
                while (cur.moveToNext()) {
                    long calID = cur.getLong(calendarIdIndex);
                    String displayName = cur.getString(calendarDisplayNameIndex);
                    int color = cur.getInt(calendarColorIndex);

                    this.calendars.add(new CalendarData(this.contentResolver, calID, displayName, color));
                }
            }
            return this.calendars;
        });
    }

    public CompletableFuture<List<RRule>> loadRRules() {
        return CompletableFuture.supplyAsync(() -> {
            final String[] projection = new String[]{
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.RRULE,
                    CalendarContract.Events.ALL_DAY
            };

            final int calendarIdIndex = 0;
            final int titleIndex = 1;
            final int dtStartIndex = 2;
            final int rRuleIndex = 3;
            final int allDayIndex = 4;

            String selection = CalendarContract.Events.DELETED + " != 1 AND " + CalendarContract.Events.RRULE + " IS NOT NULL";
            Uri uri = CalendarContract.Events.CONTENT_URI;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY);

            try (Cursor cursor = this.contentResolver.query(uri, projection, selection, null, CalendarContract.Events.DTSTART + " DESC")) {

                this.rRules.clear();
                while (cursor.moveToNext()) {
                    long calID = cursor.getLong(calendarIdIndex);
                    String title = cursor.getString(titleIndex);
                    String dtStart = formatter.format(new Date(cursor.getLong(dtStartIndex)));
                    LocalDate localDateStart = LocalDate.parse(dtStart.substring(0, dtStart.indexOf(" ")));
                    String rRuleRaw = cursor.getString(rRuleIndex);
                    int allDay = cursor.getInt(allDayIndex);

                    RRule rRule = new RRule(rRuleRaw, localDateStart, allDay == 1 ? null : LocalTime.parse(dtStart.substring(dtStart.indexOf(" ") + 1)), title, this.calendars.stream().filter(calendarData -> calendarData.getCalendarId() == calID).findFirst().get(), rRuleRaw.contains("BYDAY") ? null : RRule.DayAbbr.valueOf(localDateStart.getDayOfWeek().toString().substring(0, 2).toUpperCase()));
                    this.rRules.add(rRule);
                }
            }
            return this.rRules;
        });
    }

    public List<CalendarData> getCalendars() {
        return calendars;
    }

    public List<RRule> getrRules() {
        return rRules;
    }

}
