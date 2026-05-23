package com.brazilbow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BrazilBowPlugin extends JavaPlugin {

    private BowManager bowManager;
    private ArrowTrailListener trailListener;

    @Override
    public void onEnable() {
        bowManager = new BowManager();
        trailListener = new ArrowTrailListener(bowManager, this);

        Bukkit.getPluginManager().registerEvents(trailListener, this);

        getLogger().info(ChatColor.GREEN + "BrazilBow plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "BrazilBow plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("givebrazilbow")) {
            if (!sender.hasPermission("brazilbow.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }

            Player player = (Player) sender;
            player.getInventory().addItem(bowManager.createBrazilBow());
            player.sendMessage(ChatColor.GREEN + "You received a Brazil flag bow!");

            return true;
        }

        return false;
    }
}
