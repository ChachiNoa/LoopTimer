package com.example.timerrepeticiones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.timerrepeticiones.adapter.StageEditAdapter;
import com.example.timerrepeticiones.data.TimerRepository;
import com.example.timerrepeticiones.databinding.FragmentTimerEditBinding;
import com.example.timerrepeticiones.model.TimerStage;
import com.example.timerrepeticiones.model.TimerWorkout;
import java.util.ArrayList;
import java.util.List;

public class TimerEditFragment extends Fragment {

    private FragmentTimerEditBinding binding;
    private TimerRepository repository;
    private List<TimerStage> stages = new ArrayList<>();
    private StageEditAdapter adapter;
    private TimerWorkout editingWorkout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerEditBinding.inflate(inflater, container, false);
        repository = new TimerRepository(requireContext());
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("timer")) {
            editingWorkout = (TimerWorkout) getArguments().getSerializable("timer");
            if (editingWorkout != null) {
                binding.editTimerName.setText(editingWorkout.getTitle());
                binding.editTimerReps.setText(String.valueOf(editingWorkout.getRepetitions()));
                stages.clear();
                stages.addAll(editingWorkout.getStages());
                binding.btnDeleteTimer.setVisibility(View.VISIBLE);
            }
        }

        // Adaptador para las etapas (Nombre, Tiempo, Color, Sonido)
        adapter = new StageEditAdapter(stages, requireContext());
        binding.recyclerEditStages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEditStages.setAdapter(adapter);

        binding.btnAddStage.setOnClickListener(v -> {
            // Nueva etapa con valores por defecto (Magma Red)
            stages.add(new TimerStage("Etapa " + (stages.size() + 1), 30, "#B22222"));
            adapter.notifyItemInserted(stages.size() - 1);
        });

        binding.btnDeleteTimer.setOnClickListener(v -> {
            if (editingWorkout != null) {
                repository.deleteTimer(editingWorkout.getId());
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        binding.btnSaveTimer.setOnClickListener(v -> {
            String title = binding.editTimerName.getText().toString();
            if (title.isEmpty()) title = "Nuevo Temporizador";
            
            TimerWorkout workout;
            if (editingWorkout != null) {
                workout = editingWorkout;
                workout.setTitle(title);
            } else {
                workout = new TimerWorkout(title);
            }
            
            workout.setStages(new ArrayList<>(stages));
            try {
                workout.setRepetitions(Integer.parseInt(binding.editTimerReps.getText().toString()));
            } catch (NumberFormatException e) {
                workout.setRepetitions(1);
            }

            repository.addOrUpdateTimer(workout);
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
