package com.brazilbow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BowManager {

    private static final String BOW_IDENTIFIER = "BRAZIL_BOW";

    public ItemStack createBrazilBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();

        if (meta == null) {
            return bow;
        }

        meta.setDisplayName(ChatColor.GREEN + "B" + ChatColor.YELLOW + "r" + ChatColor.BLUE + "a" +
                ChatColor.GREEN + "z" + ChatColor.YELLOW + "i" + ChatColor.BLUE + "l " +
                ChatColor.GREEN + "B" + ChatColor.YELLOW + "o" + ChatColor.BLUE + "w");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "ID: " + BOW_IDENTIFIER);

        meta.setLore(lore);

        // Make it unbreakable
        meta.setUnbreakable(true);

        // Add best bow enchantments
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true); // Power V
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, true); // Flame I
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true); // Infinity I
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true); // Punch II

        bow.setItemMeta(meta);
        return bow;
    }

    public boolean isBrazilBow(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }

        List<String> lore = meta.getLore();
        return lore != null && lore.stream()
                .anyMatch(line -> line.contains(BOW_IDENTIFIER));
    }
}
