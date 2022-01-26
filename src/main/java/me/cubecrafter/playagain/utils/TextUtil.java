package me.cubecrafter.playagain.utils;

import com.andrei1058.bedwars.api.arena.IArena;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String format(String s, IArena arena){
        return ChatColor.translateAlternateColorCodes('&', s
                .replace("{arenadisplayname}", arena.getDisplayName())
                .replace("{arenaplayers}", String.valueOf(arena.getPlayers().size()))
                .replace("{arenamaxplayers}", String.valueOf(arena.getMaxPlayers()))
                .replace("{arenagroup}", arena.getGroup())
                .replace("{arenastatus}", arena.getStatus().name())
                .replace("{arenaname}", arena.getArenaName()));
    }

    public static List<String> format(List<String> list, IArena arena){
        List<String> format = new ArrayList<>();
        list.forEach(s -> format.add(format(s, arena)));
        return format;
    }

    public static List<String> color(List<String> list){
        List<String> color = new ArrayList<>();
        list.forEach(s -> color.add(color(s)));
        return color;
    }

}
