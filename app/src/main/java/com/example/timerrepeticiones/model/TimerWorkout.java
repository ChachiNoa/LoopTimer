package com.example.timerrepeticiones.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimerWorkout implements Serializable {
    private String id;
    private String title;
    private List<TimerStage> stages;
    private int repetitions;
    private String group;

    public TimerWorkout(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.stages = new ArrayList<>();
        this.repetitions = 1;
        this.group = "General";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<TimerStage> getStages() { return stages; }
    public void setStages(List<TimerStage> stages) { this.stages = stages; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
