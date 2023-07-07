package de.jaskerx.calendar.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.provider.CalendarContract;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.jaskerx.calendar.Event;

public class CalendarData {

    private final ContentResolver contentResolver;
    private final long calendarId;
    private final String calendarName;
    private final int calendarColor;

    public CalendarData(ContentResolver contentResolver, long calendarId, String calendarName, int calendarColor) {
        this.contentResolver = contentResolver;
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.calendarColor = calendarColor;
    }

    public List<Event> getEvents(LocalDate localDate) {
        List<Event> events = new ArrayList<>();

        String titleCol = CalendarContract.Events.TITLE;
        String startDateCol = CalendarContract.Events.DTSTART;
        String endDateCol = CalendarContract.Events.DTEND;
        String calendarNameCol = CalendarContract.Events.CALENDAR_DISPLAY_NAME;
        String allDayCol = CalendarContract.Events.ALL_DAY;
        String rDateCol = CalendarContract.Events.RDATE;
        String exDateCol = CalendarContract.Events.EXDATE;
        String timezoneCol = CalendarContract.Events.EVENT_TIMEZONE;
        String timezoneEndCol = CalendarContract.Events.EVENT_END_TIMEZONE;

        String[] projection = new String[] {titleCol, startDateCol, endDateCol, calendarNameCol, rDateCol, exDateCol, allDayCol, timezoneCol, timezoneEndCol};
        String selection = CalendarContract.Events.DELETED + " != 1 AND " + CalendarContract.Events.CALENDAR_ID + " = ? AND " + CalendarContract.Events.RRULE + " IS NULL AND ((" + CalendarContract.Events.DTSTART + " <= ? AND " + CalendarContract.Events.DTEND + " >= ?) OR (" + CalendarContract.Events.EVENT_END_TIMEZONE + " IS NULL AND " + CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + " < ?))";
        String[] selectionArgs = new String[] {String.valueOf(this.calendarId), String.valueOf(localDate.atTime(0, 0, 0).toInstant(ZoneOffset.ofHours(0)).toEpochMilli()), String.valueOf(localDate.atTime(0, 0, 0).plusDays(1).toInstant(ZoneOffset.ofHours(0)).toEpochMilli()), String.valueOf(localDate.atTime(0, 0, 0).toInstant(ZoneOffset.ofHours(0)).toEpochMilli()), String.valueOf(localDate.atTime(0, 0, 0).plusDays(1).toInstant(ZoneOffset.ofHours(0)).toEpochMilli())};

        try(Cursor cursor = this.contentResolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, CalendarContract.Events.DTSTART + ", " + CalendarContract.Events.CALENDAR_DISPLAY_NAME)) {

            int titleColIdx = cursor.getColumnIndex(titleCol);
            int startDateColIdx = cursor.getColumnIndex(startDateCol);
            int endDateColIdx = cursor.getColumnIndex(endDateCol);
            int calendarNameColIdx = cursor.getColumnIndex(calendarNameCol);
            int allDayIdx = cursor.getColumnIndex(allDayCol);
            int rDateColIdx = cursor.getColumnIndex(rDateCol);
            int exDateColIdx = cursor.getColumnIndex(exDateCol);
            int timezoneColIdx = cursor.getColumnIndex(timezoneCol);
            int timezoneEndColIdx = cursor.getColumnIndex(timezoneEndCol);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY);

            while (cursor.moveToNext()) {
                String title = cursor.getString(titleColIdx);
                String startDate = formatter.format(new Date(cursor.getLong(startDateColIdx)));
                String endDate = formatter.format(new Date(cursor.getLong(endDateColIdx)));
                String calendarName = cursor.getString(calendarNameColIdx);
                int allDay = cursor.getInt(allDayIdx);
                String rDate = cursor.getString(rDateColIdx);
                String exDate = cursor.getString(exDateColIdx);
                String timezone = cursor.getString(timezoneColIdx);
                String timezoneEnd = cursor.getString(timezoneEndColIdx);

                events.add(new Event(title, this));
            }
        }

        return events;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public int getCalendarColor() {
        return calendarColor;
    }

}
