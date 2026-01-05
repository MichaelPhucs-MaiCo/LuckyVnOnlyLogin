package com.vanphuc.autologin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoLoginConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "auto_login_luckyvn.json");

    private String password = "";
    private String firstSlotString = "22";
    private String secondSlotString = "30";
    private boolean autoClickEnabled = true;

    private static AutoLoginConfig instance;

    public static AutoLoginConfig getInstance() {
        if (instance == null) {
            instance = new AutoLoginConfig();
            load();
        }
        return instance;
    }

    private AutoLoginConfig() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        save();
    }

    public String getFirstSlotString() {
        return firstSlotString;
    }

    public void setFirstSlotString(String firstSlotString) {
        this.firstSlotString = firstSlotString;
        save();
    }

    public String getSecondSlotString() {
        return secondSlotString;
    }

    public void setSecondSlotString(String secondSlotString) {
        this.secondSlotString = secondSlotString;
        save();
    }

    public boolean isAutoClickEnabled() {
        return autoClickEnabled;
    }

    public void setAutoClickEnabled(boolean enabled) {
        this.autoClickEnabled = enabled;
        save();
    }

    public static void load() {
        if (!configFile.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            AutoLoginConfig loadedConfig = GSON.fromJson(reader, AutoLoginConfig.class);
            if (loadedConfig != null) {
                instance = loadedConfig;
            }
        } catch (IOException e) {
            ChatUtils.addModMessage("§cKhông thể đọc file cấu hình: " + e.getMessage());
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(getInstance(), writer);
        } catch (IOException e) {
            ChatUtils.addModMessage("§cKhông thể lưu file cấu hình: " + e.getMessage());
        }
    }
}