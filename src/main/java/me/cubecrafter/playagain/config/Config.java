package me.cubecrafter.playagain.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Config {

    INVENTORY_ITEM("inventory-item"),
    INVENTORY_ITEM_SPECTATOR_SLOT("inventory-item.spectator-slot"),
    INVENTORY_ITEM_WIN_SLOT("inventory-item.win-slot"),
    MENU_TITLE("menu.title"),
    MENU_ROWS("menu.rows"),
    MENU_FILLER("menu.filler"),
    MENU_FILLER_ENABLED("menu.filler.enabled"),
    MENU_FILLER_SLOTS("menu.filler.slots"),
    ARENA_ITEM("menu.items.arena-item"),
    ARENA_ITEM_SLOTS("menu.items.arena-item.slots"),
    RANDOM_ARENA_ITEM("menu.items.random-arena-item"),
    RANDOM_ARENA_ITEM_SLOT("menu.items.random-arena-item.slot"),
    FILTER_ITEM("menu.items.filter-item"),
    FILTER_ITEM_SLOT("menu.items.filter-item.slot"),
    PREVIOUS_PAGE_ITEM("menu.items.previous-page-item"),
    PREVIOUS_PAGE_ITEM_SLOT("menu.items.previous-page-item.slot"),
    NEXT_PAGE_ITEM("menu.items.next-page-item"),
    NEXT_PAGE_ITEM_SLOT("menu.items.next-page-item.slot"),
    BACK_ITEM("menu.items.back-item"),
    BACK_ITEM_SLOT("menu.items.back-item.slot"),
    SOUNDS_PERMISSION_DENIED("sounds.permission-denied"),
    SOUNDS_MENU_CLICK("sounds.menu-click"),
    MESSAGES_SELECTOR_PERMISSION_DENIED("messages.selector-permission-denied"),
    MESSAGES_NOT_PARTY_OWNER("messages.not-party-owner");

    private static final YamlConfiguration CONFIG = PlayAgain.getInstance().getFileManager().getConfig();
    private final String path;

    public String asString() {
        return CONFIG.getString(path);
    }

    public int asInt() {
        return CONFIG.getInt(path);
    }

    public boolean asBoolean() {
        return CONFIG.getBoolean(path);
    }

    public ConfigurationSection asSection() {
        return CONFIG.getConfigurationSection(path);
    }

    public List<Integer> asIntegerList() {
        return Arrays.stream(CONFIG.getString(path).split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

}
