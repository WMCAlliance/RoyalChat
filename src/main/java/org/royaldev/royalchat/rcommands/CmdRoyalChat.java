package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

import static org.royaldev.royalchat.Language.RCHAT_RELOADED;

public class CmdRoyalChat implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdRoyalChat(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("royalchat")) {
            if (!plugin.isAuthorized(cs, "rchat.royalchat")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.reloadConfig();
            plugin.reloadConfigVals();
            cs.sendMessage(ChatColor.BLUE + RCHAT_RELOADED.toString() + " " + ChatColor.GRAY + "v" + plugin.getDescription().getVersion() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
