package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Configuration;
import me.cubecrafter.playagain.menus.PlayAgainMenu;
import me.cubecrafter.playagain.utils.ItemBuilder;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArenaListener implements Listener {

    private final PlayAgain plugin;

    public ArenaListener(PlayAgain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFinalKill(PlayerKillEvent e) {
        if (!e.getCause().isFinalKill()) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> giveItem(e.getVictim(), true), 10L);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        IArena arena = e.getArena();
        arena.getPlayers().forEach(player -> giveItem(player, false));
        Bukkit.getScheduler().runTaskLater(PlayAgain.getInstance(), () -> {
            for (Player player : e.getTeamWinner().getMembers()) {
                if (!player.hasPermission("bw.playagain.auto")) continue;
                if (checkParty(player)) {
                    if (plugin.isBungee()) {
                        plugin.getBungeeManager().joinRandomArena(player, arena.getGroup());
                    } else {
                        arena.removePlayer(player, true);
                        plugin.getBedWars().getArenaUtil().joinRandomFromGroup(player, arena.getGroup());
                    }
                }
            }
        }, 160L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        Player player = e.getPlayer();
        IArena arena = plugin.getBedWars().getArenaUtil().getArenaByPlayer(player);
        if (arena == null) return;
        if (ItemBuilder.getTag(e.getItem(), "playagain").equals("playagain-item")) {
            if (checkParty(player)) {
                new PlayAgainMenu(player, arena).openMenu();
            } else {
                TextUtil.sendMessage(player, Configuration.MESSAGES_NOT_PARTY_OWNER.getAsString());
            }
        }
    }

    public void giveItem(Player player, boolean spectator) {
        player.getInventory().setItem(spectator ? Configuration.INVENTORY_ITEM_SPECTATOR_SLOT.getAsInt() : Configuration.INVENTORY_ITEM_WIN_SLOT.getAsInt(), ItemBuilder.fromConfig(Configuration.INVENTORY_ITEM.getAsConfigSection()).setTag("playagain", "playagain-item").build());
    }

    public boolean checkParty(Player player) {
        return !plugin.getBedWars().getPartyUtil().hasParty(player) || plugin.getBedWars().getPartyUtil().isOwner(player);
    }

}
