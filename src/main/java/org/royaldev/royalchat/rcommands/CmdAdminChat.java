package org.royaldev.royalchat.rcommands;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

import java.util.ArrayList;
import java.util.List;

public class CmdAdminChat implements CommandExecutor {

    private final RoyalChat plugin;

    public CmdAdminChat(RoyalChat instance) {
        plugin = instance;
    }

    private static final List<String> toggled = new ArrayList<>();

    public static boolean isToggled(Player p) {
        synchronized (toggled) {
            return toggled.contains(p.getName());
        }
    }

    public static boolean isToggled(String name) {
        synchronized (toggled) {
            return toggled.contains(name);
        }
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
                boolean isToggled = isToggled(cs.getName());
                synchronized (toggled) {
                    if (isToggled) toggled.remove(cs.getName());
                    else toggled.add(cs.getName());
                }
                cs.sendMessage(ChatColor.BLUE + "Toggled admin chat " + ChatColor.GRAY + BooleanUtils.toStringOnOff(!isToggled) + ChatColor.BLUE + ".");
                return true;
            }
            String message = StringUtils.join(args, ' ');
            String format = plugin.getConfig().getString("chat.admin-chat-format", "&b[Admin] {dispname}:&3 {message}");
            format = plugin.dm.formatChat(cs, format, message, false, true, false, false);
            plugin.getServer().broadcast(format, "rchat.adminchat");
            return true;
        }
        return false;
    }

}
