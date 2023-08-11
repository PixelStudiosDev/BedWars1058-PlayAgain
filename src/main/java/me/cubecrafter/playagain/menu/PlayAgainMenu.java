package me.cubecrafter.playagain.menu;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Config;
import me.cubecrafter.xutils.SoundUtil;
import me.cubecrafter.xutils.item.ItemBuilder;
import me.cubecrafter.xutils.menu.MenuItem;
import me.cubecrafter.xutils.menu.PaginatedMenu;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayAgainMenu extends PaginatedMenu {

    private static final PlayAgain PLUGIN = PlayAgain.getInstance();
    private static final List<String> ARENA_GROUPS = PLUGIN.getArenaGroups();
    private static final int ARENAS_PER_PAGE = Config.ARENA_ITEM_SLOTS.asIntegerList().size();

    private final IArena playerArena;
    private String group;

    public PlayAgainMenu(Player player, IArena arena) {
        super(player);
        this.playerArena = arena;
        this.group = arena.getGroup();
        
        setAutoUpdate(true);
        setParsePlaceholders(true);
    }

    @Override
    public String getTitle() {
        return Config.MENU_TITLE.asString();
    }

    @Override
    public int getRows() {
        return Config.MENU_ROWS.asInt();
    }

    @Override
    public void update(int page) {
        if (Config.MENU_FILLER_ENABLED.asBoolean()) {
            setItem(new MenuItem(ItemBuilder.fromConfig(Config.MENU_FILLER.asSection())), Config.MENU_FILLER_SLOTS.asIntegerList());
        }

        setItem(new MenuItem(ItemBuilder.fromConfig(Config.RANDOM_ARENA_ITEM.asSection())
                .addPlaceholder("{current}", group)).addAction(() -> {
                    playerArena.removePlayer(player, true);
                    if (group.equals("All")) {
                        PLUGIN.getBedWars().getArenaUtil().joinRandomArena(player);
                    } else {
                        PLUGIN.getBedWars().getArenaUtil().joinRandomFromGroup(player, group);
                    }
                    close();
        }), Config.RANDOM_ARENA_ITEM_SLOT.asInt());
        
        setItem(new MenuItem(ItemBuilder.fromConfig(Config.FILTER_ITEM.asSection())
                .addPlaceholder("{current}", group)).addAction(this::switchGroup)
                .sound(Config.SOUNDS_MENU_CLICK.asString()), Config.FILTER_ITEM_SLOT.asInt());
        
        Iterator<Integer> slots = Config.ARENA_ITEM_SLOTS.asIntegerList().iterator();
        for (IArena arena : getArenasSorted()) {
            setItem(new MenuItem(getArenaItem(arena)).addAction(() -> {
                if (!player.hasPermission("bw.playagain.selector")) {
                    SoundUtil.play(player, Config.SOUNDS_PERMISSION_DENIED.asString());
                    TextUtil.sendMessage(player, Config.MESSAGES_SELECTOR_PERMISSION_DENIED.asString());
                    close();
                    return;
                }
                playerArena.removePlayer(player, true);
                arena.addPlayer(player, false);
            }), slots.next());
        }

        if (page > 0) {
            setItem(new MenuItem(ItemBuilder.fromConfig(Config.PREVIOUS_PAGE_ITEM.asSection())
                    .addPlaceholder("{page}", String.valueOf(page))).addAction(this::previousPage)
                    .sound(Config.SOUNDS_MENU_CLICK.asString()), Config.PREVIOUS_PAGE_ITEM_SLOT.asInt());
        }

        if (page < getMaxPages() - 1) {
            setItem(new MenuItem(ItemBuilder.fromConfig(Config.NEXT_PAGE_ITEM.asSection())
                    .addPlaceholder("{page}", String.valueOf(page))).addAction(this::nextPage)
                    .sound(Config.SOUNDS_MENU_CLICK.asString()), Config.NEXT_PAGE_ITEM_SLOT.asInt());
        }

        setItem(new MenuItem(ItemBuilder.fromConfig(Config.BACK_ITEM.asSection()))
                        .addAction(this::close), Config.BACK_ITEM_SLOT.asInt());
    }

    @Override
    public int getMaxPages() {
        return calculateMaxPages(getAvailableArenas(), ARENAS_PER_PAGE);
    }

    public List<IArena> getArenasSorted() {
        List<IArena> arenas = getAvailableArenas();
        arenas.sort((o1, o2) -> {
            if (o1.getStatus() == GameState.starting && o2.getStatus() == GameState.waiting) {
                return -1;
            } else if (o1.getStatus() == GameState.waiting && o2.getStatus() == GameState.starting) {
                return 1;
            } else {
                return o2.getPlayers().size() - o1.getPlayers().size();
            }
        });
        return getPageItems(arenas, ARENAS_PER_PAGE);
    }

    public List<IArena> getAvailableArenas() {
        if (group.equals("All")) {
            return PLUGIN.getBedWars().getArenaUtil().getArenas().stream().filter(arena -> arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting).collect(Collectors.toList());
        }
        return PLUGIN.getBedWars().getArenaUtil().getArenas().stream().filter(arena -> arena.getGroup().equals(group)).filter(arena -> arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting).collect(Collectors.toList());
    }

    public void switchGroup() {
        int index = ARENA_GROUPS.indexOf(group);
        if (index == ARENA_GROUPS.size() - 1) {
            group = ARENA_GROUPS.get(0);
        } else {
            group = ARENA_GROUPS.get(index + 1);
        }
    }

    public ItemBuilder getArenaItem(IArena arena) {
        ItemBuilder builder = ItemBuilder.fromConfig(Config.ARENA_ITEM.asSection())
                .addPlaceholder("{displayname}", arena.getDisplayName())
                .addPlaceholder("{players}", String.valueOf(arena.getPlayers().size()))
                .addPlaceholder("{max_players}", String.valueOf(arena.getMaxPlayers()))
                .addPlaceholder("{max_team_players}", String.valueOf(arena.getMaxInTeam()))
                .addPlaceholder("{group}", arena.getGroup())
                .addPlaceholder("{state}", arena.getStatus().toString())
                .addPlaceholder("{name}", arena.getArenaName());

        return builder.setGlow(arena.getStatus() == GameState.starting);
    }

}
