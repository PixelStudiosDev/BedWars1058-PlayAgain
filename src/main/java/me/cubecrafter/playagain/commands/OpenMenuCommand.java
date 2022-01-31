package me.cubecrafter.playagain.commands;

import com.andrei1058.bedwars.api.command.ParentCommand;
import com.andrei1058.bedwars.api.command.SubCommand;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.menu.PlayAgainMenu;
import me.cubecrafter.playagain.utils.TextUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenMenuCommand extends SubCommand {

    public OpenMenuCommand(ParentCommand parent, String name) {
        super(parent, name);
        showInList(true);
        setDisplayInfo(textComponentBuilder("§6 ▪ §7/bw " + getSubCommandName() + " §8- §eplay again"));
        setPriority(20);
        setArenaSetupCommand(false);
    }

    @Override
    public boolean execute(String[] strings, CommandSender commandSender) {
        if(commandSender instanceof Player){
            new PlayAgainMenu((Player) commandSender);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    public TextComponent textComponentBuilder(String s){
        TextComponent textComponent = new TextComponent(s);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bw " + getSubCommandName()));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to play again!").create()));
        return textComponent;
    }

}
