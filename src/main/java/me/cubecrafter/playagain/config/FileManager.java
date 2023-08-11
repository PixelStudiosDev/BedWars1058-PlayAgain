package me.cubecrafter.playagain.config;

import lombok.Getter;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.xutils.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class FileManager {

    private final YamlConfiguration config;

    public FileManager(PlayAgain plugin) {
        File file = new File("plugins/BedWars1058/Addons/PlayAgain/config.yml");
        if (!file.exists()) {
            FileUtil.copy(plugin.getResource("config.yml"), file);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

}
