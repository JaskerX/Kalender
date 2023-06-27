package de.jaskerx.calendar.adapters;

import android.os.Handler;
import android.os.Looper;
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
import java.util.Collections;
import java.util.List;

import de.jaskerx.calendar.Day;
import de.jaskerx.calendar.MainActivity;
import de.jaskerx.calendar.R;

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

        holder.getTextViewDayOfMonth().setText(String.valueOf(day.getLocalDate().getDayOfMonth()));
        holder.getTextViewDayOfWeekName().setText(day.getLocalizedDayOfWeek());

        holder.getParent().setBackground(AppCompatResources.getDrawable(this.mainActivity, day.getLocalDate().equals(LocalDate.now()) ? R.drawable.overview_back_today : R.drawable.overview_back));

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
            Day day = new Day(localDate);
            this.days.add(0, day);
        }
        new Handler().post(() -> notifyItemRangeInserted(0, 20));
    }

    public void loadDataAfter(LocalDate initialDate) {
        for(int i = 1; i < 21; i++) {
            LocalDate localDate = initialDate.plus(i, ChronoUnit.DAYS);
            Day day = new Day(localDate);
            this.days.add(day);
        }
        new Handler().post(() -> notifyItemRangeInserted(days.size() - 21, 20));
    }

    public void scrollToMonth(int month) {
        new Thread(() -> {
            Looper.prepare();
            int year = this.mainActivity.getYearDisplayed();
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
                if (day.getLocalDate().getDayOfMonth() == 1 && day.getLocalDate().getMonthValue() == month + 1) {
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

        public ViewHolder(View view) {
            super(view);

            this.parent = (LinearLayout) view;
            this.textViewDayOfMonth = view.findViewById(R.id.textView_dayOfMonth);
            this.textViewDayOfWeekName = view.findViewById(R.id.textView_dayOfWeekName);
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
    }

}
