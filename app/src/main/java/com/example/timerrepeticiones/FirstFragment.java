package com.example.timerrepeticiones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.timerrepeticiones.adapter.TimerAdapter;
import com.example.timerrepeticiones.data.TimerRepository;
import com.example.timerrepeticiones.databinding.FragmentFirstBinding;
import com.example.timerrepeticiones.model.TimerWorkout;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TimerRepository repository;
    private TimerAdapter adapter;
    private String currentGroupFilter = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        repository = new TimerRepository(requireContext());
        
        if (getArguments() != null) {
            currentGroupFilter = getArguments().getString("filter_group");
        }
        
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        updateToolbarTitle();
        setupRecyclerView();
        setupFab();
    }

    private void updateToolbarTitle() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                String title = (currentGroupFilter == null) ? "Mis Timers" : currentGroupFilter;
                activity.getSupportActionBar().setTitle(title);
            }
        }
    }

    private void setupFab() {
        if (getActivity() != null) {
            View fab = getActivity().findViewById(R.id.fab);
            if (fab != null) {
                fab.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("default_group", currentGroupFilter);
                    NavHostFragment.findNavController(this).navigate(R.id.action_FirstFragment_to_timerEditFragment, bundle);
                });
            }
        }
    }

    private void setupRecyclerView() {
        List<TimerWorkout> allTimers = repository.getAllTimers();
        List<TimerWorkout> filteredTimers = new ArrayList<>();

        if (currentGroupFilter == null) {
            filteredTimers.addAll(allTimers);
        } else {
            for (TimerWorkout t : allTimers) {
                if (currentGroupFilter.equals(t.getGroup())) {
                    filteredTimers.add(t);
                }
            }
        }

        adapter = new TimerAdapter(filteredTimers, new TimerAdapter.OnTimerClickListener() {
            @Override
            public void onTimerClick(TimerWorkout timer) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("timer", timer);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            }

            @Override
            public void onEditClick(TimerWorkout timer) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("timer", timer);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_timerEditFragment, bundle);
            }
        });

        binding.recyclerTimers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTimers.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbarTitle();
        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
