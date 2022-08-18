package me.cubecrafter.playagain.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.playagain.PlayAgain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack item;
    private final Map<String, String> placeholders = new HashMap<>();

    public ItemBuilder(String material) {
        item = XMaterial.matchXMaterial(material).get().parseItem();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(TextUtil.color(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        ItemMeta meta = item.getItemMeta();
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.DURABILITY);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setTexture(String identifier) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta)) return this;
        item.setItemMeta(SkullUtils.applySkin(meta, identifier));
        return this;
    }

    public ItemBuilder setTag(String key, String value) {
        item = PlayAgain.getInstance().getBedWars().getVersionSupport().setTag(item, key, value);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addPlaceholder(String target, String replacement) {
        placeholders.put(target, replacement);
        return this;
    }

    public ItemBuilder hideFlags() {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        hideFlags();
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                displayName = displayName.replace(entry.getKey(), entry.getValue());
            }
            setDisplayName(displayName);
        }
        setDisplayName(displayName);
        List<String> lore = item.getItemMeta().getLore();
        if (lore != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                lore.replaceAll(line -> line.replace(entry.getKey(), entry.getValue()));
            }
        }
        setLore(lore);
        return item;
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(section.getString("material"));
        if (section.contains("displayname")) builder.setDisplayName(section.getString("displayname"));
        if (section.contains("lore")) builder.setLore(section.getStringList("lore"));
        if (section.contains("texture")) builder.setTexture(section.getString("texture"));
        return builder;
    }

    public static String getTag(ItemStack item, String key) {
        String tag = PlayAgain.getInstance().getBedWars().getVersionSupport().getTag(item, key);
        return tag == null ? "" : tag;
    }

}
