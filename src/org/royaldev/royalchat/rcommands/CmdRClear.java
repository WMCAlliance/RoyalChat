package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdRClear implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdRClear(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rclear")) {
            if (!plugin.isAuthorized(cs, "rchat.rclear")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (!(cs instanceof Player) && args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            Player t = (args.length > 0) ? plugin.getServer().getPlayer(args[0]) : (Player) cs;
            if (t == null && args.length > 0) {
                if (cs == null) return false; // satisfies IntelliJ
                if (args[0].equals("*")) {
                    for (int i = 0; i < 120; i++) plugin.getServer().broadcastMessage("");
                    cs.sendMessage(ChatColor.BLUE + "Cleared the screen of " + ChatColor.GRAY + "all players" + ChatColor.BLUE + ".");
                    return true;
                }
            }
            if (t == null || plugin.isVanished(t)) {
                if (cs == null) return false;
                cs.sendMessage(ChatColor.RED + "That player does not exist!");
                return true;
            }
            for (int i = 0; i < 120; i++) t.sendMessage("");
            if (!cs.equals(t))
                cs.sendMessage(ChatColor.BLUE + "Cleared the screen of " + ChatColor.GRAY + t.getName() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
