package com.example.timerrepeticiones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timerrepeticiones.R;
import com.example.timerrepeticiones.model.TimerWorkout;
import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {

    private final List<TimerWorkout> timers;
    private final OnTimerClickListener listener;

    public interface OnTimerClickListener {
        void onTimerClick(TimerWorkout timer);
        void onEditClick(TimerWorkout timer);
    }

    public TimerAdapter(List<TimerWorkout> timers, OnTimerClickListener listener) {
        this.timers = timers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimerWorkout timer = timers.get(position);
        holder.textTitle.setText(timer.getTitle());
        holder.textGroup.setText(timer.getGroup());
        
        int totalStages = timer.getStages().size();
        holder.textSummary.setText(totalStages + " etapas - " + timer.getRepetitions() + " repeticiones");
        
        holder.itemView.setOnClickListener(v -> listener.onTimerClick(timer));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(timer));
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textGroup, textSummary;
        View btnEdit;
        ViewHolder(View view) {
            super(view);
            textTitle = view.findViewById(R.id.text_timer_title);
            textGroup = view.findViewById(R.id.text_timer_group);
            textSummary = view.findViewById(R.id.text_timer_summary);
            btnEdit = view.findViewById(R.id.btn_edit);
        }
    }
}
