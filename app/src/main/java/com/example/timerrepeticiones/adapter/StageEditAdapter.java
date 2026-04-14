package com.example.timerrepeticiones.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timerrepeticiones.R;
import com.example.timerrepeticiones.model.TimerStage;
import java.util.ArrayList;
import java.util.List;

public class StageEditAdapter extends RecyclerView.Adapter<StageEditAdapter.ViewHolder> {

    private final List<TimerStage> stages;
    private final Context context;
    private final String[] soundNames = {"Predeterminado", "Pitido", "Alarma", "Campana"};
    private final List<CustomColor> availableColors = new ArrayList<>();

    private static class CustomColor {
        String name;
        String hex;
        CustomColor(String name, String hex) { this.name = name; this.hex = hex; }
    }

    public StageEditAdapter(List<TimerStage> stages, Context context) {
        this.stages = stages;
        this.context = context;
        availableColors.add(new CustomColor("Rojo Magma", "#B22222"));
        availableColors.add(new CustomColor("Naranja", "#CC5500"));
        availableColors.add(new CustomColor("Verde", "#4CAF50"));
        availableColors.add(new CustomColor("Azul", "#2196F3"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stage_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimerStage stage = stages.get(position);
        
        holder.editName.setText(stage.getName());
        holder.editDuration.setText(String.valueOf(stage.getDurationSeconds()));
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(stage.getColorHex()));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.RED);
        }

        // Configuración de Spinner de sonidos
        ArrayAdapter<String> soundAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, soundNames);
        soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerSound.setAdapter(soundAdapter);

        holder.viewColor.setOnClickListener(v -> {
            String[] names = new String[availableColors.size()];
            for (int i = 0; i < availableColors.size(); i++) names[i] = availableColors.get(i).name;
            
            new AlertDialog.Builder(context)
                .setTitle("Elegir Color")
                .setItems(names, (dialog, which) -> {
                    stage.setColorHex(availableColors.get(which).hex);
                    holder.viewColor.setBackgroundColor(Color.parseColor(stage.getColorHex()));
                }).show();
        });

        holder.editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { stage.setName(s.toString()); }
        });

        holder.editDuration.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    stage.setDurationSeconds(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    stage.setDurationSeconds(0);
                }
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                stages.remove(currentPos);
                notifyItemRemoved(currentPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editName, editDuration;
        View viewColor;
        Spinner spinnerSound;
        View btnRemove;
        ViewHolder(View view) {
            super(view);
            editName = view.findViewById(R.id.edit_stage_name);
            editDuration = view.findViewById(R.id.edit_stage_duration);
            viewColor = view.findViewById(R.id.view_color_indicator);
            spinnerSound = view.findViewById(R.id.spinner_sound);
            btnRemove = view.findViewById(R.id.btn_remove_stage);
        }
    }
}
