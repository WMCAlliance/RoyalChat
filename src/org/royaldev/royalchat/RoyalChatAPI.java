package org.royaldev.royalchat;

import org.bukkit.entity.Player;

public class RoyalChatAPI {
    
    private static RoyalChat plugin = RoyalChat.instance;
    
    public static void updatePluginInstance(RoyalChat instance) {
        plugin = instance;
    }
    
    //-- Getters --//

    public static String getPrefix(Player p) {
        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(p);
        } catch (Exception e) {
            return null;
        }
        return plugin.getConfig().getString("prefixes." + group, "");
    }

    public static String getPrefix(String world, String name) {
        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(world, name);
        } catch (Exception e) {
            return null;
        }
        return plugin.getConfig().getString("prefixes." + group, "");
    }

    public static String getSuffix(Player p) {
        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(p);
        } catch (Exception e) {
            return null;
        }
        return plugin.getConfig().getString("suffixes." + group, "");
    }

    public static String getSuffix(String world, String name) {
        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(world, name);
        } catch (Exception e) {
            return null;
        }
        return plugin.getConfig().getString("suffixes." + group, "");
    }
    
}
