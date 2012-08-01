package org.royaldev.royalchat.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.depends.MultiverseUtils;

public class CmdSay implements CommandExecutor {

    RoyalChat plugin;

    public CmdSay(RoyalChat instance) {
        plugin = instance;
    }

    private String formatSay(String originalMessage, CommandSender cs) {
        if (originalMessage == null) originalMessage = "";
        if (cs == null) throw new NullPointerException("cs can't be null!");
        originalMessage = RUtils.sanitizeInput(originalMessage, false);
        String newMessage = RUtils.colorize(RoyalChat.sayFormat);
        newMessage = RUtils.sanitizeInput(newMessage, false);
        newMessage = RUtils.colorize(RUtils.replaceTownyVars(newMessage, cs));
        newMessage = newMessage.replaceAll("(?i)\\{name\\}", cs.getName());
        newMessage = newMessage.replaceAll("(?i)\\{dispname\\}", ((cs instanceof Player) ? ((Player) cs).getDisplayName() : cs.getName()));
        originalMessage = (plugin.isAuthorized(cs, "rchat.colors")) ? RUtils.colorize(originalMessage) : RUtils.removeColorCodes(originalMessage);
        if (RoyalChat.removeAllCaps && !RoyalChat.hasAuthorization(cs, "rchat.caps"))
            originalMessage = RUtils.removeCaps(originalMessage);
        if (RoyalChat.firstWordCapital)
            originalMessage = originalMessage.substring(0, 1).toUpperCase() + originalMessage.substring(1);
        newMessage = newMessage.replaceAll("(?i)\\{message\\}", originalMessage);
        String world = (cs instanceof Player) ? MultiverseUtils.getMVWorldName(((Player) cs).getWorld()) : "";
        newMessage = newMessage.replaceAll("(?i)\\{world\\}", world);
        newMessage = newMessage.replaceAll("(?i)\\{prefix\\}", RUtils.getPrefix(cs));
        newMessage = newMessage.replaceAll("(?i)\\{suffix\\}", RUtils.getSuffix(cs));
        return newMessage;
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
            String message = formatSay(RUtils.join(args, 0), cs);
            plugin.getServer().broadcastMessage(message);
            return true;
        }
        return false;
    }

}
