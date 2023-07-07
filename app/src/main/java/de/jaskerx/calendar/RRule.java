package de.jaskerx.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import de.jaskerx.calendar.db.CalendarData;

public class RRule {

    private final String input;
    private final LocalDate firstDate;
    private final String title;
    private final CalendarData calendarData;
    private Freq freq;
    private DayAbbr byDay;
    private int interval = 1;

    public RRule(String input, LocalDate firstDate, String title, CalendarData calendarData, @Nullable DayAbbr byDay) {
        this.input = input;
        this.firstDate = firstDate;
        this.title = title;
        this.calendarData = calendarData;
        this.byDay = byDay;
        String[] args = input.split(";");
        for(String arg : args) {
            String key = arg.substring(0, arg.indexOf("="));
            String value = arg.substring(arg.indexOf("=") + 1);
            switch (key) {
                case "FREQ" -> this.freq = Freq.valueOf(value);
                case "BYDAY" -> this.byDay = DayAbbr.valueOf(value);
                case "INTERVAL" -> this.interval = Integer.parseInt(value);
            }
        }
        if(this.byDay == null) {
            try {
                throw new Exception("Please provide a valid day! Examples: MO, TU, ...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean contains(LocalDate localDate) {
        if(!localDate.getDayOfWeek().toString().substring(0, 2).equals(this.byDay.toString())) {
            return false;
        }
        if(this.freq.getChronoUnit().between(this.firstDate, localDate) % this.interval != 0) {
            return false;
        }
        if(!this.firstDate.plus(this.freq.getChronoUnit().between(this.firstDate, localDate), this.freq.getChronoUnit()).equals(localDate)) {
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return this.input;
    }

    public String getTitle() {
        return title;
    }

    public CalendarData getCalendarData() {
        return calendarData;
    }

    public enum Freq {
        YEARLY (ChronoUnit.YEARS),
        MONTHLY (ChronoUnit.MONTHS),
        WEEKLY (ChronoUnit.WEEKS),
        DAILY (ChronoUnit.DAYS),
        HOURLY (ChronoUnit.HOURS),
        MINUTELY (ChronoUnit.MINUTES),
        SECONDLY (ChronoUnit.SECONDS);

        private final ChronoUnit chronoUnit;

        private Freq(ChronoUnit chronoUnit) {
            this.chronoUnit = chronoUnit;
        }

        public ChronoUnit getChronoUnit() {
            return chronoUnit;
        }
    }

    public enum DayAbbr {
        MO,
        TU,
        WE,
        TH,
        FR,
        SA,
        SU
    }

}
