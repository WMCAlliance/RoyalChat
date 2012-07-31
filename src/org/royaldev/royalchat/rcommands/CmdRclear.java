package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdRclear implements CommandExecutor {

    RoyalChat plugin;

    public CmdRclear(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rclear")) {
            if (!plugin.isAuthorized(cs, "rchat.rclear")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                for (int i = 0; i < 120; i++) cs.sendMessage("");
                return true;
            }
            Player t = plugin.getServer().getPlayer(args[0]);
            if (t == null) {
                cs.sendMessage(ChatColor.RED + "That player does not exist!");
                return true;
            }
            for (int i = 0; i < 120; i++) t.sendMessage("");
            cs.sendMessage(ChatColor.BLUE + "Cleared the chat of " + ChatColor.GRAY + t.getName() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
