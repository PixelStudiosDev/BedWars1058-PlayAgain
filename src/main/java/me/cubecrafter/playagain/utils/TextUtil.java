package me.cubecrafter.playagain.utils;

import com.andrei1058.bedwars.api.arena.IArena;
import lombok.experimental.UtilityClass;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.arena.ArenaData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class TextUtil {

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public List<String> color(List<String> lines) {
        lines.replaceAll(TextUtil::color);
        return lines;
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(color(message));
    }

    public void info(String message) {
        PlayAgain.getInstance().getLogger().info(message);
    }

    public void severe(String message) {
        PlayAgain.getInstance().getLogger().severe(message);
    }

    public String format(String s, ArenaData arena) {
        return color(s)
                .replace("{displayname}", arena.getName())
                .replace("{players}", String.valueOf(arena.getPlayers()))
                .replace("{max_players}", String.valueOf(arena.getMaxPlayers()))
                .replace("{max_team_players}", String.valueOf(arena.getMaxInTeam()))
                .replace("{group}", arena.getGroup())
                .replace("{state}", arena.getState())
                .replace("{name}", arena.getName());
    }

    public List<String> format(List<String> lines, ArenaData arena) {
        lines.replaceAll(s -> format(s, arena));
        return lines;
    }

    public String format(String s, IArena arena) {
        return color(s)
                .replace("{displayname}", arena.getDisplayName())
                .replace("{players}", String.valueOf(arena.getPlayers().size()))
                .replace("{max_players}", String.valueOf(arena.getMaxPlayers()))
                .replace("{max_team_players}", String.valueOf(arena.getMaxInTeam()))
                .replace("{group}", arena.getGroup())
                .replace("{state}", arena.getStatus().toString())
                .replace("{name}", arena.getArenaName());
    }

    public List<String> format(List<String> lines, IArena arena) {
        lines.replaceAll(s -> format(s, arena));
        return lines;
    }

    public TextComponent buildTextComponent(String text, String click, String hover, ClickEvent.Action action) {
        TextComponent component = new TextComponent(color(text));
        component.setClickEvent(new ClickEvent(action, click));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hover)).create()));
        return component;
    }

}
