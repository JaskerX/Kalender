package de.jaskerx.calendar.adapter;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jaskerx.calendar.Day;
import de.jaskerx.calendar.Event;
import de.jaskerx.calendar.MainActivity;
import de.jaskerx.calendar.R;
import de.jaskerx.calendar.db.CalendarData;

public class RecyclerViewOverviewAdapter extends RecyclerView.Adapter<RecyclerViewOverviewAdapter.ViewHolder> {

    private final MainActivity mainActivity;
    private final LinearLayoutManager linearLayoutManager;
    private final List<Day> days;

    public RecyclerViewOverviewAdapter(MainActivity mainActivity, LinearLayoutManager linearLayoutManager, List<Day> days) {
        this.mainActivity = mainActivity;
        this.linearLayoutManager = linearLayoutManager;
        this.days = Collections.synchronizedList(days);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = this.days.get(position);

        //new Thread(() -> {
            //new Handler().post(() -> {
                holder.getTextViewDayOfMonth().setText(String.valueOf(day.getLocalDate().getDayOfMonth()));
                holder.getTextViewDayOfWeekName().setText(day.getLocalizedDayOfWeek());
                holder.getLinearLayoutEvents().removeAllViews();
            //});

            for (Event event : day.getEvents()) {
                View view = LayoutInflater.from(this.mainActivity).inflate(R.layout.calendar_event_entry, null);
                ((TextView) view.findViewById(R.id.textView_title)).setText(event.getTitle());
                ((TextView) view.findViewById(R.id.textView_time)).setText(event.getLocalTime() == null ? "" : event.getLocalTime().toString());
                if(event.getLocalTime() == null) {
                    view.findViewById(R.id.textView_time).setPadding(0, 0, 0, 0);
                }
                view.findViewById(R.id.linearLayout_eventEntry).setBackgroundColor(event.getCalendarData().getCalendarColor());
                new Handler().post(() -> {
                    holder.getLinearLayoutEvents().addView(view);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.findViewById(R.id.linearLayout_eventEntry).getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 10);
                    view.setLayoutParams(layoutParams);
                });
            }

