package com.example.timerrepeticiones;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.timerrepeticiones.data.SettingsRepository;
import com.example.timerrepeticiones.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsRepository settingsRepository;
    private ToneGenerator toneGenerator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        settingsRepository = new SettingsRepository(requireContext());
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, settingsRepository.getVolume());
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.switchDarkMode.setChecked(settingsRepository.isDarkMode());
        binding.seekVolume.setProgress(settingsRepository.getVolume());
        binding.switchVibration.setChecked(settingsRepository.isVibrationEnabled());

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsRepository.setDarkMode(isChecked);
            if (getActivity() != null) getActivity().recreate();
        });

        binding.seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    settingsRepository.setVolume(progress);
                    if (toneGenerator != null) toneGenerator.release();
                    toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnTestSound.setOnClickListener(v -> {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
            if (binding.switchVibration.isChecked()) vibrate();
        });

        binding.switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> 
            settingsRepository.setVibrationEnabled(isChecked));
    }

    private void vibrate() {
        Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(200);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toneGenerator != null) toneGenerator.release();
        binding = null;
    }
}
