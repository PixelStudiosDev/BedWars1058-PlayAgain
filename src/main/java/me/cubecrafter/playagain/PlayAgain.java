package me.cubecrafter.playagain;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import lombok.Getter;
import me.cubecrafter.playagain.config.FileManager;
import me.cubecrafter.playagain.listeners.ArenaListener;
import me.cubecrafter.xutils.Tasks;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class PlayAgain extends JavaPlugin {

    @Getter
    private static PlayAgain instance;

    private FileManager fileManager;
    private BedWars bedWars;

    @Override
    public void onEnable() {
        instance = this;

        this.fileManager = new FileManager(this);
        this.bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);

        new Metrics(this, 14060);
    }

    @Override
    public void onDisable() {
        Tasks.cancelAll();
    }

    public List<String> getArenaGroups() {
        List<String> groups = new ArrayList<>();

        groups.add("All");
        groups.addAll(bedWars.getArenaUtil().getArenas().stream().map(IArena::getGroup).distinct().collect(Collectors.toList()));

        return groups;
    }

}
