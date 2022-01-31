package me.cubecrafter.playagain.utils;

import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class FileManager {

    private YamlConfiguration yml;
    private final File config;

    public FileManager(String filename, String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        config = new File(path + "/" + filename + ".yml");
        if(!config.exists()){
            try{
               config.createNewFile();
            }catch(IOException ex){
                ex.printStackTrace();
            }
            Reader reader = new InputStreamReader(PlayAgain.getInstance().getResource(filename + ".yml"));
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(reader);
            try{
                cfg.save(config);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        load();
        updateConfig();
    }

    public void load(){
        yml = YamlConfiguration.loadConfiguration(config);
    }

    public void save(){
        try{
           yml.save(config);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public int getConfigVersion(){
        return yml.getInt("config-version");
    }

    public void setConfigVersion(int version){
        yml.set("config-version", version);
    }

    public YamlConfiguration getYml(){
        return yml;
    }

    public void updateConfig(){
        if(getConfigVersion() == 1){
            yml.set("messages.no-permission-arena-selector", "&cYou don't have the permission to select the arena!");
            yml.set("sounds.no-permission-arena-selector", "ENTITY_VILLAGER_NO");
            yml.set("playagain-countdown.enabled", true);
            yml.set("playagain-countdown.type", "ACTIONBAR");
            yml.set("sounds.play-again-countdown", "ENTITY_CHICKEN_EGG");
            yml.set("messages.countdown-message", "&ePlaying again in &6{seconds}s");
            yml.set("messages.countdown-stopped", "&cCountdown stopped because you have changed world!");
            setConfigVersion(3);
            save();
        }else if(getConfigVersion() == 2){
            yml.set("playagain-countdown.enabled", true);
            yml.set("playagain-countdown.type", "ACTIONBAR");
            yml.set("sounds.play-again-countdown", "ENTITY_CHICKEN_EGG");
            yml.set("messages.countdown-message", "&ePlaying again in &6{seconds}s");
            yml.set("messages.countdown-stopped", "&cCountdown stopped because you have changed world!");
            setConfigVersion(3);
            save();
        }
    }

}
