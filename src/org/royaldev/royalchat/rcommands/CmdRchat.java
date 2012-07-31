package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdRchat implements CommandExecutor {

    RoyalChat plugin;

    public CmdRchat(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rchat")) {
            if (!plugin.isAuthorized(cs, "rchat.rchat")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.reloadConfig();
            plugin.reloadConfigValues();
            cs.sendMessage(ChatColor.BLUE + "RoyalChat " + ChatColor.GRAY + "v" + plugin.getDescription().getVersion() + ChatColor.BLUE + " reloaded.");
            return true;
        }
        return false;
    }

}
