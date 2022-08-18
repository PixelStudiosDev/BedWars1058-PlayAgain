package me.cubecrafter.playagain.arena;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.language.Language;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Configuration;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BungeeArenaManager {

    private final List<ArenaData> cache = new ArrayList<>();
    private final PlayAgain plugin;
    private final String serverId;
    private final SocketTask socketTask;
    private final Gson gson = new Gson();

    public BungeeArenaManager(PlayAgain plugin) {
        this.plugin = plugin;
        serverId = plugin.getBedWars().getConfigs().getMainConfig().getString("bungee-settings.server-id");
        socketTask = new SocketTask();
    }

    public ArenaData getCache(String server, String name) {
        return cache.stream().filter(arena -> arena.getServer().equals(server) && arena.getName().equals(name)).findAny().orElse(null);
    }

    public void createCache(ArenaData data) {
        cache.add(data);
    }

    public void removeCache(ArenaData data) {
        cache.remove(data);
    }

    public List<ArenaData> getArenas() {
        return new ArrayList<>(cache);
    }

    public void joinRandomArena(Player player, String group) {
        List<ArenaData> available = getAvailableArenas(group);
        ArenaData arena = available.stream().max(Comparator.comparingInt(ArenaData::getPlayers)).orElse(available.get(0));
        joinArena(player, arena);
    }

    public List<ArenaData> getAvailableArenas(String group) {
        if (group.equals("All")) {
            return getArenas().stream().filter(data -> data.getState().equals("WAITING") || data.getState().equals("STARTING")).collect(Collectors.toList());
        } else {
            return getArenas().stream().filter(data -> data.getGroup().equals(group) && (data.getState().equals("WAITING") || data.getState().equals("STARTING"))).collect(Collectors.toList());
        }
    }

    public void joinArena(Player player, ArenaData data) {
        boolean local = data.getServer().equals(serverId);
        if (local) {
            IArena arena = plugin.getBedWars().getArenaUtil().getArenaByPlayer(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.removePlayer(player, true), 10L);
        }
        List<Map<String, String>> message = new ArrayList<>();
        if (plugin.getBedWars().getPartyUtil().hasParty(player) && plugin.getBedWars().getPartyUtil().isOwner(player)) {
            int size = plugin.getBedWars().getPartyUtil().partySize(player);
            if (size > data.getMaxInTeam() || size > data.getMaxPlayers() - data.getPlayers()) {
                TextUtil.sendMessage(player, Configuration.MESSAGES_PARTY_TOO_BIG.getAsString());
                return;
            }
            for (Player member : plugin.getBedWars().getPartyUtil().getMembers(player)) {
                if (member.equals(player)) continue;
                message.add(getUserData(member, data, local, player.getName()));
                if (!local) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> connect(member, data.getServer()), 20L);
                }
            }
        }
        message.add(getUserData(player, data, local, null));
        socketTask.sendMessage(gson.toJson(message));
        if (!local) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> connect(player, data.getServer()), 20L);
        }
    }

    public Map<String, String> getUserData(Player player, ArenaData data, boolean localArena, String partyOwner) {
        Map<String, String> message = new HashMap<>();
        message.put("uuid", player.getUniqueId().toString());
        message.put("iso", Language.getPlayerLanguage(player).getIso());
        message.put("id", data.getId());
        message.put("server", data.getServer());
        message.put("local", String.valueOf(localArena));
        message.put("party", partyOwner == null ? "" : partyOwner);
        return message;
    }

    public void connect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(PlayAgain.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void close() {
        socketTask.close();
    }

}
