package de.jaskerx.calendar.templates;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import de.jaskerx.calendar.Day;
import de.jaskerx.calendar.MainActivity;
import de.jaskerx.calendar.R;
import de.jaskerx.calendar.adapters.RecyclerViewOverviewAdapter;

public class TemplatesMain {

    private final MainActivity mainActivity;

    public TemplatesMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Toolbar getToolbar() {
        return this.mainActivity.findViewById(R.id.toolbar);
    }

    public RecyclerView getRecyclerViewOverview() {
        return this.mainActivity.findViewById(R.id.recyclerview_overview);
    }

    public RecyclerViewOverviewAdapter getRecyclerViewOverviewAdapter(LinearLayoutManager linearLayoutManager) {
        LocalDate localDateStart = LocalDate.now().minus(20, ChronoUnit.DAYS);
        List<Day> days = new ArrayList<>();
        for(int i = 0; i < 41; i++) {
            LocalDate localDate = localDateStart.plus(i, ChronoUnit.DAYS);
            Day day = new Day(localDate);
            days.add(day);
        }
        return new RecyclerViewOverviewAdapter(this.mainActivity, linearLayoutManager, days);
    }

    public TextView getTextViewYear() {
        return this.mainActivity.findViewById(R.id.textview_year);
    }

    public List<Button> getButtonsMonths() {
        List<Button> buttonsMonths = new ArrayList<>();
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_january));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_february));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_march));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_april));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_may));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_june));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_july));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_august));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_september));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_october));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_november));
        buttonsMonths.add(this.mainActivity.findViewById(R.id.button_december));
        return buttonsMonths;
    }
}
