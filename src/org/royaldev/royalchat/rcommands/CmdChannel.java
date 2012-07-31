package org.royaldev.royalchat.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.Channel;
import org.royaldev.royalchat.Channeler;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class CmdChannel implements CommandExecutor {

    RoyalChat plugin;

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
                if (cs instanceof Player) {
                    Channel c = Channeler.getPlayerChannel((Player) cs);
                    String message = (c == null) ? ChatColor.BLUE + "You are currently not in a channel." : ChatColor.BLUE + "You are currently in " + ChatColor.GRAY + c.getName() + ChatColor.BLUE + ".";
                    cs.sendMessage(message);
                }
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            String channel = args[0];
            String password = (args.length > 1) ? args[1] : "";
            Channel c = Channeler.getChannel(channel);
            if (c == null) {
                cs.sendMessage(ChatColor.RED + "No such channel!");
                return true;
            }
            if (c.getUsePassword() && password.equals("")) {
                cs.sendMessage(ChatColor.RED + "This channel requires a password!");
                return true;
            } else if (c.getUsePassword() && !password.equals(c.getPassword())) {
                cs.sendMessage(ChatColor.RED + "Wrong password!");
                return true;
            }
            Channel curChan = Channeler.getPlayerChannel(p);
            if (curChan != null) curChan.removeMember(p);
            c.addMember(p);
            cs.sendMessage(ChatColor.BLUE + "Joined " + ChatColor.GRAY + c.getName() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
