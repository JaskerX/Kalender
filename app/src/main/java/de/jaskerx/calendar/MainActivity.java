package de.jaskerx.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.jaskerx.calendar.adapter.RecyclerViewOverviewAdapter;
import de.jaskerx.calendar.db.CalendarHolder;
import de.jaskerx.calendar.template.TemplatesMain;

public class MainActivity extends Activity {

    private TextView textViewYear;
    private List<Button> buttonsMonths = new ArrayList<>();
    private int yearDisplayed;
    private int monthDisplayed;
    private CalendarHolder calendarHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        TemplatesMain templates = new TemplatesMain(this);
        Toolbar toolbar = templates.getToolbar();
        toolbar.setTitle(R.string.activity_name_main);
        this.textViewYear = templates.getTextViewYear();
        this.calendarHolder = new CalendarHolder(this.getContentResolver());

        this.calendarHolder.loadCalendars().thenRun(() -> {
            this.calendarHolder.loadRRules().thenRun(() -> {
                this.runOnUiThread(() -> {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    RecyclerView recyclerViewOverview = templates.getRecyclerViewOverview();
                    RecyclerViewOverviewAdapter recyclerViewOverviewAdapter = templates.getRecyclerViewOverviewAdapter(linearLayoutManager);
                    recyclerViewOverview.setLayoutManager(linearLayoutManager);
                    recyclerViewOverview.setAdapter(recyclerViewOverviewAdapter);
                    recyclerViewOverview.scrollToPosition(recyclerViewOverviewAdapter.getItemCount() / 2);

                    this.buttonsMonths = templates.getButtonsMonths();
                    for (int i = 0; i < this.buttonsMonths.size(); i++) {
                        Button button = this.buttonsMonths.get(i);
                        final int finalI = i;
                        button.setOnClickListener(v -> {
                            recyclerViewOverviewAdapter.scrollToMonth(finalI, this.yearDisplayed);
                            recyclerViewOverview.stopScroll();
                        });
                    }
                });
            });
        });
    }

    public void refreshDateDisplayed(LocalDate localDate) {
        this.textViewYear.setText(String.valueOf(localDate.getYear()));
        for(Button button : this.buttonsMonths) {
            button.setBackground(AppCompatResources.getDrawable(this, R.drawable.button_month));
        }
        this.buttonsMonths.get(localDate.getMonthValue() - 1).setBackground(AppCompatResources.getDrawable(this, R.drawable.button_month_selected));
        this.yearDisplayed = localDate.getYear();
        this.monthDisplayed = localDate.getMonthValue();
    }

    public CalendarHolder getCalendarHolder() {
        return calendarHolder;
    }

}
