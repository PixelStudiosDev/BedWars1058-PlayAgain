package me.cubecrafter.playagain.menu;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PlayAgainMenu {

    YamlConfiguration config = PlayAgain.getInstance().config.getYml();
    BedWars.ArenaUtil arenaUtil = PlayAgain.getInstance().bw.getArenaUtil();
    String currentGroup;

    public PlayAgainMenu(Player p){
        IArena playerArena = arenaUtil.getArenaByPlayer(p);
        if(playerArena == null)return;
        currentGroup = playerArena.getGroup();
        Menu playAgain = new Menu(config.getInt("playagain-menu-size"), TextUtil.color(config.getString("playagain-menu-displayname").replace("{arenagroup}", playerArena.getGroup())));
        Iterator<String> it = Arrays.stream(config.getString("playagain-menu-items.arena-item.slots").split(",")).iterator();
        for(IArena arena : new ArrayList<>(arenaUtil.getArenas())){
            if(arena.getGroup().equals(playerArena.getGroup()) && arena.getStatus().equals(GameState.waiting) || arena.getStatus().equals(GameState.starting)){
                if(it.hasNext()){
                    playAgain.setItem(config.getString("playagain-menu-items.arena-item.material"), Integer.parseInt(it.next()), arena.getStatus().equals(GameState.starting), TextUtil.format(config.getString("playagain-menu-items.arena-item.displayname"), arena), TextUtil.format(config.getStringList("playagain-menu-items.arena-item.lore"), arena), "arena#" + arena.getArenaName());
                }
            }
        }
        if(config.getBoolean("playagain-menu-items.filler-item.enabled")){
            Iterator<String> it2 = Arrays.stream(config.getString("playagain-menu-items.filler-item.slots").split(",")).iterator();
            while(it2.hasNext()){
                playAgain.setItem(config.getString("playagain-menu-items.filler-item.material"), Integer.parseInt(it2.next()), config.getBoolean("playagain-menu-items.filler-item.enchanted"), TextUtil.color(config.getString("playagain-menu-items.filler-item.displayname")), TextUtil.color(config.getStringList("playagain-menu-items.filler-item.lore")));
            }
        }
        playAgain.setItem(config.getString("playagain-menu-items.random-join-item.material"), config.getInt("playagain-menu-items.random-join-item.slot"), config.getBoolean("playagain-menu-items.random-join-item.enchanted"), TextUtil.color(config.getString("playagain-menu-items.random-join-item.displayname")), TextUtil.color(config.getStringList("playagain-menu-items.random-join-item.lore")), "random#random");
        playAgain.openMenu(p);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!(p.getOpenInventory().getTopInventory().getHolder() instanceof Menu))cancel();
                Iterator<String> it2 = Arrays.stream(config.getString("playagain-menu-items.arena-item.slots").split(",")).iterator();
                for(String slot : config.getString("playagain-menu-items.arena-item.slots").split(",")){
                    playAgain.clearSlot(Integer.parseInt(slot));
                }
                for(IArena arena : new ArrayList<>(arenaUtil.getArenas())){
                    if(arena.getGroup().equals(playerArena.getGroup()) && arena.getStatus().equals(GameState.waiting) || arena.getStatus().equals(GameState.starting)){
                        if(it2.hasNext()){
                            playAgain.setItem(config.getString("playagain-menu-items.arena-item.material"), Integer.parseInt(it2.next()), arena.getStatus().equals(GameState.starting), TextUtil.format(config.getString("playagain-menu-items.arena-item.displayname"), arena), TextUtil.format(config.getStringList("playagain-menu-items.arena-item.lore"), arena), "arena#" + arena.getArenaName());
                        }
                    }
                }
            }
        }.runTaskTimer(PlayAgain.getInstance(), 20L, 20L);

    }
}
