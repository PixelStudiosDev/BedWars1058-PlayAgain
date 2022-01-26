package me.cubecrafter.playagain.listeners;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DeathListener implements Listener {

    @EventHandler
    public void onFinalKill(PlayerKillEvent e){
        if(e.getCause().isFinalKill()){
            PlayAgain.getInstance().addPlayAgainItem(e.getVictim());
        }
    }

}
