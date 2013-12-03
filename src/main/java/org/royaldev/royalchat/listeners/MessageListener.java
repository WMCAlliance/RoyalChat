package org.royaldev.royalchat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.royaldev.royalchat.RoyalChat;

import java.util.List;

public class MessageListener implements Listener {

    private final RoyalChat plugin;

    public MessageListener(RoyalChat instance) {
        plugin = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.join");
        if (format.isEmpty() || plugin.isVanished(p)) {
            e.setJoinMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        e.setJoinMessage(format);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.quit");
        if (format.isEmpty() || plugin.isVanished(p)) {
            e.setQuitMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        e.setQuitMessage(format);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.kick");
        if (format.isEmpty()) {
            e.setLeaveMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, e.getReason(), false, false, false, false);
        e.setLeaveMessage(format);
    }

    @EventHandler
    public void onWorld(PlayerTeleportEvent e) {
        if (e.getTo().getWorld().equals(e.getFrom().getWorld())) return;
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.world.message");
        if (format.isEmpty() || format.equals("no-handle")) return;
        if (plugin.getConfig().getBoolean("game-messages.world.display-whitelist.enabled")) {
            List<String> whitelistedWorlds = plugin.getConfig().getStringList("game-messages.world.display-whitelist.list");
            if (!whitelistedWorlds.contains(e.getTo().getWorld().getName())) return;
        }
        format = format.replace("{fromworld}", plugin.dm.getWorldName(e.getFrom().getWorld()));
        format = format.replace("{world}", plugin.dm.getWorldName(e.getTo().getWorld()));
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        for (Player t : plugin.getServer().getOnlinePlayers()) t.sendMessage(format);
    }

}
