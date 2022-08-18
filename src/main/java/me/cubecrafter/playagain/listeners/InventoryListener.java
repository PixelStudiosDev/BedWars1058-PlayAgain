package me.cubecrafter.playagain.listeners;

import me.cubecrafter.playagain.menus.Menu;
import me.cubecrafter.playagain.menus.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.function.Consumer;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getInventory().getHolder() instanceof Menu) {
            e.setCancelled(true);
            Menu menu = (Menu) e.getInventory().getHolder();
            MenuItem clicked = menu.getItems().get(e.getSlot());
            if (clicked == null) return;
            for (Map.Entry<Consumer<InventoryClickEvent>, ClickType[]> entry : clicked.getActions().entrySet()) {
                for (ClickType clickType : entry.getValue()) {
                    if (e.getClick() == clickType) {
                        entry.getKey().accept(e);
                        menu.updateMenu();
                    }
                }
            }
        }
    }

}
