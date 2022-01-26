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
            InputStream inputStream = PlayAgain.getInstance().getResource(filename + ".yml");
            Reader reader = new InputStreamReader(inputStream);
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
            setConfigVersion(2);
            save();
        }
    }

}
