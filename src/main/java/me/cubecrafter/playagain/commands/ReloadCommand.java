package me.cubecrafter.playagain.commands;

import com.andrei1058.bedwars.api.command.ParentCommand;
import com.andrei1058.bedwars.api.command.SubCommand;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.utils.TextUtil;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(ParentCommand parent, String name) {
        super(parent, name);
        showInList(true);
        setDisplayInfo(textComponentBuilder("§6 ▪ §7/bw " + getSubCommandName() + " §8- §ereload configuration"));
        setPriority(20);
        setArenaSetupCommand(false);
        setPermission("bw.playagain.admin");
    }

    @Override
    public boolean execute(String[] strings, CommandSender commandSender) {
        PlayAgain.getInstance().config.load();
        commandSender.sendMessage(TextUtil.color(PlayAgain.getInstance().config.getYml().getString("messages.config-reloaded")));
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    public TextComponent textComponentBuilder(String s){
        TextComponent textComponent = new TextComponent(s);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bw " + getSubCommandName()));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to reload the configuration!").create()));
        return textComponent;
    }
}
