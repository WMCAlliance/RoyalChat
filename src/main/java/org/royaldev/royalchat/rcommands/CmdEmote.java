package org.royaldev.royalchat.rcommands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdEmote implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdEmote(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("emote")) {
            if (!plugin.isAuthorized(cs, "rchat.emote")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1){
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String format = plugin.getConfig().getString("chat.me-format", "&d * &b{dispname}&d {message}");
            String message = StringUtils.join(args, ' ');
            format = plugin.dm.formatChat(cs, format, message, true, false, false, false);
            plugin.getServer().broadcastMessage(format);
            return true;
        }
        return false;
    }

}
