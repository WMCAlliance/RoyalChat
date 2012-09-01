package org.royaldev.royalchat.rcommands;

import org.apache.commons.lang.BooleanUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.depends.MultiverseUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CmdAdminChat implements CommandExecutor {

    RoyalChat plugin;

    public CmdAdminChat(RoyalChat instance) {
        plugin = instance;
    }

    private static List<String> toggles = new ArrayList<String>();

    /**
     * Checks to see if the specified player has admin chat toggled on.
     *
     * @param p Player to check for
     * @return Boolean value
     */
    public static boolean hasAdminChatOn(Player p) {
        return toggles.contains(p.getName());
    }

    /**
     * Checks to see if the specified CommandSender has admin chat toggled on.
     *
     * @param cs CommandSender to check for
     * @return Boolean value
     */
    public static boolean hasAdminChatOn(CommandSender cs) {
        return toggles.contains(cs.getName());
    }

    /**
     * Formats a message with the format set by admin-format in config.yml.
     *
     * @param originalMessage The message without formatting
     * @param sender          Sender of the message
     * @return Formatted string with variables processed
     * @throws NullPointerException If sender is null
     */
    public static String formatAdminChat(String originalMessage, CommandSender sender) throws NullPointerException {
        if (originalMessage == null) originalMessage = "";
        if (sender == null) throw new NullPointerException("sender can't be null!");
        originalMessage = RUtils.sanitizeInput(originalMessage, false);
        String newMessage = RUtils.colorize(RoyalChat.adminFormat);
        newMessage = RUtils.sanitizeInput(newMessage, false);
        newMessage = RUtils.colorize(RUtils.replaceTownyVars(newMessage, sender));
        newMessage = newMessage.replaceAll("(?i)\\{name\\}", sender.getName());
        newMessage = newMessage.replaceAll("(?i)\\{dispname\\}", ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName()));
        originalMessage = (RoyalChat.hasAuthorization(sender, "rchat.colors")) ? RUtils.colorize(originalMessage) : RUtils.removeColorCodes(originalMessage);
        if (RoyalChat.removeAllCaps && !RoyalChat.hasAuthorization(sender, "rchat.caps"))
            originalMessage = RUtils.removeCaps(originalMessage);
        if (RoyalChat.firstWordCapital)
            originalMessage = RUtils.capitalize(originalMessage);
        if (RoyalChat.highlightLinks)
            originalMessage = RUtils.highlightLinks(originalMessage);
        newMessage = newMessage.replaceAll("(?i)\\{message\\}", originalMessage);
        String world = (sender instanceof Player) ? MultiverseUtils.getMVWorldName(((Player) sender).getWorld()) : "";
        newMessage = newMessage.replaceAll("(?i)\\{world\\}", world);
        newMessage = newMessage.replaceAll("(?i)\\{prefix\\}", RUtils.getPrefix(sender));
        newMessage = newMessage.replaceAll("(?i)\\{suffix\\}", RUtils.getSuffix(sender));
        return newMessage.trim();
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("adminchat")) {
            if (!plugin.isAuthorized(cs, "rchat.adminchat")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                if (!(cs instanceof Player)) {
                    cs.sendMessage(cmd.getDescription());
                    return false;
                }
                if (toggles.contains(cs.getName())) toggles.remove(cs.getName());
                else toggles.add(cs.getName());
                String status = BooleanUtils.toStringOnOff(toggles.contains(cs.getName()));
                cs.sendMessage(ChatColor.BLUE + "Toggled admin chat " + ChatColor.GRAY + status + ChatColor.BLUE + ".");
                return true;
            }
            String message = formatAdminChat(RUtils.join(args, 0), cs);
            plugin.getServer().broadcast(message, "rchat.adminchat");
            return true;
        }
        return false;
    }

}
