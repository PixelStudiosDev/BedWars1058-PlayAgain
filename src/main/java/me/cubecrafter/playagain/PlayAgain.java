package me.cubecrafter.playagain;

import com.andrei1058.bedwars.api.BedWars;
import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.playagain.commands.OpenMenuCommand;
import me.cubecrafter.playagain.commands.ReloadCommand;
import me.cubecrafter.playagain.listeners.DeathListener;
import me.cubecrafter.playagain.listeners.GameEndListener;
import me.cubecrafter.playagain.listeners.InteractListener;
import me.cubecrafter.playagain.listeners.MenuListener;
import me.cubecrafter.playagain.utils.FileManager;
import me.cubecrafter.playagain.utils.Metrics;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayAgain extends JavaPlugin {

    private static PlayAgain instance;
    public FileManager config;
    public ItemStack playAgainItem;
    public BedWars bw;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("BedWars1058") != null){
            instance = this;
            bw = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
            new ReloadCommand(bw.getBedWarsCommand(), "playagain-reload");
            new OpenMenuCommand(bw.getBedWarsCommand(), "playagain");
            config = new FileManager("config", "plugins/BedWars1058/Addons/PlayAgain");
            getServer().getPluginManager().registerEvents(new GameEndListener(), this);
            getServer().getPluginManager().registerEvents(new InteractListener(), this);
            getServer().getPluginManager().registerEvents(new DeathListener(), this);
            getServer().getPluginManager().registerEvents(new MenuListener(), this);
            int pluginId = 14060;
            new Metrics(this, pluginId);
            Bukkit.getConsoleSender().sendMessage(TextUtil.color("&8--------------------------------------------------"));
            Bukkit.getConsoleSender().sendMessage(TextUtil.color("&aBedWars1058-PlayAgain &7v" + getDescription().getVersion() + " &7by &cCubeCrafter"));
            Bukkit.getConsoleSender().sendMessage(TextUtil.color("&7Server Version: &6" + getServer().getName() + " " + getServer().getBukkitVersion()));
            Bukkit.getConsoleSender().sendMessage(TextUtil.color("&7Java Version: &6" + System.getProperty("java.version")));
            Bukkit.getConsoleSender().sendMessage(TextUtil.color("&8--------------------------------------------------"));
        }else{
            getLogger().severe("BedWars1058 was not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void addPlayAgainItem(Player p){
        Bukkit.getScheduler().runTaskLater(this, () -> {
            playAgainItem = XMaterial.matchXMaterial(config.getYml().getString("play-again-item.material")).get().parseItem();
            ItemMeta meta = playAgainItem.getItemMeta();
            meta.setDisplayName(TextUtil.color(config.getYml().getString("play-again-item.displayname")));
            meta.setLore(TextUtil.color(config.getYml().getStringList("play-again-item.lore")));
            playAgainItem.setItemMeta(meta);
            p.getInventory().setItem(config.getYml().getInt("play-again-item.slot"), playAgainItem);
        }, 20L);
    }

    public static PlayAgain getInstance(){
        return instance;
    }

}
