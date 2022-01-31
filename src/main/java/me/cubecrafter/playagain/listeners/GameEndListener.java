package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class GameEndListener implements Listener {

    BedWars.ArenaUtil arenaUtil = PlayAgain.getInstance().bw.getArenaUtil();
    YamlConfiguration config = PlayAgain.getInstance().config.getYml();

    @EventHandler
    public void onGameEnd(GameEndEvent e){

        String arenaGroup = e.getArena().getGroup();

        if(config.getBoolean("playagain-countdown.enabled") && config.getBoolean("auto-playagain-on-game-end")){
            for(Player p : new ArrayList<>(e.getArena().getSpectators())){
                if(p.hasPermission("bw.playagain.auto")){
                    countdown(p, e.getArena());
                }
            }
            for(UUID uuid : new ArrayList<>(e.getAliveWinners())){
                Player p = Bukkit.getPlayer(uuid);
                if(p.hasPermission("bw.playagain.auto")){
                    countdown(p, e.getArena());
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(PlayAgain.getInstance(), () -> {

            if(config.getBoolean("auto-playagain-on-game-end")){
                for(UUID uuid : new ArrayList<>(e.getAliveWinners())){
                    Player p = Bukkit.getPlayer(uuid);
                    if(p.hasPermission("bw.playagain.auto") && p.getWorld().equals(e.getArena().getWorld())){
                        e.getArena().removePlayer(p, true);
                        arenaUtil.joinRandomFromGroup(p, arenaGroup);
                    }
                }
                for(Player p : new ArrayList<>(e.getArena().getSpectators())){
                    if(p.hasPermission("bw.playagain.auto") && p.getWorld().equals(e.getArena().getWorld())){
                        e.getArena().removePlayer(p, true);
                        arenaUtil.joinRandomFromGroup(p, arenaGroup);
                    }
                }
            }
        }, 160L);
    }

    public void countdown(Player p, IArena arena){
        new BukkitRunnable(){
            int i = 5;
            @Override
            public void run() {
                String message = config.getString("messages.countdown-message").replace("{seconds}", String.valueOf(i));
                if(!p.getWorld().equals(arena.getWorld())){
                    p.sendMessage(TextUtil.color(config.getString("messages.countdown-stopped")));
                    cancel();
                }
                p.playSound(p.getLocation(), XSound.matchXSound(config.getString("sounds.play-again-countdown")).get().parseSound(), 1f, 1f);
                switch(config.getString("playagain-countdown.type")){
                    case "CHAT":
                        p.sendMessage(TextUtil.color(message));
                        break;
                    case "ACTIONBAR":
                        ActionBar.sendActionBar(PlayAgain.getInstance(), p, TextUtil.color(message), 20L);
                        break;
                    case "TITLE":
                        Titles.sendTitle(p, 1, 20, 1, "", TextUtil.color(message));
                        break;
                }
                i--;
                if(i == 0){
                    cancel();
                }
            }
        }.runTaskTimer(PlayAgain.getInstance(), 60L, 20L);
    }
}


