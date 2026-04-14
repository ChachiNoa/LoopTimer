package com.example.timerrepeticiones.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.timerrepeticiones.model.TimerStage;
import com.example.timerrepeticiones.model.TimerWorkout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TimerRepository {
    private static final String PREFS_NAME = "custom_timer_storage";
    private static final String KEY_TIMERS = "all_timers_json";
    private final SharedPreferences sharedPreferences;

    public TimerRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<TimerWorkout> getAllTimers() {
        String json = sharedPreferences.getString(KEY_TIMERS, null);
        if (json == null) {
            return createDefaultTimers();
        }
        try {
            return parseTimers(json);
        } catch (JSONException e) {
            return createDefaultTimers();
        }
    }

    public void saveTimers(List<TimerWorkout> timers) {
        try {
            JSONArray array = new JSONArray();
            for (TimerWorkout timer : timers) {
                JSONObject obj = new JSONObject();
                obj.put("id", timer.getId());
                obj.put("title", timer.getTitle());
                obj.put("repetitions", timer.getRepetitions());
                obj.put("group", timer.getGroup());
                
                JSONArray stagesArray = new JSONArray();
                for (TimerStage stage : timer.getStages()) {
                    JSONObject sObj = new JSONObject();
                    sObj.put("name", stage.getName());
                    sObj.put("duration", stage.getDurationSeconds());
                    sObj.put("sound", stage.getSoundId());
                    sObj.put("color", stage.getColorHex());
                    stagesArray.put(sObj);
                }
                obj.put("stages", stagesArray);
                array.put(obj);
            }
            sharedPreferences.edit().putString(KEY_TIMERS, array.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addOrUpdateTimer(TimerWorkout workout) {
        List<TimerWorkout> timers = getAllTimers();
        int index = -1;
        for (int i = 0; i < timers.size(); i++) {
            if (timers.get(i).getId().equals(workout.getId())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            timers.set(index, workout);
        } else {
            timers.add(workout);
        }
        saveTimers(timers);
    }

    public void deleteTimer(String id) {
        List<TimerWorkout> timers = getAllTimers();
        TimerWorkout toDelete = null;
        for (TimerWorkout t : timers) {
            if (t.getId().equals(id)) {
                toDelete = t;
                break;
            }
        }
        if (toDelete != null) {
            timers.remove(toDelete);
            saveTimers(timers);
        }
    }

    private List<TimerWorkout> parseTimers(String json) throws JSONException {
        List<TimerWorkout> timers = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            TimerWorkout timer = new TimerWorkout(obj.getString("title"));
            timer.setId(obj.getString("id"));
            timer.setRepetitions(obj.getInt("repetitions"));
            timer.setGroup(obj.getString("group"));
            
            JSONArray stagesArray = obj.getJSONArray("stages");
            List<TimerStage> stages = new ArrayList<>();
            for (int j = 0; j < stagesArray.length(); j++) {
                JSONObject sObj = stagesArray.getJSONObject(j);
                TimerStage stage = new TimerStage(sObj.getString("name"), sObj.getInt("duration"), sObj.getString("color"));
                stage.setSoundId(sObj.getString("sound"));
                stages.add(stage);
            }
            timer.setStages(stages);
            timers.add(timer);
        }
        return timers;
    }

    private List<TimerWorkout> createDefaultTimers() {
        List<TimerWorkout> defaults = new ArrayList<>();
        
        TimerWorkout tabata = new TimerWorkout("Tabata Estándar");
        tabata.getStages().add(new TimerStage("Ejercicio", 20, "#B22222"));
        tabata.getStages().add(new TimerStage("Descanso", 10, "#4CAF50"));
        tabata.setRepetitions(8);
        tabata.setGroup("HIIT");

        TimerWorkout pomodoro = new TimerWorkout("Estudio Pomodoro");
        pomodoro.getStages().add(new TimerStage("Concentración", 1500, "#2196F3"));
        pomodoro.getStages().add(new TimerStage("Descanso", 300, "#FFEB3B"));
        pomodoro.setRepetitions(4);
        pomodoro.setGroup("Estudio");

        defaults.add(tabata);
        defaults.add(pomodoro);
        saveTimers(defaults);
        return defaults;
    }
}
