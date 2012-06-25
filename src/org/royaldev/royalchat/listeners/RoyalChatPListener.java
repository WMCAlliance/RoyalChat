package org.royaldev.royalchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.utils.Channeler;
import org.royaldev.royalchat.utils.RUtils;

import java.util.List;

public class RoyalChatPListener implements Listener {

    RoyalChat plugin;

    public RoyalChatPListener(RoyalChat instance) {
        this.plugin = instance;
    }

    public boolean isAuthorized(final Player player, final String node) {
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(player, node);
    }

    public String parseVariables(Player p, String format) {
        try {
            format = format.replace("{prefix}", plugin.colorize(RoyalChat.chat.getPlayerPrefix(p)));
        } catch (Exception e) {
            format = format.replace("{prefix}", "");
        }
        try {
            format = format.replace("{suffix}", plugin.colorize(RoyalChat.chat.getPlayerSuffix(p)));
        } catch (Exception e) {
            format = format.replace("{suffix}", "");
        }
        String name = p.getName();
        String dispname = p.getDisplayName();
        String group = RoyalChat.permission.getPrimaryGroup(p);
        if (group != null) {
            String prefix = plugin.getConfig().getString("pbukkit.prefixes." + group);
            String suffix = plugin.getConfig().getString("pbukkit.suffixes." + group);
            if (prefix != null) {
                name = prefix + name;
                dispname = prefix + name;
            }
            if (suffix != null) {
                name = name + suffix;
                name = name + suffix;
            }
        }
        format = format.replace("{name}", plugin.colorize(name));
        format = format.replace("{dispname}", plugin.colorize(dispname));
        format = format.replace("{world}", plugin.returnAlias(p.getWorld()));
        return format;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String message = parseVariables(e.getPlayer(), plugin.joinMessage);
        if (message.equals("")) message = null;
        e.setJoinMessage(message);
        if (Channeler.playerChans.containsKey(e.getPlayer())) return;
        ConfigurationSection channels = plugin.getConfig().getConfigurationSection("channels");
        for (String chan : channels.getValues(true).keySet()) {
            if (!(channels.get(chan) instanceof ConfigurationSection)) continue;
            ConfigurationSection chanc = (ConfigurationSection) channels.get(chan);
            Boolean defChan = chanc.getBoolean("default");
            if (defChan == null) continue;
            if (defChan) plugin.c.addToChannel(e.getPlayer(), chanc.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String message = parseVariables(e.getPlayer(), plugin.quitMessage);
        if (message.equals("")) message = null;
        e.setQuitMessage(message);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.isCancelled()) return;
        String message = parseVariables(e.getPlayer(), plugin.kickMessage);
        if (message.equals("")) message = null;
        e.setLeaveMessage(message);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled()) return;
        if (e.getTo().getWorld().equals(e.getFrom().getWorld())) return;
        World to = e.getTo().getWorld();
        World from = e.getFrom().getWorld();
        String format = plugin.worldMessage.replace("{world}", plugin.returnAlias(to));
        format = format.replace("{fromworld}", plugin.returnAlias(from));
        format = parseVariables(e.getPlayer(), format);
        if (format.equals("")) return;
        plugin.getServer().broadcastMessage(format);
    }

    // The chat processor
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;

        // Get sent message
        String message = event.getMessage().trim();

        // Get player object of sender
        Player sender = event.getPlayer();

        if (plugin.acd.contains(sender)) {
            String format = plugin.f.formatChat(message, sender, plugin.formatAdmin);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!isAuthorized(p, "rchat.ac")) continue;
                p.sendMessage(format);
            }
            plugin.log.info(format);
            event.setCancelled(true);
            event.setFormat("");
            return;
        }

        if (!plugin.interWorld) {
            event.getRecipients().clear();
            event.getRecipients().addAll(sender.getWorld().getPlayers());
        }

        if (plugin.useChannels) {
            message = plugin.c.channelChat(sender, message, event);
            if (message.equals("")) event.setCancelled(true);
            event.setFormat(RUtils.colorize(message));
            for (Player p : plugin.getServer().getOnlinePlayers())
                if (isAuthorized(p, "rchat.snoop")) event.getRecipients().add(p);
            return;
        }

        if (plugin.maxRadius > 0) {
            double radius = plugin.maxRadius;
            List<Entity> ents = sender.getNearbyEntities(radius, radius, radius);
            event.getRecipients().clear();
            for (Entity e : ents) {
                if (!(e instanceof Player)) continue;
                Player t = (Player) e;
                event.getRecipients().add(t);
            }
            if (event.getRecipients().isEmpty()) sender.sendMessage(ChatColor.GRAY + "You feel lonely.");
            event.getRecipients().add(sender);
        }

        String format = plugin.f.formatChat(message, sender, plugin.formatBase);
        if (format.equals("")) event.setCancelled(true);
        event.setFormat(format);
    }
}