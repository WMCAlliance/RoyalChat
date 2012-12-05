package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.Channel;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

import static org.royaldev.royalchat.Language.*;

public class CmdChannel implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdChannel(RoyalChat instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("channel")) {
            if (!plugin.isAuthorized(cs, "rchat.channel")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + COMMAND_ONLY_PLAYERS.toString());
                return true;
            }
            if (!plugin.useChannels) {
                cs.sendMessage(ChatColor.RED + CHANNELS_OFF.toString());
                return true;
            }
            Player p = (Player) cs;
            String wantedChannel = args[0];
            String password = (args.length > 1) ? args[1] : null;
            Channel c = plugin.dm.getChannel(wantedChannel);
            if (c == null) {
                cs.sendMessage(ChatColor.RED + NO_CHANNEL.toString());
                return true;
            }
            if (!c.isPasswordProtected() && password != null)
                cs.sendMessage(ChatColor.RED + CHANNEL_EXTRA_PASSWORD.toString());
            if (c.isPasswordProtected()) {
                if (password == null) {
                    cs.sendMessage(ChatColor.RED + CHANNEL_MISSING_PASSWORD.toString() + " " + ChatColor.GRAY + "/" + label + ChatColor.RED + ".");
                    return true;
                }
                if (!password.equals(c.getPassword())) {
                    cs.sendMessage(ChatColor.RED + CHANNEL_WRONG_PASSWORD.toString());
                    return true;
                }
            }
            Channel current = plugin.dm.getChannelOf(p);
            if (current != null) current.removeMember(p);
            c.addMember(p);
            cs.sendMessage(ChatColor.BLUE + CHANNEL_JOINED.toString() + " " + ChatColor.GRAY + c.getName() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
