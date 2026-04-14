package com.example.timerrepeticiones.model;

import java.io.Serializable;
import java.util.UUID;

public class TimerStage implements Serializable {
    private String id;
    private String name;
    private int durationSeconds;
    private String soundId; // ID o ruta del sonido
    private String colorHex; // Color en formato Hex

    public TimerStage(String name, int durationSeconds, String colorHex) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.colorHex = colorHex;
        this.soundId = "default_beep";
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public String getSoundId() { return soundId; }
    public void setSoundId(String soundId) { this.soundId = soundId; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}
