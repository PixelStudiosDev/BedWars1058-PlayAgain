package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Config;
import me.cubecrafter.playagain.menu.PlayAgainMenu;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.item.ItemBuilder;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ArenaListener implements Listener {

    private final PlayAgain plugin;

    @EventHandler
    public void onFinalKill(PlayerKillEvent event) {
        if (!event.getCause().isFinalKill()) return;

        Tasks.later(() -> giveItem(event.getKiller(), false), 10L);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        IArena arena = event.getArena();
        arena.getPlayers().forEach(player -> giveItem(player, false));

        Tasks.later(() -> {
            for (Player player : event.getTeamWinner().getMembers()) {
                if (!player.hasPermission("bw.playagain.auto")) continue;

                if (checkParty(player)) {
                    arena.removePlayer(player, true);
                    plugin.getBedWars().getArenaUtil().joinRandomFromGroup(player, arena.getGroup());
                }
            }
        }, 160L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        Player player = event.getPlayer();
        IArena arena = plugin.getBedWars().getArenaUtil().getArenaByPlayer(player);

        if (arena == null) return;

        if (plugin.getBedWars().getVersionSupport().getTag(event.getItem(), "play-again") != null) {
            if (!checkParty(player)) {
                TextUtil.sendMessage(player, Config.MESSAGES_NOT_PARTY_OWNER.asString());
                return;
            }

            new PlayAgainMenu(player, arena).open();
        }
    }

    public void giveItem(Player player, boolean spectator) {
        ItemStack item = ItemBuilder.fromConfig(Config.INVENTORY_ITEM.asSection()).build();
        plugin.getBedWars().getVersionSupport().setTag(item, "play-again", "true");

        player.getInventory().setItem(spectator ? Config.INVENTORY_ITEM_SPECTATOR_SLOT.asInt() : Config.INVENTORY_ITEM_WIN_SLOT.asInt(), item);
    }

    public boolean checkParty(Player player) {
        return !plugin.getBedWars().getPartyUtil().hasParty(player) ||
                plugin.getBedWars().getPartyUtil().isOwner(player);
    }

}
