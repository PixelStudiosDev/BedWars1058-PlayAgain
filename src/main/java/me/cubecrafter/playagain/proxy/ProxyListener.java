package me.cubecrafter.playagain.proxy;

import com.andrei1058.bedwars.proxy.api.ArenaStatus;
import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.arena.ArenaData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProxyListener implements Runnable, Listener {

    private final ProxySocket proxySocket;
    private final Map<UUID, PlayerCache> joinCache = new HashMap<>();
    private final Gson gson = new Gson();

    public ProxyListener() {
        proxySocket = new ProxySocket();
        Bukkit.getPluginManager().registerEvents(this, PlayAgain.getInstance());
        Bukkit.getScheduler().runTaskTimer(PlayAgain.getInstance(), this, 0, 20L);
    }

    public void addCache(UUID uuid, String server) {
        joinCache.put(uuid, new PlayerCache(uuid, server, System.currentTimeMillis()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (joinCache.containsKey(player.getUniqueId())) {
            PlayerCache cache = joinCache.get(player.getUniqueId());
            joinCache.remove(player.getUniqueId());
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(cache.getServer());
            Bukkit.getScheduler().runTaskLater(PlayAgain.getInstance(), () -> player.sendPluginMessage(PlayAgain.getInstance(), "BungeeCord", out.toByteArray()), 1L);
        }
    }

    @Override
    public void run() {
        List<ArenaData> data = new ArrayList<>();
        for (CachedArena arena : new ArrayList<>(ArenaManager.getArenas())) {
            if (arena.getStatus() != ArenaStatus.WAITING && arena.getStatus() != ArenaStatus.STARTING) continue;
            ArenaData arenaData = new ArenaData(arena.getServer(), arena.getArenaName(), arena.getRemoteIdentifier(), arena.getArenaGroup(), arena.getMaxPlayers(), arena.getMaxInTeam());
            arenaData.setState(arena.getStatus().toString());
            arenaData.setPlayers(arena.getCurrentPlayers());
            data.add(arenaData);
        }
        proxySocket.sendMessage(gson.toJson(data));
        for (PlayerCache cache : new ArrayList<>(joinCache.values())) {
            if (cache.isExpired()) {
                joinCache.remove(cache.getUuid());
            }
        }
    }

    public void close() {
        proxySocket.close();
    }

}
