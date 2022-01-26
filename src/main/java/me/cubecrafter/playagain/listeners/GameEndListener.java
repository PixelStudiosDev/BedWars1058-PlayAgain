package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

public class GameEndListener implements Listener {

    @EventHandler
    public void onGameEnd(GameEndEvent e){

        String arenaGroup = e.getArena().getGroup();
        BedWars.ArenaUtil arenaUtil = PlayAgain.getInstance().bw.getArenaUtil();
        YamlConfiguration config = PlayAgain.getInstance().config.getYml();

        Bukkit.getScheduler().runTaskLater(PlayAgain.getInstance(), () -> {

            if(config.getBoolean("auto-playagain-on-game-end")){
                for(UUID uuid : new ArrayList<>(e.getAliveWinners())){
                    Player p = Bukkit.getPlayer(uuid);
                    if(p.hasPermission("bw.playagain.auto")){
                        e.getArena().removePlayer(p, true);
                        arenaUtil.joinRandomFromGroup(p, arenaGroup);
                    }
                }
                for(Player p : new ArrayList<>(e.getArena().getSpectators())){
                    if(p.hasPermission("bw.playagain.auto")){
                        e.getArena().removePlayer(p, true);
                        arenaUtil.joinRandomFromGroup(p, arenaGroup);
                    }
                }
            }
        }, 150L);
    }

}
