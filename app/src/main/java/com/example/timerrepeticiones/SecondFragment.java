package com.example.timerrepeticiones;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.timerrepeticiones.data.SettingsRepository;
import com.example.timerrepeticiones.databinding.FragmentSecondBinding;
import com.example.timerrepeticiones.model.TimerStage;
import com.example.timerrepeticiones.model.TimerWorkout;
import java.util.Locale;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private TimerWorkout workout;
    private SettingsRepository settingsRepository;
    
    private int currentRepetition = 1;
    private int currentStageIndex = 0;
    private long millisLeft;
    private boolean isRunning = false;
    
    private CountDownTimer countDownTimer;
    private ToneGenerator toneGenerator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        settingsRepository = new SettingsRepository(requireContext());
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, settingsRepository.getVolume());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            workout = (TimerWorkout) getArguments().getSerializable("timer");
            if (workout != null) {
                initWorkout();
            }
        }

        binding.btnPlayPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        binding.btnReset.setOnClickListener(v -> resetWorkout());
    }

    private void initWorkout() {
        binding.textWorkoutTitle.setText(workout.getTitle());
        currentRepetition = 1;
        currentStageIndex = 0;
        loadStage(workout.getStages().get(currentStageIndex));
    }

    private void loadStage(TimerStage stage) {
        binding.textStageName.setText(stage.getName());
        binding.textCycleReps.setText(String.format(Locale.getDefault(), "Ciclo %d / %d", currentRepetition, workout.getRepetitions()));
        
        // Feedback Visual: Cambio de fondo según color de la etapa
        try {
            binding.timerBackground.setBackgroundColor(Color.parseColor(stage.getColorHex()));
        } catch (Exception e) {
            binding.timerBackground.setBackgroundColor(Color.WHITE);
        }

        millisLeft = stage.getDurationSeconds() * 1000L;
        binding.progressTimer.setMax((int) stage.getDurationSeconds());
        updateCountdownUI();
    }

    private void startTimer() {
        isRunning = true;
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        
        countDownTimer = new CountDownTimer(millisLeft, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateCountdownUI();
            }

            @Override
            public void onFinish() {
                playNotification(workout.getStages().get(currentStageIndex));
                nextStep();
            }
        }.start();
    }

    private void pauseTimer() {
        isRunning = false;
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void nextStep() {
        currentStageIndex++;
        if (currentStageIndex >= workout.getStages().size()) {
            currentStageIndex = 0;
            currentRepetition++;
        }

        if (currentRepetition > workout.getRepetitions()) {
            finishWorkout();
        } else {
            loadStage(workout.getStages().get(currentStageIndex));
            startTimer();
        }
    }

    private void updateCountdownUI() {
        int seconds = (int) (millisLeft / 1000);
        binding.textCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
        binding.progressTimer.setProgress(seconds);
    }

    private void playNotification(TimerStage stage) {
        // Feedback Auditivo
        int tone = ToneGenerator.TONE_CDMA_PIP;
        if ("Alarma".equals(stage.getSoundId())) tone = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
        else if ("Campana".equals(stage.getSoundId())) tone = ToneGenerator.TONE_CDMA_HIGH_L;
        
        toneGenerator.startTone(tone, 300);

        // Feedback Háptico (Vibración)
        if (settingsRepository.isVibrationEnabled()) {
            vibrate();
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(300);
            }
        }
    }

    private void resetWorkout() {
        pauseTimer();
        initWorkout();
    }

    private void finishWorkout() {
        isRunning = false;
        binding.textStageName.setText("¡Completado!");
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        binding.timerBackground.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        if (toneGenerator != null) toneGenerator.release();
        binding = null;
    }
}
