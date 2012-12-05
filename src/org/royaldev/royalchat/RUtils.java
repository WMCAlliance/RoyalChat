package org.royaldev.royalchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static org.royaldev.royalchat.Language.NO_PERMISSIONS;
import static org.royaldev.royalchat.Language.NO_PERMISSIONS_CONSOLE;

public class RUtils {

    public static void dispNoPerms(final CommandSender cs) {
        cs.sendMessage(ChatColor.RED + NO_PERMISSIONS.toString());
        Bukkit.getLogger().warning("[RoyalChat] " + cs.getName() + " " + NO_PERMISSIONS_CONSOLE);
    }

}
