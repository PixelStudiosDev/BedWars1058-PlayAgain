package me.cubecrafter.playagain.config;

import lombok.Getter;
import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileManager {

    @Getter
    private YamlConfiguration config;
    private final File configFile;

    public FileManager(PlayAgain plugin, boolean proxy) {
        File folder = new File("plugins/" + (proxy ? "BedWarsProxy" : "BedWars1058") + "/Addons/PlayAgain");
        if (!folder.exists()) folder.mkdirs();
        configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            saveResource(plugin.getResource("config.yml"), configFile);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveResource(InputStream in, File destination) {
        try (OutputStream out = Files.newOutputStream(destination.toPath())) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

}
