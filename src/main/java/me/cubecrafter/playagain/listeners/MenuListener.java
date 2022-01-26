package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.cryptomorin.xseries.XSound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.menu.Menu;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    YamlConfiguration config = PlayAgain.getInstance().config.getYml();
    BedWars.ArenaUtil arenaUtil = PlayAgain.getInstance().bw.getArenaUtil();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        IArena playerArena = arenaUtil.getArenaByPlayer(p);
        if(playerArena == null || e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)return;
        if(!(e.getInventory().getHolder() instanceof Menu))return;
        e.setCancelled(true);
        NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        if(!nbtItem.hasKey("id"))return;
        String[] ids = nbtItem.getString("id").split("#");
        if(ids[0].equals("arena")){
            if(p.hasPermission("bw.playagain.selector")){
                playerArena.removePlayer(p, true);
                arenaUtil.getArenaByName(ids[1]).addPlayer(p, false);
            }else{
                p.sendMessage(TextUtil.color(config.getString("messages.no-permission-arena-selector")));
                p.playSound(p.getLocation(), XSound.matchXSound(config.getString("sounds.no-permission-arena-selector")).get().parseSound(), 1f, 1f);
            }
        }else if(ids[0].equals("random")){
            playerArena.removePlayer(p, true);
            arenaUtil.joinRandomFromGroup(p, playerArena.getGroup());
        }
    }

}
