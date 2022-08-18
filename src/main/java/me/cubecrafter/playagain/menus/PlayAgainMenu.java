package me.cubecrafter.playagain.menus;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.cryptomorin.xseries.XSound;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.arena.ArenaData;
import me.cubecrafter.playagain.arena.BungeeArenaManager;
import me.cubecrafter.playagain.config.Configuration;
import me.cubecrafter.playagain.utils.ItemBuilder;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayAgainMenu extends Menu {

    private final IArena playerArena;
    private Iterator<String> groupIterator;
    private String group;
    private int page = 1;
    private static final List<String> groups = PlayAgain.getInstance().getArenaGroups();
    private static final int arenasPerPage = Configuration.ARENA_ITEM_SLOTS.getAsIntegerList().size();
    private static final BungeeArenaManager manager = PlayAgain.getInstance().getBungeeManager();
    private static final PlayAgain plugin = PlayAgain.getInstance();

    public PlayAgainMenu(Player player, IArena arena) {
        super(player);
        this.playerArena = arena;
        group = arena.getGroup();
        groupIterator = getIterator(groups.indexOf(group));
    }

    @Override
    public String getTitle() {
        return Configuration.MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Configuration.MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {

        if (Configuration.MENU_FILLER_ENABLED.getAsBoolean()) {
            setFiller(ItemBuilder.fromConfig(Configuration.MENU_FILLER.getAsConfigSection()).build(), Configuration.MENU_FILLER_SLOTS.getAsIntegerList());
        }

        Map<Integer, MenuItem> items = new HashMap<>();

        items.put(Configuration.RANDOM_ARENA_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Configuration.RANDOM_ARENA_ITEM.getAsConfigSection()).addPlaceholder("{current}", group).build()).addAction(e -> {
            if (plugin.isBungee()) {
                manager.joinRandomArena(player, group);
            } else {
                playerArena.removePlayer(player, true);
                if (group.equals("All")) {
                    plugin.getBedWars().getArenaUtil().joinRandomArena(player);
                } else {
                    plugin.getBedWars().getArenaUtil().joinRandomFromGroup(player, group);
                }
            }
            closeMenu();
        }));

        items.put(Configuration.FILTER_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Configuration.FILTER_ITEM.getAsConfigSection()).addPlaceholder("{current}", group).build()).addAction(e -> {
            XSound.play(player, Configuration.SOUNDS_MENU_CLICK.getAsString());
            switchGroup();
        }));

        Iterator<Integer> it = Configuration.ARENA_ITEM_SLOTS.getAsIntegerList().iterator();
        if (plugin.isBungee()) {
            for (ArenaData arena : getBungeeArenas()) {
                items.put(it.next(), new MenuItem(getArenaItem(arena)).addAction(e -> {
                    if (!player.hasPermission("bw.playagain.selector")) {
                        XSound.play(player, Configuration.SOUNDS_PERMISSION_DENIED.getAsString());
                        TextUtil.sendMessage(player, Configuration.MESSAGES_SELECTOR_PERMISSION_DENIED.getAsString());
                        closeMenu();
                        return;
                    }
                    manager.joinArena(player, arena);
                    closeMenu();
                }));
            }
        } else {
            for (IArena arena : getLocalArenas()) {
                items.put(it.next(), new MenuItem(getArenaItem(arena)).addAction(e -> {
                    if (!player.hasPermission("bw.playagain.selector")) {
                        XSound.play(player, Configuration.SOUNDS_PERMISSION_DENIED.getAsString());
                        TextUtil.sendMessage(player, Configuration.MESSAGES_SELECTOR_PERMISSION_DENIED.getAsString());
                        closeMenu();
                        return;
                    }
                    playerArena.removePlayer(player, true);
                    arena.addPlayer(player, false);
                }));
            }
        }

        if (page > 1) {
            items.put(Configuration.PREVIOUS_PAGE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Configuration.PREVIOUS_PAGE_ITEM.getAsConfigSection()).addPlaceholder("{page}", String.valueOf(page - 1)).build()).addAction(e -> {
                page--;
                XSound.play(player, Configuration.SOUNDS_MENU_CLICK.getAsString());
            }));
        }

        if (page < getMaxPages()) {
            items.put(Configuration.NEXT_PAGE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Configuration.NEXT_PAGE_ITEM.getAsConfigSection()).addPlaceholder("{page}", String.valueOf(page + 1)).build()).addAction(e -> {
                page++;
                XSound.play(player, Configuration.SOUNDS_MENU_CLICK.getAsString());
            }));
        }

        items.put(Configuration.BACK_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Configuration.BACK_ITEM.getAsConfigSection()).build()).addAction(e -> {
            closeMenu();
        }));

        return items;
    }

    @Override
    public boolean update() {
        return true;
    }

    private List<ArenaData> getBungeeArenas() {
        List<ArenaData> arenas = manager.getAvailableArenas(group);
        arenas.sort((o1, o2) -> {
            if (o1.getState().equals("STARTING") && o2.getState().equals("WAITING")) {
                return -1;
            } else if (o1.getState().equals("WAITING") && o2.getState().equals("STARTING")) {
                return 1;
            } else {
                return o2.getPlayers() - o1.getPlayers();
            }
        });
        return arenas.subList((page - 1) * arenasPerPage, Math.min(page * arenasPerPage, arenas.size()));
    }

    private List<IArena> getLocalArenas() {
        List<IArena> arenas = getAvailableArenas(group);
        arenas.sort((o1, o2) -> {
            if (o1.getStatus() == GameState.starting && o2.getStatus() == GameState.waiting) {
                return -1;
            } else if (o1.getStatus() == GameState.waiting && o2.getStatus() == GameState.starting) {
                return 1;
            } else {
                return o2.getPlayers().size() - o1.getPlayers().size();
            }
        });
        return arenas.subList((page - 1) * arenasPerPage, Math.min(page * arenasPerPage, arenas.size()));
    }

    private List<IArena> getAvailableArenas(String group) {
        if (group.equals("All")) {
            return PlayAgain.getInstance().getBedWars().getArenaUtil().getArenas().stream().filter(arena -> arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting).collect(Collectors.toList());
        }
        return PlayAgain.getInstance().getBedWars().getArenaUtil().getArenas().stream().filter(arena -> arena.getGroup().equals(group)).filter(arena -> arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting).collect(Collectors.toList());
    }

    private Iterator<String> getIterator(int index) {
        Iterator<String> it = groups.iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        return it;
    }

    private void switchGroup() {
        if (!groupIterator.hasNext()) {
            groupIterator = groups.iterator();
        }
        group = groupIterator.next();
        page = 1;
    }

    private int getMaxPages() {
        int pages;
        if (plugin.isBungee()) {
            pages = (int) Math.ceil((double) manager.getAvailableArenas(group).size() / arenasPerPage);
        } else {
            pages = (int) Math.ceil((double) getAvailableArenas(group).size() / arenasPerPage);
        }
        return Math.max(pages, 1);
    }

    private ItemStack getArenaItem(ArenaData data) {
        ItemStack original = ItemBuilder.fromConfig(Configuration.ARENA_ITEM.getAsConfigSection()).build();
        ItemMeta meta = original.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(TextUtil.format(meta.getDisplayName(), data));
        }
        if (meta.hasLore()) {
            meta.setLore(TextUtil.format(meta.getLore(), data));
        }
        original.setItemMeta(meta);
        return new ItemBuilder(original).setGlow(data.getState().equals("STARTING")).build();
    }

    private ItemStack getArenaItem(IArena arena) {
        ItemStack original = ItemBuilder.fromConfig(Configuration.ARENA_ITEM.getAsConfigSection()).build();
        ItemMeta meta = original.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(TextUtil.format(meta.getDisplayName(), arena));
        }
        if (meta.hasLore()) {
            meta.setLore(TextUtil.format(meta.getLore(), arena));
        }
        original.setItemMeta(meta);
        return new ItemBuilder(original).setGlow(arena.getStatus() == GameState.starting).build();
    }

}
