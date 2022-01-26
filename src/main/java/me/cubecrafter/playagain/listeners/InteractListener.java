package me.cubecrafter.playagain.listeners;

import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.menu.PlayAgainMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getItemInHand().equals(PlayAgain.getInstance().playAgainItem)){
            PlayAgainMenu playAgainMenu = new PlayAgainMenu(p);
        }
    }

}
