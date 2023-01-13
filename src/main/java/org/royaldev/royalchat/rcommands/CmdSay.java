package org.royaldev.royalchat.rcommands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdSay implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdSay(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("say")) {
            if (!plugin.isAuthorized(cs, "rchat.say")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String format = plugin.getConfig().getString("chat.say-format", "&d[Server] {message}");
            String message = StringUtils.join(args, ' ');
            format = plugin.dm.formatChat(cs, format, message, false, false, false, false);
            plugin.getServer().broadcastMessage(format);
            return true;
        }
        return false;
    }

}