            this.mainActivity.getCalendarHolder().getrRules().stream().filter(rRule -> rRule.contains(day.getLocalDate())).forEach(rRule -> {
                View view = LayoutInflater.from(this.mainActivity).inflate(R.layout.calendar_event_entry, null);
                ((TextView) view.findViewById(R.id.textView_title)).setText(rRule.getTitle());
                ((TextView) view.findViewById(R.id.textView_time)).setText(rRule.getLocalTime() == null ? "" : rRule.getLocalTime().toString());
                if(rRule.getLocalTime() == null) {
                    view.findViewById(R.id.textView_time).setPadding(0, 0, 0, 0);
                }
                view.findViewById(R.id.linearLayout_eventEntry).setBackgroundColor(rRule.getCalendarData().getCalendarColor());
                new Handler().post(() -> {
                    holder.getLinearLayoutEvents().addView(view);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.findViewById(R.id.linearLayout_eventEntry).getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 10);
                    view.setLayoutParams(layoutParams);
                });
            });

            //new Handler().post(() -> {
                holder.getParent().setBackground(AppCompatResources.getDrawable(this.mainActivity, day.getLocalDate().equals(LocalDate.now()) ? R.drawable.overview_back_today : R.drawable.overview_back));
                holder.getParent().setOnClickListener(v -> {
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(day.getLocalDate().getYear(), day.getLocalDate().getMonthValue() - 1, day.getLocalDate().getDayOfMonth(), 0, 0);
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                    mainActivity.startActivity(intent);
                });
            //});
        //}).start();

        /*if(position <= 5) {
            for (int i = 0; i < 5; i++) {
                LocalDate localDate = this.days.get(0).getLocalDate().minus(1, ChronoUnit.DAYS);
                List<Event> events = new ArrayList<>();
                for (CalendarData calendarData : this.mainActivity.getCalendarHolder().getCalendars()) {
                    List<Event> eventsData = calendarData.getEvents(localDate);
                    if (eventsData.size() == 0) {
                        continue;
                    }
                    events.addAll(eventsData);
                }
                this.days.add(0, new Day(localDate, events));
                new Handler().post(() -> notifyItemRangeInserted(0, 1));
            }
        }*/

        if(position == this.getItemCount() - 1) {
            this.loadDataAfter(day.getLocalDate());
        } else if(position == 0) {
            this.loadDataBefore(day.getLocalDate());
        }
    }

    @Override
    public int getItemCount() {
        return this.days.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        this.refreshDateDisplayed();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        this.refreshDateDisplayed();
    }

    public void refreshDateDisplayed() {
        int firstVisibleItemPosition = this.linearLayoutManager.findFirstVisibleItemPosition();
        if(firstVisibleItemPosition < 0) {
            return;
        }
        this.mainActivity.refreshDateDisplayed(this.days.get(firstVisibleItemPosition).getLocalDate());
    }

    public void loadDataBefore(LocalDate initialDate) {
        for(int i = 1; i < 21; i++) {
            LocalDate localDate = initialDate.minus(i, ChronoUnit.DAYS);
            List<Event> events = new ArrayList<>();
            for(CalendarData calendarData : this.mainActivity.getCalendarHolder().getCalendars()) {
                List<Event> eventsData = calendarData.getEvents(localDate);
                if(eventsData.size() == 0) {
                    continue;
                }
                events.addAll(eventsData);
            }
            Day day = new Day(localDate, events);
            this.days.add(0, day);
        }
        new Handler().post(() -> notifyItemRangeInserted(0, 20));
    }

    public void loadDataAfter(LocalDate initialDate) {
        for(int i = 1; i < 21; i++) {
            LocalDate localDate = initialDate.plus(i, ChronoUnit.DAYS);
            List<Event> events = new ArrayList<>();
            for(CalendarData calendarData : this.mainActivity.getCalendarHolder().getCalendars()) {
                List<Event> eventsData = calendarData.getEvents(localDate);
                if(eventsData.size() == 0) {
                    continue;
                }
                events.addAll(eventsData);
            }
            Day day = new Day(localDate, events);
            this.days.add(day);
        }
        new Handler().post(() -> notifyItemRangeInserted(days.size() - 21, 20));
    }

    public void scrollToMonth(int month, int year) {
        new Thread(() -> {
            Looper.prepare();
            int startIndex = 0;
            while (this.days.get(0).getLocalDate().getMonthValue() >= month + 1 && this.days.get(0).getLocalDate().getYear() == year) {
                this.loadDataBefore(this.days.get(0).getLocalDate());
            }
            while (this.days.get(this.days.size() - 1).getLocalDate().getMonthValue() < month + 1 && this.days.get(this.days.size() - 1).getLocalDate().getYear() == year) {
                startIndex = this.days.size();
                this.loadDataAfter(this.days.get(this.days.size() - 1).getLocalDate());
            }
            for (int i = startIndex; i < this.days.size(); i++) {
                Day day = this.days.get(i);
                if (day.getLocalDate().getDayOfMonth() == 1 && day.getLocalDate().getMonthValue() == month + 1 && day.getLocalDate().getYear() == year) {
                    final int finalI = i;
                    this.mainActivity.runOnUiThread(() -> {
                        linearLayoutManager.scrollToPositionWithOffset(finalI, 0);
                        refreshDateDisplayed();
                    });
                }
            }
        }).start();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout parent;
        private final TextView textViewDayOfMonth;
        private final TextView textViewDayOfWeekName;
        private final LinearLayout linearLayoutEvents;

        public ViewHolder(View view) {
            super(view);

            this.parent = (LinearLayout) view;
            this.textViewDayOfMonth = view.findViewById(R.id.textView_dayOfMonth);
            this.textViewDayOfWeekName = view.findViewById(R.id.textView_dayOfWeekName);
            this.linearLayoutEvents = view.findViewById(R.id.linearlayout_events);
        }

        public TextView getTextViewDayOfMonth() {
            return textViewDayOfMonth;
        }

        public TextView getTextViewDayOfWeekName() {
            return textViewDayOfWeekName;
        }

        public LinearLayout getParent() {
            return parent;
        }

        public LinearLayout getLinearLayoutEvents() {
            return linearLayoutEvents;
        }
    }

}
