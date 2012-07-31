package org.royaldev.royalchat.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.depends.MultiverseUtils;

public class CmdMe implements CommandExecutor {

    RoyalChat plugin;

    public CmdMe(RoyalChat instance) {
        plugin = instance;
    }

    private String formatMe(String originalMessage, CommandSender cs) {
        if (originalMessage == null) originalMessage = "";
        if (cs == null) throw new NullPointerException("cs can't be null!");
        String newMessage = RUtils.colorize(RoyalChat.meFormat);
        newMessage = RUtils.colorize(RUtils.replaceTownyVars(newMessage, cs));
        newMessage = newMessage.replaceAll("(?i)\\{name\\}", cs.getName());
        newMessage = newMessage.replaceAll("(?i)\\{dispname\\}", ((cs instanceof Player) ? ((Player) cs).getDisplayName() : cs.getName()));
        originalMessage = (plugin.isAuthorized(cs, "rchat.colors")) ? RUtils.colorize(originalMessage) : RUtils.removeColorCodes(originalMessage);
        if (RoyalChat.removeAllCaps && !RoyalChat.hasAuthorization(cs, "rchat.caps"))
            originalMessage = RUtils.removeCaps(originalMessage);
        newMessage = newMessage.replaceAll("(?i)\\{message\\}", originalMessage);
        String world = (cs instanceof Player) ? MultiverseUtils.getMVWorldName(((Player) cs).getWorld()) : "";
        newMessage = newMessage.replaceAll("(?i)\\{world\\}", world);
        newMessage = newMessage.replaceAll("(?i)\\{prefix\\}", RUtils.getPrefix(cs));
        newMessage = newMessage.replaceAll("(?i)\\{suffix\\}", RUtils.getSuffix(cs));
        return newMessage;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("me")) {
            if (!plugin.isAuthorized(cs, "rchat.me")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String message = formatMe(RUtils.join(args, 0), cs);
            plugin.getServer().broadcastMessage(message);
            return true;
        }
        return false;
    }

}
