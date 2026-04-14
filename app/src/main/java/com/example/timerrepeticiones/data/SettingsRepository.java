package com.example.timerrepeticiones.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SettingsRepository {
    private static final String PREFS_NAME = "custom_timer_settings";
    private static final String KEY_VOLUME = "master_volume";
    private static final String KEY_VIBRATION = "vibration_enabled";
    private static final String KEY_THEME_DARK = "theme_dark_mode";
    private static final String KEY_CUSTOM_COLORS = "custom_colors_list";
    private static final String KEY_CUSTOM_SOUNDS = "custom_sounds_list";
    private static final String KEY_GROUPS = "groups_list";

    private final SharedPreferences prefs;

    public static class CustomColor {
        public String name;
        public String hex;
        public CustomColor(String name, String hex) { this.name = name; this.hex = hex; }
    }

    public static class CustomSound {
        public String name;
        public String uri;
        public CustomSound(String name, String uri) { this.name = name; this.uri = uri; }
    }

    public SettingsRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getVolume() { return prefs.getInt(KEY_VOLUME, 80); }
    public void setVolume(int volume) { prefs.edit().putInt(KEY_VOLUME, volume).apply(); }

    public boolean isVibrationEnabled() { return prefs.getBoolean(KEY_VIBRATION, true); }
    public void setVibrationEnabled(boolean enabled) { prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply(); }

    public boolean isDarkMode() { return prefs.getBoolean(KEY_THEME_DARK, false); }
    public void setDarkMode(boolean enabled) { prefs.edit().putBoolean(KEY_THEME_DARK, enabled).apply(); }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        String json = prefs.getString(KEY_GROUPS, null);
        if (json == null) {
            groups.add("General");
            groups.add("HIIT");
            groups.add("Gym");
            groups.add("Estudio");
        } else {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) groups.add(array.getString(i));
            } catch (JSONException ignored) {}
        }
        return groups;
    }

    public void saveGroups(List<String> groups) {
        JSONArray array = new JSONArray(groups);
        prefs.edit().putString(KEY_GROUPS, array.toString()).apply();
    }

    public List<CustomColor> getAvailableColors() {
        List<CustomColor> colors = new ArrayList<>();
        String json = prefs.getString(KEY_CUSTOM_COLORS, null);
        if (json == null) {
            colors.add(new CustomColor("Rojo Magma", "#B22222"));
            colors.add(new CustomColor("Naranja Oscuro", "#CC5500"));
            colors.add(new CustomColor("Verde Bosque", "#2E7D32"));
            colors.add(new CustomColor("Azul Marino", "#1565C0"));
        } else {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    colors.add(new CustomColor(obj.getString("name"), obj.getString("hex")));
                }
            } catch (JSONException ignored) {}
        }
        return colors;
    }

    public void addCustomColor(String name, String hex) {
        List<CustomColor> colors = getAvailableColors();
        colors.add(new CustomColor(name, hex));
        saveColors(colors);
    }

    private void saveColors(List<CustomColor> colors) {
        JSONArray array = new JSONArray();
        try {
            for (CustomColor c : colors) {
                JSONObject obj = new JSONObject();
                obj.put("name", c.name);
                obj.put("hex", c.hex);
                array.put(obj);
            }
            prefs.edit().putString(KEY_CUSTOM_COLORS, array.toString()).apply();
        } catch (JSONException ignored) {}
    }

    public List<CustomSound> getAvailableSounds() {
        List<CustomSound> sounds = new ArrayList<>();
        String json = prefs.getString(KEY_CUSTOM_SOUNDS, null);
        if (json == null) {
            sounds.add(new CustomSound("Pitido", "default"));
            sounds.add(new CustomSound("Alarma", "alarm"));
            sounds.add(new CustomSound("Campana", "bell"));
        } else {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    sounds.add(new CustomSound(obj.getString("name"), obj.getString("uri")));
                }
            } catch (JSONException ignored) {}
        }
        return sounds;
    }

    public void addCustomSound(String name, String uri) {
        List<CustomSound> sounds = getAvailableSounds();
        sounds.add(new CustomSound(name, uri));
        saveSounds(sounds);
    }

    private void saveSounds(List<CustomSound> sounds) {
        JSONArray array = new JSONArray();
        try {
            for (CustomSound s : sounds) {
                JSONObject obj = new JSONObject();
                obj.put("name", s.name);
                obj.put("uri", s.uri);
                array.put(obj);
            }
            prefs.edit().putString(KEY_CUSTOM_SOUNDS, array.toString()).apply();
        } catch (JSONException ignored) {}
    }
}
