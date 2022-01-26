package me.cubecrafter.playagain.menu;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Menu implements InventoryHolder {

    private final Inventory inv;

    public Menu(int size, String title){
        inv = Bukkit.createInventory(this, size, title);
    }

    public void setItem(String material, int slot, boolean glow, String name, List<String> lore){
        ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(glow){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void setItem(String material, int slot, boolean glow, String name, List<String> lore, String nbt){
        ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(glow){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        NBTItem ni = new NBTItem(item);
        ni.setString("id", nbt);
        item = ni.getItem();
        inv.setItem(slot, item);
    }

    public void clearSlot(int slot){
        inv.clear(slot);
    }

    public void openMenu(Player p){
        p.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
