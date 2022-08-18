package me.cubecrafter.playagain;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.server.ServerType;
import lombok.Getter;
import me.cubecrafter.playagain.arena.ArenaData;
import me.cubecrafter.playagain.arena.BungeeArenaManager;
import me.cubecrafter.playagain.config.FileManager;
import me.cubecrafter.playagain.listeners.ArenaListener;
import me.cubecrafter.playagain.listeners.InventoryListener;
import me.cubecrafter.playagain.proxy.ProxyListener;
import me.cubecrafter.playagain.utils.TextUtil;
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
    private BungeeArenaManager bungeeManager;
    private ProxyListener proxyListener;
    private boolean bungee = false;
    private boolean lobby = false;

    @Override
    public void onEnable() {
        instance = this;
        if (isBedWarsEnabled()) {
            fileManager = new FileManager(this, false);
            bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
            getServer().getPluginManager().registerEvents(new InventoryListener(), this);
            getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
            if (bedWars.getServerType() == ServerType.BUNGEE) {
                bungee = true;
                bungeeManager = new BungeeArenaManager(this);
                getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            }
        } else if (isBedWarsProxyEnabled()) {
            fileManager = new FileManager(this, true);
            lobby = true;
            proxyListener = new ProxyListener();
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        } else {
            TextUtil.severe("Bedwars plugin not found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new Metrics(this, 14060);
    }

    public boolean isBedWarsEnabled() {
        return getServer().getPluginManager().isPluginEnabled("BedWars1058");
    }

    public boolean isBedWarsProxyEnabled() {
        return getServer().getPluginManager().isPluginEnabled("BedWarsProxy");
    }

    @Override
    public void onDisable() {
        if (bungeeManager != null) bungeeManager.close();
        if (proxyListener != null) proxyListener.close();
        getServer().getScheduler().cancelTasks(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
    }

    public List<String> getArenaGroups() {
        List<String> groups = new ArrayList<>();
        groups.add("All");
        if (bungee) {
            groups.addAll(bungeeManager.getArenas().stream().map(ArenaData::getGroup).distinct().collect(Collectors.toList()));
        } else {
            groups.addAll(bedWars.getArenaUtil().getArenas().stream().map(IArena::getGroup).distinct().collect(Collectors.toList()));
        }
        return groups;
    }

}
